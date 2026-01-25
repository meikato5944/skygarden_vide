package com.example.skygarden.logic;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.example.skygarden.bean.DirectoryNodeBean;
import com.example.skygarden.config.AppProperties;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;
import com.example.skygarden.service.EmailService;
import com.example.skygarden.util.PaginationUtil;
import com.example.skygarden.util.ScreenNameConverter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * コンテンツ管理のビジネスロジッククラス
 * 
 * このサービスクラスはCMSのコア機能であるコンテンツ管理のビジネスロジックを提供します。
 * コンテンツの作成、更新、削除、検索、表示、テンプレート適用などの処理を行います。
 * 
 * 主な機能:
 * - コンテンツのCRUD操作（作成・読取・更新・削除）
 * - テンプレートと構成要素の組み合わせによるコンテンツ生成
 * - スケジュール公開・非公開の管理
 * - ページネーション
 * - URL重複チェック
 * - URLディレクトリツリーの生成
 * - [movie id=] タグのYouTube埋め込みコード変換
 * 
 * テンプレートシステム:
 * テンプレートのcontentフィールドには構成要素の配置が保存されます。
 * 形式: "###element(ID),###content###,###element(ID)"
 * - ###element(ID): 構成要素のID
 * - ###content###: コンテンツ本文の挿入位置
 * 
 * 公開テーブル（content_public）:
 * 公開フラグが"1"のコンテンツは、contentテーブルとcontent_publicテーブルの
 * 両方に保存されます。公開ページの配信時はcontent_publicテーブルを参照します。
 * 
 * @see ContentMapper データベース操作
 * @see AppProperties アプリケーション設定
 */
@Slf4j
@Service
public class Content {
	
	/** コンテンツ管理用のMyBatis Mapper */
	@Autowired
	private ContentMapper mapper;
	
	/** アプリケーション設定プロパティ */
	@Autowired
	private AppProperties appProperties;
	
	/** メール送信サービス */
	@Autowired(required = false)
	private EmailService emailService;

	/**
	 * コンテンツをIDで検索する
	 * 
	 * @param id コンテンツID
	 * @return コンテンツ情報を含むHashMap
	 */
	public HashMap<String, String> doSearch(String id) {
		return mapper.search(id, Constants.TABLE_CONTENT);
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
		int pageSize = appProperties.getPagination().getPageSize();
		int offset = PaginationUtil.calculateOffset(page, pageSize);
		return mapper.selectAllLimit(Constants.TABLE_CONTENT, sort, type, pageSize, offset);
	}

	/**
	 * キーワードでコンテンツを検索する（ページネーション対応）
	 * タイトル、URL、コンテンツ本文を対象に部分一致検索を行う
	 * 
	 * @param sort ソート順
	 * @param page ページ番号
	 * @param type コンテンツタイプ（フィルタリング用）
	 * @param keyword 検索キーワード
	 * @return 検索結果のコンテンツ一覧
	 */
	public List<HashMap<String, String>> searchList(String sort, int page, String type, String keyword) {
		int pageSize = appProperties.getPagination().getPageSize();
		int offset = PaginationUtil.calculateOffset(page, pageSize);
		return mapper.searchByKeyword(Constants.TABLE_CONTENT, type, keyword, sort, pageSize, offset);
	}

