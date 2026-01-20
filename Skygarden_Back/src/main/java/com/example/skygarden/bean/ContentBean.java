package com.example.skygarden.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

/**
 * コンテンツ情報を保持するBeanクラス
 * コンテンツの作成・編集画面で使用されるデータを格納する
 */
@Data
public class ContentBean implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 画面名（コンテンツ、テンプレート、構成要素など） */
	String screenName = "";
	/** 公開予定日時 */
	String schedule_published = "";
	/** 非公開予定日時 */
	String schedule_unpublished = "";
	/** テンプレートID */
	String template = "";
	/** タイトル */
	String title = "";
	/** ヘッダー部分のHTML */
	String head = "";
	/** コンテンツ本文 */
	String content = "";
	/** URLパス */
	String url = "";
	/** 構成要素の色コード */
	String elementcolor = "";
	/** テンプレート選択用のHTMLオプション */
	String templateOutput = "";
	/** 色選択用のHTMLオプション */
	String colorOutput = "";
	/** 構成要素のリスト */
	List<HashMap<String, String>> eleResults = new ArrayList<HashMap<String, String>>();
	/** 公開フラグ（保持用） */
	String publishflgKeep = "";
}
