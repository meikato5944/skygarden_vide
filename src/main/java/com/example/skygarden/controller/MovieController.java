package com.example.skygarden.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.CommonProc;
import com.example.skygarden.mapper.ContentMapper;
import com.example.skygarden.service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 動画管理に関するREST APIコントローラー
 * 
 * このコントローラーはYouTube動画の登録・更新機能を提供するAPIエンドポイントを定義します。
 * YouTube URLからビデオIDを抽出し、データベースに登録します。
 * 
 * 主な機能:
 * - YouTube動画URLの登録
 * - 動画情報の更新
 * - 動画サイズ（幅・高さ）の指定
 * - スケジュール公開・非公開設定
 * 
 * 対応するYouTube URL形式:
 * - https://www.youtube.com/watch?v=VIDEO_ID
 * - https://youtu.be/VIDEO_ID
 * - https://www.youtube.com/embed/VIDEO_ID
 * 
 * データベース格納:
 * - url: YouTube URL（フルURL）
 * - title: 動画タイトル
 * - head: サイズ情報（JSON形式）
 * - content: YouTubeビデオID
 * 
 * コンテンツでの使用方法:
 * コンテンツ内で [movie id=XXX] タグを使用すると、
 * Content.convertMovieTags() メソッドによりYouTube埋め込みコードに変換されます。
 * 
 * @see ContentMapper データベース操作
 * @see Content#convertMovieTags(String) 動画タグ変換処理
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
@Slf4j
public class MovieController {
	
	/** コンテンツ管理用のMyBatis Mapper */
	@Autowired
	private ContentMapper mapper;
	
	/** メール送信サービス */
	@Autowired(required = false)
	private EmailService emailService;
	
