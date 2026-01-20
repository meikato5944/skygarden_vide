package com.example.skygarden.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 設定管理のビジネスロジッククラス
 * アプリケーション設定（構成要素の色設定など）の更新・取得を提供する
 */
public class Setting {
	private DB db = new DB();
	/** ルートパス（ファイル操作時に使用） */
	String ROOTPATH;

	/**
	 * 設定情報を更新する
	 * 現在は構成要素の色設定のみ対応
	 * 
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 */
	public void doUpdate(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		String name = "elements-color-value";
		String elementsValue = request.getParameter(name);
		String redirectUrl =  CommonProc.FRONTEND_PATH + "/";
		db.updateSetting(elementsValue, name);
		session.setAttribute("registerMessage", "設定を登録しました。");
		try {
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 構成要素の色設定リストを取得する
	 * データベースから取得した文字列をパースしてリスト形式に変換する
	 * 
	 * @return 色要素のリスト（各要素はname、codeなどの情報を含む）
	 */
	public List<HashMap<String, String>> elementsColorList() {
		List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		String result = db.getElementColor();
		String[] values = result.split("\\*");
		for (int i = 0; i < values.length; i++) {
			if (values[i] != null && !values[i].equals("")) {
				HashMap<String, String> m = new HashMap<String, String>();
				String[] v = values[i].split("=");
				String name = v[0];
				String value = v[1];
				m.put("name", name);
				m.put("code", value);
				results.add(m);
			}
		}
		return results;
	}
}
