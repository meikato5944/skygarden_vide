package com.example.skygarden.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

/**
 * コンテンツ一覧情報を保持するBeanクラス
 * コンテンツ一覧画面で表示するデータを格納する
 */
@Data
public class ListBean implements Serializable {
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
