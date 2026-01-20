package com.example.skygarden.logic;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * バッチ処理のビジネスロジッククラス
 * スケジュール公開・非公開のバッチ処理を提供する
 */
@Slf4j
@Service
public class Batch {
	@Autowired
	private ContentMapper mapper;
	@Autowired
	private Content content;

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
				if (publicResult == null || publicResult.isEmpty() || publicResult.get("id") == null || publicResult.get("id").equals(Constants.EMPTY_STRING)) {
					String nowTime = CommonProc.createNow();
					mapper.createPublic(Integer.valueOf(id), nowTime, nowTime, name, name, url, title, head, contentStr, type, elementcolor, template, schedule_published, schedule_unpublished, publishflg_keep);
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
