package com.example.skygarden.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.skygarden.bean.UserBean;
import com.example.skygarden.bean.UserListBean;
import com.example.skygarden.logic.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ユーザー管理に関するREST APIコントローラー
 * ユーザーの作成、更新、取得、一覧取得などの処理を提供する
 */
@RestController
@RequestMapping("/webadmin")
public class UserController {

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
	@PostMapping("/user_post")
	@ResponseBody
	public void userUpdate(@RequestParam(defaultValue = "") String id, @RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String password, @RequestParam(defaultValue = "") String email, @RequestParam(defaultValue = "") String admin, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		User user = new User();
		if (id.equals("")) {
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
		User user = new User();
		String name = "";
		String password = "";
		String email = "";
		String admin = "";
		if (!id.equals("")) {
			HashMap<String, String> results = user.getUser(id);
			name = results.get("name");
			password = results.get("password");
			email = results.get("email");
			admin = results.get("admin");
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
	@GetMapping("/getlist-user")
	@ResponseBody
	public UserListBean getList(@RequestParam(defaultValue = "") String sort, @RequestParam(defaultValue = "1") String page, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		String[][] sortOptions = { { "id", "ID(降順)" }, { "id desc", "ID(昇順)" }, { "name", "ユーザ名(降順)" }, { "name desc", "ユーザ名(昇順)" } };
		StringBuffer sortOutput = new StringBuffer();
		User user = new User();
		String loginName = "";
		String registerMessage = "";
		int thisPage = 1;
		
		if (session.getAttribute("loginName") != null && !session.getAttribute("loginName").equals("")) {
			loginName = (String) session.getAttribute("loginName");
		}
		if (session.getAttribute("registerMessage") != null && !session.getAttribute("registerMessage").equals("")) {
			registerMessage = (String) session.getAttribute("registerMessage");
			session.setAttribute("registerMessage", "");
		}
		if (request.getParameter("sort") != null && !request.getParameter("sort").equals("")) {
			sort = request.getParameter("sort");
		}
		for (int i = 0; i < sortOptions.length; i++) {
			if (sortOptions[i][0].equals(sort)) {
				sortOutput.append("<option value=\"" + sortOptions[i][0] + "\" selected>" + sortOptions[i][1] + "</option>");
			} else {
				sortOutput.append("<option value=\"" + sortOptions[i][0] + "\">" + sortOptions[i][1] + "</option>");
			}
		}
		if (request.getParameter("page") != null && !request.getParameter("page").equals("")) {
			try {
				thisPage = Integer.valueOf(request.getParameter("page"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				thisPage = 1;
			}
		}
		List<HashMap<String, String>> results = user.getList(sort, thisPage);
		String pagerOutput = user.getPager(thisPage, "/user-list", sort);
		UserListBean bean = new UserListBean();
		bean.setLoginName(loginName);
		bean.setRegisterMessage(registerMessage);
		bean.setSortOutput(sortOutput.toString());
		bean.setPagerOutput(pagerOutput);
		bean.setResults(results);
		return bean;
	}
}
