package com.example.skygarden.logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;
import com.example.skygarden.service.EmailService;

import lombok.extern.slf4j.Slf4j;

/**
 * バッチ処理のビジネスロジッククラス
 * 
 * このサービスクラスはスケジュール公開・非公開のバッチ処理を実行するビジネスロジックを提供します。
 * 定期的に実行されることで、指定された日時にコンテンツを自動的に公開・非公開にします。
 * 
 * 主な機能:
 * - スケジュール公開処理（publishedBatch）
 * - スケジュール非公開処理（unPublishedBatch）
 * 
 * スケジュール公開処理フロー:
 * 1. schedule_published が現在時刻以前のコンテンツを検索
 * 2. 該当コンテンツを content_public テーブルに登録（既存の場合は更新）
 * 3. contentテーブルの schedule_published をクリア
 * 
 * スケジュール非公開処理フロー:
 * 1. schedule_unpublished が現在時刻以前のコンテンツを検索
 * 2. 該当コンテンツを content_public テーブルから削除
 * 3. contentテーブルの schedule_unpublished をクリア
 * 
 * 実行方法:
 * BatchController の /webadmin/batch エンドポイントを呼び出すことで実行されます。
 * 本番環境では、cronジョブやタスクスケジューラから定期的に呼び出すことを推奨します。
 * 例: "0 * * * * curl http://localhost:8080/webadmin/batch" (毎分実行)
 * 
 * @see ContentMapper データベース操作
 * @see BatchController バッチ処理APIコントローラー
 */
@Slf4j
@Service
public class Batch {
	
	/** コンテンツ管理用のMyBatis Mapper */
	@Autowired
	private ContentMapper mapper;
	
	/** メール送信サービス */
	@Autowired(required = false)
	private EmailService emailService;

	/**
	 * スケジュール公開のバッチ処理を実行する
	 * 公開予定日時が現在時刻以前のコンテンツを公開テーブルに登録・更新する
	 */
	public void publishedBatch() {
		String now = CommonProc.createNow();
		List<HashMap<String, String>> results = mapper.getSchedulePublishedIds(now);

		for (HashMap<String, String> resultItem : results) {
			String id = resultItem.get("id");
			HashMap<String, String> result = mapper.search(id, Constants.TABLE_CONTENT);
			HashMap<String, String> publicResult = mapper.search(id, Constants.TABLE_CONTENT_PUBLIC);
			String name = result.get("created_by");
			String url = result.get("url");
			String title = result.get("title");
			String head = result.get("head");
			String contentStr = result.get("content");
			String type = result.get("type");
			String elementcolor = result.get("elementcolor");
			String template = result.get("template");
			String schedule_published = Constants.EMPTY_STRING; // 公開後のため空で登録
			String schedule_unpublished = result.get("schedule_unpublished");
			String publishflg_keep = result.get("publishflg_keep");
			try {
				boolean isFirstPublish = (publicResult == null || publicResult.isEmpty() || publicResult.get("id") == null || publicResult.get("id").equals(Constants.EMPTY_STRING));
				if (isFirstPublish) {
					String nowTime = CommonProc.createNow();
					mapper.createPublic(Integer.valueOf(id), nowTime, nowTime, name, name, url, title, head, contentStr, type, elementcolor, template, schedule_published, schedule_unpublished, publishflg_keep);
					
					// メール送信処理（初回公開時のみ）
					if (emailService != null) {
						try {
							SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DATETIME);
							Date publishDate = sdf.parse(nowTime.replaceAll("/", "-"));
							String emailError = emailService.sendContentPublishedNotification(title, url, publishDate);
							if (emailError != null) {
								log.warn("メール送信に失敗しました: {}", emailError);
							} else {
								log.info("メール送信成功: タイトル={}, URL={}", title, url);
							}
						} catch (ParseException e) {
							log.error("日時パースエラー: {}", e.getMessage());
						} catch (Exception e) {
							log.error("メール送信中にエラーが発生しました: {}", e.getMessage(), e);
						}
					}
				} else {
					String nowTime = CommonProc.createNow();
					mapper.updatePublic(id, nowTime, name, url, title, head, contentStr, type, elementcolor, template, schedule_published, schedule_unpublished, publishflg_keep);
				}
				mapper.clearSchedulePublished(id);
			} catch (Exception e) {
				log.error("バッチ処理エラー: " + e.toString());
				e.printStackTrace();
			}
		}
	}

	/**
	 * スケジュール非公開のバッチ処理を実行する
	 * 非公開予定日時が現在時刻以前のコンテンツを公開テーブルから削除する
	 */
	public void unPublishedBatch() {
		String now = CommonProc.createNow();
		List<HashMap<String, String>> results = mapper.getScheduleUnpublishedIds(now);

		for (HashMap<String, String> resultItem : results) {
			String id = resultItem.get("id");
			try {
				mapper.delete(Constants.TABLE_CONTENT_PUBLIC, id);
				mapper.clearScheduleUnpublished(id);
			} catch (Exception e) {
				log.error("バッチ処理エラー: " + e.toString());
				e.printStackTrace();
			}
		}
	}
}
