package com.example.skygarden.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Batch;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * バッチ処理に関するREST APIコントローラー
 * スケジュール公開・非公開のバッチ処理を提供する
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
public class BatchController {
	@Autowired
	private Batch batch;

	/**
	 * バッチ処理を実行する
	 * スケジュール公開・非公開の処理を実行する
	 * 
	 * @param id 未使用（互換性のため保持）
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @throws IOException IO例外
	 */
	@GetMapping("/batch")
	@ResponseBody
	public void getUser(@RequestParam(defaultValue = "") String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		this.batch.publishedBatch();
		this.batch.unPublishedBatch();
	}
}
