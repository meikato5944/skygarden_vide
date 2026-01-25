-- g-test配下のファイルのcontentカラムがNULLかどうかを確認するSQLクエリ

USE skygarden;

-- 1. g-test配下のファイルのcontentカラムの状態を確認
SELECT 
    id,
    url,
    title,
    type,
    CASE 
        WHEN content IS NULL THEN 'NULL'
        WHEN content = '' THEN '空文字列'
        ELSE CONCAT('データあり (', LENGTH(content), '文字)')
    END as content_status,
    LEFT(content, 50) as content_preview
FROM 
    content_public
WHERE 
    url LIKE 'g-test%'
ORDER BY 
    url;

-- 2. contentカラムがNULLのレコードを確認
SELECT 
    id,
    url,
    title,
    type,
    'contentカラムがNULLです' as issue
FROM 
    content_public
WHERE 
    url LIKE 'g-test%'
    AND content IS NULL;
