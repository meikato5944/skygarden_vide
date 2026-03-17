# データベース更新 - クイックガイド

## 問題: SQLコマンドがシェルで実行されてしまう

シェルプロンプト（`ubuntu@tk2-124-62326:~$`）で直接SQLを実行するとエラーになります。
**MySQLに接続してから**SQLを実行する必要があります。

## 正しい実行手順

### ステップ1: MySQLに接続

```bash
mysql -u root -p
```

パスワードを求められたら、MySQLのrootパスワードを入力してください。
パスワードが設定されていない場合は、`-p`オプションを付けずに：

```bash
mysql -u root
```

### ステップ2: データベースを選択

MySQLに接続できたら、以下のコマンドでデータベースを選択します：

```sql
USE skygarden;
```

### ステップ3: SQLスクリプトを実行

以下のSQLを実行して、contentカラムをLONGTEXTに変更します：

```sql
ALTER TABLE `content` 
MODIFY COLUMN `content` LONGTEXT DEFAULT NULL COMMENT 'コンテンツ本文';

ALTER TABLE `content_public` 
MODIFY COLUMN `content` LONGTEXT DEFAULT NULL COMMENT 'コンテンツ本文';
```

### ステップ4: 変更内容を確認

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

### ステップ5: MySQLから退出

```sql
EXIT;
```

または

```sql
QUIT;
```

## 一括実行方法（SQLファイルを使用）

リモートサーバーでSQLファイルを実行する場合：

```bash
# 1. SQLファイルをサーバーにアップロード（SCPなどで）
# 2. 以下のコマンドで実行
mysql -u root -p skygarden < update_content_column_to_longtext.sql
```

## 実行例（完全な流れ）

```bash
# 1. MySQLに接続
ubuntu@tk2-124-62326:~$ mysql -u root -p
Enter password: [パスワードを入力]

# 2. MySQLプロンプトが表示される
mysql> USE skygarden;
Database changed

# 3. SQLを実行
mysql> ALTER TABLE `content` 
    -> MODIFY COLUMN `content` LONGTEXT DEFAULT NULL COMMENT 'コンテンツ本文';
Query OK, 0 rows affected (0.05 sec)

mysql> ALTER TABLE `content_public` 
    -> MODIFY COLUMN `content` LONGTEXT DEFAULT NULL COMMENT 'コンテンツ本文';
Query OK, 0 rows affected (0.05 sec)

# 4. 確認
mysql> SELECT 
    ->     TABLE_NAME,
    ->     COLUMN_NAME,
    ->     COLUMN_TYPE,
    ->     COLUMN_COMMENT
    -> FROM 
    ->     INFORMATION_SCHEMA.COLUMNS
    -> WHERE 
    ->     TABLE_SCHEMA = 'skygarden'
    ->     AND TABLE_NAME IN ('content', 'content_public')
    ->     AND COLUMN_NAME = 'content';
+----------------+-------------+-----------+------------------+
| TABLE_NAME     | COLUMN_NAME | COLUMN_TYPE | COLUMN_COMMENT |
+----------------+-------------+-----------+------------------+
| content        | content     | longtext  | コンテンツ本文   |
| content_public | content     | longtext  | コンテンツ本文   |
+----------------+-------------+-----------+------------------+
2 rows in set (0.00 sec)

# 5. 退出
mysql> EXIT;
Bye
ubuntu@tk2-124-62326:~$
```

## 注意点

- `mysql>` プロンプトが表示されている時だけSQLコマンドが実行できます
- シェルプロンプト（`ubuntu@tk2-124-62326:~$`）ではSQLコマンドは実行できません
- SQLコマンドの最後にセミコロン（`;`）を忘れずに付けてください
