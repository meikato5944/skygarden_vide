package com.example.skygarden.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * バッチ処理のビジネスロジッククラス
 * スケジュール公開・非公開のバッチ処理を提供する
 */
@Slf4j
public class Batch {
	private static final String SELECT_SCHEDULE_PUBLISHED = "SELECT id FROM content WHERE schedule_published <= '@@@now@@@' AND schedule_published <> '' AND schedule_published IS NOT NULL";
	private static final String SELECT_SCHEDULE_UNPUBLISHED = "SELECT id FROM content WHERE schedule_unpublished <= '@@@now@@@' AND schedule_unpublished <> '' AND schedule_unpublished IS NOT NULL";
	private static final String UPDATE_SCHEDULE_PUBLISHED = "UPDATE content set schedule_published = '' WHERE id = ?";
	private static final String UPDATE_SCHEDULE_UNPUBLISHED = "UPDATE content set schedule_unpublished = '' WHERE id = ?";
	private DB db = new DB();

	/**
	 * スケジュール公開のバッチ処理を実行する
	 * 公開予定日時が現在時刻以前のコンテンツを公開テーブルに登録・更新する
	 */
	public void publishedBatch() {
		List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		String sql = SELECT_SCHEDULE_PUBLISHED.replaceAll("@@@now@@@", CommonProc.createNow());
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

		for (int i = 0; i < results.size(); i++) {
			String id = results.get(i).get("id");
			HashMap<String, String> result = db.search(id);
			HashMap<String, String> publicResult = db.search(id, "content_public");
			String name = (String) result.get("name");
			String url = result.get("url");
			String title = result.get("title");
			String head = result.get("head");
			String content = result.get("content");
			String type = result.get("type");
			String elementcolor = result.get("elementcolor");
			String template = result.get("template");
			String schedule_published = "";//公開後のため空で登録
			String schedule_unpublished = result.get("schedule_unpublished");
			try {
				if (publicResult.get("id") == null || publicResult.get("id").equals("")) {
					db.create_public(id, name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished);
				} else {
					db.update_public(id, name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished);
				}
				try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_SCHEDULE_PUBLISHED)) {
					ps.setString(1, id);
					ps.executeUpdate();
				} catch (SQLException e) {
					log.info("接続失敗\n理由：" + e.toString());
				} catch (Exception e) {
					throw e;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * スケジュール非公開のバッチ処理を実行する
	 * 非公開予定日時が現在時刻以前のコンテンツを公開テーブルから削除する
	 */
	public void unPublishedBatch() {
		List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		String sql = SELECT_SCHEDULE_UNPUBLISHED.replaceAll("@@@now@@@", CommonProc.createNow());
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

		for (int i = 0; i < results.size(); i++) {
			String id = results.get(i).get("id");
			try {
				db.delete("content_public", id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_SCHEDULE_UNPUBLISHED)) {
				ps.setString(1, id);
				ps.executeUpdate();
			} catch (SQLException e) {
				log.info("接続失敗\n理由：" + e.toString());
			} catch (Exception e) {
				throw e;
			}
		}
	}
}