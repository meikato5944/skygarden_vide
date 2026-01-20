package com.example.skygarden.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * データベースアクセスを管理するクラス（レガシー）
 * 
 * このクラスはJDBCを使用してMySQLデータベースへの直接接続・操作を提供するレガシークラスです。
 * 現在はMyBatis（ContentMapper）への移行が完了しており、このクラスは使用されていません。
 * 
 * 注意: 
 * 新規開発では ContentMapper インターフェースを使用してください。
 * このクラスは互換性のために残されていますが、将来的に削除される可能性があります。
 * 
 * 旧機能（参考用）:
 * - データベース接続の取得（getConnection）
 * - ユーザー情報の取得・作成・更新
 * - コンテンツの作成・更新・検索・削除
 * - 設定情報の取得・更新
 * - ページネーション対応の一覧取得
 * 
 * @deprecated ContentMapper を使用してください
 * @see ContentMapper MyBatisによるデータベース操作
 */
@Slf4j
public class DB {
	
	/** データベースURL */
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/skygarden?enabledTLSProtocols=TLSv1.2";
	
	/** データベースユーザー名（ローカル開発環境用） */
	private static final String USER = "root";//local:root
//	private static final String USER = "heidi_sql";//server
	
	/** データベースパスワード（ローカル開発環境用） */
	private static final String PASS = "";//local-xampp:""
//	private static final String PASS = "root";//local-mysql:""
//	private static final String PASS = "admin";//server:admin
	
	/** JDBCドライバクラス名 */
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

	private static final String SELECT_SQL = "SELECT id, name, password, email, admin FROM user WHERE name = ?";
	private static final String SELECTID_SQL = "SELECT id, name, password, email, admin FROM user WHERE id = ?";
	private static final String SELECT_CONTENT_ATTRIBUTE_SQL = "SELECT ";
	private static final String SELECT_CONTENT_ATTRIBUTE_SQL2 = " FROM @@@table@@@ WHERE id = ?";
	private static final String INSERT_CONTENT_SQL = "INSERT INTO content (created, updated, created_by, updated_by, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, publishflg_keep) VALUE(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String INSERT_CONTENT_PUBLIC_SQL = "INSERT INTO content_public (id, created, updated, created_by, updated_by, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, publishflg_keep) VALUE(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_CONTENT_SQL = "UPDATE content set updated = ?, updated_by = ?, url = ?, title = ?, head = ?, content = ?, type = ?, elementcolor = ?, template = ?, schedule_published = ?, schedule_unpublished = ?, publishflg_keep = ? WHERE id = ?";
	private static final String UPDATE_CONTENT_PUBLIC_SQL = "UPDATE content_public set updated = ?, updated_by = ?, url = ?, title = ?, head = ?, content = ?, type = ?, elementcolor = ?, template = ?, schedule_published = ?, schedule_unpublished = ?, publishflg_keep = ? WHERE id = ?";
	private static final String CREATE_USER_SQL = "INSERT INTO user(name, password, email, admin) VALUE(?, ?, ?, ?)";
	private static final String UPDATE_USER_SQL = "UPDATE user set name = ?, password = ?, email = ?, admin = ? WHERE id = ?";
	private static final String UPDATE_SETTING_SQL = "UPDATE config set value = ? WHERE name = ?";
	private static final String SELECT_CONTENT_SQL = "SELECT * FROM @@@table@@@ WHERE id = ?";
	private static final String SELECT_CONTENT_URL_SQL = "SELECT * FROM @@@table@@@ WHERE url = ?";
	private static final String SELECTALL_SQL = "SELECT * FROM ";
	private static final String SELECT_CONFIG_ELEMENTSCOLOR_SQL = "SELECT name, value FROM config WHERE name = 'elements-color-value'";
	private static final String LASTID_SQL = "SELECT MAX(id) as maxid from content;";
	private static final String COUNT_CONTENT_SQL = "SELECT COUNT(id) as contentSize from ";
	private static final String ORDERBY_SQL = " ORDER BY ";
	private static final String PAGING_SQL = " LIMIT ? OFFSET ? ";
	private static final String WHERE_TYPE_SQL = " WHERE type = ? ";
	private static final String SELECT_URLLIST = "SELECT id, url FROM content WHERE url <> '' ORDER BY url";
	private static final String DELETE_CONTENT_PUBLIC_SQL = "DELETE FROM @@@table@@@ WHERE id = ?";

	/**
	 * データベースへの接続を取得する
	 * 
	 * @return データベース接続オブジェクト
	 */
	public Connection getConnection() {
		Connection con = null;
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			log.info("クラスが見つかりません");
		}
		try {
			con = DriverManager.getConnection(URL, USER, PASS);
		} catch (SQLException e) {
			log.info("コネクション中に例外が発生しました。");
			log.info("接続失敗\n理由：" + e.toString());
		}
		return con;
	}

