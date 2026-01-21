package com.example.skygarden.constants;

/**
 * アプリケーション全体で使用する定数を定義するクラス
 * 
 * このクラスはCMSアプリケーション全体で使用される定数を一元管理します。
 * 定数はカテゴリ別にグループ化されています。
 * 
 * 定数カテゴリ:
 * - コンテンツタイプ: コンテンツの種類を識別する値
 * - フラグ値: はい/いいえを表すフラグ
 * - 画面名: 日本語の画面表示名
 * - URLパス: アプリケーション内のURLパス
 * - APIエンドポイント: REST APIのエンドポイントパス
 * - テーブル名: データベースのテーブル名
 * - 設定名: configテーブルの設定項目名
 * - 日時フォーマット: 日時の文字列フォーマット
 * - プレースホルダー: テンプレート内で置換される文字列
 * - セッション属性名: HTTPセッションに保存する属性名
 * - メッセージ: ユーザーに表示するメッセージ
 * - ソートオプション: 一覧画面のソート選択肢
 * 
 * このクラスはstaticフィールドのみを持ち、インスタンス化できません。
 */
public final class Constants {
	
	/**
	 * プライベートコンストラクタ
	 * 定数クラスのインスタンス化を防止する
	 */
	private Constants() {
		// インスタンス化を防ぐ
	}
	
	// ============================================
	// コンテンツタイプ
	// ============================================
	public static final String CONTENT_TYPE_CONTENT = "";
	public static final String CONTENT_TYPE_TEMPLATE = "template";
	public static final String CONTENT_TYPE_ELEMENT = "element";
	public static final String CONTENT_TYPE_STYLESHEET = "stylesheet";
	public static final String CONTENT_TYPE_SCRIPT = "script";
	public static final String CONTENT_TYPE_IMAGE = "image";
	public static final String CONTENT_TYPE_FILE = "file";
	public static final String CONTENT_TYPE_MOVIE = "movie";
	public static final String CONTENT_TYPE_ELEMENT_ITEM = "elementItem";
	
	// ============================================
	// フラグ値
	// ============================================
	public static final String FLAG_YES = "1";
	public static final String FLAG_NO = "0";
	
	// ============================================
	// 画面名
	// ============================================
	public static final String SCREEN_NAME_CONTENT = "コンテンツ";
	public static final String SCREEN_NAME_TEMPLATE = "テンプレート";
	public static final String SCREEN_NAME_ELEMENT = "構成要素";
	public static final String SCREEN_NAME_STYLESHEET = "CSS";
	public static final String SCREEN_NAME_SCRIPT = "JS";
	public static final String SCREEN_NAME_IMAGE = "画像";
	public static final String SCREEN_NAME_FILE = "ファイル";
	public static final String SCREEN_NAME_MOVIE = "動画";
	
	// ============================================
	// URLパス
	// ============================================
	public static final String PATH_ROOT = "/";
	public static final String PATH_LOGIN = "/login";
	public static final String PATH_CONTENT = "/content";
	public static final String PATH_USER_LIST = "/user-list";
	public static final String PATH_USER = "/user";
	public static final String PATH_SETTING = "/setting";
	public static final String PATH_WEBADMIN = "/webadmin";
	public static final String PATH_ELEMENT_ITEM = "/element-item";
	
	// ============================================
	// APIエンドポイント（@RequestMapping(Constants.PATH_WEBADMIN)と組み合わせて使用）
	// ============================================
	public static final String API_LOGIN_POST = "/login_post";
	public static final String API_LOGOUT = "/logout";
	public static final String API_UPDATE_POST = "/update_post";
	public static final String API_DELETE_POST = "/delete_post";
	public static final String API_USER_POST = "/user_post";
	public static final String API_SETTING_POST = "/setting_post";
	public static final String API_GET_CONTENT = "/getcontent";
	public static final String API_GET_LIST = "/getlist";
	public static final String API_GET_LIST_USER = "/getlist-user";
	public static final String API_GET_TEMPLATE = "/gettemplate";
	public static final String API_GET_ELEMENT = "/getelement";
	public static final String API_PREVIEW = "/preview";
	public static final String API_URL_MATCHES = "/urlmatches";
	
	// ============================================
	// テーブル名
	// ============================================
	public static final String TABLE_CONTENT = "content";
	public static final String TABLE_CONTENT_PUBLIC = "content_public";
	public static final String TABLE_USER = "user";
	public static final String TABLE_CONFIG = "config";
	
	// ============================================
	// 設定名
	// ============================================
	public static final String CONFIG_ELEMENTS_COLOR_VALUE = "elements-color-value";
	public static final String CONFIG_DEFAULT_PUBLISH_ON = "default-publish-on";
	
	// ============================================
	// 日時フォーマット
	// ============================================
	public static final String DATE_FORMAT_DATETIME = "yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT_DATETIME_FOR_COMPARE = "yyyy-MM-dd HH:mm";
	
	// ============================================
	// その他の定数
	// ============================================
	public static final String EMPTY_STRING = "";
	public static final String TEMPLATE_CONTENT_PLACEHOLDER = "###content###";
	public static final String TEMPLATE_TITLE_PLACEHOLDER = "###title###";
	public static final String TEMPLATE_HEAD_PLACEHOLDER = "###head###";
	public static final String TEMPLATE_CONTENT_PLACEHOLDER_IN_TEMPLATE = "###content###";
	public static final String ELEMENT_PREFIX = "###";
	public static final String ELEMENT_FUNCTION_START = "element(";
	public static final String ELEMENT_FUNCTION_END = ")";
	
	// ============================================
	// セッション属性名
	// ============================================
	public static final String SESSION_NAME = "name";
	public static final String SESSION_ADMIN = "admin";
	public static final String SESSION_LOGIN_NAME = "loginName";
	public static final String SESSION_REGISTER_MESSAGE = "registerMessage";
	
	// ============================================
	// エラーメッセージ
	// ============================================
	public static final String ERROR_USER_NOT_FOUND = "ユーザーが見つかりません";
	public static final String ERROR_PASSWORD_INCORRECT = "パスワードが正しくありません";
	public static final String MESSAGE_REGISTER_SUCCESS = "登録しました。";
	public static final String MESSAGE_REGISTER_FAILED = "登録に失敗しました。";
	public static final String MESSAGE_SETTING_REGISTER_SUCCESS = "設定を登録しました。";
	public static final String MESSAGE_DELETE_SUCCESS = "削除しました。";
	public static final String MESSAGE_DELETE_FAILED = "削除に失敗しました。";
	
	// ============================================
	// ソートオプション
	// ============================================
	public static final String[][] SORT_OPTIONS_CONTENT = {
		{ "updated", "更新日時(降順)" },
		{ "updated desc", "更新日時(昇順)" },
		{ "id", "ID(降順)" },
		{ "id desc", "ID(昇順)" },
		{ "title", "タイトル(降順)" },
		{ "title desc", "タイトル(昇順)" },
		{ "url", "URL(降順)" },
		{ "url desc", "URL(昇順)" }
	};
	
	public static final String[][] SORT_OPTIONS_USER = {
		{ "id", "ID(降順)" },
		{ "id desc", "ID(昇順)" },
		{ "name", "ユーザ名(降順)" },
		{ "name desc", "ユーザ名(昇順)" }
	};
}
