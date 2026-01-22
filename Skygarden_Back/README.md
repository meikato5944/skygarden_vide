# Skygarden Backend

Skygardenのバックエンドシステムです。Spring Bootを使用したRESTful APIサーバーで、コンテンツ管理、ユーザー管理、認証などの機能を提供します。

## 概要

Skygardenは、コンテンツ管理システム（CMS）のバックエンドAPIです。コンテンツ、テンプレート、構成要素の作成・編集・削除、ユーザー管理、認証機能を提供します。

## 技術スタック

- **フレームワーク**: Spring Boot 3.4.1
- **言語**: Java 21
- **データベース**: MySQL
- **ビルドツール**: Gradle
- **主要ライブラリ**:
  - Spring Boot Web
  - Spring Data JPA/JDBC
  - Lombok
  - MySQL Connector

## システム構成

### パッケージ構成

```
com.example.skygarden/
├── Application.java          # アプリケーションエントリーポイント
├── bean/                     # データ転送オブジェクト（DTO）
│   ├── ContentBean.java      # コンテンツ情報
│   ├── ElementItemBean.java  # 構成要素情報
│   ├── ListBean.java         # 一覧情報
│   ├── UserBean.java         # ユーザー情報
│   ├── UserListBean.java     # ユーザー一覧情報
│   └── SettingColorElementsBean.java  # 色設定情報
├── controller/               # REST APIコントローラー
│   ├── ContentController.java    # コンテンツ管理API
│   ├── LoginController.java     # 認証API
│   ├── UserController.java      # ユーザー管理API
│   ├── SettingController.java   # 設定管理API
│   ├── ElementItemController.java  # 構成要素API
│   ├── PreviewController.java   # プレビューAPI
│   ├── BatchController.java     # バッチ処理API
│   └── HomeController.java      # フロントエンドルーティング
├── logic/                    # ビジネスロジック
│   ├── Content.java         # コンテンツ管理ロジック
│   ├── DB.java              # データベースアクセス
│   ├── Login.java           # 認証ロジック
│   ├── User.java            # ユーザー管理ロジック
│   ├── Setting.java         # 設定管理ロジック
│   ├── Batch.java           # バッチ処理ロジック
│   └── CommonProc.java      # 共通処理ユーティリティ
└── config/                  # 設定クラス
    ├── CorsConfig.java      # CORS設定
    └── RequestRoutingFilter.java  # リクエストルーティングフィルター
```

## 主要機能

### 1. コンテンツ管理

- **コンテンツの作成・更新・削除**
  - コンテンツ、テンプレート、構成要素、CSS、JSなどの各種タイプに対応
  - スケジュール公開・非公開機能
  - URL重複チェック機能

- **コンテンツ取得**
  - IDによる検索
  - 一覧取得（ページネーション対応）
  - ソート機能

- **プレビュー機能**
  - テンプレートと構成要素を組み合わせたプレビュー生成

### 2. ユーザー管理

- **ユーザーの作成・更新**
  - ユーザー名、パスワード、メールアドレス、管理者フラグの管理
  - 管理者権限の設定

- **ユーザー一覧取得**
  - ページネーション対応
  - ソート機能

### 3. 認証機能

- **ログイン・ログアウト**
  - セッション管理による認証
  - 認証状態の確認API

### 4. 設定管理

- **構成要素の色設定**
  - 色要素の追加・削除・更新
  - 設定値の保存

### 5. バッチ処理

- **スケジュール公開・非公開**
  - 公開予定日時が到来したコンテンツを自動公開
  - 非公開予定日時が到来したコンテンツを自動非公開

### 6. リクエストルーティング

- **動的コンテンツ配信**
  - URLに基づいて公開テーブルからコンテンツを検索
  - テンプレートと構成要素を組み合わせてHTMLを生成
  - CSS、JSファイルの配信

## API エンドポイント

### コンテンツ管理

- `POST /webadmin/update_post` - コンテンツの作成・更新
- `GET /webadmin/getcontent` - コンテンツ情報の取得
- `GET /webadmin/getlist` - コンテンツ一覧の取得
- `POST /webadmin/delete_post` - コンテンツの削除
- `GET /webadmin/gettemplate` - テンプレート選択用オプション取得
- `GET /webadmin/getelement` - 構成要素選択用オプション取得
- `GET /webadmin/urlmatches` - URL重複チェック
- `POST /webadmin/preview` - プレビュー生成

### ユーザー管理

- `POST /webadmin/user_post` - ユーザーの作成・更新
- `GET /webadmin/getuser` - ユーザー情報の取得
- `GET /webadmin/getlist-user` - ユーザー一覧の取得

### 認証

- `POST /webadmin/login_post` - ログイン
- `GET /webadmin/logout` - ログアウト
- `GET /webadmin/auth` - 認証状態確認

### 設定

- `POST /webadmin/setting_post` - 設定の更新
- `GET /webadmin/get-setting` - 設定情報の取得
- `GET /webadmin/get-session` - セッション属性の取得

### その他

- `GET /webadmin/getElementItem` - 構成要素アイテム一覧取得
- `GET /webadmin/batch` - バッチ処理実行

## データベース構成

### 主要テーブル

- **content**: コンテンツ情報（下書き）
- **content_public**: 公開中のコンテンツ情報
- **user**: ユーザー情報
- **config**: 設定情報

## セットアップ

### 前提条件

- Java 21以上
- MySQL 8.0以上
- Gradle 7.x以上

### データベース設定

1. MySQLでデータベースを作成:
```sql
CREATE DATABASE skygarden;
```

2. `src/main/resources/application.properties` を編集してデータベース接続情報を設定:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/skygarden
spring.datasource.username=root
spring.datasource.password=your_password
```

### ビルドと実行

```bash
# ビルド
./gradlew build

# 実行
./gradlew bootRun
```

サーバーは `http://localhost:8080` で起動します。

## 設定

### フロントエンドパス

`CommonProc.java` でフロントエンドのパスを設定できます:
```java
public static String FRONTEND_PATH = "http://localhost:3000";
```

### CORS設定

`CorsConfig.java` でCORS設定を管理しています。フロントエンドのURLを許可するように設定してください。

## 開発

### プロジェクト構造

- `src/main/java`: Javaソースコード
- `src/main/resources`: 設定ファイル、静的リソース
- `src/test/java`: テストコード

### ログ

Lombokの `@Slf4j` アノテーションを使用してログ出力を行っています。

## ライセンス

このプロジェクトは内部使用を目的としています。
