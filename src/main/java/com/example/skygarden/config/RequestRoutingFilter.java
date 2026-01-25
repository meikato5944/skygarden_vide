package com.example.skygarden.config;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.CommonProc;
import com.example.skygarden.logic.Content;
import com.example.skygarden.mapper.ContentMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * リクエストルーティングフィルター
 * 
 * このフィルターは全てのHTTPリクエストをインターセプトし、
 * コントローラーにマッピングされていないURLを公開コンテンツとして処理します。
 * これにより、CMS上で作成したコンテンツを任意のURLで公開できます。
 * 
 * 処理フロー:
 * 1. /webadmin/** パスは常にコントローラーへ転送（早期リターン）
 * 2. Spring MVCのハンドラーマッピングをチェック
 *    - マッピングが存在する場合: フィルターチェーンを継続
 * 3. content_public テーブルでURLを検索
 *    - コンテンツが見つかった場合: タイプに応じてレスポンスを生成
 *    - 見つからない場合: 404エラー処理へ
 * 
 * コンテンツタイプ別の処理:
 * - 通常コンテンツ（空文字列）: HTMLページとして返却
 *   - テンプレートヘッダー + コンテンツヘッダー + コンテンツ本文
 *   - original.html をテンプレートとして使用
 * - CSS（stylesheet）: text/css として返却
 * - JavaScript（script）: application/javascript として返却
 * - 画像（image）: バイナリファイルとして返却（適切なMIMEタイプ）
 * - ファイル（file）: ダウンロードファイルとして返却
 * 
 * プレースホルダー置換:
 * - ###title###: コンテンツタイトル
 * - ###head###: ヘッダー部分（CSS、JS参照など）
 * - ###content###: コンテンツ本文（テンプレート・構成要素適用後）
 * 
 * @see Content#displayContent(String) コンテンツ表示処理
 * @see Content#getTemplateHead(String, String) テンプレートヘッダー取得
 */
@Component
@Slf4j
public class RequestRoutingFilter extends OncePerRequestFilter {
	
	/** Spring MVCのリクエストマッピングハンドラー */
	@Autowired
	private RequestMappingHandlerMapping handlerMapping;
	
	/** コンテンツ管理用のMyBatis Mapper */
	@Autowired
	private ContentMapper mapper;
	
	/** コンテンツ管理のビジネスロジック */
	@Autowired
	private Content content;
	
	/** アプリケーション設定プロパティ */
	@Autowired
	private AppProperties appProperties;

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
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		String path = request.getRequestURI();
		String contentPath = path;
		String rootpath = CommonProc.getRootPath();

		// /webadmin/** パスは常にコントローラーに渡す（早期リターン）
		if (path.startsWith(Constants.PATH_WEBADMIN)) {
			filterChain.doFilter(request, response);
			return;
		}

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
			HashMap<String, String> result = mapper.searchByUrl(contentPath, Constants.TABLE_CONTENT_PUBLIC);
			if (result != null && !result.isEmpty() && !Constants.EMPTY_STRING.equals(result.get("id"))) {
				String id = result.get("id");
				String type = result.get("type");
				String head = Constants.EMPTY_STRING;
				String title = Constants.EMPTY_STRING;
				String contentResult = Constants.EMPTY_STRING;
				String originalFilePath = Constants.EMPTY_STRING;
				if (type == null || type.equals(Constants.CONTENT_TYPE_CONTENT)) {
					originalFilePath = rootpath + "/original.html";
					title = result.get("title");
					head = content.getTemplateHead(id, Constants.TABLE_CONTENT_PUBLIC) + content.getHead(id, Constants.TABLE_CONTENT_PUBLIC);
					contentResult = content.displayContent(id);
					response.setContentType("text/html; charset=UTF-8");
				} else if (type.equals(Constants.CONTENT_TYPE_STYLESHEET)) {
					originalFilePath = rootpath + "/original.stylesheet.html";
					contentResult = content.getStylesheet(id, Constants.TABLE_CONTENT_PUBLIC);
					response.setContentType("text/css");
				} else if (type.equals(Constants.CONTENT_TYPE_SCRIPT)) {
					originalFilePath = rootpath + "/original.script.html";
					contentResult = content.getContent(id, Constants.TABLE_CONTENT_PUBLIC);
					response.setContentType("application/javascript");
				} else if (type.equals(Constants.CONTENT_TYPE_IMAGE)) {
					// 画像ファイルの配信
					String savedFileName = result.get("content");
					if (savedFileName != null && !savedFileName.isEmpty()) {
						String uploadDir = appProperties.getFile().getUploadDir();
						Path imagePath = Paths.get(uploadDir, savedFileName);
						
						if (Files.exists(imagePath)) {
							// MIMEタイプを判定
							String mimeType = Files.probeContentType(imagePath);
							if (mimeType == null) {
								mimeType = "application/octet-stream";
							}
							
							response.setContentType(mimeType);
							response.setContentLengthLong(Files.size(imagePath));
							
							// キャッシュヘッダーを設定（1日間）
							response.setHeader("Cache-Control", "public, max-age=86400");
							
							try (OutputStream out = response.getOutputStream()) {
								Files.copy(imagePath, out);
								out.flush();
							}
							return;
						}
					}
					// ファイルが見つからない場合は404へ
					filterChain.doFilter(request, response);
					return;
				} else if (type.equals(Constants.CONTENT_TYPE_FILE)) {
					// ファイルのダウンロード配信
					String savedFileName = result.get("content");
					String originalFileName = result.get("head"); // 元のファイル名
					if (savedFileName != null && !savedFileName.isEmpty()) {
						String uploadDir = appProperties.getFile().getFileUploadDir();
						Path filePath = Paths.get(uploadDir, savedFileName);
						
						if (Files.exists(filePath)) {
							// MIMEタイプを判定
							String mimeType = Files.probeContentType(filePath);
							if (mimeType == null) {
								mimeType = "application/octet-stream";
							}
							
							response.setContentType(mimeType);
							response.setContentLengthLong(Files.size(filePath));
							
							// ダウンロード用のファイル名を設定（元のファイル名を使用）
							String downloadFileName = (originalFileName != null && !originalFileName.isEmpty()) 
								? originalFileName 
								: savedFileName;
							// 日本語ファイル名対応（RFC 5987）
							String encodedFileName = java.net.URLEncoder.encode(downloadFileName, "UTF-8").replace("+", "%20");
							response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName + "\"; filename*=UTF-8''" + encodedFileName);
							
							try (OutputStream out = response.getOutputStream()) {
								Files.copy(filePath, out);
								out.flush();
							}
							return;
						}
					}
					// ファイルが見つからない場合は404へ
					filterChain.doFilter(request, response);
					return;
				}
				
				// 画像以外のコンテンツの場合のみ処理を継続
				if (originalFilePath.isEmpty()) {
					filterChain.doFilter(request, response);
					return;
				}
				
				String original = CommonProc.readFile(originalFilePath);
				original = original.replaceAll(Constants.TEMPLATE_TITLE_PLACEHOLDER, title);
				original = original.replaceAll(Constants.TEMPLATE_HEAD_PLACEHOLDER, head);
				original = original.replaceAll(Constants.TEMPLATE_CONTENT_PLACEHOLDER, contentResult);
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
