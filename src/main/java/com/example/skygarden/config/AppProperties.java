package com.example.skygarden.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * アプリケーション設定プロパティクラス
 * 
 * このクラスはapplication.propertiesから"app."プレフィックスの設定値を読み込み、
 * アプリケーション全体で使用可能なプロパティとして提供します。
 * 
 * Spring Boot の @ConfigurationProperties を使用して、
 * 設定ファイルの値を型安全に取得できます。
 * 
 * 対応する設定項目（application.properties）:
 * - app.pagination.page-size: 1ページあたりの表示件数（デフォルト: 20）
 * - app.file.preview-file-name: プレビューテンプレートファイル名（デフォルト: preview.html）
 * - app.file.upload-dir: 画像アップロードディレクトリ（デフォルト: uploads/images）
 * - app.file.file-upload-dir: ファイルアップロードディレクトリ（デフォルト: uploads/files）
 * 
 * 使用例:
 * <pre>
 * {@code
 * @Autowired
 * private AppProperties appProperties;
 * 
 * int pageSize = appProperties.getPagination().getPageSize();
 * String uploadDir = appProperties.getFile().getUploadDir();
 * }
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
	
	/** ページネーション関連の設定 */
	private Pagination pagination = new Pagination();
	
	/** ファイル関連の設定 */
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
