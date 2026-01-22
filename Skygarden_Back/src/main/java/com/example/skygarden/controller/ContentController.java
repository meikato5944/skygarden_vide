package com.example.skygarden.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.bean.ContentBean;
import com.example.skygarden.bean.ListBean;
import com.example.skygarden.logic.CommonProc;
import com.example.skygarden.logic.Content;
import com.example.skygarden.logic.Setting;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * コンテンツ管理に関するREST APIコントローラー
 * コンテンツの作成、更新、削除、取得などの処理を提供する
 */
@RestController
@RequestMapping("/webadmin")
public class ContentController {

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
	@PostMapping("/update_post")
	@ResponseBody
	public void update(@RequestParam String id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		String ROOTPATH = CommonProc.getRootPath();
		Content content = new Content(ROOTPATH);
		if (!id.equals("")) {
			content.doUpdate(request, response, session);
		} else {
			content.doCreate(request, response, session);
		}
		return;
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
	@GetMapping("/getcontent")
	@ResponseBody
	public ContentBean getById(@RequestParam(defaultValue = "") String id, @RequestParam(defaultValue = "") String mode, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String screenName = "";
		String title = "";
		String template = "";
		String head = "";
		String content = "";
		String url = "";
		String elementcolor = "";
		String schedule_published = "";
		String schedule_unpublished = "";
		String publishflg_keep = "";
		Content con = new Content();
		ContentBean bean = new ContentBean();
		List<HashMap<String, String>> eleResults = new ArrayList<HashMap<String, String>>();
		StringBuffer templateOutput = new StringBuffer();
		StringBuffer colorOutput = new StringBuffer();
		if (request.getParameter("id") != null && !request.getParameter("id").equals("")) {
			id = request.getParameter("id");
		}
		if (request.getParameter("mode") != null && !request.getParameter("mode").equals("")) {
			mode = request.getParameter("mode");
		}
		if (!id.equals("")) {
			HashMap<String, String> result = con.doSearch(id);
			template = result.get("template");
			title = result.get("title");
			head = result.get("head");
			content = result.get("content");
			url = result.get("url");
			elementcolor = result.get("elementcolor");
			schedule_published = result.get("schedule_published") != null ? result.get("schedule_published") : "";
			schedule_unpublished = result.get("schedule_unpublished") != null ? result.get("schedule_unpublished") : "";
			publishflg_keep = result.get("publishflg_keep") != null ? result.get("publishflg_keep") : "";
		}
		if (mode.equals("")) {
			screenName = "コンテンツ";
			List<HashMap<String, String>> templateResults = con.getList("", 1, "template");
			templateOutput.append("<option value=\"\">--none--</option>");
			for (int i = 0; i < templateResults.size(); i++) {
				if (templateResults.get(i).get("id").equals(template)) {
					templateOutput.append("<option value=\"" + templateResults.get(i).get("id") + "\" selected>" + templateResults.get(i).get("title") + "</option>");
				} else {
					templateOutput.append("<option value=\"" + templateResults.get(i).get("id") + "\">" + templateResults.get(i).get("title") + "</option>");
				}
			}
		} else if (mode.equals("template")) {
			screenName = "テンプレート";
			if (!id.equals("")) {
				HashMap<String, String> result = con.doSearch(id);
				title = result.get("title");
				head = result.get("head");
				content = result.get("content");
				String[] elements = content.split(",");
				for (int i = 0; i < elements.length; i++) {
					String templateContent = elements[i];
					if (templateContent.equals("###content###")) {
						HashMap<String, String> m = new HashMap<String, String>();
						m.put("content", "1");
						eleResults.add(m);
					} else {
						String elementId = templateContent.replace("###", "").replace("element(", "").replace(")", "");
						HashMap<String, String> eleResult = con.doSearch(elementId);
						HashMap<String, String> m = new HashMap<String, String>();
						m.put("id", eleResult.get("id"));
						m.put("title", eleResult.get("title"));
						m.put("code", eleResult.get("elementcolor"));
						eleResults.add(m);
					}
				}
			} else {
				HashMap<String, String> m = new HashMap<String, String>();
				m.put("content", "1");
				eleResults.add(m);
			}
		} else if (mode.equals("element")) {
			screenName = "構成要素";
			Setting setting = new Setting();
			List<HashMap<String, String>> colorElements = setting.elementsColorList();
			colorOutput.append("<option value=\"\">--none--</option>");
			for (int i = 0; i < colorElements.size(); i++) {
				if (colorElements.get(i).get("code").equals(elementcolor)) {
					colorOutput.append("<option value=\"" + colorElements.get(i).get("code") + "\" style=\"background-color:" + colorElements.get(i).get("code") + ";\" selected>" + colorElements.get(i).get("name") + "</option>");
				} else {
					colorOutput.append("<option value=\"" + colorElements.get(i).get("code") + "\" style=\"background-color:" + colorElements.get(i).get("code") + ";\">" + colorElements.get(i).get("name") + "</option>");
				}
			}
		} else if (mode.equals("stylesheet")) {
			screenName = "CSS";
		} else if (mode.equals("script")) {
			screenName = "JS";
		}
		bean.setScreenName(screenName);
		bean.setSchedule_published(schedule_published);
		bean.setSchedule_unpublished(schedule_unpublished);
		bean.setTemplate(template);
		bean.setTitle(title);
		bean.setHead(head);
		bean.setContent(content);
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
	@GetMapping("/gettemplate")
	@ResponseBody
	public String getTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Content con = new Content();
		StringBuffer templateOutput = new StringBuffer();
		List<HashMap<String, String>> templateResults = con.getList("", 1, "template");
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
	@GetMapping("/getelement")
	@ResponseBody
	public String getElement(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Setting setting = new Setting();
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
	@PostMapping("/delete_post")
	@ResponseBody
	public void delete(@RequestParam String id, @RequestParam String mode, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		Content content = new Content();
		if (!id.equals("")) {
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
	@GetMapping("/getlist")
	@ResponseBody
	public ListBean getList(@RequestParam(defaultValue = "") String mode, @RequestParam(defaultValue = "") String sort, @RequestParam(defaultValue = "1") String page, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		ListBean bean = new ListBean();
		Content content = new Content();
		String[][] sortOptions = { { "updated", "更新日時(降順)" }, { "updated desc", "更新日時(昇順)" }, { "id", "ID(降順)" }, { "id desc", "ID(昇順)" }, { "title", "タイトル(降順)" }, { "title desc", "タイトル(昇順)" }, { "url", "URL(降順)" }, { "url desc", "URL(昇順)" } };
		StringBuffer sortOutput = new StringBuffer();
		String loginName = "";
		String registerMessage = "";
		String screenName = "";
		int thisPage = 1;
		if (session.getAttribute("loginName") != null && !session.getAttribute("loginName").equals("")) {
			loginName = (String) session.getAttribute("loginName");
			session.setAttribute("loginName", "");
		}
		if (session.getAttribute("registerMessage") != null && !session.getAttribute("registerMessage").equals("")) {
			registerMessage = (String) session.getAttribute("registerMessage");
			session.setAttribute("registerMessage", "");
		}
		try {
			thisPage = Integer.valueOf(page);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			thisPage = 1;
		}
		screenName = convString(mode, "mode");
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
	 * モード文字列を画面名に変換する
	 * 
	 * @param str モード文字列
	 * @param type 変換タイプ（現在は"mode"のみ）
	 * @return 画面名
	 */
	private String convString(String str, String type) {
		if (type.equals("mode")) {
			String screenName = "";
			if (str.equals("")) {
				screenName = "コンテンツ";
			} else if (str.equals("template")) {
				screenName = "テンプレート";
			} else if (str.equals("element")) {
				screenName = "構成要素";
			} else if (str.equals("stylesheet")) {
				screenName = "CSS";
			} else if (str.equals("script")) {
				screenName = "JS";
			} else if (str.equals("image")) {
				screenName = "画像";
			} else if (str.equals("file")) {
				screenName = "ファイル";
			} else if (str.equals("movie")) {
				screenName = "動画";
			}
			return screenName;
		} else {
			return "";
		}
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
	@GetMapping("/urlmatches")
	@ResponseBody
	public boolean urlMatches(@RequestParam(defaultValue = "") String url,@RequestParam(defaultValue = "") String myId) throws IOException {
		Content content = new Content();
		return content.urlMatches(url, myId);
	}
}
