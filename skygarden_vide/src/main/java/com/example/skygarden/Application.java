package com.example.skygarden;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Skygardenアプリケーションのメインクラス
 * Spring Bootアプリケーションのエントリーポイント
 */
@SpringBootApplication
@MapperScan("com.example.skygarden.mapper")
public class Application {

	/**
	 * アプリケーションのメインメソッド
	 * Spring Bootアプリケーションを起動する
	 * 
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
