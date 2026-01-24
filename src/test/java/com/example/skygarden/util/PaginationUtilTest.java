package com.example.skygarden.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * PaginationUtilのテストクラス
 */
class PaginationUtilTest {

    @Test
    void testCalculateTotalPages_NormalCase() {
        // 正常ケース: 85件を20件/ページで表示
        int result = PaginationUtil.calculateTotalPages(85, 20);
        assertEquals(5, result);
    }

    @Test
    void testCalculateTotalPages_ExactDivision() {
        // ちょうど割り切れる場合: 100件を20件/ページで表示
        int result = PaginationUtil.calculateTotalPages(100, 20);
        assertEquals(5, result);
    }

    @Test
    void testCalculateTotalPages_OnePage() {
        // 1ページに収まる場合: 15件を20件/ページで表示
        int result = PaginationUtil.calculateTotalPages(15, 20);
        assertEquals(1, result);
    }

    @Test
    void testCalculateTotalPages_ZeroCount() {
        // 0件の場合
        int result = PaginationUtil.calculateTotalPages(0, 20);
        assertEquals(0, result);
    }

    @Test
    void testCalculateTotalPages_ZeroPageSize() {
        // ページサイズが0の場合
        int result = PaginationUtil.calculateTotalPages(100, 0);
        assertEquals(0, result);
    }

    @Test
    void testCalculateTotalPages_NegativePageSize() {
        // ページサイズが負の数の場合
        int result = PaginationUtil.calculateTotalPages(100, -1);
        assertEquals(0, result);
    }

    @Test
    void testCalculateTotalPages_SingleItem() {
        // 1件のみの場合
        int result = PaginationUtil.calculateTotalPages(1, 20);
        assertEquals(1, result);
    }

    @Test
    void testCalculateOffset_FirstPage() {
        // 1ページ目の場合
        int result = PaginationUtil.calculateOffset(1, 20);
        assertEquals(0, result);
    }

    @Test
    void testCalculateOffset_SecondPage() {
        // 2ページ目の場合
        int result = PaginationUtil.calculateOffset(2, 20);
        assertEquals(20, result);
    }

    @Test
    void testCalculateOffset_ThirdPage() {
        // 3ページ目の場合
        int result = PaginationUtil.calculateOffset(3, 20);
        assertEquals(40, result);
    }

    @Test
    void testCalculateOffset_ZeroPage() {
        // ページ番号が0以下の場合
        int result = PaginationUtil.calculateOffset(0, 20);
        assertEquals(0, result);
    }

    @Test
    void testCalculateOffset_NegativePage() {
        // ページ番号が負の数の場合
        int result = PaginationUtil.calculateOffset(-1, 20);
        assertEquals(0, result);
    }

    @Test
    void testCalculateOffset_LargePage() {
        // 大きなページ番号の場合
        int result = PaginationUtil.calculateOffset(100, 20);
        assertEquals(1980, result);
    }
}
