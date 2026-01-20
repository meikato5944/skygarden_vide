package com.example.skygarden.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

/**
 * コンテンツ情報を保持するBeanクラス（DTO）
 * 
 * このクラスはコンテンツの作成・編集画面でController層からView層へ
 * データを転送するためのDTO（Data Transfer Object）です。
 * REST APIのレスポンスとしてJSON形式でシリアライズされます。
 * 
 * 主な用途:
 * - ContentController.getById() のレスポンス
 * - コンテンツ編集画面のデータバインディング
 * 
 * フィールド説明:
 * - screenName: 画面名（コンテンツ、テンプレート、構成要素など）
 * - schedule_published: 公開予定日時
 * - schedule_unpublished: 非公開予定日時
 * - template: テンプレートID
 * - title: コンテンツタイトル
 * - head: ヘッダー部分のHTML（CSS/JS参照など）
 * - content: コンテンツ本文
 * - url: 公開URL
 * - elementcolor: 構成要素の色コード
 * - templateOutput: テンプレート選択プルダウンのHTML
 * - colorOutput: 色選択プルダウンのHTML
 * - eleResults: 構成要素のリスト（テンプレート編集用）
 * - publishflgKeep: 公開フラグ
 * 
 * @see ContentController コンテンツ管理APIコントローラー
 */
@Data
public class ContentBean implements Serializable {
	
	/** シリアルバージョンUID */
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
