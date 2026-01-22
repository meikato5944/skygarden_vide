package com.example.skygarden.logic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ユーザー管理のビジネスロジッククラス
 * ユーザーの作成、更新、取得、一覧取得などの処理を提供する
 */
public class User {
	private DB db = new DB();

	/**
	 * 新規ユーザーを作成する
	 * 
	 * @param name ユーザー名
	 * @param password パスワード
	 * @param email メールアドレス
	 * @param admin 管理者フラグ（"1"が管理者、"0"が一般ユーザー）
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @return 処理成功時true
	 */
	public boolean create(String name, String password, String email, String admin, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		if (!admin.equals("1")) {
			admin = "0";
		}
		try {
			db.createUser(name, password, email, admin);
			session.setAttribute("registerMessage", "登録しました。");
		} catch (SQLException e) {
			session.setAttribute("registerMessage", "登録に失敗しました。");
		} catch (Exception e) {
			session.setAttribute("registerMessage", "異常が発生しました。");
		}
		try {
			response.sendRedirect(CommonProc.FRONTEND_PATH + "/user-list");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 既存のユーザー情報を更新する
	 * 
	 * @param id ユーザーID
	 * @param name ユーザー名
	 * @param password パスワード
	 * @param email メールアドレス
	 * @param admin 管理者フラグ（"1"が管理者、"0"が一般ユーザー）
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @return 処理成功時true
	 */
	public boolean update(String id, String name, String password, String email, String admin, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		if (!admin.equals("1")) {
			admin = "0";
		}
		try {
			db.updateUser(id, name, password, email, admin);
			session.setAttribute("registerMessage", "登録しました。");
		} catch (SQLException e) {
			session.setAttribute("registerMessage", "登録に失敗しました。");
		} catch (Exception e) {
			session.setAttribute("registerMessage", "異常が発生しました。");
		}
		try {
			response.sendRedirect(CommonProc.FRONTEND_PATH + "/user-list");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * ユーザー一覧を取得する（ページネーション対応）
	 * 
	 * @param sort ソート順
	 * @param page ページ番号
	 * @return ユーザー一覧のリスト
	 */
	public List<HashMap<String, String>> getList(String sort, int page) {
		int limit = 20;
		int offset = 0;
		if (1 < page) {
			offset = (page - 1) * 20;
		}
		return db.selectAllLimit("user", sort, "", limit, offset);
	}

	/**
	 * ユーザーIDでユーザー情報を取得する
	 * 
	 * @param id ユーザーID
	 * @return ユーザー情報を含むHashMap
	 */
	public HashMap<String, String> getUser(String id) {
		return db.getUserById(id);
	}

	/**
	 * ページネーション用のHTMLを生成する
	 * 
	 * @param page 現在のページ番号
	 * @param pageUrl ページURL
	 * @param sort ソート順
	 * @return ページネーション用のHTML文字列
	 */
	public String getPager(int page, String pageUrl, String sort) {
		StringBuffer output = new StringBuffer();
		int contentSize = db.getCountentSize("user", "");
		int pageSize = (contentSize / 20);

		if (1 <= contentSize % 20) {
			pageSize = pageSize + 1;
		}
		//Prev
		if (1 < page && pageSize != 0) {
			output.append("<li class=\"page-item me-4\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + "?page=" + (page - 1) + "&sort=" + sort + "\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>");
		}
		//PageNum
		for (int i = 1; i <= pageSize; i++) {
			if (i == page) {
				output.append("<li class=\"page-item mx-2\">" + i + "</span></li>");
			} else {
				output.append("<li class=\"page-item mx-2\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + "?page=" + i + "&sort=" + sort + "\">" + i + "</a></li>");
			}
		}
		//Next
		if (page < pageSize) {
			output.append("<li class=\"page-item ms-4\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + "?page=" + (page + 1) + "&sort=" + sort + "\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>");
		}
		return output.toString();
	}
}