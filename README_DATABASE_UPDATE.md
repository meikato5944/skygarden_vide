# データベース更新手順

## contentカラムをLONGTEXTに変更する

### 前提条件
- MySQLが起動していること
- データベース`skygarden`が存在すること
- 適切な権限を持っていること

### 実行前の注意事項
⚠️ **重要**: データベースを変更する前に、必ずバックアップを取得してください。

```bash
# バックアップの取得（例）
mysqldump -u root -p skygarden > skygarden_backup_$(date +%Y%m%d_%H%M%S).sql
```

### 方法1: MySQLコマンドラインから実行（推奨）

```bash
# パスワードが設定されている場合
mysql -u root -p skygarden < update_content_column_to_longtext.sql

# パスワードが設定されていない場合（application.propertiesの設定に基づく）
mysql -u root skygarden < update_content_column_to_longtext.sql
```

### 方法2: MySQLコマンドラインに直接接続して実行

```bash
# MySQLに接続
mysql -u root -p

# データベースを選択
USE skygarden;

# SQLスクリプトの内容を実行
ALTER TABLE `content` 
MODIFY COLUMN `content` LONGTEXT DEFAULT NULL COMMENT 'コンテンツ本文';

ALTER TABLE `content_public` 
MODIFY COLUMN `content` LONGTEXT DEFAULT NULL COMMENT 'コンテンツ本文';

# 変更内容を確認
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE,
    COLUMN_COMMENT
FROM 
    INFORMATION_SCHEMA.COLUMNS
WHERE 
    TABLE_SCHEMA = 'skygarden'
    AND TABLE_NAME IN ('content', 'content_public')
    AND COLUMN_NAME = 'content';
```

### 方法3: MySQL WorkbenchなどのGUIツールから実行

1. MySQL Workbenchを起動
2. `skygarden`データベースに接続
3. `update_content_column_to_longtext.sql`ファイルを開く
4. SQLスクリプトを実行（実行ボタンをクリック）

### 変更内容の確認

実行後、以下のSQLクエリで変更が正しく適用されたか確認できます：

```sql
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE,
    COLUMN_COMMENT
FROM 
    INFORMATION_SCHEMA.COLUMNS
WHERE 
    TABLE_SCHEMA = 'skygarden'
    AND TABLE_NAME IN ('content', 'content_public')
    AND COLUMN_NAME = 'content';
```

期待される結果：
- `COLUMN_TYPE`が`longtext`になっていること

### トラブルシューティング

#### エラー: "Access denied"
- ユーザー名とパスワードを確認してください
- 適切な権限があるか確認してください

#### エラー: "Unknown database 'skygarden'"
- データベースが存在するか確認してください
- `database_schema.sql`を実行してデータベースを作成してください

#### エラー: "Table doesn't exist"
- `content`テーブルと`content_public`テーブルが存在するか確認してください
- `database_schema.sql`を実行してテーブルを作成してください

### ロールバック方法（変更を取り消す場合）

もし変更を取り消したい場合は、以下のSQLを実行してください：

```sql
USE skygarden;

ALTER TABLE `content` 
MODIFY COLUMN `content` TEXT DEFAULT NULL COMMENT 'コンテンツ本文';

ALTER TABLE `content_public` 
MODIFY COLUMN `content` TEXT DEFAULT NULL COMMENT 'コンテンツ本文';
```

⚠️ **注意**: ロールバックする場合、既存のデータがTEXT型のサイズ制限（約64KB）を超えている場合はエラーが発生する可能性があります。
