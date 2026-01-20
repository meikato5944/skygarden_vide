package com.example.skygarden.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * フロントエンドのルーティングを処理するコントローラー
 * SPA（Single Page Application）の各ルートに対してindex.htmlを返す
 */
@Controller
public class HomeController {
    
    /**
     * トップページ（コンテンツ一覧）のルーティング
     * 
     * @return index.html
     */
    @GetMapping("/")
    public String index() {
        return "index.html";
    }
    
    /**
     * ログイン画面のルーティング
     * 
     * @return index.html
     */
    @GetMapping("/login")
    public String index2() {
        return "index.html";
    }
    
    /**
     * コンテンツ編集画面のルーティング
     * 
     * @return index.html
     */
    @GetMapping("/content")
    public String index3() {
        return "index.html";
    }
    
    /**
     * ユーザー一覧画面のルーティング
     * 
     * @return index.html
     */
    @GetMapping("/user-list")
    public String index4() {
        return "index.html";
    }
    
    /**
     * ユーザー編集画面のルーティング
     * 
     * @return index.html
     */
    @GetMapping("/user")
    public String index5() {
        return "index.html";
    }
    
    /**
     * 設定画面のルーティング
     * 
     * @return index.html
     */
    @GetMapping("/setting")
    public String index6() {
        return "index.html";
    }
}
