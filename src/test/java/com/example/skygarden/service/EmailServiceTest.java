package com.example.skygarden.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Setting;

/**
 * EmailServiceのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private Setting setting;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private Date publishDate;

    @BeforeEach
    void setUp() {
        publishDate = new Date();
    }

    @Test
    void testSendContentPublishedNotification_EmailDisabled() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("0");

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertNull(result);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_EmailEnabledNull() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn(null);

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertNull(result);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_MailSenderNull() {
        EmailService service = new EmailService();
        // Reflectionを使用してprivateフィールドにアクセス
        try {
            java.lang.reflect.Field settingField = EmailService.class.getDeclaredField("setting");
            settingField.setAccessible(true);
            settingField.set(service, setting);
            
            java.lang.reflect.Field mailSenderField = EmailService.class.getDeclaredField("mailSender");
            mailSenderField.setAccessible(true);
            mailSenderField.set(service, null);
        } catch (Exception e) {
            fail("Failed to set private fields: " + e.getMessage());
        }

        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");

        String result = service.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertEquals(Constants.ERROR_EMAIL_POSTFIX_NOT_CONFIGURED, result);
    }

    @Test
    void testSendContentPublishedNotification_MissingRecipient() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn(null);

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertEquals(Constants.ERROR_EMAIL_MISSING_RECIPIENT, result);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_EmptyRecipient() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("");

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertEquals(Constants.ERROR_EMAIL_MISSING_RECIPIENT, result);
    }

    @Test
    void testSendContentPublishedNotification_InvalidFromAddress() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("invalid-email");

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertEquals(Constants.ERROR_EMAIL_INVALID_ADDRESS, result);
    }

    @Test
    void testSendContentPublishedNotification_InvalidToAddress() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("invalid-email");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertEquals(Constants.ERROR_EMAIL_INVALID_ADDRESS, result);
    }

    @Test
    void testSendContentPublishedNotification_Success() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Template");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertNull(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_MultipleRecipients() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test1@example.com,test2@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Template");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertNull(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_InvalidRecipientInMultiple() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test1@example.com,invalid-email");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertEquals(Constants.ERROR_EMAIL_INVALID_ADDRESS, result);
    }

    @Test
    void testSendContentPublishedNotification_DefaultBodyTemplate() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn(null);
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertNull(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_EmptyBodyTemplate() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertNull(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_DefaultBaseUrl() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Template");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn(null);

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertNull(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_BaseUrlWithTrailingSlash() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Template");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com/");

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertNull(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_UrlWithLeadingSlash() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Template");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");

        String result = emailService.sendContentPublishedNotification("Test Title", "/test/page", publishDate);

        assertNull(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_PlaceholderReplacement() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE))
            .thenReturn("Title: ###title###, URL: ###url###, Date: ###publish_date###");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertNull(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_NullTitle() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Template");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");

        String result = emailService.sendContentPublishedNotification(null, "test/page", publishDate);

        assertNull(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_MailException_Connection() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Template");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");
        doThrow(new MailException("Connection refused") {}).when(mailSender).send(any(SimpleMailMessage.class));

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertEquals(Constants.ERROR_EMAIL_COMMUNICATION, result);
    }

    @Test
    void testSendContentPublishedNotification_MailException_Timeout() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Template");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");
        doThrow(new MailException("timeout") {}).when(mailSender).send(any(SimpleMailMessage.class));

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertEquals(Constants.ERROR_EMAIL_COMMUNICATION, result);
    }

    @Test
    void testSendContentPublishedNotification_MailException_Other() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Template");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");
        doThrow(new MailException("Other error") {}).when(mailSender).send(any(SimpleMailMessage.class));

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertEquals(Constants.ERROR_EMAIL_SEND_FAILED, result);
    }

    @Test
    void testSendContentPublishedNotification_GeneralException() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Template");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://example.com");
        doThrow(new RuntimeException("Unexpected error")).when(mailSender).send(any(SimpleMailMessage.class));

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertEquals(Constants.ERROR_EMAIL_SEND_FAILED, result);
    }

    @Test
    void testGetDefaultEmailBody() {
        String result = emailService.getDefaultEmailBody();

        assertNotNull(result);
        assertTrue(result.contains("###title###"));
        assertTrue(result.contains("###url###"));
        assertTrue(result.contains("###publish_date###"));
    }

    @Test
    void testSendContentPublishedNotification_RefusedConnection() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Test body");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://localhost:8080");
        
        MailException mailException = mock(MailException.class);
        when(mailException.getMessage()).thenReturn("Connection refused");
        doThrow(mailException).when(mailSender).send(any(SimpleMailMessage.class));

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertEquals(Constants.ERROR_EMAIL_COMMUNICATION, result);
    }

    @Test
    void testSendContentPublishedNotification_UrlWithMultipleLeadingSlashes() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("Test body");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://localhost:8080");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        String result = emailService.sendContentPublishedNotification("Test Title", "///test/page", publishDate);

        assertNull(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendContentPublishedNotification_BaseUrlWithoutTrailingSlash() {
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_ENABLED)).thenReturn("1");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_TO)).thenReturn("test@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_FROM)).thenReturn("from@example.com");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BODY_TEMPLATE)).thenReturn("URL: ###url###");
        when(setting.getEmailSetting(Constants.CONFIG_EMAIL_BASE_URL)).thenReturn("http://localhost:8080");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        String result = emailService.sendContentPublishedNotification("Test Title", "test/page", publishDate);

        assertNull(result);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
