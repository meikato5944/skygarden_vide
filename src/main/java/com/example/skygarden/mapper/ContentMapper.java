package com.example.skygarden.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * コンテンツ管理用のMyBatis Mapperインターフェース
 * 
 * このインターフェースはMyBatisを使用してデータベース操作を行うためのMapper定義です。
 * SQLは resources/mapper/ContentMapper.xml に定義されています。
 * 
 * 操作対象テーブル:
 * - content: コンテンツ情報（下書き）
 * - content_public: 公開中のコンテンツ
 * - user: ユーザー情報
 * - config: 設定情報
 * 
 * 主な機能:
 * - ユーザー関連: 取得、作成、更新
 * - コンテンツ関連: 作成、更新、検索、削除、一覧取得
 * - 公開コンテンツ関連: 作成、更新（content_publicテーブル）
 * - 設定関連: 取得、更新
 * - バッチ処理関連: スケジュール公開・非公開対象の取得、クリア
 * 
 * パラメータの@Param注釈:
 * MyBatisのXMLマッピングファイルからパラメータ名でアクセスするために使用します。
 * 
 * @see resources/mapper/ContentMapper.xml SQL定義ファイル
 */
@Mapper
public interface ContentMapper {
	
	/**
	 * ユーザー名でユーザー情報を取得する
	 */
	HashMap<String, String> getUser(@Param("name") String name);
	
	/**
	 * ユーザーIDでユーザー情報を取得する
	 */
	HashMap<String, String> getUserById(@Param("id") String id);
	
	/**
	 * 新規ユーザーを作成する
	 */
	void createUser(@Param("name") String name, @Param("password") String password, 
			@Param("email") String email, @Param("admin") String admin);
	
	/**
	 * 既存のユーザー情報を更新する
	 */
	void updateUser(@Param("id") String id, @Param("name") String name, 
			@Param("password") String password, @Param("email") String email, 
			@Param("admin") String admin);
	
	/**
	 * 新規コンテンツを作成する
	 */
	int create(@Param("created") String created, @Param("updated") String updated,
			@Param("created_by") String createdBy, @Param("updated_by") String updatedBy,
			@Param("url") String url, @Param("title") String title, @Param("head") String head,
			@Param("content") String content, @Param("type") String type,
			@Param("elementcolor") String elementcolor, @Param("template") String template,
			@Param("schedule_published") String schedulePublished,
			@Param("schedule_unpublished") String scheduleUnpublished,
			@Param("publishflg_keep") String publishflgKeep);
	
	/**
	 * 公開用コンテンツテーブルに新規コンテンツを作成する
	 */
	int createPublic(@Param("id") int id, @Param("created") String created, 
			@Param("updated") String updated, @Param("created_by") String createdBy,
			@Param("updated_by") String updatedBy, @Param("url") String url,
			@Param("title") String title, @Param("head") String head,
			@Param("content") String content, @Param("type") String type,
			@Param("elementcolor") String elementcolor, @Param("template") String template,
			@Param("schedule_published") String schedulePublished,
			@Param("schedule_unpublished") String scheduleUnpublished,
			@Param("publishflg_keep") String publishflgKeep);
	
	/**
	 * 既存のコンテンツを更新する
	 */
	void update(@Param("id") String id, @Param("updated") String updated,
			@Param("updated_by") String updatedBy, @Param("url") String url,
			@Param("title") String title, @Param("head") String head,
			@Param("content") String content, @Param("type") String type,
			@Param("elementcolor") String elementcolor, @Param("template") String template,
			@Param("schedule_published") String schedulePublished,
			@Param("schedule_unpublished") String scheduleUnpublished,
			@Param("publishflg_keep") String publishflgKeep);
	
	/**
	 * 公開用コンテンツテーブルの既存コンテンツを更新する
	 */
	void updatePublic(@Param("id") String id, @Param("updated") String updated,
			@Param("updated_by") String updatedBy, @Param("url") String url,
			@Param("title") String title, @Param("head") String head,
			@Param("content") String content, @Param("type") String type,
			@Param("elementcolor") String elementcolor, @Param("template") String template,
			@Param("schedule_published") String schedulePublished,
			@Param("schedule_unpublished") String scheduleUnpublished,
			@Param("publishflg_keep") String publishflgKeep);
	
	/**
	 * コンテンツをIDで検索する
	 */
	HashMap<String, String> search(@Param("id") String id, @Param("table") String table);
	
	/**
	 * URLでコンテンツを検索する
	 */
	HashMap<String, String> searchByUrl(@Param("url") String url, @Param("table") String table);
	
	/**
	 * コンテンツの特定の属性値を取得する
	 */
	String searchContentByAttribute(@Param("id") String id, @Param("attribute") String attribute,
			@Param("table") String table);
	
	/**
	 * すべてのコンテンツを取得する（ページネーションなし）
	 */
	List<HashMap<String, String>> selectAll(@Param("table") String table, 
			@Param("sort") String sort, @Param("type") String type);
	
	/**
	 * コンテンツ一覧を取得する（ページネーション対応）
	 */
	List<HashMap<String, String>> selectAllLimit(@Param("table") String table,
			@Param("sort") String sort, @Param("type") String type,
			@Param("limit") int limit, @Param("offset") int offset);
	
	/**
	 * 最後に作成されたコンテンツのIDを取得する
	 */
	int getLastId();
	
	/**
	 * コンテンツの総件数を取得する
	 */
	int getContentSize(@Param("table") String table, @Param("type") String type);
	
	/**
	 * 設定情報を更新する
	 */
	void updateSetting(@Param("value") String value, @Param("name") String name);
	
	/**
	 * 構成要素の色設定を取得する
	 */
	String getElementColor();
	
	/**
	 * 設定値を名前で取得する
	 */
	String getSettingByName(@Param("name") String name);
	
	/**
	 * URL一覧を取得する
	 */
	List<HashMap<String, String>> getUrlList();
	
	/**
	 * コンテンツを削除する
	 */
	void delete(@Param("table") String table, @Param("id") String id);
	
	/**
	 * スケジュール公開対象のコンテンツIDを取得する
	 */
	List<HashMap<String, String>> getSchedulePublishedIds(@Param("now") String now);
	
	/**
	 * スケジュール非公開対象のコンテンツIDを取得する
	 */
	List<HashMap<String, String>> getScheduleUnpublishedIds(@Param("now") String now);
	
	/**
	 * スケジュール公開日時をクリアする
	 */
	void clearSchedulePublished(@Param("id") String id);
	
	/**
	 * スケジュール非公開日時をクリアする
	 */
	void clearScheduleUnpublished(@Param("id") String id);
}
