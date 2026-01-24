package com.example.skygarden.bean;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * SettingColorElementsBeanのテストクラス
 */
class SettingColorElementsBeanTest {

    private SettingColorElementsBean bean;

    @BeforeEach
    void setUp() {
        bean = new SettingColorElementsBean();
    }

    @Test
    void testDefaultValues() {
        assertNull(bean.getColorElements());
    }

    @Test
    void testSetAndGetColorElements() {
        List<HashMap<String, String>> colorElements = new ArrayList<>();
        HashMap<String, String> color1 = new HashMap<>();
        color1.put("name", "header");
        color1.put("code", "#000000");
        colorElements.add(color1);
        
        HashMap<String, String> color2 = new HashMap<>();
        color2.put("name", "footer");
        color2.put("code", "#333333");
        colorElements.add(color2);
        
        bean.setColorElements(colorElements);
        assertEquals(2, bean.getColorElements().size());
        assertEquals("header", bean.getColorElements().get(0).get("name"));
        assertEquals("#000000", bean.getColorElements().get(0).get("code"));
        assertEquals("footer", bean.getColorElements().get(1).get("name"));
        assertEquals("#333333", bean.getColorElements().get(1).get("code"));
    }

    @Test
    void testColorElements_AddElement() {
        List<HashMap<String, String>> colorElements = new ArrayList<>();
        HashMap<String, String> color = new HashMap<>();
        color.put("name", "sidebar");
        color.put("code", "#FF0000");
        colorElements.add(color);
        
        bean.setColorElements(colorElements);
        assertEquals(1, bean.getColorElements().size());
        assertEquals("sidebar", bean.getColorElements().get(0).get("name"));
        assertEquals("#FF0000", bean.getColorElements().get(0).get("code"));
    }

    @Test
    void testColorElements_EmptyList() {
        bean.setColorElements(new ArrayList<>());
        assertNotNull(bean.getColorElements());
        assertTrue(bean.getColorElements().isEmpty());
    }

    @Test
    void testColorElements_NullList() {
        bean.setColorElements(null);
        assertNull(bean.getColorElements());
    }

    @Test
    void testColorElements_MultipleElements() {
        List<HashMap<String, String>> colorElements = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            HashMap<String, String> color = new HashMap<>();
            color.put("name", "color" + i);
            color.put("code", "#" + String.format("%06d", i));
            colorElements.add(color);
        }
        
        bean.setColorElements(colorElements);
        assertEquals(10, bean.getColorElements().size());
    }

    @Test
    void testColorElements_ElementWithNullValues() {
        List<HashMap<String, String>> colorElements = new ArrayList<>();
        HashMap<String, String> color = new HashMap<>();
        color.put("name", null);
        color.put("code", null);
        colorElements.add(color);
        
        bean.setColorElements(colorElements);
        assertEquals(1, bean.getColorElements().size());
        assertNull(bean.getColorElements().get(0).get("name"));
        assertNull(bean.getColorElements().get(0).get("code"));
    }

    @Test
    void testColorElements_ElementWithEmptyValues() {
        List<HashMap<String, String>> colorElements = new ArrayList<>();
        HashMap<String, String> color = new HashMap<>();
        color.put("name", "");
        color.put("code", "");
        colorElements.add(color);
        
        bean.setColorElements(colorElements);
        assertEquals(1, bean.getColorElements().size());
        assertEquals("", bean.getColorElements().get(0).get("name"));
        assertEquals("", bean.getColorElements().get(0).get("code"));
    }

    @Test
    void testColorElements_VariousColorCodes() {
        List<HashMap<String, String>> colorElements = new ArrayList<>();
        
        HashMap<String, String> hexColor = new HashMap<>();
        hexColor.put("name", "hex");
        hexColor.put("code", "#FF0000");
        colorElements.add(hexColor);
        
        HashMap<String, String> rgbColor = new HashMap<>();
        rgbColor.put("name", "rgb");
        rgbColor.put("code", "rgb(255, 0, 0)");
        colorElements.add(rgbColor);
        
        HashMap<String, String> namedColor = new HashMap<>();
        namedColor.put("name", "named");
        namedColor.put("code", "red");
        colorElements.add(namedColor);
        
        bean.setColorElements(colorElements);
        assertEquals(3, bean.getColorElements().size());
        assertEquals("#FF0000", bean.getColorElements().get(0).get("code"));
        assertEquals("rgb(255, 0, 0)", bean.getColorElements().get(1).get("code"));
        assertEquals("red", bean.getColorElements().get(2).get("code"));
    }
}
