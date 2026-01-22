package com.example.skygarden.logic;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ログイン処理のビジネスロジッククラス
 * ユーザー認証を提供する
 */
@Service
public class Login {
	@Autowired
	private ContentMapper mapper;

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
	public void doLogin(String name, String password, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		HashMap<String, String> result = mapper.getUser(name);
		if (result == null || result.isEmpty()) {
			try {
				response.sendRedirect(Constants.PATH_LOGIN + "?loginError=" + Constants.ERROR_USER_NOT_FOUND);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		String userPassword = result.get("password");
		String loginName = result.get("name");
		String admin = result.get("admin");
		try {
			if (userPassword != null && password.equals(userPassword)) {
				response.sendRedirect(Constants.PATH_ROOT);
				session.setAttribute(Constants.SESSION_LOGIN_NAME, loginName);
				session.setAttribute(Constants.SESSION_NAME, name);
				session.setAttribute(Constants.SESSION_ADMIN, admin);
			} else {
				response.sendRedirect(Constants.PATH_LOGIN + "?loginError=" + Constants.ERROR_PASSWORD_INCORRECT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
