package com.example.skygarden.bean;

import java.io.Serializable;

import lombok.Data;

/**
 * ユーザー情報を保持するBeanクラス
 * ユーザー管理画面で使用されるデータを格納する
 */
@Data
public class UserBean implements Serializable {
	private static final long serialVersionUID = 1L;
	/** ユーザー名 */
	String name = "";
	/** パスワード */
	String password = "";
	/** メールアドレス */
	String email = "";
	/** 管理者フラグ（"1"が管理者、"0"が一般ユーザー） */
	String admin = "";
}
