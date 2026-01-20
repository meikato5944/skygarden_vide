package com.example.skygarden.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.logic.CommonProc;
import com.example.skygarden.logic.Login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ログイン・認証に関するREST APIコントローラー
 * ユーザーのログイン、ログアウト、認証状態の確認を提供する
 */
@RestController
@RequestMapping("/webadmin")
public class LoginController {

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
	@PostMapping("/login_post")
	@ResponseBody
	public void login(@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String password, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		Login login = new Login();
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
	@GetMapping("/logout")
	@ResponseBody
	public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		session.invalidate();
		response.sendRedirect(CommonProc.FRONTEND_PATH + "/login");
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
		return session.getAttribute("name") != null && !session.getAttribute("name").equals("");
	}
}
