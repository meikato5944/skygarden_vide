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
 * テンプレート編集時に使用する構成要素の一覧を提供する
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
public class ElementItemController {
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
