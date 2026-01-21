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
 * 
 * このサービスクラスはCMSのアプリケーション設定を管理するビジネスロジックを提供します。
 * 構成要素の色設定など、システム全体で使用される設定の更新・取得を行います。
 * 
 * 主な機能:
 * - 構成要素の色設定の更新
 * - 構成要素の色設定の取得（リスト形式）
 * 
 * 設定データの形式:
 * 色設定は configテーブルの "elements-color-value" という名前で保存されます。
 * 形式: "name=code*name=code*..."
 * 例: "header=#000000*footer=#333333*"
 * 
 * パース後のデータ構造:
 * リスト内の各要素は以下のキーを持つHashMap:
 * - name: 色の名前（例: "header", "footer"）
 * - code: 色コード（例: "#000000"）
 * 
 * この色設定は、構成要素編集画面で構成要素に色を割り当てる際に使用されます。
 * テンプレート編集画面では、構成要素が割り当てられた色で視覚的に区別されます。
 * 
 * @see ContentMapper データベース操作
 */
@Service
public class Setting {
	
	/** コンテンツ管理用のMyBatis Mapper（設定操作にも使用） */
	@Autowired
	private ContentMapper mapper;
	
	/** ルートパス（ファイル操作時に使用、現在は未使用） */
	String ROOTPATH;

	/**
	 * 設定情報を更新する
	 * 構成要素の色設定とデフォルト公開設定に対応
	 * 
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 */
	public void doUpdate(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// 構成要素の色設定を更新
		String elementsValue = request.getParameter(Constants.CONFIG_ELEMENTS_COLOR_VALUE);
		mapper.updateSetting(elementsValue, Constants.CONFIG_ELEMENTS_COLOR_VALUE);
		
		// デフォルト公開設定を更新
		String defaultPublishOn = request.getParameter(Constants.CONFIG_DEFAULT_PUBLISH_ON);
		if (defaultPublishOn == null) {
			defaultPublishOn = Constants.FLAG_NO;
		}
		mapper.updateSetting(defaultPublishOn, Constants.CONFIG_DEFAULT_PUBLISH_ON);
		
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
	
	/**
	 * デフォルト公開設定を取得する
	 * 新規コンテンツ作成時にpublishをオンで開くかオフで開くかを決定する
	 * 
	 * @return "1" = オン（公開）、"0" or null = オフ（非公開）
	 */
	public String getDefaultPublishOn() {
		String result = mapper.getSettingByName(Constants.CONFIG_DEFAULT_PUBLISH_ON);
		return result != null ? result : Constants.FLAG_NO;
	}
}
