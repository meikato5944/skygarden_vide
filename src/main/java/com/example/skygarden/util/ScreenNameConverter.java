package com.example.skygarden.util;

import com.example.skygarden.constants.Constants;

/**
 * 画面名変換ユーティリティクラス
 * 
 * このクラスはモード文字列（コンテンツタイプ）と画面名・URL間の変換処理を提供します。
 * リクエストパラメータのモードに応じて、適切な画面名やリダイレクトURLを取得するために使用されます。
 * 
 * 主な機能:
 * - モード文字列を画面名に変換（convertModeToScreenName）
 * - モードに応じたページURLを生成（getPageUrlByMode）
 * - タイプに応じたリダイレクトURLを生成（getRedirectUrlByType）
 * 
 * モード/タイプと画面名の対応:
 * - ""（空文字列）: コンテンツ
 * - "template": テンプレート
 * - "element": 構成要素
 * - "stylesheet": CSS
 * - "script": JS
 * - "image": 画像
 * - "file": ファイル
 * - "movie": 動画
 * 
 * このクラスはstaticメソッドのみを持ち、インスタンス化できません。
 * 
 * @see Constants コンテンツタイプと画面名の定数定義
 */
public final class ScreenNameConverter {
	
	/**
	 * プライベートコンストラクタ
	 * ユーティリティクラスのインスタンス化を防止する
	 */
	private ScreenNameConverter() {
		// インスタンス化を防ぐ
	}
	
	/**
	 * モード文字列を画面名に変換する
	 * 
	 * @param mode モード文字列
	 * @return 画面名
	 */
	public static String convertModeToScreenName(String mode) {
		if (mode == null || mode.isEmpty()) {
			return Constants.SCREEN_NAME_CONTENT;
		}
		
		switch (mode) {
			case Constants.CONTENT_TYPE_TEMPLATE:
				return Constants.SCREEN_NAME_TEMPLATE;
			case Constants.CONTENT_TYPE_ELEMENT:
				return Constants.SCREEN_NAME_ELEMENT;
			case Constants.CONTENT_TYPE_STYLESHEET:
				return Constants.SCREEN_NAME_STYLESHEET;
			case Constants.CONTENT_TYPE_SCRIPT:
				return Constants.SCREEN_NAME_SCRIPT;
			case Constants.CONTENT_TYPE_IMAGE:
				return Constants.SCREEN_NAME_IMAGE;
			case Constants.CONTENT_TYPE_FILE:
				return Constants.SCREEN_NAME_FILE;
			case Constants.CONTENT_TYPE_MOVIE:
				return Constants.SCREEN_NAME_MOVIE;
			default:
				return Constants.SCREEN_NAME_CONTENT;
		}
	}
	
	/**
	 * モードに応じたページURLを生成する
	 * 
	 * @param mode モード文字列
	 * @return ページURL
	 */
	public static String getPageUrlByMode(String mode) {
		if (mode == null || mode.isEmpty()) {
			return Constants.PATH_ROOT + "?mode=";
		}
		
		switch (mode) {
			case Constants.CONTENT_TYPE_TEMPLATE:
				return Constants.PATH_ROOT + "?mode=template";
			case Constants.CONTENT_TYPE_ELEMENT:
				return Constants.PATH_ROOT + "?mode=element";
			case Constants.CONTENT_TYPE_STYLESHEET:
				return Constants.PATH_ROOT + "?mode=stylesheet";
			case Constants.CONTENT_TYPE_SCRIPT:
				return Constants.PATH_ROOT + "?mode=script";
			case Constants.CONTENT_TYPE_IMAGE:
				return Constants.PATH_ROOT + "?mode=image";
			case Constants.CONTENT_TYPE_FILE:
				return Constants.PATH_ROOT + "?mode=file";
			case Constants.CONTENT_TYPE_MOVIE:
				return Constants.PATH_ROOT + "?mode=movie";
			case Constants.CONTENT_TYPE_ELEMENT_ITEM:
				return Constants.PATH_ELEMENT_ITEM + "/?mode=";
			default:
				return Constants.PATH_ROOT + "?mode=";
		}
	}
	
	/**
	 * タイプに応じたリダイレクトURLを生成する
	 * 
	 * @param type コンテンツタイプ
	 * @return リダイレクトURL
	 */
	public static String getRedirectUrlByType(String type) {
		if (type == null || type.isEmpty()) {
			return Constants.PATH_ROOT;
		}
		
		switch (type) {
			case Constants.CONTENT_TYPE_TEMPLATE:
				return Constants.PATH_ROOT + "?mode=template";
			case Constants.CONTENT_TYPE_ELEMENT:
				return Constants.PATH_ROOT + "?mode=element";
			case Constants.CONTENT_TYPE_STYLESHEET:
				return Constants.PATH_ROOT + "?mode=stylesheet";
			case Constants.CONTENT_TYPE_SCRIPT:
				return Constants.PATH_ROOT + "?mode=script";
			case Constants.CONTENT_TYPE_IMAGE:
				return Constants.PATH_ROOT + "?mode=image";
			case Constants.CONTENT_TYPE_FILE:
				return Constants.PATH_ROOT + "?mode=file";
			case Constants.CONTENT_TYPE_MOVIE:
				return Constants.PATH_ROOT + "?mode=movie";
			default:
				return Constants.PATH_ROOT;
		}
	}
}