	/**
	 * 検索結果用のページネーションHTMLを生成する
	 * 
	 * @param page 現在のページ番号
	 * @param mode モード（URL生成用）
	 * @param sort ソート順
	 * @param keyword 検索キーワード
	 * @return ページネーション用のHTML文字列
	 */
	public String getSearchPager(int page, String mode, String sort, String keyword) {
		StringBuffer output = new StringBuffer();
		int contentSize = mapper.getContentSizeByKeyword(Constants.TABLE_CONTENT, mode, keyword);
		int pageSize = appProperties.getPagination().getPageSize();
		int totalPages = PaginationUtil.calculateTotalPages(contentSize, pageSize);
		String pageUrl = ScreenNameConverter.getPageUrlByMode(mode);
		String encodedKeyword = "";
		try {
			encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			encodedKeyword = keyword;
		}
		//Previous
		if (1 < page && totalPages != 0) {
			String separator = pageUrl.contains("?") ? "&" : "?";
			output.append("<li class=\"page-item me-4\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + separator + "page=" + (page - 1) + "&sort=" + sort + "&keyword=" + encodedKeyword + "\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>");
		}
		//PageNum
		for (int i = 1; i <= totalPages; i++) {
			if (i == page) {
				output.append("<li class=\"page-item mx-2\">" + i + "</span></li>");
			} else {
				String separator = pageUrl.contains("?") ? "&" : "?";
				output.append("<li class=\"page-item mx-2\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + separator + "page=" + i + "&sort=" + sort + "&keyword=" + encodedKeyword + "\">" + i + "</a></li>");
			}
		}
		//Next
		if (page < totalPages) {
			String separator = pageUrl.contains("?") ? "&" : "?";
			output.append("<li class=\"page-item ms-4\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + separator + "page=" + (page + 1) + "&sort=" + sort + "&keyword=" + encodedKeyword + "\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>");
		}
		return output.toString();
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
		return mapper.selectAll(table, sort, type);
	}
	
	/**
	 * 指定タイプのコンテンツ一覧を取得する（挿入用）
	 * 
	 * @param type コンテンツタイプ
	 * @return コンテンツ一覧のリスト
	 */
	public List<HashMap<String, String>> getContentListByType(String type) {
		return mapper.selectAll(Constants.TABLE_CONTENT, "updated desc", type);
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
			url = Constants.EMPTY_STRING;
		}
		while (url.indexOf("/") == 0) {
			url = url.substring(1, url.length());
		}
		if (title == null) {
			title = Constants.EMPTY_STRING;
		}
		if (head == null) {
			head = Constants.EMPTY_STRING;
		}
		if (content == null) {
			content = Constants.EMPTY_STRING;
		}
		if (type == null) {
			type = Constants.EMPTY_STRING;
		}
		if (elementcolor == null) {
			elementcolor = Constants.EMPTY_STRING;
		}
		if (template == null) {
			template = Constants.EMPTY_STRING;
		}
		if (published == null) {
			published = Constants.EMPTY_STRING;
		}
		if (schedule_published == null) {
			schedule_published = Constants.EMPTY_STRING;
		} else {
			schedule_published = schedule_published.replaceAll("/", "-").replaceAll("T", " ");
		}
		if (schedule_unpublished == null) {
			schedule_unpublished = Constants.EMPTY_STRING;
		} else {
			schedule_unpublished = schedule_unpublished.replaceAll("/", "-").replaceAll("T", " ");
		}
		boolean dbSuccess = false;
		int id = 0;
		try {
			String nowForCompare = CommonProc.createNow().replaceAll("/", "-");
			SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DATETIME);
			int publishParseResult = 0;
			int unpublishParseResult = 0;
			// 日付パース処理で例外が発生した場合は日付を空欄にして処理を続行
			try {
				if (!schedule_published.equals(Constants.EMPTY_STRING)) {
					publishParseResult = sdf.parse(nowForCompare).compareTo(sdf.parse(schedule_published));
				}
				if (!schedule_unpublished.equals(Constants.EMPTY_STRING)) {
					unpublishParseResult = sdf.parse(nowForCompare).compareTo(sdf.parse(schedule_unpublished));
				}
			} catch (ParseException e) {
				// 日付パースエラーは無視（スケジュール日時を空にして登録を続行）
				e.printStackTrace();
				schedule_published = Constants.EMPTY_STRING;
				schedule_unpublished = Constants.EMPTY_STRING;
			}
			//公開、非公開共に過去日、現在日は空で登録
			if (publishParseResult == 1 || publishParseResult == 0) {
				schedule_published = Constants.EMPTY_STRING;
			}
			if (unpublishParseResult == 1 || unpublishParseResult == 0) {
				schedule_unpublished = Constants.EMPTY_STRING;
			}
			String nowTime = CommonProc.createNow();
			// データベースへの登録を実行
			id = mapper.create(nowTime, nowTime, name, name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, published);
			
			// IDが正しく取得できているか確認
			if (id <= 0) {
				throw new RuntimeException("コンテンツの作成に失敗しました。IDが取得できませんでした。");
			}
			
			dbSuccess = true; // データベースへの登録が成功したことを記録
			
			boolean isPublished = false;
			if (published.equals(Constants.FLAG_YES)) {
				//過去日、現在日時、空だった場合に公開(未来日はバッチで公開)
				if (publishParseResult == 1 || publishParseResult == 0 || schedule_published.equals(Constants.EMPTY_STRING)) {
					try {
						// mapper.create()が成功した時点でcontentテーブルにレコードは存在している
						// 外部キー制約違反を防ぐため、直接createPublic()を実行
						// エラーが発生した場合は、メインの登録は成功しているので例外をキャッチして処理を継続
						mapper.createPublic(id, nowTime, nowTime, name, name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, published);
						isPublished = true;
					} catch (org.springframework.dao.DataIntegrityViolationException e) {
						// 外部キー制約違反が発生した場合（contentテーブルにレコードが存在しない場合）
						// メインの登録は成功しているので、ログに記録して処理を継続
						log.warn("content_publicテーブルへの登録に失敗しました（ID: {}）。contentテーブルへの登録は成功しています。", id, e);
					} catch (Exception e) {
						// その他の例外が発生した場合も、メインの登録は成功している
						log.warn("content_publicテーブルへの登録で例外が発生しました（ID: {}）。contentテーブルへの登録は成功しています。", id, e);
					}
				}
			}
			
			// メール送信処理（初回公開時のみ）
			String registerMessage = Constants.MESSAGE_REGISTER_SUCCESS;
			if (isPublished && emailService != null) {
				try {
					// 既存のsdf変数を使用
					Date publishDate = sdf.parse(nowTime.replaceAll("/", "-"));
					String emailError = emailService.sendContentPublishedNotification(title, url, publishDate);
					if (emailError != null) {
						registerMessage = Constants.MESSAGE_REGISTER_SUCCESS_WITH_EMAIL_ERROR + " " + emailError;
					} else {
						registerMessage = Constants.MESSAGE_REGISTER_SUCCESS_WITH_EMAIL_SUCCESS;
					}
				} catch (ParseException e) {
					// 日時パースエラーは無視（メール送信のみ失敗）
					e.printStackTrace();
				} catch (Exception e) {
					// その他のエラーも無視（メール送信のみ失敗）
					e.printStackTrace();
				}
			}
			// メッセージ設定で例外が発生しても、データベースへの登録は成功しているので成功メッセージを設定
			try {
				session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, registerMessage);
			} catch (Exception e) {
				// セッション設定で例外が発生しても、登録は成功しているので成功メッセージを設定
				e.printStackTrace();
				try {
					session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_SUCCESS);
				} catch (Exception e2) {
					// セッション設定が完全に失敗した場合でも、登録は成功している
					e2.printStackTrace();
				}
			}
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			e.printStackTrace();
			// データベースへの登録が成功している場合は成功メッセージを設定
			if (dbSuccess) {
				try {
					session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_SUCCESS);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			} else {
				// データベースへの登録が失敗した場合のみエラーメッセージを設定
				String errorMessage = Constants.MESSAGE_REGISTER_FAILED;
				// データが長すぎる場合のエラーメッセージを追加
				if (e.getCause() != null && e.getCause().getMessage() != null) {
					String causeMessage = e.getCause().getMessage();
					if (causeMessage.contains("Data too long for column")) {
						errorMessage = "登録に失敗しました。コンテンツが長すぎます。データベースのcontentカラムをLONGTEXTに変更してください。";
					}
				}
				try {
					session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, errorMessage);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// データベースへの登録が成功している場合は成功メッセージを設定
			if (dbSuccess) {
				try {
					session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_SUCCESS);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			} else {
				// データベースへの登録が失敗した場合のみエラーメッセージを設定
				try {
					session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_FAILED);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
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
			url = Constants.EMPTY_STRING;
		}
		while (url.indexOf("/") == 0) {
			url = url.substring(1, url.length());
		}
		if (title == null) {
			title = Constants.EMPTY_STRING;
		}
		if (head == null) {
			head = Constants.EMPTY_STRING;
		}
		if (content == null) {
			content = Constants.EMPTY_STRING;
		}
		if (type == null) {
			type = Constants.EMPTY_STRING;
		}
		if (elementcolor == null) {
			elementcolor = Constants.EMPTY_STRING;
		}
		if (template == null) {
			template = Constants.EMPTY_STRING;
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
			String nowForCompare = CommonProc.createNow().replaceAll("/", "-").replaceAll("T", " ");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			int publishParseResult = 0;
			int unpublishParseResult = 0;
			if (!schedule_published.equals(Constants.EMPTY_STRING)) {
				publishParseResult = sdf.parse(nowForCompare).compareTo(sdf.parse(schedule_published));
			}
			if (!schedule_unpublished.equals(Constants.EMPTY_STRING)) {
				unpublishParseResult = sdf.parse(nowForCompare).compareTo(sdf.parse(schedule_unpublished));
			}
			//公開、非公開共に過去日、現在日は空で登録
			if (publishParseResult == 1 || publishParseResult == 0) {
				schedule_published = "";
			}
			if (unpublishParseResult == 1 || unpublishParseResult == 0) {
				schedule_unpublished = "";
			}
			String nowTime = CommonProc.createNow();
			mapper.update(id, nowTime, name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, published);
			boolean isFirstPublish = false;
			if (published.equals(Constants.FLAG_YES)) {
				//過去日、現在日時、空だった場合に公開(未来日はバッチで公開)
				if (publishParseResult == 1 || publishParseResult == 0 || schedule_published.equals(Constants.EMPTY_STRING)) {
					String public_id = mapper.searchContentByAttribute(id, "id", Constants.TABLE_CONTENT_PUBLIC);
					if (public_id != null && !public_id.equals(Constants.EMPTY_STRING)) {
						mapper.updatePublic(id, nowTime, name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, published);
					} else {
						mapper.createPublic(Integer.valueOf(id), nowTime, nowTime, name, name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, published);
						isFirstPublish = true; // 初回公開
					}
				}
			}
			
			// メール送信処理（初回公開時のみ）
			String registerMessage = Constants.MESSAGE_REGISTER_SUCCESS;
			if (isFirstPublish && emailService != null) {
				try {
					// 既存のsdf変数を使用
					Date publishDate = sdf.parse(nowTime.replaceAll("/", "-"));
					String emailError = emailService.sendContentPublishedNotification(title, url, publishDate);
					if (emailError != null) {
						registerMessage = Constants.MESSAGE_REGISTER_SUCCESS_WITH_EMAIL_ERROR + " " + emailError;
					} else {
						registerMessage = Constants.MESSAGE_REGISTER_SUCCESS_WITH_EMAIL_SUCCESS;
					}
				} catch (ParseException e) {
					// 日時パースエラーは無視（メール送信のみ失敗）
					e.printStackTrace();
				} catch (Exception e) {
					// その他のエラーも無視（メール送信のみ失敗）
					e.printStackTrace();
				}
			}
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, registerMessage);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_REGISTER_FAILED);
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
		return mapper.searchContentByAttribute(id, "title", table);
	}

	/**
	 * コンテンツ本文を取得する
	 * 
	 * @param id コンテンツID
	 * @param table テーブル名
	 * @return コンテンツ本文
	 */
	public String getContent(String id, String table) {
		return mapper.searchContentByAttribute(id, "content", table);
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
		String output = mapper.searchContentByAttribute(id, "content", table).replaceAll("\r\n", "");
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
		return mapper.searchContentByAttribute(id, "head", table);
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
		HashMap<String, String> results = mapper.search(id, Constants.TABLE_CONTENT);
		if (results == null) {
			return Constants.EMPTY_STRING;
		}
		String tempId = results.get("template");
		if (tempId != null && !tempId.equals(Constants.EMPTY_STRING)) {
			return mapper.searchContentByAttribute(tempId, "head", table);
		} else {
			return Constants.EMPTY_STRING;
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
		HashMap<String, String> results = mapper.search(id, Constants.TABLE_CONTENT);
		if (results == null) {
			return Constants.EMPTY_STRING;
		}
		String tempId = results.get("template");
		if (tempId != null && !tempId.equals(Constants.EMPTY_STRING)) {
			return mapper.searchContentByAttribute(tempId, "content", table);
		} else {
			return Constants.EMPTY_STRING;
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
		HashMap<String, String> result = mapper.search(id, "content_public");
		if (result == null || result.isEmpty()) {
			return Constants.EMPTY_STRING;
		}
		String templateId = result.get("template");
		if (templateId != null && !templateId.equals(Constants.EMPTY_STRING)) {
			HashMap<String, String> tempResult = mapper.search(templateId, Constants.TABLE_CONTENT);
			if (tempResult != null) {
				String tempCon = tempResult.get("content");
				if (tempCon != null && !tempCon.equals(Constants.EMPTY_STRING)) {
					String[] elements = tempCon.split(",");
					for (int i = 0; i < elements.length; i++) {
						String templateContent = elements[i];
						if (templateContent.equals(Constants.TEMPLATE_CONTENT_PLACEHOLDER)) {
							String content = result.get("content");
							output.append(content != null ? content : Constants.EMPTY_STRING);
						} else {
							String elementId = templateContent.replace(Constants.ELEMENT_PREFIX, Constants.EMPTY_STRING)
									.replace(Constants.ELEMENT_FUNCTION_START, Constants.EMPTY_STRING)
									.replace(Constants.ELEMENT_FUNCTION_END, Constants.EMPTY_STRING);
							HashMap<String, String> eleResult = mapper.search(elementId, Constants.TABLE_CONTENT);
							if (eleResult != null) {
								String eleContent = eleResult.get("content");
								output.append(eleContent != null ? eleContent : Constants.EMPTY_STRING);
							}
						}
					}
				}
			}
		} else {
			String content = result.get("content");
			output.append(content != null ? content : Constants.EMPTY_STRING);
		}
		// [movie id=xxx]タグをYouTube埋め込みコードに変換
		return convertMovieTags(output.toString());
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
		if (content == null) {
			content = Constants.EMPTY_STRING;
		}
		if (templateId != null && !templateId.equals(Constants.EMPTY_STRING)) {
			HashMap<String, String> tempResult = mapper.search(templateId, Constants.TABLE_CONTENT);
			if (tempResult != null) {
				String tempCon = tempResult.get("content");
				if (tempCon != null && !tempCon.equals(Constants.EMPTY_STRING)) {
					String[] elements = tempCon.split(",");
					for (int i = 0; i < elements.length; i++) {
						String templateContent = elements[i];
						if (templateContent.equals(Constants.TEMPLATE_CONTENT_PLACEHOLDER)) {
							output.append(content);
						} else {
							String elementId = templateContent.replace(Constants.ELEMENT_PREFIX, Constants.EMPTY_STRING)
									.replace(Constants.ELEMENT_FUNCTION_START, Constants.EMPTY_STRING)
									.replace(Constants.ELEMENT_FUNCTION_END, Constants.EMPTY_STRING);
							HashMap<String, String> eleResult = mapper.search(elementId, Constants.TABLE_CONTENT);
							if (eleResult != null) {
								String eleContent = eleResult.get("content");
								output.append(eleContent != null ? eleContent : Constants.EMPTY_STRING);
							}
						}
					}
				}
			}
		} else {
			output.append(content);
		}
		// [movie id=xxx]タグをYouTube埋め込みコードに変換
		return convertMovieTags(output.toString());
	}
	
	/**
	 * [movie id=xxx]または[movie id=xxx, width=xxx, height=xxx]タグをYouTube埋め込みコードに変換する
	 * 
	 * @param content 変換対象のコンテンツ
	 * @return 変換後のコンテンツ
	 */
	private String convertMovieTags(String content) {
		if (content == null || content.isEmpty()) {
			return content;
		}
		
		// [movie id=xxx]または[movie id=xxx, width=xxx, height=xxx]のパターンを検索して変換
		// 対応形式: [movie id=11], [movie id=11, width=560px], [movie id=11, height=315px], [movie id=11, width=560px, height=315px]
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
			"\\[movie\\s+id=(\\d+)(?:,\\s*width=([^,\\]]+))?(?:,\\s*height=([^\\]]+))?\\]"
		);
		java.util.regex.Matcher matcher = pattern.matcher(content);
		StringBuffer result = new StringBuffer();
		
		while (matcher.find()) {
			String movieId = matcher.group(1);
			String width = matcher.group(2);
			String height = matcher.group(3);
			
			// 動画コンテンツを取得（公開テーブルから）
			HashMap<String, String> movieResult = mapper.search(movieId, Constants.TABLE_CONTENT_PUBLIC);
			
			String replacement;
			if (movieResult != null && Constants.CONTENT_TYPE_MOVIE.equals(movieResult.get("type"))) {
				String videoId = movieResult.get("content"); // YouTube ビデオID
				if (videoId != null && !videoId.isEmpty()) {
					// YouTube埋め込みコードを生成
					if (width != null || height != null) {
						// サイズ指定がある場合は固定サイズで表示
						String widthStyle = (width != null && !width.trim().isEmpty()) ? width.trim() : "560px";
						String heightStyle = (height != null && !height.trim().isEmpty()) ? height.trim() : "315px";
						replacement = "<div class=\"sky-movie-container\">"
							+ "<iframe src=\"https://www.youtube.com/embed/" + videoId + "\" "
							+ "width=\"" + widthStyle + "\" "
							+ "height=\"" + heightStyle + "\" "
							+ "frameborder=\"0\" "
							+ "allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" "
							+ "allowfullscreen></iframe></div>";
					} else {
						// サイズ指定がない場合はレスポンシブ（16:9比率）
						replacement = "<div class=\"sky-movie-container\" style=\"position:relative;padding-bottom:56.25%;height:0;overflow:hidden;max-width:100%;\">"
							+ "<iframe src=\"https://www.youtube.com/embed/" + videoId + "\" "
							+ "style=\"position:absolute;top:0;left:0;width:100%;height:100%;\" "
							+ "frameborder=\"0\" "
							+ "allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" "
							+ "allowfullscreen></iframe></div>";
					}
				} else {
					replacement = "<!-- Movie ID " + movieId + ": Video ID not found -->";
				}
			} else {
				replacement = "<!-- Movie ID " + movieId + ": Not found or not published -->";
			}
			
			matcher.appendReplacement(result, java.util.regex.Matcher.quoteReplacement(replacement));
		}
		matcher.appendTail(result);
		
		return result.toString();
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
		StringBuffer output = new StringBuffer();
		int contentSize = mapper.getContentSize(Constants.TABLE_CONTENT, mode);
		int pageSize = appProperties.getPagination().getPageSize();
		int totalPages = PaginationUtil.calculateTotalPages(contentSize, pageSize);
		String pageUrl = ScreenNameConverter.getPageUrlByMode(mode);
		//Previous
		if (1 < page && totalPages != 0) {
			String separator = pageUrl.contains("?") ? "&" : "?";
			output.append("<li class=\"page-item me-4\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + separator + "page=" + (page - 1) + "&sort=" + sort + "\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span></a></li>");
		}
		//PageNum
		for (int i = 1; i <= totalPages; i++) {
			if (i == page) {
				output.append("<li class=\"page-item mx-2\">" + i + "</span></li>");
			} else {
				String separator = pageUrl.contains("?") ? "&" : "?";
				output.append("<li class=\"page-item mx-2\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + separator + "page=" + i + "&sort=" + sort + "\">" + i + "</a></li>");
			}
		}
		//Next
		if (page < totalPages) {
			String separator = pageUrl.contains("?") ? "&" : "?";
			output.append("<li class=\"page-item ms-4\"><a class=\"page-link sky-pagination-link\" href=\"" + pageUrl + separator + "page=" + (page + 1) + "&sort=" + sort + "\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span></a></li>");
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
		return ScreenNameConverter.getRedirectUrlByType(type);
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
			mapper.delete("content", id);
			String public_id = mapper.searchContentByAttribute(id, "id", "content_public");
					if (public_id != null && !public_id.equals(Constants.EMPTY_STRING)) {
			mapper.delete(Constants.TABLE_CONTENT_PUBLIC, id);
		}
		session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_DELETE_SUCCESS);
	} catch (Exception e) {
		session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_DELETE_FAILED);
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
	 * 複数のコンテンツを一括削除する
	 * 公開テーブルからも削除する
	 * 
	 * @param ids 削除するコンテンツIDの配列
	 * @param mode モード（削除後のリダイレクト先を決定する）
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @return 処理成功時true
	 */
	public boolean doBatchDelete(String[] ids, String mode, HttpServletResponse response, HttpSession session) {
		int successCount = 0;
		int failCount = 0;
		try {
			for (String id : ids) {
				if (id != null && !id.equals(Constants.EMPTY_STRING)) {
					try {
						mapper.delete("content", id);
						String public_id = mapper.searchContentByAttribute(id, "id", "content_public");
						if (public_id != null && !public_id.equals(Constants.EMPTY_STRING)) {
							mapper.delete(Constants.TABLE_CONTENT_PUBLIC, id);
						}
						successCount++;
					} catch (Exception e) {
						failCount++;
						e.printStackTrace();
					}
				}
			}
			if (successCount > 0) {
				if (failCount > 0) {
					session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, 
						successCount + "件の削除に成功しました。" + failCount + "件の削除に失敗しました。");
				} else {
					session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, 
						successCount + "件のコンテンツを削除しました。");
				}
			} else {
				session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_DELETE_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.MESSAGE_DELETE_FAILED);
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
	 * 複数のコンテンツを一括コピーする
	 * URLは空欄にして、公開フラグも"0"（非公開）にする
	 * 
	 * @param ids コピーするコンテンツIDの配列
	 * @param urls URLの配列（各コンテンツに対応するURL）
	 * @param mode モード（コピー後のリダイレクト先を決定する）
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @return 処理成功時true
	 */
	public boolean doBatchCopy(String[] ids, String[] urls, String mode, HttpServletResponse response, HttpSession session) {
		String name = (String) session.getAttribute("name");
		int successCount = 0;
		int failCount = 0;
		String copiedType = mode; // デフォルトは現在のmode、コピー元のtypeがあればそれを使用
		try {
			String nowTime = CommonProc.createNow();
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				String url = (urls != null && i < urls.length) ? urls[i] : Constants.EMPTY_STRING;
				
				if (id != null && !id.equals(Constants.EMPTY_STRING)) {
					try {
						// 既存のコンテンツ情報を取得
						HashMap<String, String> original = mapper.search(id, Constants.TABLE_CONTENT);
						if (original != null) {
							// URLの先頭スラッシュを除去
							if (url != null && !url.equals(Constants.EMPTY_STRING)) {
								while (url.indexOf("/") == 0) {
									url = url.substring(1, url.length());
								}
							} else {
								url = Constants.EMPTY_STRING;
							}
							
							String title = original.get("title") != null ? original.get("title") : Constants.EMPTY_STRING;
							String head = original.get("head") != null ? original.get("head") : Constants.EMPTY_STRING;
							String content = original.get("content") != null ? original.get("content") : Constants.EMPTY_STRING;
							String type = original.get("type") != null ? original.get("type") : Constants.EMPTY_STRING;
							String elementcolor = original.get("elementcolor") != null ? original.get("elementcolor") : Constants.EMPTY_STRING;
							String template = original.get("template") != null ? original.get("template") : Constants.EMPTY_STRING;
							
							// コピー元のtypeを記録（最初のコピー元のtypeを使用）
							if (i == 0 && type != null && !type.equals(Constants.EMPTY_STRING)) {
								copiedType = type;
							}
							
							// コピー時は公開フラグを"0"（非公開）、スケジュール日時も空欄にする
							String published = Constants.FLAG_NO;
							String schedule_published = Constants.EMPTY_STRING;
							String schedule_unpublished = Constants.EMPTY_STRING;
							
							// 新しいコンテンツを作成（typeはコピー元のtypeを保持）
							int newId = mapper.create(nowTime, nowTime, name, name, url, title, head, content, type, elementcolor, template, schedule_published, schedule_unpublished, published);
							successCount++;
						}
					} catch (Exception e) {
						failCount++;
						e.printStackTrace();
					}
				}
			}
			if (successCount > 0) {
				if (failCount > 0) {
					session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, 
						successCount + "件のコピーに成功しました。" + failCount + "件のコピーに失敗しました。");
				} else {
					session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, 
						successCount + "件のコンテンツをコピーしました。");
				}
			} else {
				session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, "コピーに失敗しました。");
			}
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, "コピーに失敗しました。");
		}
		try {
			// コピー元のtypeに基づいてリダイレクト（コピー元のtypeがなければ現在のmodeを使用）
			String redirectUrl = getRedirectUrl(copiedType);
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
		HashMap<String, String> result = mapper.searchByUrl(url, Constants.TABLE_CONTENT);
		if (result != null && !result.isEmpty() && !Constants.EMPTY_STRING.equals(result.get("id")) && !myId.equals(result.get("id"))) {
			return true;
		}
		return false;
	}

	/**
	 * 指定タイプのコンテンツをURL階層構造のツリーとして取得する
	 * URLが定義されていないコンテンツは含まない
	 * 
	 * @param type コンテンツタイプ
	 * @return 階層構造のルートノードリスト
	 */
	public List<DirectoryNodeBean> getUrlDirectoryTree(String type) {
		List<DirectoryNodeBean> rootNodes = new ArrayList<>();
		List<HashMap<String, String>> contents = mapper.selectAll(Constants.TABLE_CONTENT, "url", type);
		
		if (contents == null || contents.isEmpty()) {
			return rootNodes;
		}
		
		// ROOTノードを作成
		DirectoryNodeBean rootNode = new DirectoryNodeBean("ROOT", "", true);
		
		for (HashMap<String, String> content : contents) {
			String url = content.get("url");
			String id = content.get("id");
			String title = content.get("title");
			
			// URLが空のコンテンツはスキップ
			if (url == null || url.trim().isEmpty()) {
				continue;
			}
			
			// URLを正規化（先頭のスラッシュを除去）
			while (url.startsWith("/")) {
				url = url.substring(1);
			}
			
			// URLをパス部分に分割
			String[] pathParts = url.split("/");
			
			if (pathParts.length == 0) {
				continue;
			}
			
			// ツリーにノードを追加
			addNodeToTree(rootNode, pathParts, 0, id, title);
		}
		
		// ROOTノードに子がある場合のみ追加
		if (!rootNode.getChildren().isEmpty()) {
			rootNodes.add(rootNode);
		}
		
		return rootNodes;
	}

	/**
	 * ツリーにノードを再帰的に追加する
	 * 
	 * @param parent 親ノード
	 * @param pathParts パス部分の配列
	 * @param index 現在のインデックス
	 * @param id コンテンツID
	 * @param title コンテンツタイトル
	 */
	private void addNodeToTree(DirectoryNodeBean parent, String[] pathParts, int index, String id, String title) {
		if (index >= pathParts.length) {
			return;
		}
		
		String currentPart = pathParts[index];
		boolean isLastPart = (index == pathParts.length - 1);
		
		if (isLastPart) {
			// 最後のパート = ファイル
			DirectoryNodeBean fileNode = new DirectoryNodeBean(currentPart, buildPath(pathParts, index + 1), false);
			fileNode.setId(id);
			fileNode.setTitle(title);
			parent.addChild(fileNode);
		} else {
			// 中間のパート = ディレクトリ
			DirectoryNodeBean existingDir = parent.findChildDirectory(currentPart);
			if (existingDir == null) {
				// ディレクトリが存在しない場合は作成
				existingDir = new DirectoryNodeBean(currentPart, buildPath(pathParts, index + 1), true);
				parent.addChild(existingDir);
			}
			// 再帰的に子を追加
			addNodeToTree(existingDir, pathParts, index + 1, id, title);
		}
	}

	/**
	 * パス部分の配列からパス文字列を構築する
	 * 
	 * @param pathParts パス部分の配列
	 * @param length 使用する長さ
	 * @return パス文字列
	 */
	private String buildPath(String[] pathParts, int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length && i < pathParts.length; i++) {
			if (sb.length() > 0) {
				sb.append("/");
			}
			sb.append(pathParts[i]);
		}
		return sb.toString();
	}
}