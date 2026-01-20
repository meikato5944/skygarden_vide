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
 * 
 * このサービスクラスはユーザー認証のビジネスロジックを提供します。
 * ユーザー名とパスワードによる認証を行い、セッションにユーザー情報を保存します。
 * 
 * 主な機能:
 * - ユーザー認証（ログイン処理）
 * - セッションへのユーザー情報保存
 * - 認証失敗時のエラーハンドリング
 * 
 * 認証フロー:
 * 1. ユーザー名でデータベースからユーザー情報を取得
 * 2. ユーザーが存在しない場合、エラーメッセージと共にログイン画面へリダイレクト
 * 3. パスワードを比較（現状は平文比較）
 * 4. 認証成功: セッションにユーザー情報を保存し、トップページへリダイレクト
 * 5. 認証失敗: エラーメッセージと共にログイン画面へリダイレクト
 * 
 * セッションに保存される情報:
 * - loginName: ログインユーザー名（表示用）
 * - name: ユーザー名（識別用）
 * - admin: 管理者フラグ（"1":管理者、"0":一般ユーザー）
 * 
 * セキュリティ注意事項:
 * - パスワードは現状平文で保存・比較されています
 * - 本番環境ではパスワードのハッシュ化（BCryptなど）を推奨
 * 
 * @see ContentMapper データベース操作
 */
@Service
public class Login {
	
	/** コンテンツ管理用のMyBatis Mapper（ユーザー情報取得にも使用） */
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
