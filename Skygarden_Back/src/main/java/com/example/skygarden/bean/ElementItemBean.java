package com.example.skygarden.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

/**
 * 構成要素アイテム情報を保持するBeanクラス
 * テンプレート編集時に使用する構成要素のリストを格納する
 */
@Data
public class ElementItemBean implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 構成要素のリスト（各要素はid、title、codeなどの情報を含む） */
	List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
}
