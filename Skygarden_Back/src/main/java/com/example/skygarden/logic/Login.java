package com.example.skygarden.logic;

import java.io.IOException;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ログイン処理のビジネスロジッククラス
 * ユーザー認証を提供する
 */
public class Login {
	private DB db = new DB();

	/**
	 * ユーザーのログイン処理を行う
	 * ユーザー名とパスワードを検証し、認証成功時はセッションに情報を保存する
	 * 
	 * @param name ユーザー名
	 * @param password パスワード
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 */
	public void doLogin(String name, String password, HttpServletRequest request, HttpServletResponse response,    HttpSession session) {
		HashMap<String, String> result = db.getUser(name);
		String userPassword = result.get("password");
		String loginName = result.get("name");
		String admin = result.get("admin");
		try {
			if (password.equals(userPassword)) {
				response.sendRedirect(CommonProc.FRONTEND_PATH + "/");
				session.setAttribute("loginName", loginName);
				session.setAttribute("name", name);
				session.setAttribute("admin", admin);
			} else {
				response.sendRedirect(CommonProc.FRONTEND_PATH + "/login?loginError=don't login. Please continue");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
