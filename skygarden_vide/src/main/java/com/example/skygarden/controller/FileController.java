package com.example.skygarden.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * ファイル管理に関するREST APIコントローラー
 * ファイルのアップロード、更新、削除などの処理を提供する
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
@Slf4j
public class FileController {
	
	@Autowired
	private ContentMapper mapper;
	
	@Autowired
	private AppProperties appProperties;
	
	/**
	 * ファイルをアップロードし、コンテンツとして登録する
	 * 
	 * @param file アップロードされたファイル
	 * @param id コンテンツID（空の場合は新規作成）
	 * @param title ファイルのタイトル
	 * @param url ファイルのURL（物理パス）
	 * @param published 公開フラグ
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @throws IOException IO例外
	 */
	@PostMapping("/file_upload")
	@ResponseBody
	public void uploadFile(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(defaultValue = "") String id,
			@RequestParam(defaultValue = "") String title,
			@RequestParam(defaultValue = "") String url,
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
			String originalFileName = "";
			
			// ファイルがアップロードされた場合のみ保存処理を行う
			if (file != null && !file.isEmpty()) {
				// 元のファイル名を取得
				originalFileName = file.getOriginalFilename();
				String extension = "";
				if (originalFileName != null && originalFileName.contains(".")) {
					extension = originalFileName.substring(originalFileName.lastIndexOf("."));
				}
				
				// ファイル名を生成（UUID + 拡張子）
				savedFileName = UUID.randomUUID().toString() + extension;
				
				// アップロードディレクトリの作成
				String uploadDir = appProperties.getFile().getFileUploadDir();
				Path uploadPath = Paths.get(uploadDir);
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				
				// ファイルを保存
				Path filePath = uploadPath.resolve(savedFileName);
				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				log.info("File saved: {}", filePath.toString());
			}
			
			// データベースへの登録/更新
			// headフィールドに元のファイル名を保存（ダウンロード時のファイル名として使用）
			if (id.isEmpty()) {
				// 新規作成
				int newId = mapper.create(
					nowTime, nowTime, name, name,
					url, title, originalFileName, savedFileName,
					Constants.CONTENT_TYPE_FILE, "", "",
					schedule_published, schedule_unpublished, published
				);
				
				if (published.equals(Constants.FLAG_YES)) {
					mapper.createPublic(
						newId, nowTime, nowTime, name, name,
						url, title, originalFileName, savedFileName,
						Constants.CONTENT_TYPE_FILE, "", "",
						schedule_published, schedule_unpublished, published
					);
				}
			} else {
				// 更新
				// 既存のデータを取得
				HashMap<String, String> existing = mapper.search(id, Constants.TABLE_CONTENT);
				String content = savedFileName;
				String head = originalFileName;
				
				// ファイルがアップロードされていない場合は既存のファイル名を使用
				if (savedFileName.isEmpty() && existing != null) {
					content = existing.get("content") != null ? existing.get("content") : "";
					head = existing.get("head") != null ? existing.get("head") : "";
				}
				
				mapper.update(
					id, nowTime, name,
					url, title, head, content,
					Constants.CONTENT_TYPE_FILE, "", "",
					schedule_published, schedule_unpublished, published
				);
				
				if (published.equals(Constants.FLAG_YES)) {
					String publicId = mapper.searchContentByAttribute(id, "id", Constants.TABLE_CONTENT_PUBLIC);
					if (publicId != null && !publicId.isEmpty()) {
						mapper.updatePublic(
							id, nowTime, name,
							url, title, head, content,
							Constants.CONTENT_TYPE_FILE, "", "",
							schedule_published, schedule_unpublished, published
						);
					} else {
						mapper.createPublic(
							Integer.valueOf(id), nowTime, nowTime, name, name,
							url, title, head, content,
							Constants.CONTENT_TYPE_FILE, "", "",
							schedule_published, schedule_unpublished, published
						);
					}
				}
			}
			
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_SUCCESS);
		} catch (Exception e) {
			log.error("File upload error", e);
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_FAILED);
		}
		
		response.sendRedirect("/?mode=file");
	}
}
