package com.example.skygarden.logic;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * コンテンツ管理のビジネスロジッククラス
 * コンテンツの作成、更新、削除、検索、表示などの処理を提供する
 */
public class Content {
	private DB db = new DB();
	/** ルートパス（ファイル操作時に使用） */
	String ROOTPATH;

	/**
	 * デフォルトコンストラクタ
	 */
	public Content() {
	}

	/**
	 * ルートパスを指定するコンストラクタ
	 * 
	 * @param path ルートパス
	 */
	public Content(String path) {
		ROOTPATH = path;
	}

	/**
	 * コンテンツをIDで検索する
	 * 
	 * @param id コンテンツID
	 * @return コンテンツ情報を含むHashMap
	 */
	public HashMap<String, String> doSearch(String id) {
		return db.search(id);
	}

	/**
	 * コンテンツ一覧を取得する（ページネーション対応）
	 * 
	 * @param sort ソート順
	 * @param page ページ番号
	 * @param type コンテンツタイプ（フィルタリング用）
	 * @return コンテンツ一覧のリスト
	 */
	public List<HashMap<String, String>> getList(String sort, int page, String type) {
		int limit = 20;
		int offset = 0;
		if (1 < page) {
			offset = (page - 1) * 20;
		}
		return db.selectAllLimit("content", sort, type, limit, offset);
	}

	/**
	 * すべてのコンテンツ一覧を取得する（ページネーションなし）
	 * 
	 * @param table テーブル名
	 * @param sort ソート順
	 * @param type コンテンツタイプ（フィルタリング用）
	 * @return コンテンツ一覧のリスト
	 */
	public List<HashMap<String, String>> getAllList(String table, String sort, String type) {
		return db.selectAll(table, sort, type);
	}