	/**
	 * ユーザー名でユーザー情報を取得する
	 * 
	 * @param name ユーザー名
	 * @return ユーザー情報を含むHashMap
	 */
	public HashMap<String, String> getUser(String name) {
		HashMap<String, String> result = new HashMap<String, String>();
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_SQL)) {
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			while (rs.next()) {
				for (int i = 0; i < rsm.getColumnCount(); i++) {
					String sColName = rsm.getColumnName(i + 1);
					result.put(sColName, rs.getString(sColName));
				}
			}
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * ユーザーIDでユーザー情報を取得する
	 * 
	 * @param id ユーザーID
	 * @return ユーザー情報を含むHashMap
	 */
	public HashMap<String, String> getUserById(String id) {
		HashMap<String, String> result = new HashMap<String, String>();
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SELECTID_SQL)) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			while (rs.next()) {
				for (int i = 0; i < rsm.getColumnCount(); i++) {
					String sColName = rsm.getColumnName(i + 1);
					result.put(sColName, rs.getString(sColName));
				}
			}
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 新規ユーザーを作成する
	 * 
	 * @param name ユーザー名
	 * @param password パスワード
	 * @param email メールアドレス
	 * @param admin 管理者フラグ（"1"が管理者、"0"が一般ユーザー）
	 * @throws SQLException SQL例外
	 * @throws Exception その他の例外
	 */
	public void createUser(String name, String password, String email, String admin) throws SQLException, Exception {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(CREATE_USER_SQL)) {
			ps.setString(1, name);//name
			ps.setString(2, password);//password
			ps.setString(3, email);//password
			ps.setString(4, admin);//admin
			ps.executeUpdate();
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 既存のユーザー情報を更新する
	 * 
	 * @param id ユーザーID
	 * @param name ユーザー名
	 * @param password パスワード
	 * @param email メールアドレス
	 * @param admin 管理者フラグ（"1"が管理者、"0"が一般ユーザー）
	 * @throws SQLException SQL例外
	 * @throws Exception その他の例外
	 */
	public void updateUser(String id, String name, String password, String email, String admin) throws SQLException, Exception {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_USER_SQL)) {
			ps.setString(1, name);
			ps.setString(2, password);
			ps.setString(3, email);
			ps.setString(4, admin);
			ps.setString(5, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 新規コンテンツを作成する
	 * 
	 * @param name 作成者名
	 * @param url URLパス
	 * @param title タイトル
	 * @param head ヘッダー部分のHTML
	 * @param content コンテンツ本文
	 * @param type コンテンツタイプ
	 * @param elementcolor 構成要素の色コード
	 * @param template テンプレートID
	 * @param schedule_published 公開予定日時
	 * @param schedule_unpublished 非公開予定日時
	 * @param publishflg_keep 公開フラグ
	 * @return 作成されたコンテンツのID
	 * @throws SQLException SQL例外
	 * @throws Exception その他の例外
	 */
	public int create(String name, String url, String title, String head, String content, String type, String elementcolor, String template, String schedule_published, String schedule_unpublished, String publishflg_keep) throws SQLException, Exception {
		String now = CommonProc.createNow();
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT_CONTENT_SQL)) {
			conn.setAutoCommit(false);
			ps.setString(1, now);//created
			ps.setString(2, now);//updated
			ps.setString(3, name);//created_by
			ps.setString(4, name);//updated_by
			ps.setString(5, url);
			ps.setString(6, title);
			ps.setString(7, head);
			ps.setString(8, content);
			ps.setString(9, type);
			ps.setString(10, elementcolor);
			ps.setString(11, template);
			ps.setString(12, schedule_published);
			ps.setString(13, schedule_unpublished);
			ps.setString(14, publishflg_keep);
			ps.executeUpdate();
			conn.commit();
			return getLastId();
		} catch (SQLException e) {
			log.info("登録失敗\n理由：" + e.toString());
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 公開用コンテンツテーブルに新規コンテンツを作成する
	 * 
	 * @param id コンテンツID
	 * @param name 作成者名
	 * @param url URLパス
	 * @param title タイトル
	 * @param head ヘッダー部分のHTML
	 * @param content コンテンツ本文
	 * @param type コンテンツタイプ
	 * @param elementcolor 構成要素の色コード
	 * @param template テンプレートID
	 * @param schedule_published 公開予定日時
	 * @param schedule_unpublished 非公開予定日時
	 * @param publishflg_keep 公開フラグ
	 * @return 作成されたコンテンツのID
	 * @throws SQLException SQL例外
	 * @throws Exception その他の例外
	 */
	public int create_public(String id, String name, String url, String title, String head, String content, String type, String elementcolor, String template, String schedule_published, String schedule_unpublished, String publishflg_keep) throws SQLException, Exception {
		String now = CommonProc.createNow();
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT_CONTENT_PUBLIC_SQL)) {
			conn.setAutoCommit(false);
			ps.setInt(1, Integer.valueOf(id));//created
			ps.setString(2, now);//created
			ps.setString(3, now);//updated
			ps.setString(4, name);//created_by
			ps.setString(5, name);//updated_by
			ps.setString(6, url);
			ps.setString(7, title);
			ps.setString(8, head);
			ps.setString(9, content);
			ps.setString(10, type);
			ps.setString(11, elementcolor);
			ps.setString(12, template);
			ps.setString(13, schedule_published);
			ps.setString(14, schedule_unpublished);
			ps.setString(15, publishflg_keep);
			ps.executeUpdate();
			conn.commit();
			return getLastId();
		} catch (SQLException e) {
			log.info("登録失敗\n理由：" + e.toString());
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 既存のコンテンツを更新する
	 * 
	 * @param id コンテンツID
	 * @param name 更新者名
	 * @param url URLパス
	 * @param title タイトル
	 * @param head ヘッダー部分のHTML
	 * @param content コンテンツ本文
	 * @param type コンテンツタイプ
	 * @param elementcolor 構成要素の色コード
	 * @param template テンプレートID
	 * @param schedule_published 公開予定日時
	 * @param schedule_unpublished 非公開予定日時
	 * @param publishflg_keep 公開フラグ
	 * @throws SQLException SQL例外
	 * @throws Exception その他の例外
	 */
	public void update(String id, String name, String url, String title, String head, String content, String type, String elementcolor, String template, String schedule_published, String schedule_unpublished, String publishflg_keep) throws SQLException, Exception {
		String now = CommonProc.createNow();
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_CONTENT_SQL)) {
			ps.setString(1, now);
			ps.setString(2, name);
			ps.setString(3, url);
			ps.setString(4, title);
			ps.setString(5, head);
			ps.setString(6, content);
			ps.setString(7, type);
			ps.setString(8, elementcolor);
			ps.setString(9, template);
			ps.setString(10, schedule_published);
			ps.setString(11, schedule_unpublished);
			ps.setString(12, publishflg_keep);
			ps.setString(13, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 公開用コンテンツテーブルの既存コンテンツを更新する
	 * 
	 * @param id コンテンツID
	 * @param name 更新者名
	 * @param url URLパス
	 * @param title タイトル
	 * @param head ヘッダー部分のHTML
	 * @param content コンテンツ本文
	 * @param type コンテンツタイプ
	 * @param elementcolor 構成要素の色コード
	 * @param template テンプレートID
	 * @param schedule_published 公開予定日時
	 * @param schedule_unpublished 非公開予定日時
	 * @param publishflg_keep 公開フラグ
	 * @throws SQLException SQL例外
	 * @throws Exception その他の例外
	 */
	public void update_public(String id, String name, String url, String title, String head, String content, String type, String elementcolor, String template, String schedule_published, String schedule_unpublished, String publishflg_keep) throws SQLException, Exception {
		String now = CommonProc.createNow();
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_CONTENT_PUBLIC_SQL)) {
			ps.setString(1, now);
			ps.setString(2, name);
			ps.setString(3, url);
			ps.setString(4, title);
			ps.setString(5, head);
			ps.setString(6, content);
			ps.setString(7, type);
			ps.setString(8, elementcolor);
			ps.setString(9, template);
			ps.setString(10, schedule_published);
			ps.setString(11, schedule_unpublished);
			ps.setString(12, publishflg_keep);
			ps.setString(13	, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * コンテンツをIDで検索する（デフォルトテーブル:content）
	 * 
	 * @param id コンテンツID
	 * @return コンテンツ情報を含むHashMap
	 */
	public HashMap<String, String> search(String id) {
		return search(id, "content");
	}

	/**
	 * コンテンツをIDで検索する
	 * 
	 * @param id コンテンツID
	 * @param table テーブル名
	 * @return コンテンツ情報を含むHashMap
	 */
	public HashMap<String, String> search(String id, String table) {
		HashMap<String, String> result = new HashMap<String, String>();
		String sql = SELECT_CONTENT_SQL.replaceAll("@@@table@@@", table);
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			while (rs.next()) {
				for (int i = 0; i < rsm.getColumnCount(); i++) {
					String sColName = rsm.getColumnName(i + 1);
					result.put(sColName, rs.getString(sColName));
				}
			}
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * URLでコンテンツを検索する
	 * 
	 * @param url URLパス
	 * @param table テーブル名
	 * @return コンテンツ情報を含むHashMap
	 */
	public HashMap<String, String> searchByUrl(String url, String table) {
		HashMap<String, String> result = new HashMap<String, String>();
		String sql = SELECT_CONTENT_URL_SQL.replaceAll("@@@table@@@", table);
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, url);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			while (rs.next()) {
				for (int i = 0; i < rsm.getColumnCount(); i++) {
					String sColName = rsm.getColumnName(i + 1);
					result.put(sColName, rs.getString(sColName));
				}
			}
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * コンテンツの特定の属性値を取得する
	 * 
	 * @param id コンテンツID
	 * @param attribute 取得する属性名（例: "title", "content"）
	 * @param table テーブル名
	 * @return 属性値
	 */
	public String searchContentByAttribute(String id, String attribute, String table) {
		String result = "";
		String sql = SELECT_CONTENT_ATTRIBUTE_SQL + attribute + SELECT_CONTENT_ATTRIBUTE_SQL2.replaceAll("@@@table@@@", table);
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, Integer.valueOf(id));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = rs.getString(attribute);
			}
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			log.info(e.toString());
			throw e;
		}
		return result;
	}
	
	/**
	 * すべてのコンテンツを取得する（ページネーションなし）
	 * 
	 * @param table テーブル名
	 * @param sort ソート順
	 * @param type コンテンツタイプ（フィルタリング用）
	 * @return コンテンツ一覧のリスト
	 */
	public List<HashMap<String, String>> selectAll(String table, String sort, String type) {
		List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		String sql = "";
		if (sort != null && !sort.equals("")) {
			sql = SELECTALL_SQL + table + WHERE_TYPE_SQL + ORDERBY_SQL + sort;
		} else if (table.equals("content")) {
			sql = SELECTALL_SQL + table + WHERE_TYPE_SQL + ORDERBY_SQL + "updated desc";
		} else if (table.equals("user")) {
			sql = SELECTALL_SQL + table + WHERE_TYPE_SQL + ORDERBY_SQL + "id";
		}
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, type);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				HashMap<String, String> result = new HashMap<String, String>();
				ResultSetMetaData rsm = rs.getMetaData();
				for (int i = 0; i < rsm.getColumnCount(); i++) {
					String sColName = rsm.getColumnName(i + 1);
					result.put(sColName, rs.getString(sColName));
				}
				results.add(result);
			}
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
		return results;
	}
	
	/**
	 * コンテンツ一覧を取得する（ページネーション対応）
	 * 
	 * @param table テーブル名
	 * @param sort ソート順
	 * @param type コンテンツタイプ（フィルタリング用）
	 * @param limit 取得件数の上限
	 * @param offset オフセット（スキップする件数）
	 * @return コンテンツ一覧のリスト
	 */
	public List<HashMap<String, String>> selectAllLimit(String table, String sort, String type, int limit, int offset) {
		List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		String sql = "";
		if (sort != null && !sort.equals("")) {
			sql = SELECTALL_SQL + table + WHERE_TYPE_SQL + ORDERBY_SQL + sort + PAGING_SQL;
		} else if (table.equals("content")) {
			sql = SELECTALL_SQL + table + WHERE_TYPE_SQL + ORDERBY_SQL + "updated desc" + PAGING_SQL;
		} else if (table.equals("user")) {
			sql = SELECTALL_SQL + table + WHERE_TYPE_SQL + ORDERBY_SQL + "id" + PAGING_SQL;
		}
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, type);
			ps.setInt(2, limit);
			ps.setInt(3, offset);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				HashMap<String, String> result = new HashMap<String, String>();
				ResultSetMetaData rsm = rs.getMetaData();
				for (int i = 0; i < rsm.getColumnCount(); i++) {
					String sColName = rsm.getColumnName(i + 1);
					result.put(sColName, rs.getString(sColName));
				}
				results.add(result);
			}
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
		return results;
	}

	/**
	 * 最後に作成されたコンテンツのIDを取得する
	 * 
	 * @return 最後のID
	 */
	public int getLastId() {
		int id = 0;
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(LASTID_SQL)) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("maxid");
			}
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
		return id;
	}

	/**
	 * コンテンツの総件数を取得する
	 * 
	 * @param table テーブル名
	 * @param type コンテンツタイプ（フィルタリング用）
	 * @return コンテンツの総件数
	 */
	public int getCountentSize(String table, String type) {
		int contentSize = 0;
		String sql = COUNT_CONTENT_SQL + table + WHERE_TYPE_SQL;
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, type);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				contentSize = rs.getInt("contentSize");
			}
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
		return contentSize;
	}

	/**
	 * 設定情報を更新する
	 * 
	 * @param value 設定値
	 * @param name 設定名
	 */
	public void updateSetting(String value, String name) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_SETTING_SQL)) {
			ps.setString(1, value);
			ps.setString(2, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 構成要素の色設定を取得する
	 * 
	 * @return 色設定の文字列（"name=code*name=code*"形式）
	 */
	public String getElementColor() {
		String result = "";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_CONFIG_ELEMENTSCOLOR_SQL)) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ResultSetMetaData rsm = rs.getMetaData();
				for (int i = 0; i < rsm.getColumnCount(); i++) {
					String sColName = rsm.getColumnName(i + 1);
					result = rs.getString(sColName);
				}
			}
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * URL一覧を取得する
	 * 
	 * @return URL一覧のリスト
	 */
	public List<HashMap<String, String>> getUrlList() {
		List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_URLLIST)) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				HashMap<String, String> result = new HashMap<String, String>();
				ResultSetMetaData rsm = rs.getMetaData();
				for (int i = 0; i < rsm.getColumnCount(); i++) {
					String sColName = rsm.getColumnName(i + 1);
					result.put(sColName, rs.getString(sColName));
				}
				results.add(result);
			}
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
		return results;
	}

	/**
	 * コンテンツを削除する
	 * 
	 * @param table テーブル名
	 * @param id 削除するコンテンツID
	 */
	public void delete(String table, String id) {
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE_CONTENT_PUBLIC_SQL.replaceAll("@@@table@@@", table))) {
			ps.setString(1, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			log.info("接続失敗\n理由：" + e.toString());
		} catch (Exception e) {
			throw e;
		}
	}
}