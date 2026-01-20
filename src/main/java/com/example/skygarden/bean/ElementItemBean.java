package com.example.skygarden.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

/**
 * 構成要素アイテム情報を保持するBeanクラス（DTO）
 * 
 * このクラスはテンプレート編集画面でController層からView層へ
 * 構成要素リストを転送するためのDTO（Data Transfer Object）です。
 * REST APIのレスポンスとしてJSON形式でシリアライズされます。
 * 
 * 主な用途:
 * - ElementItemController.getElementItem() のレスポンス
 * - テンプレート編集画面の構成要素選択リスト
 * 
 * フィールド説明:
 * - results: 構成要素のリスト
 *   各要素は以下のキーを持つHashMap:
 *   - id: 構成要素ID
 *   - title: 構成要素タイトル
 *   - elementcolor: 構成要素の色コード
 * 
 * テンプレート編集画面での使用:
 * テンプレート編集画面では、このリストからドラッグ＆ドロップで
 * 構成要素をテンプレートに配置します。配置された構成要素は
 * 割り当てられた色で視覚的に区別されます。
 * 
 * @see ElementItemController 構成要素アイテムAPIコントローラー
 */
@Data
public class ElementItemBean implements Serializable {
	
	/** シリアルバージョンUID */
	private static final long serialVersionUID = 1L;
	/** 構成要素のリスト（各要素はid、title、codeなどの情報を含む） */
	List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
}
