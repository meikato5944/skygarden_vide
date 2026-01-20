-- Skygarden CMS データベーススキーマ
-- データベース: skygarden

-- データベースの作成（既に作成済みの場合はスキップ）
-- CREATE DATABASE IF NOT EXISTS skygarden CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE skygarden;

-- ============================================
-- 1. userテーブル（ユーザー情報）
-- ============================================
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ユーザーID',
  `name` VARCHAR(255) NOT NULL COMMENT 'ユーザー名',
  `password` VARCHAR(255) NOT NULL COMMENT 'パスワード',
  `email` VARCHAR(255) DEFAULT NULL COMMENT 'メールアドレス',
  `admin` VARCHAR(1) DEFAULT '0' COMMENT '管理者フラグ（1:管理者、0:一般ユーザー）',
  INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ユーザー情報テーブル';

-- ============================================
-- 2. contentテーブル（コンテンツ情報 - 下書き）
-- ============================================
CREATE TABLE IF NOT EXISTS `content` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'コンテンツID',
  `created` VARCHAR(19) DEFAULT NULL COMMENT '作成日時（yyyy-MM-dd HH:mm形式）',
  `updated` VARCHAR(19) DEFAULT NULL COMMENT '更新日時（yyyy-MM-dd HH:mm形式）',
  `created_by` VARCHAR(255) DEFAULT NULL COMMENT '作成者',
  `updated_by` VARCHAR(255) DEFAULT NULL COMMENT '更新者',
  `url` VARCHAR(255) DEFAULT NULL COMMENT 'URLパス',
  `title` VARCHAR(255) DEFAULT NULL COMMENT 'タイトル',
  `head` TEXT DEFAULT NULL COMMENT 'ヘッダー部分のHTML',
  `content` TEXT DEFAULT NULL COMMENT 'コンテンツ本文',
  `type` VARCHAR(50) DEFAULT NULL COMMENT 'コンテンツタイプ（空文字列:コンテンツ、template:テンプレート、element:構成要素、stylesheet:CSS、script:JS、image:画像、file:ファイル、movie:動画）',
  `elementcolor` VARCHAR(50) DEFAULT NULL COMMENT '構成要素の色コード',
  `template` VARCHAR(50) DEFAULT NULL COMMENT 'テンプレートID',
  `schedule_published` VARCHAR(19) DEFAULT NULL COMMENT '公開予定日時（yyyy-MM-dd HH:mm形式）',
  `schedule_unpublished` VARCHAR(19) DEFAULT NULL COMMENT '非公開予定日時（yyyy-MM-dd HH:mm形式）',
  `publishflg_keep` VARCHAR(1) DEFAULT NULL COMMENT '公開フラグ（1:公開、0:非公開）',
  INDEX `idx_type` (`type`),
  INDEX `idx_url` (`url`),
  INDEX `idx_schedule_published` (`schedule_published`),
  INDEX `idx_schedule_unpublished` (`schedule_unpublished`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='コンテンツ情報テーブル（下書き）';

-- ============================================
-- 3. content_publicテーブル（公開中のコンテンツ情報）
-- ============================================
CREATE TABLE IF NOT EXISTS `content_public` (
  `id` INT PRIMARY KEY COMMENT 'コンテンツID（contentテーブルのidと同じ）',
  `created` VARCHAR(19) DEFAULT NULL COMMENT '作成日時（yyyy-MM-dd HH:mm形式）',
  `updated` VARCHAR(19) DEFAULT NULL COMMENT '更新日時（yyyy-MM-dd HH:mm形式）',
  `created_by` VARCHAR(255) DEFAULT NULL COMMENT '作成者',
  `updated_by` VARCHAR(255) DEFAULT NULL COMMENT '更新者',
  `url` VARCHAR(255) DEFAULT NULL COMMENT 'URLパス',
  `title` VARCHAR(255) DEFAULT NULL COMMENT 'タイトル',
  `head` TEXT DEFAULT NULL COMMENT 'ヘッダー部分のHTML',
  `content` TEXT DEFAULT NULL COMMENT 'コンテンツ本文',
  `type` VARCHAR(50) DEFAULT NULL COMMENT 'コンテンツタイプ',
  `elementcolor` VARCHAR(50) DEFAULT NULL COMMENT '構成要素の色コード',
  `template` VARCHAR(50) DEFAULT NULL COMMENT 'テンプレートID',
  `schedule_published` VARCHAR(19) DEFAULT NULL COMMENT '公開予定日時（yyyy-MM-dd HH:mm形式）',
  `schedule_unpublished` VARCHAR(19) DEFAULT NULL COMMENT '非公開予定日時（yyyy-MM-dd HH:mm形式）',
  `publishflg_keep` VARCHAR(1) DEFAULT NULL COMMENT '公開フラグ（1:公開、0:非公開）',
  INDEX `idx_type` (`type`),
  INDEX `idx_url` (`url`),
  FOREIGN KEY (`id`) REFERENCES `content`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公開中のコンテンツ情報テーブル';

-- ============================================
-- 4. configテーブル（設定情報）
-- ============================================
CREATE TABLE IF NOT EXISTS `config` (
  `name` VARCHAR(255) PRIMARY KEY COMMENT '設定名',
  `value` TEXT DEFAULT NULL COMMENT '設定値'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='設定情報テーブル';

-- ============================================
-- 初期データの投入
-- ============================================

-- 初期ユーザーの作成（管理者）
-- パスワードは平文で保存されているため、必要に応じて変更してください
INSERT INTO `user` (`name`, `password`, `email`, `admin`) 
VALUES ('admin', 'admin', 'admin@example.com', '1')
ON DUPLICATE KEY UPDATE `name`=`name`;

-- 設定の初期値（構成要素の色設定）
INSERT INTO `config` (`name`, `value`) 
VALUES ('elements-color-value', 'header=#000000*footer=#333333*')
ON DUPLICATE KEY UPDATE `name`=`name`;

-- ============================================
-- 補足説明
-- ============================================
-- 
-- 1. 日時形式について
--    created, updated, schedule_published, schedule_unpublished は
--    文字列形式（VARCHAR(19)）で "yyyy-MM-dd HH:mm" 形式で保存されます
--    例: "2024-01-18 15:30"
--
-- 2. typeカラムの値
--    - 空文字列(''): 通常のコンテンツ
--    - 'template': テンプレート
--    - 'element': 構成要素
--    - 'stylesheet': CSS
--    - 'script': JavaScript
--    - 'image': 画像
--    - 'file': ファイル
--    - 'movie': 動画
--
-- 3. adminカラムの値
--    - '1': 管理者
--    - '0': 一般ユーザー
--
-- 4. publishflg_keepカラムの値
--    - '1': 公開
--    - '0': 非公開
--
-- 5. content_publicテーブルについて
--    contentテーブルの公開版です。公開フラグが'1'の場合、
--    このテーブルにも同じデータが登録されます。
