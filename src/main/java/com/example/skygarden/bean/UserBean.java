package com.example.skygarden.bean;

import java.io.Serializable;

import lombok.Data;

/**
 * ユーザー情報を保持するBeanクラス（DTO）
 * 
 * このクラスはユーザー編集画面でController層からView層へ
 * データを転送するためのDTO（Data Transfer Object）です。
 * REST APIのレスポンスとしてJSON形式でシリアライズされます。
 * 
 * 主な用途:
 * - UserController.getUser() のレスポンス
 * - ユーザー編集画面のデータバインディング
 * 
 * フィールド説明:
 * - name: ユーザー名（ログイン時に使用）
 * - password: パスワード（注意: 平文で送信されます）
 * - email: メールアドレス
 * - admin: 管理者フラグ（"1":管理者、"0":一般ユーザー）
 * 
 * セキュリティ注意事項:
 * - パスワードは現状平文で送受信されています
 * - 本番環境ではパスワードフィールドの送信を避けることを推奨
 * 
 * @see UserController ユーザー管理APIコントローラー
 */
@Data
public class UserBean implements Serializable {
	
	/** シリアルバージョンUID */
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
