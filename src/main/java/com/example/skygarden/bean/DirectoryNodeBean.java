package com.example.skygarden.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * URLディレクトリのツリーノードを表すBeanクラス
 * 
 * このクラスはURLディレクトリ画面でコンテンツをツリー構造で表示するための
 * ノード情報を保持します。ファイルシステムのような階層構造を表現します。
 * 
 * 主な用途:
 * - URLディレクトリ画面のツリー表示
 * - Content.getUrlDirectoryTree() の戻り値要素
 * 
 * ツリー構造の例:
 * ROOT (ディレクトリ)
 * ├── about (ディレクトリ)
 * │   ├── company.html (ファイル/コンテンツ)
 * │   └── history.html (ファイル/コンテンツ)
 * └── index.html (ファイル/コンテンツ)
 * 
 * フィールド説明:
 * - name: 表示名（ファイル名またはディレクトリ名）
 * - path: URLパス
 * - id: コンテンツID（ファイルノードの場合のみ設定）
 * - title: コンテンツタイトル（ファイルノードの場合のみ設定）
 * - isDirectory: ディレクトリノードの場合true
 * - children: 子ノードのリスト（ディレクトリノードの場合のみ使用）
 * 
 * @see Content#getUrlDirectoryTree(String) ツリー生成処理
 * @see HomeController#urlDirectory(org.springframework.ui.Model) URLディレクトリ画面
 */
public class DirectoryNodeBean {
    
    /** 表示名（ファイル名またはディレクトリ名） */
    private String name;
    
    /** URLパス */
    private String path;
    
    /** コンテンツID（ファイルノードの場合のみ） */
    private String id;
    
    /** コンテンツタイトル（ファイルノードの場合のみ） */
    private String title;
    
    /** ディレクトリノードの場合true */
    private boolean isDirectory;
    
    /** 子ノードのリスト（ディレクトリノードの場合） */
    private List<DirectoryNodeBean> children;

    public DirectoryNodeBean() {
        this.children = new ArrayList<>();
    }

    public DirectoryNodeBean(String name, String path, boolean isDirectory) {
        this.name = name;
        this.path = path;
        this.isDirectory = isDirectory;
        this.children = new ArrayList<>();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public List<DirectoryNodeBean> getChildren() {
        return children;
    }

    public void setChildren(List<DirectoryNodeBean> children) {
        this.children = children;
    }

    public void addChild(DirectoryNodeBean child) {
        this.children.add(child);
    }

    /**
     * 子ノードの中から指定された名前のディレクトリを検索する
     * 
     * @param dirName ディレクトリ名
     * @return 見つかったノード、見つからない場合はnull
     */
    public DirectoryNodeBean findChildDirectory(String dirName) {
        for (DirectoryNodeBean child : children) {
            if (child.isDirectory() && child.getName().equals(dirName)) {
                return child;
            }
        }
        return null;
    }
}
