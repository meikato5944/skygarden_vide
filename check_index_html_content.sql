-- g-test/index.htmlのcontentカラムの内容を確認するSQLクエリ

USE skygarden;

-- 1. g-test/index.htmlのcontentカラム全体を確認
SELECT 
    id,
    url,
    title,
    content
FROM 
    content_public
WHERE 
    url = 'g-test/index.html';

-- 2. contentカラム内に"null"という文字列が含まれているか確認
SELECT 
    id,
    url,
    title,
    CASE 
        WHEN content LIKE '%null%' THEN 'nullという文字列が含まれています'
        ELSE 'nullという文字列は含まれていません'
    END as null_check,
    LOCATE('null', content) as null_position
FROM 
    content_public
WHERE 
    url = 'g-test/index.html';
