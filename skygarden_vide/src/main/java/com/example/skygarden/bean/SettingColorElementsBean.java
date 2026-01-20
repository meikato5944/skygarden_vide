package com.example.skygarden.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

/**
 * 設定画面の色要素情報を保持するBeanクラス
 * 構成要素の色設定で使用されるデータを格納する
 */
@Data
public class SettingColorElementsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 色要素のリスト（各要素はname、codeなどの情報を含む） */
	List<HashMap<String, String>> colorElements;
}
