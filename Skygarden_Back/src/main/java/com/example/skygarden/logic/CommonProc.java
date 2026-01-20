package com.example.skygarden.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.system.ApplicationHome;

import lombok.extern.slf4j.Slf4j;

/**
 * 共通処理を提供するユーティリティクラス
 * ファイル読み込み、日時生成、パス取得などの共通機能を提供する
 */
@Slf4j
public class CommonProc {
	/** フロントエンドのパス（末尾スラッシュ無し） */
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
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
