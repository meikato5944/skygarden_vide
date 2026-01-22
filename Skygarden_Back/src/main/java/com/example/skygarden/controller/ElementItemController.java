package com.example.skygarden.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.bean.ElementItemBean;
import com.example.skygarden.logic.Content;

/**
 * 構成要素アイテムに関するREST APIコントローラー
 * テンプレート編集時に使用する構成要素の一覧を提供する
 */
@RestController
@RequestMapping("/webadmin")
public class ElementItemController {

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
		Content content = new Content();
		ElementItemBean bean = new ElementItemBean();
		bean.setResults(content.getAllList("content", "", "element"));
		return bean;
	}
}
