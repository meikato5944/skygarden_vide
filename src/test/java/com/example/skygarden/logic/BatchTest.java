package com.example.skygarden.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.skygarden.constants.Constants;
import com.example.skygarden.mapper.ContentMapper;
import com.example.skygarden.service.EmailService;

/**
 * Batchのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class BatchTest {

    @Mock
    private ContentMapper mapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private Batch batch;

    private HashMap<String, String> contentData;
    private HashMap<String, String> scheduleItem;

    @BeforeEach
    void setUp() {
        contentData = new HashMap<>();
        contentData.put("id", "1");
        contentData.put("created_by", "user1");
        contentData.put("url", "test/page");
        contentData.put("title", "Test Title");
        contentData.put("head", "<style>test</style>");
        contentData.put("content", "Test Content");
        contentData.put("type", "");
        contentData.put("elementcolor", "");
        contentData.put("template", "");
        contentData.put("schedule_unpublished", "");
        contentData.put("publishflg_keep", "1");

        scheduleItem = new HashMap<>();
        scheduleItem.put("id", "1");
    }

    @Test
    void testPublishedBatch_FirstPublish() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        scheduleList.add(scheduleItem);

        when(mapper.getSchedulePublishedIds(anyString())).thenReturn(scheduleList);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);

        batch.publishedBatch();

        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
        verify(mapper).clearSchedulePublished("1");
    }

    @Test
    void testPublishedBatch_FirstPublish_WithEmail() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        scheduleList.add(scheduleItem);

        when(mapper.getSchedulePublishedIds(anyString())).thenReturn(scheduleList);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);
        when(emailService.sendContentPublishedNotification(anyString(), anyString(), any())).thenReturn(null);

        batch.publishedBatch();

        verify(emailService).sendContentPublishedNotification(anyString(), anyString(), any());
        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
    }

    @Test
    void testPublishedBatch_FirstPublish_EmailError() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        scheduleList.add(scheduleItem);

        when(mapper.getSchedulePublishedIds(anyString())).thenReturn(scheduleList);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);
        when(emailService.sendContentPublishedNotification(anyString(), anyString(), any()))
            .thenReturn("Email error");

        batch.publishedBatch();

        verify(emailService).sendContentPublishedNotification(anyString(), anyString(), any());
        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
    }

    @Test
    void testPublishedBatch_UpdateExisting() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        scheduleList.add(scheduleItem);

        HashMap<String, String> publicData = new HashMap<>();
        publicData.put("id", "1");

        when(mapper.getSchedulePublishedIds(anyString())).thenReturn(scheduleList);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(publicData);

        batch.publishedBatch();

        verify(mapper).updatePublic(anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString());
        verify(mapper, never()).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
        verify(mapper).clearSchedulePublished("1");
    }

    @Test
    void testPublishedBatch_EmptyPublicResult() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        scheduleList.add(scheduleItem);

        HashMap<String, String> emptyPublic = new HashMap<>();

        when(mapper.getSchedulePublishedIds(anyString())).thenReturn(scheduleList);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(emptyPublic);

        batch.publishedBatch();

        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
    }

    @Test
    void testPublishedBatch_PublicResultWithEmptyId() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        scheduleList.add(scheduleItem);

        HashMap<String, String> publicData = new HashMap<>();
        publicData.put("id", "");

        when(mapper.getSchedulePublishedIds(anyString())).thenReturn(scheduleList);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(publicData);

        batch.publishedBatch();

        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
    }

    @Test
    void testPublishedBatch_MultipleItems() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        HashMap<String, String> item1 = new HashMap<>();
        item1.put("id", "1");
        HashMap<String, String> item2 = new HashMap<>();
        item2.put("id", "2");
        scheduleList.add(item1);
        scheduleList.add(item2);

        when(mapper.getSchedulePublishedIds(anyString())).thenReturn(scheduleList);
        when(mapper.search(anyString(), eq(Constants.TABLE_CONTENT))).thenReturn(contentData);
        when(mapper.search(anyString(), eq(Constants.TABLE_CONTENT_PUBLIC))).thenReturn(null);

        batch.publishedBatch();

        verify(mapper, times(2)).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
        verify(mapper).clearSchedulePublished("1");
        verify(mapper).clearSchedulePublished("2");
    }

    @Test
    void testPublishedBatch_EmptyList() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();

        when(mapper.getSchedulePublishedIds(anyString())).thenReturn(scheduleList);

        batch.publishedBatch();

        verify(mapper, never()).search(anyString(), anyString());
        verify(mapper, never()).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
    }

    @Test
    void testPublishedBatch_Exception() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        scheduleList.add(scheduleItem);

        when(mapper.getSchedulePublishedIds(anyString())).thenReturn(scheduleList);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenThrow(new RuntimeException("DB Error"));

        // 例外が発生しても処理は継続される（例外はキャッチされる）
        // 例外が発生してもバッチ処理は継続されることを確認
        assertDoesNotThrow(() -> batch.publishedBatch());
        
        verify(mapper).getSchedulePublishedIds(anyString());
        verify(mapper).search("1", Constants.TABLE_CONTENT);
        // 例外が発生した場合、resultがnullになるため、continueでスキップされる
        // publicResultの検索は行われない
        verify(mapper, never()).search("1", Constants.TABLE_CONTENT_PUBLIC);
    }

    @Test
    void testPublishedBatch_EmailParseException() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        scheduleList.add(scheduleItem);

        when(mapper.getSchedulePublishedIds(anyString())).thenReturn(scheduleList);
        when(mapper.search("1", Constants.TABLE_CONTENT)).thenReturn(contentData);
        when(mapper.search("1", Constants.TABLE_CONTENT_PUBLIC)).thenReturn(null);
        when(emailService.sendContentPublishedNotification(anyString(), anyString(), any()))
            .thenThrow(new RuntimeException("Parse error"));

        batch.publishedBatch();

        verify(mapper).createPublic(anyInt(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
            anyString(), anyString(), anyString());
    }

    @Test
    void testUnPublishedBatch_Success() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        scheduleList.add(scheduleItem);

        when(mapper.getScheduleUnpublishedIds(anyString())).thenReturn(scheduleList);

        batch.unPublishedBatch();

        verify(mapper).delete(Constants.TABLE_CONTENT_PUBLIC, "1");
        verify(mapper).clearScheduleUnpublished("1");
    }

    @Test
    void testUnPublishedBatch_MultipleItems() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        HashMap<String, String> item1 = new HashMap<>();
        item1.put("id", "1");
        HashMap<String, String> item2 = new HashMap<>();
        item2.put("id", "2");
        scheduleList.add(item1);
        scheduleList.add(item2);

        when(mapper.getScheduleUnpublishedIds(anyString())).thenReturn(scheduleList);

        batch.unPublishedBatch();

        verify(mapper).delete(Constants.TABLE_CONTENT_PUBLIC, "1");
        verify(mapper).delete(Constants.TABLE_CONTENT_PUBLIC, "2");
        verify(mapper).clearScheduleUnpublished("1");
        verify(mapper).clearScheduleUnpublished("2");
    }

    @Test
    void testUnPublishedBatch_EmptyList() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();

        when(mapper.getScheduleUnpublishedIds(anyString())).thenReturn(scheduleList);

        batch.unPublishedBatch();

        verify(mapper, never()).delete(anyString(), anyString());
        verify(mapper, never()).clearScheduleUnpublished(anyString());
    }

    @Test
    void testUnPublishedBatch_Exception() {
        List<HashMap<String, String>> scheduleList = new ArrayList<>();
        scheduleList.add(scheduleItem);

        when(mapper.getScheduleUnpublishedIds(anyString())).thenReturn(scheduleList);
        doThrow(new RuntimeException("DB Error")).when(mapper).delete(Constants.TABLE_CONTENT_PUBLIC, "1");

        batch.unPublishedBatch();

        // 例外が発生しても処理は継続される
        verify(mapper).getScheduleUnpublishedIds(anyString());
    }
}
