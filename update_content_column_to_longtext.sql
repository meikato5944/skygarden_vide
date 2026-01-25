-- contentテーブルとcontent_publicテーブルのcontentカラムをTEXTからLONGTEXTに変更
-- これにより、最大4GBまでのCSS/JSコードを保存できるようになります

USE skygarden;

-- contentテーブルのcontentカラムをLONGTEXTに変更
ALTER TABLE `content` 
MODIFY COLUMN `content` LONGTEXT DEFAULT NULL COMMENT 'コンテンツ本文';

-- content_publicテーブルのcontentカラムをLONGTEXTに変更
ALTER TABLE `content_public` 
MODIFY COLUMN `content` LONGTEXT DEFAULT NULL COMMENT 'コンテンツ本文';

-- 変更内容の確認
-- SELECT 
--     TABLE_NAME,
--     COLUMN_NAME,
--     COLUMN_TYPE,
--     COLUMN_COMMENT
-- FROM 
--     INFORMATION_SCHEMA.COLUMNS
-- WHERE 
--     TABLE_SCHEMA = 'skygarden'
--     AND TABLE_NAME IN ('content', 'content_public')
--     AND COLUMN_NAME = 'content';
