package com.example.skygarden.config;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.example.skygarden.logic.CommonProc;
import com.example.skygarden.logic.Content;
import com.example.skygarden.logic.DB;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * リクエストルーティングフィルター
 * 未マッピングのリクエストをコンテンツページとして処理する
 * 公開テーブルからURLに一致するコンテンツを検索し、HTMLを生成して返す
 */
@Component
@Slf4j
public class RequestRoutingFilter extends OncePerRequestFilter {
	@Autowired
	private RequestMappingHandlerMapping handlerMapping;

	/**
	 * リクエストをフィルタリングする
	 * 1. マッピングされたリクエストかチェック
	 * 2. コンテンツページとして処理
	 * 3. それ以外は404エラーページへ
	 * 
	 * @param request HTTPリクエスト
	 * @param response HTTPレスポンス
	 * @param filterChain フィルターチェーン
	 * @throws ServletException サーブレット例外
	 * @throws IOException IO例外
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		DB db = new DB();
		String path = request.getRequestURI();
		String contentPath = path;
		String rootpath = CommonProc.getRootPath();

		//1.Mapping
		try {
			HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
			if (handlerExecutionChain != null) {
				filterChain.doFilter(request, response);
				return;
			}
		} catch (Exception e) {
			log.info("[Mapping Phase Error] " + e.toString());
		}

		//2.contentPage
		try {
			if (contentPath.startsWith("/")) {
				contentPath = contentPath.substring(1);
			}
			HashMap<String, String> result = db.searchByUrl(contentPath, "content_public");
			if (result != null && !result.isEmpty() && !"".equals(result.get("id"))) {
				Content content = new Content();
				String id = result.get("id");
				String type = result.get("type");
				String head = "";
				String title = "";
				String contentResult = "";
				String originalFilePath = "";
				if (type.equals("")) {
					originalFilePath = rootpath + "/original.html";
					title = result.get("title");
					head = content.getTemplateHead(id, "content_public") + content.getHead(id, "content_public");
					contentResult = content.displayContent(id);
					response.setContentType("text/html; charset=UTF-8");
				} else if (type.equals("stylesheet")) {
					originalFilePath = rootpath + "/original.stylesheet.html";
					contentResult = content.getStylesheet(id, "content_public");
					response.setContentType("text/css");
				} else if (type.equals("script")) {
					originalFilePath = rootpath + "/original.script.html";
					contentResult = content.getContent(id, "content_public");
					response.setContentType("application/javascript");
				}
				String original = CommonProc.readFile(originalFilePath);
				original = original.replaceAll("###title###", title);
				original = original.replaceAll("###head###", head);
				original = original.replaceAll("###content###", contentResult);
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(original);
				response.getWriter().close();
				return;
			}
		} catch (Exception e) {
			log.info("[ContentPage Phase Error] " + e.toString());
		}
		// 3.template/error/404.html
		filterChain.doFilter(request, response);
		return;
	}
}
