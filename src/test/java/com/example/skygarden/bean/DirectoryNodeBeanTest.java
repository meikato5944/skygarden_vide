package com.example.skygarden.bean;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * DirectoryNodeBeanのテストクラス
 */
class DirectoryNodeBeanTest {

    private DirectoryNodeBean node;

    @BeforeEach
    void setUp() {
        node = new DirectoryNodeBean();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(node);
        assertNull(node.getName());
        assertNull(node.getPath());
        assertNull(node.getId());
        assertNull(node.getTitle());
        assertFalse(node.isDirectory());
        assertNotNull(node.getChildren());
        assertTrue(node.getChildren().isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        DirectoryNodeBean dirNode = new DirectoryNodeBean("test", "/test", true);
        assertEquals("test", dirNode.getName());
        assertEquals("/test", dirNode.getPath());
        assertTrue(dirNode.isDirectory());
        assertNotNull(dirNode.getChildren());
        assertTrue(dirNode.getChildren().isEmpty());
    }

    @Test
    void testSetAndGetName() {
        node.setName("test.html");
        assertEquals("test.html", node.getName());
    }

    @Test
    void testSetAndGetPath() {
        node.setPath("/about/company");
        assertEquals("/about/company", node.getPath());
    }

    @Test
    void testSetAndGetId() {
        node.setId("123");
        assertEquals("123", node.getId());
    }

    @Test
    void testSetAndGetTitle() {
        node.setTitle("Company Page");
        assertEquals("Company Page", node.getTitle());
    }

    @Test
    void testSetAndGetIsDirectory() {
        node.setDirectory(true);
        assertTrue(node.isDirectory());
        
        node.setDirectory(false);
        assertFalse(node.isDirectory());
    }

    @Test
    void testSetAndGetChildren() {
        List<DirectoryNodeBean> children = new ArrayList<>();
        DirectoryNodeBean child1 = new DirectoryNodeBean("child1", "/child1", false);
        DirectoryNodeBean child2 = new DirectoryNodeBean("child2", "/child2", false);
        children.add(child1);
        children.add(child2);
        
        node.setChildren(children);
        assertEquals(2, node.getChildren().size());
        assertEquals("child1", node.getChildren().get(0).getName());
        assertEquals("child2", node.getChildren().get(1).getName());
    }

    @Test
    void testAddChild() {
        DirectoryNodeBean child = new DirectoryNodeBean("child", "/child", false);
        node.addChild(child);
        
        assertEquals(1, node.getChildren().size());
        assertEquals("child", node.getChildren().get(0).getName());
    }

    @Test
    void testAddMultipleChildren() {
        DirectoryNodeBean child1 = new DirectoryNodeBean("child1", "/child1", false);
        DirectoryNodeBean child2 = new DirectoryNodeBean("child2", "/child2", false);
        DirectoryNodeBean child3 = new DirectoryNodeBean("child3", "/child3", true);
        
        node.addChild(child1);
        node.addChild(child2);
        node.addChild(child3);
        
        assertEquals(3, node.getChildren().size());
    }

    @Test
    void testFindChildDirectory_Exists() {
        DirectoryNodeBean dir1 = new DirectoryNodeBean("dir1", "/dir1", true);
        DirectoryNodeBean dir2 = new DirectoryNodeBean("dir2", "/dir2", true);
        DirectoryNodeBean file1 = new DirectoryNodeBean("file1", "/file1", false);
        
        node.addChild(dir1);
        node.addChild(file1);
        node.addChild(dir2);
        
        DirectoryNodeBean found = node.findChildDirectory("dir1");
        assertNotNull(found);
        assertEquals("dir1", found.getName());
        assertTrue(found.isDirectory());
    }

    @Test
    void testFindChildDirectory_NotExists() {
        DirectoryNodeBean dir1 = new DirectoryNodeBean("dir1", "/dir1", true);
        DirectoryNodeBean file1 = new DirectoryNodeBean("file1", "/file1", false);
        
        node.addChild(dir1);
        node.addChild(file1);
        
        DirectoryNodeBean found = node.findChildDirectory("nonexistent");
        assertNull(found);
    }

    @Test
    void testFindChildDirectory_FileNode() {
        DirectoryNodeBean file1 = new DirectoryNodeBean("file1", "/file1", false);
        node.addChild(file1);
        
        DirectoryNodeBean found = node.findChildDirectory("file1");
        assertNull(found); // ファイルノードは見つからない
    }

    @Test
    void testFindChildDirectory_EmptyChildren() {
        DirectoryNodeBean found = node.findChildDirectory("any");
        assertNull(found);
    }

    @Test
    void testFindChildDirectory_CaseSensitive() {
        DirectoryNodeBean dir1 = new DirectoryNodeBean("Dir1", "/Dir1", true);
        node.addChild(dir1);
        
        DirectoryNodeBean found = node.findChildDirectory("dir1");
        assertNull(found); // 大文字小文字を区別する
    }

    @Test
    void testNullName() {
        node.setName(null);
        assertNull(node.getName());
    }

    @Test
    void testNullPath() {
        node.setPath(null);
        assertNull(node.getPath());
    }

    @Test
    void testNullId() {
        node.setId(null);
        assertNull(node.getId());
    }

    @Test
    void testNullTitle() {
        node.setTitle(null);
        assertNull(node.getTitle());
    }

    @Test
    void testNullChildren() {
        node.setChildren(null);
        assertNull(node.getChildren());
    }

    @Test
    void testEmptyStringValues() {
        node.setName("");
        assertEquals("", node.getName());
        
        node.setPath("");
        assertEquals("", node.getPath());
    }

    @Test
    void testNestedChildren() {
        DirectoryNodeBean parent = new DirectoryNodeBean("parent", "/parent", true);
        DirectoryNodeBean child = new DirectoryNodeBean("child", "/parent/child", true);
        DirectoryNodeBean grandchild = new DirectoryNodeBean("grandchild", "/parent/child/grandchild", false);
        
        child.addChild(grandchild);
        parent.addChild(child);
        
        assertEquals(1, parent.getChildren().size());
        assertEquals(1, child.getChildren().size());
        assertEquals("grandchild", child.getChildren().get(0).getName());
    }
}
