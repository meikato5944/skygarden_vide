package com.example.skygarden.controller;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.config.AppProperties;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.CommonProc;
import com.example.skygarden.logic.Content;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * プレビュー機能に関するREST APIコントローラー
 * コンテンツのプレビュー表示を提供する
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
@Slf4j
public class PreviewController {
	@Autowired
	private Content content;
	@Autowired
	private AppProperties appProperties;

	/**
	 * コンテンツのプレビューを生成する
	 * テンプレートとコンテンツを組み合わせてHTMLを生成し、プレビュー用のHTMLを返す
	 * GETとPOSTの両方のメソッドに対応
	 * 
	 * @param template テンプレートID
	 * @param title タイトル
	 * @param head ヘッダー部分のHTML
	 * @param content コンテンツ本文
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @throws IOException IO例外
	 */
	@RequestMapping(value = Constants.API_PREVIEW, method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public void getById(@RequestParam(defaultValue = Constants.EMPTY_STRING) String template, @RequestParam(defaultValue = Constants.EMPTY_STRING) String title, @RequestParam(defaultValue = Constants.EMPTY_STRING) String head, @RequestParam(defaultValue = Constants.EMPTY_STRING) String content, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String tempHead = Constants.EMPTY_STRING;
		if (template != null && !template.equals(Constants.EMPTY_STRING)) {
			HashMap<String, String> templateResult = this.content.doSearch(template);
			if (templateResult != null && templateResult.get("head") != null) {
				tempHead = templateResult.get("head");
			}
		}
		String resultHead = tempHead + (head != null ? head : Constants.EMPTY_STRING);
		String reasultContent = this.content.previewContent(content != null ? content : Constants.EMPTY_STRING, template);
		if(reasultContent == null) {
			reasultContent = Constants.EMPTY_STRING;
		}
		
		String previewFilePath = CommonProc.getRootPath() + "/" + appProperties.getFile().getPreviewFileName();
		log.info("Preview file path: {}", previewFilePath);
		
		String previewFile = CommonProc.readFile(previewFilePath);
		
		// プレビューファイルが空の場合はデフォルトのHTMLを使用
		if (previewFile == null || previewFile.isEmpty()) {
			log.warn("Preview file not found or empty: {}", previewFilePath);
			previewFile = "<!DOCTYPE html><html lang=\"ja\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>###title###</title>###head###</head><body>###content###</body></html>";
		}
		
		// 正規表現の特殊文字をエスケープ
		String escapedTitle = java.util.regex.Matcher.quoteReplacement(title != null ? title : Constants.EMPTY_STRING);
		String escapedHead = java.util.regex.Matcher.quoteReplacement(resultHead);
		String escapedContent = java.util.regex.Matcher.quoteReplacement(reasultContent);
		
		previewFile = previewFile.replaceAll(Constants.TEMPLATE_TITLE_PLACEHOLDER, escapedTitle);
		previewFile = previewFile.replaceAll(Constants.TEMPLATE_HEAD_PLACEHOLDER, escapedHead);
		previewFile = previewFile.replaceAll(Constants.TEMPLATE_CONTENT_PLACEHOLDER, escapedContent);
		
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(previewFile);
		response.getWriter().close();
		return;
	}
}
