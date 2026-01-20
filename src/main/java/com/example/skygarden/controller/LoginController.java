package com.example.skygarden.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ログイン・認証に関するREST APIコントローラー
 * 
 * このコントローラーはユーザー認証に関連するAPIエンドポイントを提供します。
 * セッションベースの認証を使用し、ログイン状態をサーバー側で管理します。
 * 
 * 主な機能:
 * - ユーザー認証（ログイン）
 * - セッション無効化（ログアウト）
 * - 認証状態の確認
 * 
 * セキュリティ:
 * - パスワードは現状平文で比較されています（本番環境ではハッシュ化を推奨）
 * - セッションタイムアウトはapplication.propertiesで設定可能
 * 
 * @see Login ログイン処理のビジネスロジック
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
public class LoginController {
	
	/** ログイン処理のビジネスロジック */
	@Autowired
	private Login login;

	/**
	 * ユーザーのログイン処理を行う
	 * ユーザー名とパスワードを検証し、認証成功時はセッションに情報を保存してリダイレクトする
	 * 
	 * @param name ユーザー名
	 * @param password パスワード
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @throws IOException IO例外
	 */
	@PostMapping(Constants.API_LOGIN_POST)
	@ResponseBody
	public void login(@RequestParam(defaultValue = Constants.EMPTY_STRING) String name, @RequestParam(defaultValue = Constants.EMPTY_STRING) String password, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		login.doLogin(name, password, request, response, session);
	}

	/**
	 * ユーザーのログアウト処理を行う
	 * セッションを無効化し、ログイン画面にリダイレクトする
	 * 
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @throws IOException IO例外
	 */
	@GetMapping(Constants.API_LOGOUT)
	@ResponseBody
	public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		session.invalidate();
		response.sendRedirect(Constants.PATH_LOGIN);
	}

	/**
	 * 認証状態を確認する
	 * セッションにユーザー情報が存在するかチェックする
	 * 
	 * @param session HTTPセッション
	 * @return 認証済みの場合true、未認証の場合false
	 * @throws IOException IO例外
	 */
	@GetMapping("/auth")
	@ResponseBody
	public boolean auth(HttpSession session) throws IOException {
		return session.getAttribute(Constants.SESSION_NAME) != null 
				&& !session.getAttribute(Constants.SESSION_NAME).equals(Constants.EMPTY_STRING);
	}
}
