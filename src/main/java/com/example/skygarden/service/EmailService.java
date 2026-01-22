package com.example.skygarden.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Setting;

/**
 * メール送信サービスクラス
 * 
 * このサービスクラスは、コンテンツ公開時の通知メール送信機能を提供します。
 * postfixを使用してメールを送信します。
 * 
 * 主な機能:
 * - コンテンツ公開通知メールの送信
 * - メールアドレスのバリデーション
 * - メール本文テンプレートの置換処理
 * - エラー原因別の詳細なエラーメッセージ生成
 * 
 * エラーハンドリング:
 * メール送信に失敗した場合でも、コンテンツ登録処理は継続されます。
 * エラーの原因に応じて、詳細なエラーメッセージを返します。
 * 
 * @see Setting 設定管理ロジック
 */
@Service
public class EmailService {
	
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	/** メールアドレスの正規表現パターン */
	private static final Pattern EMAIL_PATTERN = Pattern.compile(
		"^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
	);
	
	/** 設定管理ロジック */
	@Autowired
	private Setting setting;
	
	/** JavaMailSender（Spring Boot Mail Starter） */
	@Autowired(required = false)
	private JavaMailSender mailSender;
	
	/**
	 * コンテンツ公開通知メールを送信する
	 * 
	 * @param title コンテンツタイトル
	 * @param url コンテンツURL
	 * @param publishDate 公開日時
	 * @return エラーメッセージ（成功時はnull、失敗時は原因別のエラーメッセージ）
	 */
	public String sendContentPublishedNotification(String title, String url, Date publishDate) {
		// メール機能が有効かチェック
		String emailEnabled = setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED);
		if (!Constants.FLAG_YES.equals(emailEnabled)) {
			logger.debug("メール機能が無効です。メール送信をスキップします。");
			return null;
		}
		
		// JavaMailSenderが利用可能かチェック（postfix設定チェック）
		if (mailSender == null) {
			logger.error("JavaMailSenderが利用できません。postfixが設定されていない可能性があります。");
			return Constants.ERROR_EMAIL_POSTFIX_NOT_CONFIGURED;
		}
		
		// 送信先メールアドレスを取得
		String emailTo = setting.getEmailSetting(Constants.CONFIG_EMAIL_TO);
		if (emailTo == null || emailTo.trim().isEmpty()) {
			logger.error("送信先メールアドレスが設定されていません。");
			return Constants.ERROR_EMAIL_MISSING_RECIPIENT;
		}
		
		// 送信元メールアドレスを取得
		String emailFrom = setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM);
		if (emailFrom == null || emailFrom.trim().isEmpty()) {
			logger.error("送信元メールアドレスが設定されていません。");
			return Constants.ERROR_EMAIL_INVALID_ADDRESS;
		}
		
		// メールアドレスのバリデーション
		String[] recipients = emailTo.split(",");
		for (String recipient : recipients) {
			String trimmedRecipient = recipient.trim();
			if (!isValidEmailAddress(trimmedRecipient)) {
				logger.error("無効なメールアドレス形式: {}", trimmedRecipient);
				return Constants.ERROR_EMAIL_INVALID_ADDRESS;
			}
		}
		
		if (!isValidEmailAddress(emailFrom.trim())) {
			logger.error("無効な送信元メールアドレス形式: {}", emailFrom);
			return Constants.ERROR_EMAIL_INVALID_ADDRESS;
		}
		
		// メール本文テンプレートを取得
		String bodyTemplate = setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE);
		if (bodyTemplate == null || bodyTemplate.trim().isEmpty()) {
			bodyTemplate = getDefaultEmailBody();
		}
		
		// ベースURLを取得
		String baseUrl = setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL);
		if (baseUrl == null || baseUrl.trim().isEmpty()) {
			baseUrl = "http://localhost:8080";
		}
		// ベースURLの末尾スラッシュを削除
		if (baseUrl.endsWith("/")) {
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}
		
		// URLの先頭スラッシュを削除（既に削除されている可能性がある）
		String contentUrl = url;
		while (contentUrl != null && contentUrl.startsWith("/")) {
			contentUrl = contentUrl.substring(1);
		}
		String fullUrl = baseUrl + "/" + contentUrl;
		
		// 公開日時をフォーマット
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String formattedPublishDate = sdf.format(publishDate);
		
		// メール本文のプレースホルダーを置換
		String body = bodyTemplate
			.replace("###title###", title != null ? title : "")
			.replace("###url###", fullUrl)
			.replace("###publish_date###", formattedPublishDate);
		
		// メール件名を生成
		String subject = "新規コンテンツが公開されました: " + (title != null ? title : "");
		
		// メール送信
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(emailFrom.trim());
			message.setTo(recipients);
			message.setSubject(subject);
			message.setText(body);
			
			mailSender.send(message);
			
			logger.info("メール送信成功: 件名={}, 送信先={}", subject, emailTo);
			return null; // 成功時はnullを返す
			
		} catch (MailException e) {
			logger.error("メール送信に失敗しました: {}", e.getMessage(), e);
			
			// 通信エラーの可能性をチェック
			String errorMessage = e.getMessage();
			if (errorMessage != null && (
				errorMessage.contains("Connection") ||
				errorMessage.contains("connect") ||
				errorMessage.contains("timeout") ||
				errorMessage.contains("refused")
			)) {
				return Constants.ERROR_EMAIL_COMMUNICATION;
			}
			
			// その他のエラー
			return Constants.ERROR_EMAIL_SEND_FAILED;
			
		} catch (Exception e) {
			logger.error("メール送信中に予期しないエラーが発生しました: {}", e.getMessage(), e);
			return Constants.ERROR_EMAIL_SEND_FAILED;
		}
	}
	
	/**
	 * メールアドレスの形式を検証する
	 * 
	 * @param email 検証するメールアドレス
	 * @return 有効な形式の場合true
	 */
	private boolean isValidEmailAddress(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		return EMAIL_PATTERN.matcher(email.trim()).matches();
	}
	
	/**
	 * デフォルトのメール本文テンプレートを取得する
	 * 
	 * @return デフォルトメール本文
	 */
	public String getDefaultEmailBody() {
		return "新しいコンテンツが公開されました。\n\n" +
			"タイトル: ###title###\n" +
			"URL: ###url###\n" +
			"公開日時: ###publish_date###\n\n" +
			"ご確認ください。";
	}
}
