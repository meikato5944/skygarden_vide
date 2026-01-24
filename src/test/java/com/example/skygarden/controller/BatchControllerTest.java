package com.example.skygarden.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.skygarden.logic.Batch;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * BatchControllerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class BatchControllerTest {

    @Mock
    private Batch batch;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private BatchController controller;

    @BeforeEach
    void setUp() {
        // モックは各テストで必要に応じて設定
    }

    @Test
    void testGetUser_EmptyId() throws IOException {
        doNothing().when(batch).publishedBatch();
        doNothing().when(batch).unPublishedBatch();
        
        controller.getUser("", request, response);

        verify(batch).publishedBatch();
        verify(batch).unPublishedBatch();
    }

    @Test
    void testGetUser_WithId() throws IOException {
        doNothing().when(batch).publishedBatch();
        doNothing().when(batch).unPublishedBatch();
        
        controller.getUser("1", request, response);

        verify(batch).publishedBatch();
        verify(batch).unPublishedBatch();
    }

    @Test
    void testGetUser_NullId() throws IOException {
        doNothing().when(batch).publishedBatch();
        doNothing().when(batch).unPublishedBatch();
        
        controller.getUser(null, request, response);

        verify(batch).publishedBatch();
        verify(batch).unPublishedBatch();
    }

    @Test
    void testGetUser_BothBatchesCalled() throws IOException {
        doNothing().when(batch).publishedBatch();
        doNothing().when(batch).unPublishedBatch();
        
        controller.getUser("", request, response);

        verify(batch, times(1)).publishedBatch();
        verify(batch, times(1)).unPublishedBatch();
    }

    @Test
    void testGetUser_ExceptionInPublishedBatch() throws IOException {
        doThrow(new RuntimeException("Batch error")).when(batch).publishedBatch();

        assertThrows(RuntimeException.class, () -> controller.getUser("", request, response));
        verify(batch).publishedBatch();
        verify(batch, never()).unPublishedBatch();
    }

    @Test
    void testGetUser_ExceptionInUnPublishedBatch() throws IOException {
        doNothing().when(batch).publishedBatch();
        doThrow(new RuntimeException("Batch error")).when(batch).unPublishedBatch();

        assertThrows(RuntimeException.class, () -> controller.getUser("", request, response));
        verify(batch).publishedBatch();
        verify(batch).unPublishedBatch();
    }
}
