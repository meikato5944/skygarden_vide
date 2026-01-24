package com.example.skygarden.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.skygarden.bean.ElementItemBean;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Content;

/**
 * ElementItemControllerのテストクラス
 */
@ExtendWith(MockitoExtension.class)
class ElementItemControllerTest {

    @Mock
    private Content content;

    @InjectMocks
    private ElementItemController controller;

    private List<HashMap<String, String>> elementList;

    @BeforeEach
    void setUp() {
        elementList = new ArrayList<>();
        HashMap<String, String> element1 = new HashMap<>();
        element1.put("id", "1");
        element1.put("title", "Header");
        element1.put("elementcolor", "#000000");
        elementList.add(element1);
    }

    @Test
    void testGetElementItem_Success() throws IOException {
        when(content.getAllList(Constants.TABLE_CONTENT, Constants.EMPTY_STRING, Constants.CONTENT_TYPE_ELEMENT))
            .thenReturn(elementList);

        ElementItemBean result = controller.getElementItem();

        assertNotNull(result);
        assertNotNull(result.getResults());
        assertEquals(1, result.getResults().size());
        assertEquals("1", result.getResults().get(0).get("id"));
        assertEquals("Header", result.getResults().get(0).get("title"));
    }

    @Test
    void testGetElementItem_EmptyList() throws IOException {
        when(content.getAllList(Constants.TABLE_CONTENT, Constants.EMPTY_STRING, Constants.CONTENT_TYPE_ELEMENT))
            .thenReturn(new ArrayList<>());

        ElementItemBean result = controller.getElementItem();

        assertNotNull(result);
        assertNotNull(result.getResults());
        assertTrue(result.getResults().isEmpty());
    }

    @Test
    void testGetElementItem_NullList() throws IOException {
        when(content.getAllList(Constants.TABLE_CONTENT, Constants.EMPTY_STRING, Constants.CONTENT_TYPE_ELEMENT))
            .thenReturn(null);

        ElementItemBean result = controller.getElementItem();

        assertNotNull(result);
        assertNull(result.getResults());
    }

    @Test
    void testGetElementItem_MultipleElements() throws IOException {
        List<HashMap<String, String>> multipleElements = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            HashMap<String, String> element = new HashMap<>();
            element.put("id", String.valueOf(i));
            element.put("title", "Element " + i);
            multipleElements.add(element);
        }
        
        when(content.getAllList(Constants.TABLE_CONTENT, Constants.EMPTY_STRING, Constants.CONTENT_TYPE_ELEMENT))
            .thenReturn(multipleElements);

        ElementItemBean result = controller.getElementItem();

        assertNotNull(result);
        assertEquals(5, result.getResults().size());
    }
}
