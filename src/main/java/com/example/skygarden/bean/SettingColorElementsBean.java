package com.example.skygarden.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

/**
 * 設定画面の色要素情報を保持するBeanクラス（DTO）
 * 
 * このクラスは設定画面でController層からView層へ
 * 構成要素の色設定データを転送するためのDTO（Data Transfer Object）です。
 * REST APIのレスポンスとしてJSON形式でシリアライズされます。
 * 
 * 主な用途:
 * - SettingController.getSetting() のレスポンス
 * - 設定画面の色設定一覧表示
 * 
 * フィールド説明:
 * - colorElements: 色要素のリスト
 *   各要素は以下のキーを持つHashMap:
 *   - name: 色の名前（例: "header", "footer"）
 *   - code: 色コード（例: "#000000"）
 * 
 * 色設定の使用:
 * この色設定は構成要素編集画面で構成要素に色を割り当てる際に使用されます。
 * テンプレート編集画面では、構成要素が割り当てられた色で視覚的に区別されます。
 * 
 * @see SettingController 設定管理APIコントローラー
 */
@Data
public class SettingColorElementsBean implements Serializable {
	
	/** シリアルバージョンUID */
	private static final long serialVersionUID = 1L;
	/** 色要素のリスト（各要素はname、codeなどの情報を含む） */
	List<HashMap<String, String>> colorElements;
}
