package com.example.skygarden.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * アプリケーション設定プロパティクラス
 * application.propertiesから設定値を読み込む
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
	
	/**
	 * ページネーション設定
	 */
	private Pagination pagination = new Pagination();
	
	/**
	 * ファイル設定
	 */
	private File file = new File();
	
	/**
	 * ページネーション設定
	 */
	@Getter
	@Setter
	public static class Pagination {
		/**
		 * 1ページあたりの表示件数
		 */
		private int pageSize = 20;
	}
	
	/**
	 * ファイル設定
	 */
	@Getter
	@Setter
	public static class File {
		/**
		 * プレビューファイル名
		 */
		private String previewFileName = "preview.html";
		
		/**
		 * 画像アップロードディレクトリ
		 */
		private String uploadDir = "uploads/images";
		
		/**
		 * ファイルアップロードディレクトリ
		 */
		private String fileUploadDir = "uploads/files";
	}
}
