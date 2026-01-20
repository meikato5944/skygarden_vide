package com.example.skygarden.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.bean.ElementItemBean;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Content;

/**
 * 構成要素アイテムに関するREST APIコントローラー
 * 
 * このコントローラーはテンプレート編集時に使用する構成要素の一覧を提供するAPIエンドポイントを定義します。
 * テンプレート編集画面で、ドラッグ＆ドロップ可能な構成要素のリストを取得するために使用されます。
 * 
 * 主な機能:
 * - 構成要素一覧の取得
 * 
 * 構成要素について:
 * 構成要素（element）は、テンプレートに組み込む再利用可能なHTMLパーツです。
 * 例: ヘッダー、フッター、サイドバー、ナビゲーションなど
 * 
 * テンプレートでの使用:
 * テンプレートの content フィールドには、構成要素とコンテンツの配置順序が
 * カンマ区切りで保存されます。
 * 例: "###element(1),###content###,###element(2)"
 * 
 * @see Content コンテンツ管理のビジネスロジック
 * @see ElementItemBean 構成要素アイテム情報Bean
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
public class ElementItemController {
	
	/** コンテンツ管理のビジネスロジック */
	@Autowired
	private Content content;

	/**
	 * 構成要素アイテムの一覧を取得する
	 * テンプレート編集画面で構成要素を選択する際に使用される
	 * 
	 * @return 構成要素アイテムのリストを含むElementItemBean
	 * @throws IOException IO例外
	 */
	@GetMapping("/getElementItem")
	@ResponseBody
	public ElementItemBean getElementItem() throws IOException {
		ElementItemBean bean = new ElementItemBean();
		bean.setResults(content.getAllList(Constants.TABLE_CONTENT, Constants.EMPTY_STRING, Constants.CONTENT_TYPE_ELEMENT));
		return bean;
	}
}
