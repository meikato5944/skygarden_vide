-- g-test配下のファイルの問題を修正するSQLクエリ

USE skygarden;

-- 1. content_publicテーブルでg-testで始まるすべてのURLを確認
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

-- 2. contentテーブルとcontent_publicテーブルの差分を確認
SELECT 
    c.id,
    c.url,
    c.title,
    c.type,
    c.publishflg_keep as content_publish,
    CASE 
        WHEN cp.id IS NOT NULL THEN 'content_publicに存在'
        ELSE 'content_publicに存在しない'
    END as public_status
FROM 
    content c
LEFT JOIN 
    content_public cp ON c.id = cp.id
WHERE 
    c.url LIKE 'g-test%'
ORDER BY 
    c.url;

-- 3. publishflg_keep=1なのにcontent_publicに存在しないレコードをcontent_publicにコピー
-- 注意: このクエリは手動で実行してください。まず上記のクエリで確認してから実行してください。

-- 例: g-test/app.jsをcontent_publicにコピーする場合
-- INSERT INTO content_public 
-- SELECT * FROM content WHERE url = 'g-test/app.js' AND id NOT IN (SELECT id FROM content_public);

-- 4. URLの修正（style.css → styles.css）
-- 注意: これはURLを変更するので、既存のリンクが壊れる可能性があります。
-- まず、どちらのURLが正しいか確認してください。

-- オプションA: データベースのURLをstyles.cssに変更する場合
-- UPDATE content SET url = 'g-test/styles.css' WHERE url = 'g-test/style.css';
-- UPDATE content_public SET url = 'g-test/styles.css' WHERE url = 'g-test/style.css';

-- オプションB: 新しいstyles.cssを作成する場合（style.cssとは別ファイルとして）
-- これはCMSの編集画面から新規作成してください。
