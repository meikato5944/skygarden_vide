package com.example.skygarden.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.system.ApplicationHome;

import com.example.skygarden.constants.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * 共通処理を提供するユーティリティクラス
 * 
 * このクラスはアプリケーション全体で使用される共通ユーティリティ機能を提供します。
 * ファイル読み込み、日時生成、パス取得など、複数のクラスで使用される処理を集約しています。
 * 
 * 主な機能:
 * - ファイル読み込み（readFile）
 * - 現在日時の文字列生成（createNow）
 * - アプリケーションルートパスの取得（getRootPath）
 * 
 * 使用例:
 * - プレビューテンプレートファイルの読み込み
 * - コンテンツの作成・更新日時の生成
 * - 公開テンプレートファイル（original.html）のパス取得
 * 
 * 日時フォーマット:
 * このクラスで生成される日時は "yyyy-MM-dd HH:mm" 形式です。
 * この形式はデータベースの created, updated, schedule_published,
 * schedule_unpublished カラムで使用されます。
 * 
 * 注意事項:
 * このクラスのメソッドは全てstaticで、インスタンス化は不要です。
 */
@Slf4j
public class CommonProc {
	
	/** 
	 * フロントエンドのパス（末尾スラッシュ無し）
	 * レガシー設定: 以前のSPA構成時に使用されていました
	 */
	public static String FRONTEND_PATH = "http://localhost:3000";
	
	/**
	 * ファイルを読み込む
	 * 
	 * @param filepath ファイルパス
	 * @return ファイルの内容
	 */
	public static String readFile(String filepath) {
		StringBuilder result = new StringBuilder();
		File f = new File(filepath);
		if (f.exists()) {
			BufferedReader input = null;
			try {
				input = new BufferedReader(new FileReader(filepath));
				String line;
				while ((line = input.readLine()) != null) {
					result.append(line).append("\r\n");
				}
				input.close();
			} catch (Exception e) {
				log.info(e.toString());
			}
		}
		return result.toString();
	}

	/**
	 * 現在の日時を文字列形式で取得する
	 * フォーマット: "yyyy-MM-dd HH:mm"
	 * 
	 * @return 現在の日時文字列
	 */
	public static String createNow() {
		Date nowDate = new Date();
		SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT_DATETIME);
		return format.format(nowDate);
	}
	
	/**
	 * アプリケーションのルートパスを取得する
	 * JAR/WARファイルの親ディレクトリを返す
	 * 
	 * @return ルートパス
	 */
	public static  String getRootPath() {
		ApplicationHome home = new ApplicationHome();
		File source = home.getSource();
		// JAR/WARファイルがない場合の代替処理
		if (source == null) {
			// IDEや開発時は、作業ディレクトリを利用
			return System.getProperty("user.dir");
		}
		return source.getParent();
	}
}