	/**
	 * 新規コンテンツを作成する
	 * スケジュール公開・非公開の日時をチェックし、適切に公開テーブルにも登録する
	 * 
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @return 処理成功時true
	 */
	public boolean doCreate(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		String name = (String) session.getAttribute("name");
		String url = request.getParameter("url");
		String title = request.getParameter("title");
		String head = request.getParameter("head");
		String content = request.getParameter("content");
		String type = request.getParameter("type");
		String template = request.getParameter("template");
		String elementcolor = request.getParameter("elementcolor");
		String published = request.getParameter("published");
		String schedule_published = request.getParameter("schedule_published");
		String schedule_unpublished = request.getParameter("schedule_unpublished");
		if (url == null) {
			url = "";
		}
		while (url.indexOf("/") == 0) {
			url = url.substring(1, url.length());
		}
		if (title == null) {
			title = "";
		}
		if (head == null) {
			head = "";
		}
		if (content == null) {
			content = "";
		}
		if (type == null) {
			type = "";
		}
		if (elementcolor == null) {
			elementcolor = "";
		}
		if (template == null) {
			template = "";
		}
		if (published == null) {
			published = "";
		}
		if (schedule_published == null) {
			schedule_published = "";
		} else {
			schedule_published = schedule_published.replaceAll("/", "-").replaceAll("T", " ");
		}
		if (schedule_unpublished == null) {
			schedule_unpublished = "";
		} else {
			schedule_unpublished = schedule_unpublished.replaceAll("/", "-").replaceAll("T", " ");
		}
		try {
			String now = CommonProc.createNow().replaceAll("/", "-");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			int publishParseResult = 0;
			int unpublishParseResult = 0;
			if (!schedule_published.equals("")) {
				publishParseResult = sdf.parse(now).compareTo(sdf.parse(schedule_published));
			}
			if (!schedule_unpublished.equals("")) {
				unpublishParseResult = sdf.parse(now).compareTo(sdf.parse(schedule_unpublished));
			}
			//公開、非公開共に過去日、現在日は空で登録
			if (publishParseResult == 1 || publishParseResult == 0) {
				schedule_published = "";
			}
			if (unpublishParseResult == 1 || unpublishParseResult == 0) {
				schedule_unpublished = "";
			}
			int id = db.create(name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, published);
			if (published.equals("1")) {
				//過去日、現在日時、空だった場合に公開(未来日はバッチで公開)
				if (publishParseResult == 1 || publishParseResult == 0 || schedule_published.equals("")) {
					db.create_public(String.valueOf(id), name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, published);
				}
			}
			session.setAttribute("registerMessage", "登録しました。");
		} catch (SQLException e) {
			session.setAttribute("registerMessage", "登録に失敗しました。");
		} catch (Exception e) {
			session.setAttribute("registerMessage", "異常が発生しました。");
		}
		try {
			String redirectUrl = getRedirectUrl(type);
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 既存のコンテンツを更新する
	 * スケジュール公開・非公開の日時をチェックし、適切に公開テーブルも更新する
	 * 
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @return 処理成功時true
	 */
	/**
	 * 既存のコンテンツを更新する
	 * スケジュール公開・非公開の日時をチェックし、適切に公開テーブルも更新する
	 * 
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @return 処理成功時true
	 */
	public boolean doUpdate(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		String id = request.getParameter("id");
		String name = (String) session.getAttribute("name");
		String url = request.getParameter("url");
		String title = request.getParameter("title");
		String head = request.getParameter("head");
		String content = request.getParameter("content");
		String type = request.getParameter("type");
		String elementcolor = request.getParameter("elementcolor");
		String template = request.getParameter("template");
		String published = request.getParameter("published");
		String schedule_published = request.getParameter("schedule_published");
		String schedule_unpublished = request.getParameter("schedule_unpublished");
		if (url == null) {
			url = "";
		}
		while (url.indexOf("/") == 0) {
			url = url.substring(1, url.length());
		}
		if (title == null) {
			title = "";
		}
		if (head == null) {
			head = "";
		}
		if (content == null) {
			content = "";
		}
		if (type == null) {
			type = "";
		}
		if (elementcolor == null) {
			elementcolor = "";
		}
		if (template == null) {
			template = "";
		}
		if (published == null) {
			published = "";
		}
		if (schedule_published == null) {
			schedule_published = "";
		} else {
			schedule_published = schedule_published.replaceAll("/", "-").replaceAll("T", " ");
		}
		if (schedule_unpublished == null) {
			schedule_unpublished = "";
		} else {
			schedule_unpublished = schedule_unpublished.replaceAll("/", "-").replaceAll("T", " ");
		}
		try {
			String now = CommonProc.createNow().replaceAll("/", "-").replaceAll("T", " ");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			int publishParseResult = 0;
			int unpublishParseResult = 0;
			if (!schedule_published.equals("")) {
				publishParseResult = sdf.parse(now).compareTo(sdf.parse(schedule_published));
			}
			if (!schedule_unpublished.equals("")) {
				unpublishParseResult = sdf.parse(now).compareTo(sdf.parse(schedule_unpublished));
			}
			//公開、非公開共に過去日、現在日は空で登録
			if (publishParseResult == 1 || publishParseResult == 0) {
				schedule_published = "";
			}
			if (unpublishParseResult == 1 || unpublishParseResult == 0) {
				schedule_unpublished = "";
			}
			db.update(id, name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, published);
			if (published.equals("1")) {
				//過去日、現在日時、空だった場合に公開(未来日はバッチで公開)
				if (publishParseResult == 1 || publishParseResult == 0 || schedule_published.equals("")) {
					String public_id = db.searchContentByAttribute(id, "id", "content_public");
					if (public_id != null && !public_id.equals("")) {
						db.update_public(id, name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, published);
					} else {
						db.create_public(id, name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, published);
					}
				}
			}
			session.setAttribute("registerMessage", "登録しました。");
		} catch (SQLException e) {
			session.setAttribute("registerMessage", "登録に失敗しました。");
		} catch (Exception e) {
			session.setAttribute("registerMessage", "異常が発生しました。");
		}
		try {
			String redirectUrl = getRedirectUrl(type);
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * コンテンツのタイトルを取得する
	 * 
	 * @param id コンテンツID
	 * @param table テーブル名
	 * @return タイトル文字列
	 */
	public String getTitle(String id, String table) {
		return db.searchContentByAttribute(id, "title", table);
	}

	/**
	 * コンテンツ本文を取得する
	 * 
	 * @param id コンテンツID
	 * @param table テーブル名
	 * @return コンテンツ本文
	 */
	public String getContent(String id, String table) {
		return db.searchContentByAttribute(id, "content", table);
	}

	/**
	 * スタイルシート（CSS）を取得する
	 * 改行コードを削除して返す
	 * 
	 * @param id コンテンツID
	 * @param table テーブル名
	 * @return スタイルシート文字列
	 */
	public String getStylesheet(String id, String table) {
		String output = db.searchContentByAttribute(id, "content", table).replaceAll("\r\n", "");
		return output;
	}

	/**
	 * ヘッダー部分（headタグ内）を取得する
	 * 
	 * @param id コンテンツID
	 * @param table テーブル名
	 * @return ヘッダー部分のHTML
	 */
	public String getHead(String id, String table) {
		return db.searchContentByAttribute(id, "head", table);
	}

	/**
	 * テンプレートのヘッダー部分を取得する
	 * コンテンツに紐づくテンプレートのヘッダー情報を取得する
	 * 
	 * @param id コンテンツID
	 * @param table テーブル名
	 * @return テンプレートのヘッダー部分のHTML
	 */
	public String getTemplateHead(String id, String table) {
		HashMap<String, String> results = db.search(id);
		String tempId = results.get("template");
		if (tempId != null && !tempId.equals("")) {
			return db.searchContentByAttribute(tempId, "head", table);
		} else {
			return "";
		}
	}

	/**
	 * テンプレートのコンテンツ部分を取得する
	 * コンテンツに紐づくテンプレートのコンテンツ情報を取得する
	 * 
	 * @param id コンテンツID
	 * @param table テーブル名
	 * @return テンプレートのコンテンツ部分
	 */
	public String getTemplateContent(String id, String table) {
		HashMap<String, String> results = db.search(id);
		String tempId = results.get("template");
		if (tempId != null && !tempId.equals("")) {
			return db.searchContentByAttribute(tempId, "content", table);
		} else {
			return "";
		}
	}

	/**
	 * 公開用のコンテンツを表示用に整形する
	 * テンプレートと構成要素を組み合わせて最終的なHTMLを生成する
	 * 
	 * @param id コンテンツID
	 * @return 整形されたHTML文字列
	 */
	public String displayContent(String id) {
		StringBuffer output = new StringBuffer();
		HashMap<String, String> result = db.search(id, "content_public");
		String templateId = result.get("template");
		if (templateId != null && !templateId.equals("")) {
			HashMap<String, String> tempResult = db.search(templateId);
			String tempCon = tempResult.get("content");
			if (tempCon != null && !tempCon.equals("")) {
				String[] elements = tempCon.split(",");
				for (int i = 0; i < elements.length; i++) {
					String templateContent = elements[i];
					if (templateContent.equals("###content###")) {
						output.append(result.get("content"));
					} else {
						String elementId = templateContent.replace("###", "").replace("element(", "").replace(")", "");
						HashMap<String, String> eleResult = db.search(elementId);
						output.append(eleResult.get("content"));
					}
				}
			}
		} else {
			output.append(result.get("content"));
		}
		return output.toString();
	}

	/**
	 * プレビュー用のコンテンツを生成する
	 * テンプレートと構成要素を組み合わせてプレビュー用のHTMLを生成する
	 * 
	 * @param content コンテンツ本文
	 * @param templateId テンプレートID
	 * @return 整形されたHTML文字列
	 */
	public String previewContent(String content, String templateId) {
		StringBuffer output = new StringBuffer();
		if (templateId != null && !templateId.equals("")) {
			HashMap<String, String> tempResult = db.search(templateId);
			String tempCon = tempResult.get("content");
			if (tempCon != null && !tempCon.equals("")) {
				String[] elements = tempCon.split(",");
				for (int i = 0; i < elements.length; i++) {
					String templateContent = elements[i];
					if (templateContent.equals("###content###")) {
						output.append(content);
					} else {
						String elementId = templateContent.replace("###", "").replace("element(", "").replace(")", "");
						HashMap<String, String> eleResult = db.search(elementId);
						output.append(eleResult.get("content"));
					}
				}
			}
		} else {
			output.append(content);
		}
		return output.toString();
	}

	/**
	 * ページネーション用のHTMLを生成する
	 * 
	 * @param page 現在のページ番号
	 * @param mode モード（URL生成用）
	 * @param sort ソート順
	 * @return ページネーション用のHTML文字列
	 */
	public String getPager(int page, String mode, String sort) {
		String pageUrl = "";
		StringBuffer output = new StringBuffer();
		int contentSize = db.getCountentSize("content", mode);
		int pageSize = (contentSize / 20);

		if (1 <= contentSize % 20) {
			pageSize = pageSize + 1;
		}
		if (mode.equals("")) {
			pageUrl = "/?mode=";
		} else if (mode.equals("template")) {
			pageUrl = "/?mode=template";
		} else if (mode.equals("element")) {
			pageUrl = "/?mode=element";
		} else if (mode.equals("stylesheet")) {
			pageUrl = "/?mode=stylesheet";
		} else if (mode.equals("script")) {
			pageUrl = "/?mode=script";
		} else if (mode.equals("elementItem")) {
			pageUrl = "/element-item/?mode=";
		}
		//Previous
		if (1 < page && pageSize != 0) {
			output.append("<li class=\"page-item me-4\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + "&page=" + (page - 1) + "&sort=" + sort + "\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>");
		}
		//PageNum
		for (int i = 1; i <= pageSize; i++) {
			if (i == page) {
				output.append("<li class=\"page-item mx-2\">" + i + "</span></li>");
			} else {
				output.append("<li class=\"page-item mx-2\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + "&page=" + i + "&sort=" + sort + "\">" + i + "</a></li>");
			}
		}
		//Next
		if (page < pageSize) {
			output.append("<li class=\"page-item ms-4\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + "&page=" + (page + 1) + "&sort=" + sort + "\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>");
		}
		return output.toString();
	}

	/**
	 * リダイレクト先のURLを取得する
	 * コンテンツタイプに応じた適切なURLを返す
	 * 
	 * @param type コンテンツタイプ
	 * @return リダイレクト先のURL
	 */
	public String getRedirectUrl(String type) {
		if (type.equals("")) {
			return CommonProc.FRONTEND_PATH + "/";
		} else if (type.equals("template")) {
			return CommonProc.FRONTEND_PATH + "/?mode=template";
		} else if (type.equals("element")) {
			return CommonProc.FRONTEND_PATH + "/?mode=element";
		} else if (type.equals("stylesheet")) {
			return CommonProc.FRONTEND_PATH + "/?mode=stylesheet";
		} else if (type.equals("script")) {
			return CommonProc.FRONTEND_PATH + "/?mode=script";
		} else {
			return CommonProc.FRONTEND_PATH + "/";
		}
	}

	/**
	 * コンテンツを削除する
	 * 公開テーブルからも削除する
	 * 
	 * @param id 削除するコンテンツID
	 * @param mode モード（削除後のリダイレクト先を決定する）
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @return 処理成功時true
	 */
	public boolean doDelete(String id, String mode, HttpServletResponse response, HttpSession session) {
		try {
			db.delete("content", id);
			String public_id = db.searchContentByAttribute(id, "id", "content_public");
			if (public_id != null && !public_id.equals("")) {
				db.delete("content_public", id);
			}
			session.setAttribute("registerMessage", "削除しました。");
		} catch (Exception e) {
			session.setAttribute("registerMessage", "異常が発生しました。");
		}
		try {
			String redirectUrl = getRedirectUrl(mode);
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * URLの重複チェックを行う
	 * 指定されたURLが既に他のコンテンツで使用されているか確認する
	 * 
	 * @param url チェックするURL
	 * @param myId 現在のコンテンツID（自分のIDは除外する）
	 * @return URLが重複している場合true、重複していない場合false
	 */
	public boolean urlMatches(String url, String myId) {
		HashMap<String, String> result = db.searchByUrl(url, "content");
		if (result != null && !result.isEmpty() && !"".equals(result.get("id")) && !myId.equals(result.get("id"))) {
			return true;
		}
		return false;
	}
}