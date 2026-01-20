package com.example.skygarden.logic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skygarden.config.AppProperties;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;
import com.example.skygarden.util.PaginationUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ユーザー管理のビジネスロジッククラス
 * 
 * このサービスクラスはCMSユーザーの管理機能を提供するビジネスロジックを実装します。
 * ユーザーの作成、更新、取得、一覧取得などの処理を行います。
 * 
 * 主な機能:
 * - ユーザーの新規作成
 * - ユーザー情報の更新
 * - ユーザー情報の取得（IDによる検索）
 * - ユーザー一覧の取得（ページネーション・ソート対応）
 * - ページネーションHTMLの生成
 * 
 * ユーザー属性:
 * - id: ユーザーID（自動採番）
 * - name: ユーザー名（ログイン時に使用）
 * - password: パスワード
 * - email: メールアドレス
 * - admin: 管理者フラグ（"1":管理者、"0":一般ユーザー）
 * 
 * 権限について:
 * 現在の実装では管理者フラグは保存されますが、
 * 権限による機能制限は実装されていません。
 * 
 * @see ContentMapper データベース操作
 * @see AppProperties アプリケーション設定（ページサイズ等）
 */
@Service
public class User {
	
	/** コンテンツ管理用のMyBatis Mapper（ユーザー操作にも使用） */
	@Autowired
	private ContentMapper mapper;
	
	/** アプリケーション設定プロパティ */
	@Autowired
	private AppProperties appProperties;

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
		if (!admin.equals(Constants.FLAG_YES)) {
			admin = Constants.FLAG_NO;
		}
		try {
			mapper.createUser(name, password, email, admin);
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_FAILED);
		}
		try {
			response.sendRedirect(Constants.PATH_USER_LIST);
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
		if (!admin.equals(Constants.FLAG_YES)) {
			admin = Constants.FLAG_NO;
		}
		try {
			mapper.updateUser(id, name, password, email, admin);
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_FAILED);
		}
		try {
			response.sendRedirect(Constants.PATH_USER_LIST);
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
		int pageSize = appProperties.getPagination().getPageSize();
		int offset = PaginationUtil.calculateOffset(page, pageSize);
		return mapper.selectAllLimit(Constants.TABLE_USER, sort, Constants.EMPTY_STRING, pageSize, offset);
	}

	/**
	 * ユーザーIDでユーザー情報を取得する
	 * 
	 * @param id ユーザーID
	 * @return ユーザー情報を含むHashMap
	 */
	public HashMap<String, String> getUser(String id) {
		return mapper.getUserById(id);
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
		int contentSize = mapper.getContentSize(Constants.TABLE_USER, Constants.EMPTY_STRING);
		int pageSize = appProperties.getPagination().getPageSize();
		int totalPages = PaginationUtil.calculateTotalPages(contentSize, pageSize);
		//Prev
		if (1 < page && totalPages != 0) {
			output.append("<li class=\"page-item me-4\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + "?page=" + (page - 1) + "&sort=" + sort + "\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>");
		}
		//PageNum
		for (int i = 1; i <= totalPages; i++) {
			if (i == page) {
				output.append("<li class=\"page-item mx-2\">" + i + "</span></li>");
			} else {
				output.append("<li class=\"page-item mx-2\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + "?page=" + i + "&sort=" + sort + "\">" + i + "</a></li>");
			}
		}
		//Next
		if (page < totalPages) {
			output.append("<li class=\"page-item ms-4\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + "?page=" + (page + 1) + "&sort=" + sort + "\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>");
		}
		return output.toString();
	}
}