	/**
	 * 動画URLを登録し、コンテンツとして登録する
	 * 
	 * @param id コンテンツID（空の場合は新規作成）
	 * @param title 動画のタイトル
	 * @param youtubeUrl YouTube動画のURL
	 * @param movieWidth 動画の幅
	 * @param movieHeight 動画の高さ
	 * @param published 公開フラグ
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @throws IOException IO例外
	 */
	@PostMapping("/movie_register")
	@ResponseBody
	public void registerMovie(
			@RequestParam(defaultValue = "") String id,
			@RequestParam(defaultValue = "") String title,
			@RequestParam(defaultValue = "") String youtubeUrl,
			@RequestParam(defaultValue = "") String movieWidth,
			@RequestParam(defaultValue = "") String movieHeight,
			@RequestParam(defaultValue = "") String published,
			@RequestParam(defaultValue = "") String schedule_published,
			@RequestParam(defaultValue = "") String schedule_unpublished,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session) throws IOException {
		
		String name = (String) session.getAttribute("name");
		String nowTime = CommonProc.createNow();
		
		// スケジュール日時の形式変換
		if (schedule_published != null && !schedule_published.isEmpty()) {
			schedule_published = schedule_published.replaceAll("/", "-").replaceAll("T", " ");
		} else {
			schedule_published = "";
		}
		if (schedule_unpublished != null && !schedule_unpublished.isEmpty()) {
			schedule_unpublished = schedule_unpublished.replaceAll("/", "-").replaceAll("T", " ");
		} else {
			schedule_unpublished = "";
		}
		
		try {
			// YouTube URLからビデオIDを抽出
			String videoId = extractYoutubeVideoId(youtubeUrl);
			
			// サイズ情報をJSON形式で保存（headフィールドに格納）
			String sizeInfo = buildSizeJson(movieWidth, movieHeight);
			
			// データベースへの登録/更新
			// urlフィールドにYouTube URL、contentフィールドにビデオID、headフィールドにサイズ情報を保存
			boolean isFirstPublish = false;
			if (id.isEmpty()) {
				// 新規作成
				int newId = mapper.create(
					nowTime, nowTime, name, name,
					youtubeUrl, title, sizeInfo, videoId,
					Constants.CONTENT_TYPE_MOVIE, "", "",
					schedule_published, schedule_unpublished, published
				);
				
				if (published.equals(Constants.FLAG_YES)) {
					mapper.createPublic(
						newId, nowTime, nowTime, name, name,
						youtubeUrl, title, sizeInfo, videoId,
						Constants.CONTENT_TYPE_MOVIE, "", "",
						schedule_published, schedule_unpublished, published
					);
					isFirstPublish = true;
				}
				log.info("Movie registered: id={}, videoId={}, size={}", newId, videoId, sizeInfo);
			} else {
				// 更新
				HashMap<String, String> existing = mapper.search(id, Constants.TABLE_CONTENT);
				String content = videoId;
				
				// URLが変更されていない場合は既存のビデオIDを使用
				if (youtubeUrl.isEmpty() && existing != null) {
					content = existing.get("content") != null ? existing.get("content") : "";
					youtubeUrl = existing.get("url") != null ? existing.get("url") : "";
				}
				
				mapper.update(
					id, nowTime, name,
					youtubeUrl, title, sizeInfo, content,
					Constants.CONTENT_TYPE_MOVIE, "", "",
					schedule_published, schedule_unpublished, published
				);
				
				if (published.equals(Constants.FLAG_YES)) {
					String publicId = mapper.searchContentByAttribute(id, "id", Constants.TABLE_CONTENT_PUBLIC);
					if (publicId != null && !publicId.isEmpty()) {
						mapper.updatePublic(
							id, nowTime, name,
							youtubeUrl, title, sizeInfo, content,
							Constants.CONTENT_TYPE_MOVIE, "", "",
							schedule_published, schedule_unpublished, published
						);
					} else {
						mapper.createPublic(
							Integer.valueOf(id), nowTime, nowTime, name, name,
							youtubeUrl, title, sizeInfo, content,
							Constants.CONTENT_TYPE_MOVIE, "", "",
							schedule_published, schedule_unpublished, published
						);
						isFirstPublish = true;
					}
				}
				log.info("Movie updated: id={}, videoId={}, size={}", id, content, sizeInfo);
			}
			
			// メール送信処理（初回公開時のみ）
			String registerMessage = Constants.MESSAGE_REGISTER_SUCCESS;
			if (isFirstPublish && emailService != null) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DATETIME);
					Date publishDate = sdf.parse(nowTime.replaceAll("/", "-"));
					String emailError = emailService.sendContentPublishedNotification(title, youtubeUrl, publishDate);
					if (emailError != null) {
						registerMessage = Constants.MESSAGE_REGISTER_SUCCESS_WITH_EMAIL_ERROR + " " + emailError;
					}
				} catch (ParseException e) {
					log.error("日時パースエラー: {}", e.getMessage());
				} catch (Exception e) {
					log.error("メール送信中にエラーが発生しました: {}", e.getMessage(), e);
				}
			}
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, registerMessage);
		} catch (Exception e) {
			log.error("Movie registration error", e);
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_FAILED);
		}
		
		response.sendRedirect("/?mode=movie");
	}
	
	/**
	 * サイズ情報をJSON形式で構築する
	 * 
	 * @param width 幅
	 * @param height 高さ
	 * @return JSON形式のサイズ情報
	 */
	private String buildSizeJson(String width, String height) {
		StringBuilder json = new StringBuilder("{");
		boolean hasValue = false;
		
		if (width != null && !width.trim().isEmpty()) {
			json.append("\"width\":\"").append(width.trim()).append("\"");
			hasValue = true;
		}
		
		if (height != null && !height.trim().isEmpty()) {
			if (hasValue) {
				json.append(",");
			}
			json.append("\"height\":\"").append(height.trim()).append("\"");
		}
		
		json.append("}");
		return json.toString();
	}
	
	/**
	 * YouTube URLからビデオIDを抽出する
	 * 対応形式:
	 * - https://www.youtube.com/watch?v=VIDEO_ID
	 * - https://youtu.be/VIDEO_ID
	 * - https://www.youtube.com/embed/VIDEO_ID
	 * 
	 * @param url YouTube URL
	 * @return ビデオID
	 */
	private String extractYoutubeVideoId(String url) {
		if (url == null || url.isEmpty()) {
			return "";
		}
		
		// youtu.be形式
		if (url.contains("youtu.be/")) {
			String[] parts = url.split("youtu.be/");
			if (parts.length > 1) {
				String videoId = parts[1].split("[?&]")[0];
				return videoId;
			}
		}
		
		// youtube.com/watch?v= 形式
		if (url.contains("youtube.com/watch")) {
			String[] params = url.split("[?&]");
			for (String param : params) {
				if (param.startsWith("v=")) {
					return param.substring(2).split("[&]")[0];
				}
			}
		}
		
		// youtube.com/embed/ 形式
		if (url.contains("youtube.com/embed/")) {
			String[] parts = url.split("youtube.com/embed/");
			if (parts.length > 1) {
				String videoId = parts[1].split("[?&]")[0];
				return videoId;
			}
		}
		
		// URLそのものがビデオIDの場合
		if (url.matches("^[a-zA-Z0-9_-]{11}$")) {
			return url;
		}
		
		return url;
	}
}
