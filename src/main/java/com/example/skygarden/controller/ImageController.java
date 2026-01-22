package com.example.skygarden.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.skygarden.config.AppProperties;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.CommonProc;
import com.example.skygarden.mapper.ContentMapper;
import com.example.skygarden.service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 画像管理に関するREST APIコントローラー
 * 
 * このコントローラーは画像ファイルのアップロード・更新機能を提供するAPIエンドポイントを定義します。
 * アップロードされた画像はファイルシステムに保存され、メタデータはデータベースに登録されます。
 * 
 * 主な機能:
 * - 画像ファイルのアップロード
 * - 画像情報の更新
 * - 画像サイズ（幅・高さ）の指定
 * - スケジュール公開・非公開設定
 * 
 * ファイル保存:
 * - 保存先: app.file.upload-dir で指定されたディレクトリ（デフォルト: uploads/images）
 * - ファイル名: UUID + 元の拡張子
 * - 対応形式: image/* のMIMEタイプを持つファイル
 * 
 * データベース格納:
 * - url: 公開URL
 * - title: 画像タイトル
 * - head: サイズ情報（JSON形式）
 * - content: 保存されたファイル名
 * 
 * @see ContentMapper データベース操作
 * @see AppProperties アプリケーション設定
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
@Slf4j
public class ImageController {
	
	/** コンテンツ管理用のMyBatis Mapper */
	@Autowired
	private ContentMapper mapper;
	
	/** アプリケーション設定プロパティ */
	@Autowired
	private AppProperties appProperties;
	
	/** メール送信サービス */
	@Autowired(required = false)
	private EmailService emailService;
	
	/**
	 * 画像をアップロードし、コンテンツとして登録する
	 * 
	 * @param file アップロードされた画像ファイル
	 * @param id コンテンツID（空の場合は新規作成）
	 * @param title 画像のタイトル
	 * @param url 画像のURL（物理パス）
	 * @param imageWidth 画像の幅
	 * @param imageHeight 画像の高さ
	 * @param published 公開フラグ
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @throws IOException IO例外
	 */
	@PostMapping("/image_upload")
	@ResponseBody
	public void uploadImage(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(defaultValue = "") String id,
			@RequestParam(defaultValue = "") String title,
			@RequestParam(defaultValue = "") String url,
			@RequestParam(defaultValue = "") String imageWidth,
			@RequestParam(defaultValue = "") String imageHeight,
			@RequestParam(defaultValue = "") String published,
			@RequestParam(defaultValue = "") String schedule_published,
			@RequestParam(defaultValue = "") String schedule_unpublished,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session) throws IOException {
		
		String name = (String) session.getAttribute("name");
		String nowTime = CommonProc.createNow();
		
		// URLの先頭スラッシュを除去
		while (url.startsWith("/")) {
			url = url.substring(1);
		}
		
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
			String savedFileName = "";
			
			// ファイルがアップロードされた場合のみ保存処理を行う
			if (file != null && !file.isEmpty()) {
				// 画像ファイルのバリデーション
				String contentType = file.getContentType();
				if (contentType == null || !contentType.startsWith("image/")) {
					session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, "画像ファイルのみアップロード可能です。");
					response.sendRedirect("/?mode=image");
					return;
				}
				
				// ファイル名を生成（UUID + 元のファイル名）
				String originalFileName = file.getOriginalFilename();
				String extension = "";
				if (originalFileName != null && originalFileName.contains(".")) {
					extension = originalFileName.substring(originalFileName.lastIndexOf("."));
				}
				savedFileName = UUID.randomUUID().toString() + extension;
				
				// アップロードディレクトリの作成
				String uploadDir = appProperties.getFile().getUploadDir();
				Path uploadPath = Paths.get(uploadDir);
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				
				// ファイルを保存
				Path filePath = uploadPath.resolve(savedFileName);
				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				log.info("Image saved: {}", filePath.toString());
			}
			
			// サイズ情報をJSON形式で保存（headフィールドに格納）
			String sizeInfo = buildSizeJson(imageWidth, imageHeight);
			
			// データベースへの登録/更新
			boolean isFirstPublish = false;
			if (id.isEmpty()) {
				// 新規作成
				int newId = mapper.create(
					nowTime, nowTime, name, name,
					url, title, sizeInfo, savedFileName,
					Constants.CONTENT_TYPE_IMAGE, "", "",
					schedule_published, schedule_unpublished, published
				);
				
				if (published.equals(Constants.FLAG_YES)) {
					mapper.createPublic(
						newId, nowTime, nowTime, name, name,
						url, title, sizeInfo, savedFileName,
						Constants.CONTENT_TYPE_IMAGE, "", "",
						schedule_published, schedule_unpublished, published
					);
					isFirstPublish = true;
				}
				log.info("Image registered: id={}, size={}", newId, sizeInfo);
			} else {
				// 更新
				// 既存のデータを取得
				HashMap<String, String> existing = mapper.search(id, Constants.TABLE_CONTENT);
				String content = savedFileName;
				
				// ファイルがアップロードされていない場合は既存のファイル名を使用
				if (savedFileName.isEmpty() && existing != null) {
					content = existing.get("content") != null ? existing.get("content") : "";
				}
				
				mapper.update(
					id, nowTime, name,
					url, title, sizeInfo, content,
					Constants.CONTENT_TYPE_IMAGE, "", "",
					schedule_published, schedule_unpublished, published
				);
				
				if (published.equals(Constants.FLAG_YES)) {
					String publicId = mapper.searchContentByAttribute(id, "id", Constants.TABLE_CONTENT_PUBLIC);
					if (publicId != null && !publicId.isEmpty()) {
						mapper.updatePublic(
							id, nowTime, name,
							url, title, sizeInfo, content,
							Constants.CONTENT_TYPE_IMAGE, "", "",
							schedule_published, schedule_unpublished, published
						);
					} else {
						mapper.createPublic(
							Integer.valueOf(id), nowTime, nowTime, name, name,
							url, title, sizeInfo, content,
							Constants.CONTENT_TYPE_IMAGE, "", "",
							schedule_published, schedule_unpublished, published
						);
						isFirstPublish = true;
					}
				}
				log.info("Image updated: id={}, size={}", id, sizeInfo);
			}
			
			// メール送信処理（初回公開時のみ）
			String registerMessage = Constants.MESSAGE_REGISTER_SUCCESS;
			if (isFirstPublish && emailService != null) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DATETIME);
					Date publishDate = sdf.parse(nowTime.replaceAll("/", "-"));
					String emailError = emailService.sendContentPublishedNotification(title, url, publishDate);
					if (emailError != null) {
						registerMessage = Constants.MESSAGE_REGISTER_SUCCESS_WITH_EMAIL_ERROR + " " + emailError;
					} else {
						registerMessage = Constants.MESSAGE_REGISTER_SUCCESS_WITH_EMAIL_SUCCESS;
					}
				} catch (ParseException e) {
					log.error("日時パースエラー: {}", e.getMessage());
				} catch (Exception e) {
					log.error("メール送信中にエラーが発生しました: {}", e.getMessage(), e);
				}
			}
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, registerMessage);
		} catch (Exception e) {
			log.error("Image upload error", e);
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_FAILED);
		}
		
		response.sendRedirect("/?mode=image");
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
}
