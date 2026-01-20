package com.example.skygarden.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.bean.SettingColorElementsBean;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Setting;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 設定管理に関するREST APIコントローラー
 * 
 * このコントローラーはCMSシステムの設定管理機能を提供するAPIエンドポイントを定義します。
 * アプリケーション設定の更新・取得を行います。
 * 
 * 主な機能:
 * - 構成要素の色設定の更新
 * - 構成要素の色設定の取得
 * - セッション属性の取得
 * 
 * 設定データの形式:
 * 色設定は「name=code*name=code*」形式の文字列として保存されます。
 * 例: "header=#000000*footer=#333333*"
 * 
 * @see Setting 設定管理のビジネスロジック
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
public class SettingController {
	
	/** 設定管理のビジネスロジック */
	@Autowired
	private Setting setting;

	/**
	 * 設定情報を更新する
	 * 現在は構成要素の色設定のみ対応
	 * 
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @throws IOException IO例外
	 */
	@PostMapping(Constants.API_SETTING_POST)
	@ResponseBody
	public void update(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		setting.doUpdate(request, response, session);
	}
	
	/**
	 * 設定情報（構成要素の色設定）を取得する
	 * 
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @return 色要素設定情報を含むSettingColorElementsBean
	 * @throws IOException IO例外
	 */
	@GetMapping("/get-setting")
	@ResponseBody
	public SettingColorElementsBean getSetting(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		List<HashMap<String, String>> colorElements = setting.elementsColorList();
		SettingColorElementsBean bean = new SettingColorElementsBean();
		bean.setColorElements(colorElements);
		return bean;
	}
	
	/**
	 * セッション属性の値を取得する
	 * 
	 * @param attribute 取得するセッション属性名
	 * @param session HTTPセッション
	 * @return セッション属性の値
	 * @throws IOException IO例外
	 */
	@GetMapping("/get-session")
	@ResponseBody
	public String getUserName(@RequestParam String attribute, HttpSession session) throws IOException {
		return (String) session.getAttribute(attribute);
	}
}
