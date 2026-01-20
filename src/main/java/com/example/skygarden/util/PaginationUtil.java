package com.example.skygarden.util;

/**
 * ページネーション計算ユーティリティクラス
 * 
 * このクラスはページネーション（ページ分割表示）に関連する計算処理を提供します。
 * コンテンツ一覧やユーザー一覧などの画面で使用されます。
 * 
 * 主な機能:
 * - 総ページ数の計算（calculateTotalPages）
 * - オフセットの計算（calculateOffset）
 * 
 * 使用例:
 * <pre>
 * {@code
 * int pageSize = 20;  // 1ページあたり20件
 * int totalCount = 85; // 総件数85件
 * 
 * int totalPages = PaginationUtil.calculateTotalPages(totalCount, pageSize);
 * // 結果: 5 (85 / 20 = 4.25 → 切り上げて5ページ)
 * 
 * int offset = PaginationUtil.calculateOffset(3, pageSize);
 * // 結果: 40 ((3-1) * 20 = 40、3ページ目は41件目から)
 * }
 * </pre>
 * 
 * このクラスはstaticメソッドのみを持ち、インスタンス化できません。
 */
public final class PaginationUtil {
	
	/**
	 * プライベートコンストラクタ
	 * ユーティリティクラスのインスタンス化を防止する
	 */
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
