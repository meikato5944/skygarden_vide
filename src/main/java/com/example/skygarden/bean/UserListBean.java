package com.example.skygarden.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

/**
 * ユーザー一覧情報を保持するBeanクラス（DTO）
 * 
 * このクラスはユーザー一覧画面でController層からView層へ
 * データを転送するためのDTO（Data Transfer Object）です。
 * REST APIのレスポンスとしてJSON形式でシリアライズされます。
 * 
 * 主な用途:
 * - UserController.getList() のレスポンス
 * - ユーザー一覧画面のデータバインディング
 * 
 * フィールド説明:
 * - loginName: ログインユーザー名（ウェルカムメッセージ用）
 * - registerMessage: 登録・更新・削除後のメッセージ
 * - sortOutput: ソート選択プルダウンのHTML
 * - pagerOutput: ページネーションナビゲーションのHTML
 * - results: ユーザー一覧（各要素はid, name, email, admin等を含むHashMap）
 * 
 * @see UserController ユーザー管理APIコントローラー
 */
@Data
public class UserListBean implements Serializable {
	
	/** シリアルバージョンUID */
	private static final long serialVersionUID = 1L;	
	/** ログインユーザー名 */
	String loginName = "";
	/** 登録・更新・削除時のメッセージ */
	String registerMessage = "";
	/** ソート選択用のHTMLオプション */
	String sortOutput = "";
	/** ページネーション用のHTML */
	String pagerOutput = "";
	/** ユーザー一覧の結果リスト */
	List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
}
