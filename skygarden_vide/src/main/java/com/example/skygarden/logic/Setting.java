package com.example.skygarden.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 設定管理のビジネスロジッククラス
 * アプリケーション設定（構成要素の色設定など）の更新・取得を提供する
 */
@Service
public class Setting {
	@Autowired
	private ContentMapper mapper;
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
		String elementsValue = request.getParameter(Constants.CONFIG_ELEMENTS_COLOR_VALUE);
		mapper.updateSetting(elementsValue, Constants.CONFIG_ELEMENTS_COLOR_VALUE);
		session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_SETTING_REGISTER_SUCCESS);
		try {
			response.sendRedirect(Constants.PATH_ROOT);
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
		List<HashMap<String, String>> results = new ArrayList<>();
		String result = mapper.getElementColor();
		if (result != null && !result.equals(Constants.EMPTY_STRING)) {
			String[] values = result.split("\\*");
			for (String value : values) {
				if (value != null && !value.equals(Constants.EMPTY_STRING)) {
					HashMap<String, String> m = new HashMap<>();
					String[] v = value.split("=");
					if (v.length >= 2) {
						m.put("name", v[0]);
						m.put("code", v[1]);
						results.add(m);
					}
				}
			}
		}
		return results;
	}
}
