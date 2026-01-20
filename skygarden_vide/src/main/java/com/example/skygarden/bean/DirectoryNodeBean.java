package com.example.skygarden.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * URLディレクトリのツリーノードを表すBean
 */
public class DirectoryNodeBean {
    private String name;           // 表示名（ファイル名またはディレクトリ名）
    private String path;           // フルパス
    private String id;             // コンテンツID（ファイルの場合のみ）
    private String title;          // コンテンツタイトル（ファイルの場合のみ）
    private boolean isDirectory;   // ディレクトリかどうか
    private List<DirectoryNodeBean> children; // 子ノード（ディレクトリの場合）

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
