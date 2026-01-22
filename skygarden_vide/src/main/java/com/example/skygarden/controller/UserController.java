package com.example.skygarden.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.bean.UserBean;
import com.example.skygarden.bean.UserListBean;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ユーザー管理に関するREST APIコントローラー
 * ユーザーの作成、更新、取得、一覧取得などの処理を提供する
 */
@RestController
@RequestMapping(Constants.PATH_WEBADMIN)
public class UserController {
	@Autowired
	private User user;

	/**
	 * ユーザーの作成または更新を行う
	 * IDが空の場合は新規作成、IDが存在する場合は更新処理を実行する
	 * 
	 * @param id ユーザーID（空の場合は新規作成）
	 * @param name ユーザー名
	 * @param password パスワード
	 * @param email メールアドレス
	 * @param admin 管理者フラグ（"1"が管理者、"0"が一般ユーザー）
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @throws IOException IO例外
	 */
	@PostMapping(Constants.API_USER_POST)
	@ResponseBody
	public void userUpdate(@RequestParam(defaultValue = Constants.EMPTY_STRING) String id, @RequestParam(defaultValue = Constants.EMPTY_STRING) String name, @RequestParam(defaultValue = Constants.EMPTY_STRING) String password, @RequestParam(defaultValue = Constants.EMPTY_STRING) String email, @RequestParam(defaultValue = Constants.EMPTY_STRING) String admin, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		if (id.equals(Constants.EMPTY_STRING)) {
			user.create(name, password, email, admin, request, response, session);
		} else {
			user.update(id, name, password, email, admin, request, response, session);
		}
		return;
	}

	/**
	 * ユーザー情報を取得する
	 * 
	 * @param id ユーザーID
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @return ユーザー情報を含むUserBean
	 * @throws IOException IO例外
	 */
	@GetMapping("/getuser")
	@ResponseBody
	public UserBean getUser(@RequestParam(defaultValue = "") String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserBean bean = new UserBean();
		String name = "";
		String password = "";
		String email = "";
		String admin = "";
		if (!id.equals(Constants.EMPTY_STRING)) {
			HashMap<String, String> results = user.getUser(id);
			if (results != null) {
				name = results.get("name");
				password = results.get("password");
				email = results.get("email");
				admin = results.get("admin");
			}
		}
		bean.setName(name);
		bean.setPassword(password);
		bean.setEmail(email);
		bean.setAdmin(admin);
		return bean;
	}
	
	/**
	 * ユーザー一覧を取得する
	 * ソート順とページネーションに対応している
	 * 
	 * @param sort ソート順
	 * @param page ページ番号
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param session HTTPセッション
	 * @return ユーザー一覧情報を含むUserListBean
	 * @throws IOException IO例外
	 */
	@GetMapping(Constants.API_GET_LIST_USER)
	@ResponseBody
	public UserListBean getList(@RequestParam(defaultValue = Constants.EMPTY_STRING) String sort, @RequestParam(defaultValue = "1") String page, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		String[][] sortOptions = Constants.SORT_OPTIONS_USER;
		StringBuffer sortOutput = new StringBuffer();
		String loginName = Constants.EMPTY_STRING;
		String registerMessage = Constants.EMPTY_STRING;
		int thisPage = 1;
		
		if (session.getAttribute(Constants.SESSION_LOGIN_NAME) != null 
				&& !session.getAttribute(Constants.SESSION_LOGIN_NAME).equals(Constants.EMPTY_STRING)) {
			loginName = (String) session.getAttribute(Constants.SESSION_LOGIN_NAME);
		}
		if (session.getAttribute(Constants.SESSION_REGISTER_MESSAGE) != null 
				&& !session.getAttribute(Constants.SESSION_REGISTER_MESSAGE).equals(Constants.EMPTY_STRING)) {
			registerMessage = (String) session.getAttribute(Constants.SESSION_REGISTER_MESSAGE);
			session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.EMPTY_STRING);
		}
		if (request.getParameter("sort") != null && !request.getParameter("sort").equals(Constants.EMPTY_STRING)) {
			sort = request.getParameter("sort");
		}
		for (String[] option : sortOptions) {
			String selected = option[0].equals(sort) ? " selected" : "";
			sortOutput.append("<option value=\"" + option[0] + "\"" + selected + ">" + option[1] + "</option>");
		}
		if (request.getParameter("page") != null && !request.getParameter("page").equals(Constants.EMPTY_STRING)) {
			try {
				thisPage = Integer.valueOf(request.getParameter("page"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				thisPage = 1;
			}
		}
		List<HashMap<String, String>> results = user.getList(sort, thisPage);
		String pagerOutput = user.getPager(thisPage, Constants.PATH_USER_LIST, sort);
		UserListBean bean = new UserListBean();
		bean.setLoginName(loginName);
		bean.setRegisterMessage(registerMessage);
		bean.setSortOutput(sortOutput.toString());
		bean.setPagerOutput(pagerOutput);
		bean.setResults(results);
		return bean;
	}
}
