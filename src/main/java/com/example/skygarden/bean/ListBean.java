package com.example.skygarden.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

/**
 * コンテンツ一覧情報を保持するBeanクラス（DTO）
 * 
 * このクラスはコンテンツ一覧画面でController層からView層へ
 * データを転送するためのDTO（Data Transfer Object）です。
 * REST APIのレスポンスとしてJSON形式でシリアライズされます。
 * 
 * 主な用途:
 * - ContentController.getListApi() のレスポンス
 * - コンテンツ一覧画面のデータバインディング
 * 
 * フィールド説明:
 * - loginName: ログインユーザー名（ウェルカムメッセージ用）
 * - screenName: 画面名（コンテンツ、テンプレート、構成要素など）
 * - registerMessage: 登録・更新・削除後のメッセージ
 * - sortOutput: ソート選択プルダウンのHTML
 * - pagerOutput: ページネーションナビゲーションのHTML
 * - results: コンテンツ一覧（各要素はid, title, url, type等を含むHashMap）
 * 
 * @see ContentController コンテンツ管理APIコントローラー
 */
@Data
public class ListBean implements Serializable {
	
	/** シリアルバージョンUID */
	private static final long serialVersionUID = 1L;
	/** ログインユーザー名 */
	String loginName = "";
	/** 画面名（コンテンツ、テンプレート、構成要素など） */
	String screenName = "";
	/** 登録・更新・削除時のメッセージ */
	String registerMessage = "";
	/** ソート選択用のHTMLオプション */
	String sortOutput = "";
	/** ページネーション用のHTML */
	String pagerOutput = "";
	/** コンテンツ一覧の結果リスト */
	List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();	
}
