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
 * 
 * このコントローラーはスケジュール公開・非公開のバッチ処理を実行するAPIエンドポイントを定義します。
 * 定期的に実行することで、スケジュールされたコンテンツの公開・非公開を自動化します。
 * 
 * 主な機能:
 * - スケジュール公開処理: schedule_published日時が現在以前のコンテンツを公開テーブルに登録
 * - スケジュール非公開処理: schedule_unpublished日時が現在以前のコンテンツを公開テーブルから削除
 * 
 * 使用方法:
 * - cronジョブやタスクスケジューラから定期的にこのAPIを呼び出す
 * - 例: curl http://localhost:8080/webadmin/batch
 * 
 * 処理フロー:
 * 1. publishedBatch(): 公開予定日時が過ぎたコンテンツを公開
 * 2. unPublishedBatch(): 非公開予定日時が過ぎたコンテンツを非公開
 * 
 * @see Batch バッチ処理のビジネスロジック
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
public class BatchController {
	
	/** バッチ処理のビジネスロジック */
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
