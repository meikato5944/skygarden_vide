-- g-test/app.js のデータベース状態を確認するSQLクエリ

USE skygarden;

-- 1. g-test/app.js というURLでcontent_publicテーブルを検索
SELECT 
    id,
    url,
    title,
    type,
    LENGTH(content) as content_length,
    LEFT(content, 100) as content_preview,
    publishflg_keep,
    created,
    updated
FROM 
    content_public
WHERE 
    url = 'g-test/app.js'
    OR url LIKE 'g-test/app.js%';

-- 2. g-test で始まるすべてのURLを検索
SELECT 
    id,
    url,
    title,
    type,
    publishflg_keep
FROM 
    content_public
WHERE 
    url LIKE 'g-test%'
ORDER BY 
    url;

-- 3. scriptタイプのすべてのコンテンツを確認
SELECT 
    id,
    url,
    title,
    type,
    LENGTH(content) as content_length,
    publishflg_keep
FROM 
    content_public
WHERE 
    type = 'script'
ORDER BY 
    updated DESC
LIMIT 10;

-- 4. contentテーブルとcontent_publicテーブルのcontentカラムの型を確認
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM 
    INFORMATION_SCHEMA.COLUMNS
WHERE 
    TABLE_SCHEMA = 'skygarden'
    AND TABLE_NAME IN ('content', 'content_public')
    AND COLUMN_NAME = 'content';
