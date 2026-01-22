package com.example.skygarden.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.skygarden.constants.Constants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * グローバル例外ハンドラー
 * アプリケーション全体で発生する例外を処理する
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@Value("${spring.servlet.multipart.max-file-size:10MB}")
	private String maxFileSize;
	
	/**
	 * ファイルアップロードサイズ超過時の例外処理
	 * 
	 * @param exc 例外
	 * @param request HTTPリクエスト
	 * @param session HTTPセッション
	 * @param redirectAttributes リダイレクト属性
	 * @return リダイレクト先
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String handleMaxSizeException(
			MaxUploadSizeExceededException exc,
			HttpServletRequest request,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		log.warn("File upload size exceeded: {}", exc.getMessage());
		
		String errorMessage = "ファイルサイズが上限（" + maxFileSize + "）を超えています。より小さいファイルを選択してください。";
		session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, errorMessage);
		
		// リファラーからリダイレクト先を決定
		String referer = request.getHeader("Referer");
		if (referer != null) {
			if (referer.contains("mode=image")) {
				return "redirect:/?mode=image";
			} else if (referer.contains("mode=file")) {
				return "redirect:/?mode=file";
			} else if (referer.contains("mode=movie")) {
				return "redirect:/?mode=movie";
			}
		}
		
		return "redirect:/?mode=image";
	}
}
