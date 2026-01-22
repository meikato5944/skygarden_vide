package com.example.skygarden.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.bean.ContentBean;
import com.example.skygarden.bean.ListBean;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Content;
import com.example.skygarden.logic.Setting;
import com.example.skygarden.util.ScreenNameConverter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * コンテンツ管理に関するREST APIコントローラー
 * コンテンツの作成、更新、削除、取得などの処理を提供する
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
public class ContentController {
	@Autowired
	private Content content;
	@Autowired
	private Setting setting;

	/**
	 * コンテンツの作成または更新を行う
	 * IDが空の場合は新規作成、IDが存在する場合は更新処理を実行する
	 * 
	 * @param id コンテンツID（空の場合は新規作成）
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @throws IOException IO例外
	 */
	@PostMapping(Constants.API_UPDATE_POST)
	@ResponseBody
	public void update(@RequestParam String id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		if (!id.equals(Constants.EMPTY_STRING)) {
			content.doUpdate(request, response, session);
		} else {
			content.doCreate(request, response, session);
		}
	}

	/**
	 * コンテンツ情報を取得する
	 * モードに応じてコンテンツ、テンプレート、構成要素などの情報を返す
	 * 
	 * @param id コンテンツID
	 * @param mode モード（空文字列:コンテンツ、template:テンプレート、element:構成要素、stylesheet:CSS、script:JS）
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @return コンテンツ情報を含むContentBean
	 * @throws IOException IO例外
	 */
	@GetMapping(Constants.API_GET_CONTENT)
	@ResponseBody
	public ContentBean getById(@RequestParam(defaultValue = Constants.EMPTY_STRING) String id, @RequestParam(defaultValue = Constants.EMPTY_STRING) String mode, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String screenName = "";
		String title = "";
		String template = "";
		String head = "";
		String contentStr = "";
		String url = "";
		String elementcolor = "";
		String schedule_published = "";
		String schedule_unpublished = "";
		String publishflg_keep = "";
		ContentBean bean = new ContentBean();
		List<HashMap<String, String>> eleResults = new ArrayList<HashMap<String, String>>();
		StringBuffer templateOutput = new StringBuffer();
		StringBuffer colorOutput = new StringBuffer();
		if (request.getParameter("id") != null && !request.getParameter("id").equals(Constants.EMPTY_STRING)) {
			id = request.getParameter("id");
		}
		if (request.getParameter("mode") != null && !request.getParameter("mode").equals(Constants.EMPTY_STRING)) {
			mode = request.getParameter("mode");
		}
		if (!id.equals(Constants.EMPTY_STRING)) {
			HashMap<String, String> result = content.doSearch(id);
			if (result != null) {
				template = result.get("template") != null ? result.get("template") : Constants.EMPTY_STRING;
				title = result.get("title") != null ? result.get("title") : Constants.EMPTY_STRING;
				head = result.get("head") != null ? result.get("head") : Constants.EMPTY_STRING;
				contentStr = result.get("content") != null ? result.get("content") : Constants.EMPTY_STRING;
				url = result.get("url") != null ? result.get("url") : Constants.EMPTY_STRING;
				elementcolor = result.get("elementcolor") != null ? result.get("elementcolor") : Constants.EMPTY_STRING;
				schedule_published = result.get("schedule_published") != null ? result.get("schedule_published") : Constants.EMPTY_STRING;
				schedule_unpublished = result.get("schedule_unpublished") != null ? result.get("schedule_unpublished") : Constants.EMPTY_STRING;
				publishflg_keep = result.get("publishflg_keep") != null ? result.get("publishflg_keep") : Constants.EMPTY_STRING;
			}
		}
		if (mode.equals(Constants.CONTENT_TYPE_CONTENT) || mode == null || mode.isEmpty()) {
			screenName = Constants.SCREEN_NAME_CONTENT;
			List<HashMap<String, String>> templateResults = content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE);
			templateOutput.append("<option value=\"" + Constants.EMPTY_STRING + "\">--none--</option>");
			for (HashMap<String, String> templateResult : templateResults) {
				String templateId = templateResult.get("id");
				String templateTitle = templateResult.get("title");
				String selected = templateId.equals(template) ? " selected" : "";
				templateOutput.append("<option value=\"" + templateId + "\"" + selected + ">" + templateTitle + "</option>");
			}
		} else if (mode.equals(Constants.CONTENT_TYPE_TEMPLATE)) {
			screenName = Constants.SCREEN_NAME_TEMPLATE;
			if (!id.equals(Constants.EMPTY_STRING)) {
				HashMap<String, String> result = content.doSearch(id);
				if (result != null) {
					title = result.get("title") != null ? result.get("title") : Constants.EMPTY_STRING;
					head = result.get("head") != null ? result.get("head") : Constants.EMPTY_STRING;
					contentStr = result.get("content") != null ? result.get("content") : Constants.EMPTY_STRING;
					if (!contentStr.equals(Constants.EMPTY_STRING)) {
						String[] elements = contentStr.split(",");
						for (String element : elements) {
							if (element.equals(Constants.TEMPLATE_CONTENT_PLACEHOLDER)) {
								HashMap<String, String> m = new HashMap<>();
								m.put("content", Constants.FLAG_YES);
								eleResults.add(m);
							} else {
								String elementId = element.replace(Constants.ELEMENT_PREFIX, Constants.EMPTY_STRING)
										.replace(Constants.ELEMENT_FUNCTION_START, Constants.EMPTY_STRING)
										.replace(Constants.ELEMENT_FUNCTION_END, Constants.EMPTY_STRING);
								HashMap<String, String> eleResult = content.doSearch(elementId);
								if (eleResult != null) {
									HashMap<String, String> m = new HashMap<>();
									m.put("id", eleResult.get("id"));
									m.put("title", eleResult.get("title"));
									m.put("code", eleResult.get("elementcolor"));
									eleResults.add(m);
								}
							}
						}
					}
				}
			} else {
				HashMap<String, String> m = new HashMap<>();
				m.put("content", Constants.FLAG_YES);
				eleResults.add(m);
			}
		} else if (mode.equals(Constants.CONTENT_TYPE_ELEMENT)) {
			screenName = Constants.SCREEN_NAME_ELEMENT;
			List<HashMap<String, String>> colorElements = setting.elementsColorList();
			colorOutput.append("<option value=\"" + Constants.EMPTY_STRING + "\">--none--</option>");
			for (HashMap<String, String> colorElement : colorElements) {
				String code = colorElement.get("code");
				String name = colorElement.get("name");
				String selected = code.equals(elementcolor) ? " selected" : "";
				colorOutput.append("<option value=\"" + code + "\" style=\"background-color:" + code + ";\"" + selected + ">" + name + "</option>");
			}
		} else if (mode.equals(Constants.CONTENT_TYPE_STYLESHEET)) {
			screenName = Constants.SCREEN_NAME_STYLESHEET;
		} else if (mode.equals(Constants.CONTENT_TYPE_SCRIPT)) {
			screenName = Constants.SCREEN_NAME_SCRIPT;
		}
		bean.setScreenName(screenName);
		bean.setSchedule_published(schedule_published);
		bean.setSchedule_unpublished(schedule_unpublished);
		bean.setTemplate(template);
		bean.setTitle(title);
		bean.setHead(head);
		bean.setContent(contentStr);
		bean.setUrl(url);
		bean.setElementcolor(elementcolor);
		bean.setTemplateOutput(templateOutput.toString());
		bean.setColorOutput(colorOutput.toString());
		bean.setEleResults(eleResults);
		bean.setPublishflgKeep(publishflg_keep);
		return bean;
	}

	/**
	 * テンプレート選択用のHTMLオプションを取得する
	 * 
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @return テンプレート選択用のHTMLオプション文字列
	 * @throws IOException IO例外
	 */
	@GetMapping(Constants.API_GET_TEMPLATE)
	@ResponseBody
	public String getTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		StringBuffer templateOutput = new StringBuffer();
		List<HashMap<String, String>> templateResults = content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE);
		templateOutput.append("<option value=\"\">--none--</option>");
		for (int i = 0; i < templateResults.size(); i++) {
			templateOutput.append("<option value=\"" + templateResults.get(i).get("id") + "\">" + templateResults.get(i).get("title") + "</option>");
		}
		return templateOutput.toString();
	}

	/**
	 * 構成要素の色選択用のHTMLオプションを取得する
	 * 
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @return 色選択用のHTMLオプション文字列
	 * @throws IOException IO例外
	 */
	@GetMapping(Constants.API_GET_ELEMENT)
	@ResponseBody
	public String getElement(HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<HashMap<String, String>> colorElements = setting.elementsColorList();
		StringBuffer colorOutput = new StringBuffer();
		colorOutput.append("<option value=\"\">--none--</option>");
		for (int i = 0; i < colorElements.size(); i++) {
			colorOutput.append("<option value=\"" + colorElements.get(i).get("code") + "\" style=\"background-color:" + colorElements.get(i).get("code") + ";\">" + colorElements.get(i).get("name") + "</option>");
		}
		return colorOutput.toString();
	}

	/**
	 * コンテンツを削除する
	 * 
	 * @param id 削除するコンテンツID
	 * @param mode モード（削除後のリダイレクト先を決定する）
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @throws IOException IO例外
	 */
	@PostMapping(Constants.API_DELETE_POST)
	@ResponseBody
	public void delete(@RequestParam String id, @RequestParam String mode, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		if (!id.equals(Constants.EMPTY_STRING)) {
			content.doDelete(id, mode, response, session);
		}
	}

	/**
	 * コンテンツ一覧を取得する
	 * ソート順とページネーションに対応している
	 * 
	 * @param mode モード（コンテンツタイプのフィルタリング）
	 * @param sort ソート順
	 * @param page ページ番号
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @return コンテンツ一覧情報を含むListBean
	 * @throws IOException IO例外
	 */
	@GetMapping(Constants.API_GET_LIST)
	@ResponseBody
	public ListBean getListApi(@RequestParam(defaultValue = Constants.EMPTY_STRING) String mode, @RequestParam(defaultValue = Constants.EMPTY_STRING) String sort, @RequestParam(defaultValue = "1") String page, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		ListBean bean = new ListBean();
		String[][] sortOptions = Constants.SORT_OPTIONS_CONTENT;
		StringBuffer sortOutput = new StringBuffer();
		String loginName = Constants.EMPTY_STRING;
		String registerMessage = Constants.EMPTY_STRING;
		int thisPage = 1;
		if (session.getAttribute(Constants.SESSION_LOGIN_NAME) != null 
				&& !session.getAttribute(Constants.SESSION_LOGIN_NAME).equals(Constants.EMPTY_STRING)) {
			loginName = (String) session.getAttribute(Constants.SESSION_LOGIN_NAME);
			session.setAttribute(Constants.SESSION_LOGIN_NAME, Constants.EMPTY_STRING);
		}
		if (session.getAttribute(Constants.SESSION_REGISTER_MESSAGE) != null 
				&& !session.getAttribute(Constants.SESSION_REGISTER_MESSAGE).equals(Constants.EMPTY_STRING)) {
			registerMessage = (String) session.getAttribute(Constants.SESSION_REGISTER_MESSAGE);
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.EMPTY_STRING);
		}
		try {
			thisPage = Integer.valueOf(page);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			thisPage = 1;
		}
		String screenName = ScreenNameConverter.convertModeToScreenName(mode);
		for (int i = 0; i < sortOptions.length; i++) {
			String selected = "";
			if (sortOptions[i][0].equals(sort)) {
				selected = "selected";
			}
			sortOutput.append("<option value=\"" + sortOptions[i][0] + "\"" + selected + ">" + sortOptions[i][1] + "</option>");
		}
		List<HashMap<String, String>> results = content.getList(sort, thisPage, mode);
		String pagerOutput = content.getPager(thisPage, mode, sort);

		bean.setLoginName(loginName);
		bean.setScreenName(screenName);
		bean.setRegisterMessage(registerMessage);
		bean.setSortOutput(sortOutput.toString());
		bean.setPagerOutput(pagerOutput);
		bean.setResults(results);
		return bean;
	}


	/**
	 * URLの重複チェックを行う
	 * 指定されたURLが既に他のコンテンツで使用されているか確認する
	 * 
	 * @param url チェックするURL
	 * @param myId 現在のコンテンツID（自分のIDは除外する）
	 * @return URLが重複している場合true、重複していない場合false
	 * @throws IOException IO例外
	 */
	@GetMapping(Constants.API_URL_MATCHES)
	@ResponseBody
	public boolean urlMatches(@RequestParam(defaultValue = Constants.EMPTY_STRING) String url, @RequestParam(defaultValue = Constants.EMPTY_STRING) String myId) throws IOException {
		return content.urlMatches(url, myId);
	}
	
	/**
	 * コンテンツの変換プレビューを取得する
	 * [movie]タグなどの特殊タグを実際のHTML（iframe等）に変換して返す
	 * 
	 * @param contentText 変換対象のコンテンツ
	 * @return 変換後のHTML
	 */
	@PostMapping("/convert_preview")
	@ResponseBody
	public String convertPreview(@RequestParam(defaultValue = Constants.EMPTY_STRING) String contentText) {
		return content.previewContent(contentText, null);
	}
	
	/**
	 * 画像一覧を取得する（挿入用）
	 * 
	 * @return 画像コンテンツの一覧
	 */
	@GetMapping("/api/images")
	@ResponseBody
	public List<HashMap<String, String>> getImages() {
		return content.getContentListByType(Constants.CONTENT_TYPE_IMAGE);
	}
	
	/**
	 * ファイル一覧を取得する（挿入用）
	 * 
	 * @return ファイルコンテンツの一覧
	 */
	@GetMapping("/api/files")
	@ResponseBody
	public List<HashMap<String, String>> getFiles() {
		return content.getContentListByType(Constants.CONTENT_TYPE_FILE);
	}
	
	/**
	 * 動画一覧を取得する（挿入用）
	 * 
	 * @return 動画コンテンツの一覧
	 */
	@GetMapping("/api/movies")
	@ResponseBody
	public List<HashMap<String, String>> getMovies() {
		return content.getContentListByType(Constants.CONTENT_TYPE_MOVIE);
	}
}
