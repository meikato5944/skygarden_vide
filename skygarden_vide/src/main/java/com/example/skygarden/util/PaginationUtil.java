package com.example.skygarden.util;

/**
 * ページネーション計算ユーティリティクラス
 */
public final class PaginationUtil {
	
	private PaginationUtil() {
		// インスタンス化を防ぐ
	}
	
	/**
	 * 総ページ数を計算する
	 * 
	 * @param totalCount 総件数
	 * @param pageSize 1ページあたりの表示件数
	 * @return 総ページ数
	 */
	public static int calculateTotalPages(int totalCount, int pageSize) {
		if (pageSize <= 0) {
			return 0;
		}
		int totalPages = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			totalPages++;
		}
		return totalPages;
	}
	
	/**
	 * オフセットを計算する
	 * 
	 * @param page 現在のページ番号（1から始まる）
	 * @param pageSize 1ページあたりの表示件数
	 * @return オフセット
	 */
	public static int calculateOffset(int page, int pageSize) {
		if (page <= 1) {
			return 0;
		}
		return (page - 1) * pageSize;
	}
}
