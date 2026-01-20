package com.example.skygarden.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.logic.CommonProc;
import com.example.skygarden.logic.Content;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * プレビュー機能に関するREST APIコントローラー
 * コンテンツのプレビュー表示を提供する
 */
@RestController
@RequestMapping("/webadmin")
public class PreviewController {

	/**
	 * コンテンツのプレビューを生成する
	 * テンプレートとコンテンツを組み合わせてHTMLを生成し、プレビュー用のHTMLを返す
	 * 
	 * @param template テンプレートID
	 * @param title タイトル
	 * @param head ヘッダー部分のHTML
	 * @param content コンテンツ本文
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @throws IOException IO例外
	 */
	@PostMapping("/preview")
	@ResponseBody
	public void getById(@RequestParam(defaultValue = "") String template, @RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") String head, @RequestParam(defaultValue = "") String content, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Content con = new Content();
		String tempHead = con.doSearch(template).get("head");
		String resultHead = tempHead + head;
		String reasultContent = con.previewContent(content, template);
		String previewFile = CommonProc.readFile(CommonProc.getRootPath() + "/preview.html");
		if(reasultContent == null) {
			reasultContent = "";
		}
		previewFile = previewFile.replaceAll("###title###", title);
		previewFile = previewFile.replaceAll("###head###", resultHead);
		previewFile = previewFile.replaceAll("###content###", reasultContent);
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(previewFile);
		response.getWriter().close();
		return;
	}
}
