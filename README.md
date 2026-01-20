# Skygarden CMS（Content Management System）

Skygarden CMSは、Webコンテンツの作成・管理・公開を行うためのコンテンツ管理システムです。
Spring Bootを基盤とし、テンプレートエンジンThymeleafを使用したサーバーサイドレンダリング型のWebアプリケーションです。

## 目次

- [技術スタック](#技術スタック)
- [システム構成](#システム構成)
- [主な機能](#主な機能)
- [プロジェクト構造](#プロジェクト構造)
- [詳細なクラス説明](#詳細なクラス説明)
- [データベース構造](#データベース構造)
- [セットアップ手順](#セットアップ手順)
- [使用方法](#使用方法)
- [API仕様](#api仕様)
- [設定項目](#設定項目)

---

## 技術スタック

| カテゴリ | 技術 | バージョン |
|----------|------|------------|
| **フレームワーク** | Spring Boot | 3.4.1 |
| **言語** | Java | 21 |
| **テンプレートエンジン** | Thymeleaf | - |
| **データベース** | MySQL | 8.0+ |
| **ORM** | MyBatis | 3.0.3 |
| **ビルドツール** | Gradle | 7.x+ |
| **その他** | Lombok | - |

---

## システム構成

```
┌─────────────────────────────────────────────────────────────┐
│                        Client (Browser)                       │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP Request
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Spring Boot Application                    │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              RequestRoutingFilter                      │  │
│  │  （未マッピングURLを公開コンテンツとして処理）          │  │
│  └───────────────────────────────────────────────────────┘  │
│                           │                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                  Controller Layer                      │  │
│  │  ・HomeController（画面ルーティング）                  │  │
│  │  ・ContentController（コンテンツAPI）                  │  │
│  │  ・LoginController（認証API）                          │  │
│  │  ・その他各種Controller                                │  │
│  └───────────────────────────────────────────────────────┘  │
│                           │                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                  Logic Layer (Service)                 │  │
│  │  ・Content（コンテンツ操作）                           │  │
│  │  ・Login（認証処理）                                   │  │
│  │  ・User（ユーザー管理）                                │  │
│  │  ・Setting（設定管理）                                 │  │
│  │  ・Batch（バッチ処理）                                 │  │
│  └───────────────────────────────────────────────────────┘  │
│                           │                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │               Mapper Layer (MyBatis)                   │  │
│  │  ・ContentMapper（DB操作インターフェース）             │  │
│  └───────────────────────────────────────────────────────┘  │
│                           │                                   │
└───────────────────────────┼─────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     MySQL Database                            │
│  ・user（ユーザー情報）                                       │
│  ・content（コンテンツ下書き）                               │
│  ・content_public（公開コンテンツ）                          │
│  ・config（設定情報）                                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 主な機能

### 1. コンテンツ管理

#### 1.1 コンテンツタイプ
| タイプ | 説明 | 用途 |
|--------|------|------|
| **コンテンツ** | 通常のWebページ | ページ本文、記事など |
| **テンプレート** | ページのレイアウト定義 | ヘッダー・フッター・サイドバーの配置 |
| **構成要素** | 再利用可能なパーツ | ヘッダー、フッター、ナビゲーションなど |
| **CSS** | スタイルシート | ページのスタイル定義 |
| **JS** | JavaScript | ページの動的機能 |
| **画像** | 画像ファイル | コンテンツで使用する画像 |
| **ファイル** | ダウンロードファイル | PDF、ドキュメント等 |
| **動画** | YouTube動画 | YouTube埋め込み |

#### 1.2 公開管理
- **即時公開**: 保存時に即時公開
- **スケジュール公開**: 指定日時に自動公開
- **スケジュール非公開**: 指定日時に自動非公開
- **下書き保存**: 公開せずに保存

### 2. テンプレートシステム

テンプレートを使用して、複数のコンテンツで共通のレイアウトを使用できます。

```
テンプレート構成例:
┌─────────────────────────────┐
│     ###element(1)###        │  ← 構成要素（ヘッダー）
├─────────────────────────────┤
│                             │
│       ###content###         │  ← コンテンツ本文
│                             │
├─────────────────────────────┤
│     ###element(2)###        │  ← 構成要素（フッター）
└─────────────────────────────┘
```

### 3. 動画埋め込み機能

コンテンツ内で `[movie id=XXX]` タグを使用してYouTube動画を埋め込めます。

```html
<!-- 基本的な埋め込み（レスポンシブ） -->
[movie id=11]

<!-- サイズ指定付き -->
[movie id=11, width=560px, height=315px]
```

### 4. ユーザー管理

- ユーザーの作成・編集・削除
- 管理者/一般ユーザーの権限管理
- セッションベースの認証

### 5. URLディレクトリ管理

コンテンツをURL階層構造で視覚的に管理できます。

---

## プロジェクト構造

```
skygarden_vide/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/skygarden/
│       │       ├── Application.java          # アプリケーションエントリーポイント
│       │       │
│       │       ├── bean/                     # データ転送オブジェクト（DTO）
│       │       │   ├── ContentBean.java      # コンテンツ情報Bean
│       │       │   ├── DirectoryNodeBean.java # ディレクトリツリーBean
│       │       │   ├── ElementItemBean.java  # 構成要素アイテムBean
│       │       │   ├── ListBean.java         # 一覧表示Bean
│       │       │   ├── SettingColorElementsBean.java # 色設定Bean
│       │       │   ├── UserBean.java         # ユーザー情報Bean
│       │       │   └── UserListBean.java     # ユーザー一覧Bean
│       │       │
│       │       ├── config/                   # 設定クラス
│       │       │   ├── AppProperties.java    # アプリケーション設定プロパティ
│       │       │   ├── CorsConfig.java       # CORS設定
│       │       │   ├── GlobalExceptionHandler.java # グローバル例外ハンドラー
│       │       │   └── RequestRoutingFilter.java # リクエストルーティングフィルター
│       │       │
│       │       ├── constants/                # 定数定義
│       │       │   └── Constants.java        # アプリケーション定数
│       │       │
│       │       ├── controller/               # REST APIコントローラー
│       │       │   ├── BatchController.java  # バッチ処理API
│       │       │   ├── ContentController.java # コンテンツ管理API
│       │       │   ├── ElementItemController.java # 構成要素アイテムAPI
│       │       │   ├── FileController.java   # ファイルアップロードAPI
│       │       │   ├── HomeController.java   # 画面ルーティング
│       │       │   ├── ImageController.java  # 画像アップロードAPI
│       │       │   ├── LoginController.java  # 認証API
│       │       │   ├── MovieController.java  # 動画登録API
│       │       │   ├── PreviewController.java # プレビューAPI
│       │       │   ├── SettingController.java # 設定API
│       │       │   └── UserController.java   # ユーザー管理API
│       │       │
│       │       ├── logic/                    # ビジネスロジック
│       │       │   ├── Batch.java            # バッチ処理ロジック
│       │       │   ├── CommonProc.java       # 共通処理ユーティリティ
│       │       │   ├── Content.java          # コンテンツ管理ロジック
│       │       │   ├── DB.java               # データベースアクセス（レガシー）
│       │       │   ├── Login.java            # ログイン処理ロジック
│       │       │   ├── Setting.java          # 設定管理ロジック
│       │       │   └── User.java             # ユーザー管理ロジック
│       │       │
│       │       ├── mapper/                   # MyBatis Mapper
│       │       │   └── ContentMapper.java    # DB操作インターフェース
│       │       │
│       │       └── util/                     # ユーティリティ
│       │           ├── PaginationUtil.java   # ページネーション計算
│       │           └── ScreenNameConverter.java # 画面名変換
│       │
│       └── resources/
│           ├── application.properties        # アプリケーション設定
│           ├── mapper/
│           │   └── ContentMapper.xml         # MyBatis SQLマッピング
│           ├── static/                       # 静的リソース
│           │   ├── common/                   # 共通リソース
│           │   │   ├── css/common.css        # 共通CSS
│           │   │   └── image/                # 共通画像
│           │   ├── css/                      # CSS・jQuery UI
│           │   └── js/                       # JavaScript・jQuery
│           └── templates/                    # Thymeleafテンプレート
│               ├── content-edit.html         # コンテンツ編集画面
│               ├── element-edit.html         # 構成要素編集画面
│               ├── file-edit.html            # ファイル編集画面
│               ├── fragments/common.html     # 共通フラグメント
│               ├── image-edit.html           # 画像編集画面
│               ├── layout.html               # レイアウト
│               ├── list.html                 # 一覧画面
│               ├── login.html                # ログイン画面
│               ├── movie-edit.html           # 動画編集画面
│               ├── setting.html              # 設定画面
│               ├── template-edit.html        # テンプレート編集画面
│               ├── url-directory.html        # URLディレクトリ画面
│               ├── user-list.html            # ユーザー一覧画面
│               └── user.html                 # ユーザー編集画面
│
├── uploads/                                  # アップロードファイル保存先
│   ├── files/                                # ファイル
│   └── images/                               # 画像
│
├── build.gradle                              # Gradle設定
├── database_schema.sql                       # データベーススキーマ
├── original.html                             # 公開ページテンプレート
├── original.script.html                      # JSファイルテンプレート
├── original.stylesheet.html                  # CSSファイルテンプレート
├── preview.html                              # プレビューテンプレート
└── README.md                                 # このファイル
```

---

## 詳細なクラス説明

### Controller層

| クラス | 説明 | 主なエンドポイント |
|--------|------|-------------------|
| `HomeController` | 画面ルーティング（Thymeleafテンプレートを返す） | `/`, `/login`, `/content`, `/user-list`, `/setting` |
| `ContentController` | コンテンツのCRUD操作API | `/webadmin/update_post`, `/webadmin/getcontent`, `/webadmin/getlist` |
| `LoginController` | 認証処理API | `/webadmin/login_post`, `/webadmin/logout` |
| `UserController` | ユーザー管理API | `/webadmin/user_post`, `/webadmin/getlist-user` |
| `SettingController` | 設定管理API | `/webadmin/setting_post`, `/webadmin/get-setting` |
| `ImageController` | 画像アップロードAPI | `/webadmin/image_upload` |
| `FileController` | ファイルアップロードAPI | `/webadmin/file_upload` |
| `MovieController` | 動画登録API | `/webadmin/movie_register` |
| `PreviewController` | プレビュー生成API | `/webadmin/preview` |
| `BatchController` | バッチ処理実行API | `/webadmin/batch` |
| `ElementItemController` | 構成要素アイテム取得API | `/webadmin/getElementItem` |

### Logic層（ビジネスロジック）

| クラス | 説明 | 主な機能 |
|--------|------|----------|
| `Content` | コンテンツ管理ロジック | 作成、更新、削除、検索、テンプレート適用、動画タグ変換 |
| `Login` | 認証ロジック | ユーザー認証、セッション管理 |
| `User` | ユーザー管理ロジック | ユーザーCRUD、一覧取得、ページネーション |
| `Setting` | 設定管理ロジック | 構成要素色設定の更新・取得 |
| `Batch` | バッチ処理ロジック | スケジュール公開・非公開処理 |
| `CommonProc` | 共通ユーティリティ | ファイル読み込み、日時生成、パス取得 |
| `DB` | データベースアクセス（レガシー） | JDBCによる直接DB操作（MyBatisへ移行中） |

### Bean（DTO）

| クラス | 説明 | 用途 |
|--------|------|------|
| `ContentBean` | コンテンツ情報 | コンテンツ編集画面のデータ転送 |
| `ListBean` | コンテンツ一覧情報 | 一覧画面のデータ転送 |
| `UserBean` | ユーザー情報 | ユーザー編集画面のデータ転送 |
| `UserListBean` | ユーザー一覧情報 | ユーザー一覧画面のデータ転送 |
| `SettingColorElementsBean` | 色設定情報 | 設定画面のデータ転送 |
| `DirectoryNodeBean` | ディレクトリツリーノード | URLディレクトリ表示 |
| `ElementItemBean` | 構成要素アイテム | テンプレート編集時の構成要素選択 |

### Config

| クラス | 説明 |
|--------|------|
| `AppProperties` | application.propertiesから設定値を読み込む |
| `CorsConfig` | CORS設定 |
| `GlobalExceptionHandler` | グローバル例外ハンドラー |
| `RequestRoutingFilter` | 未マッピングURLを公開コンテンツとして処理 |

### Utility

| クラス | 説明 |
|--------|------|
| `PaginationUtil` | ページネーション計算（総ページ数、オフセット） |
| `ScreenNameConverter` | モード文字列と画面名の変換 |
| `Constants` | アプリケーション全体の定数定義 |

---

## データベース構造

### テーブル一覧

```sql
-- 1. user: ユーザー情報
CREATE TABLE user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,      -- ユーザー名
  password VARCHAR(255) NOT NULL,  -- パスワード
  email VARCHAR(255),              -- メールアドレス
  admin VARCHAR(1) DEFAULT '0'     -- 管理者フラグ（1:管理者）
);

-- 2. content: コンテンツ情報（下書き）
CREATE TABLE content (
  id INT AUTO_INCREMENT PRIMARY KEY,
  created VARCHAR(19),             -- 作成日時
  updated VARCHAR(19),             -- 更新日時
  created_by VARCHAR(255),         -- 作成者
  updated_by VARCHAR(255),         -- 更新者
  url VARCHAR(255),                -- URLパス
  title VARCHAR(255),              -- タイトル
  head TEXT,                       -- ヘッダー部分HTML
  content TEXT,                    -- コンテンツ本文
  type VARCHAR(50),                -- コンテンツタイプ
  elementcolor VARCHAR(50),        -- 構成要素の色コード
  template VARCHAR(50),            -- テンプレートID
  schedule_published VARCHAR(19),  -- 公開予定日時
  schedule_unpublished VARCHAR(19),-- 非公開予定日時
  publishflg_keep VARCHAR(1)       -- 公開フラグ
);

-- 3. content_public: 公開中のコンテンツ
-- contentテーブルと同じ構造（公開フラグが'1'の場合に登録）

-- 4. config: 設定情報
CREATE TABLE config (
  name VARCHAR(255) PRIMARY KEY,   -- 設定名
  value TEXT                       -- 設定値
);
```

### コンテンツタイプ（type列の値）

| 値 | コンテンツタイプ |
|----|-----------------|
| `''`（空文字列） | 通常のコンテンツ |
| `template` | テンプレート |
| `element` | 構成要素 |
| `stylesheet` | CSS |
| `script` | JavaScript |
| `image` | 画像 |
| `file` | ファイル |
| `movie` | 動画 |

---

## セットアップ手順

### 前提条件

- Java 21以上
- MySQL 8.0以上
- Gradle 7.x以上

### 1. データベースの作成

```sql
CREATE DATABASE skygarden CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. テーブルの作成

```bash
mysql -u root -p skygarden < database_schema.sql
```

### 3. 設定ファイルの編集

`src/main/resources/application.properties` を編集:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/skygarden?enabledTLSProtocols=TLSv1.2
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. ビルドと実行

```bash
# ビルド
./gradlew build

# 実行
./gradlew bootRun
```

### 5. アクセス

ブラウザで `http://localhost:8080` にアクセス

**初期ログイン情報:**
- ユーザー名: `admin`
- パスワード: `admin`

---

## 使用方法

### コンテンツの作成

1. ログイン後、コンテンツ一覧画面から「新規作成」をクリック
2. タイトル、URL、本文を入力
3. テンプレートを選択（任意）
4. 「保存」または「保存して公開」をクリック

### テンプレートの作成

1. 「テンプレート」タブを選択
2. 「新規作成」をクリック
3. タイトルを入力
4. 構成要素をドラッグ＆ドロップで配置
5. `###content###`（コンテンツ本文の挿入位置）を必ず含める
6. 「保存」をクリック

### 画像のアップロード

1. 「画像」タブを選択
2. 「新規作成」をクリック
3. ファイルを選択、URLを入力
4. 「保存」をクリック

---

## API仕様

### 認証API

| メソッド | URL | 説明 |
|----------|-----|------|
| POST | `/webadmin/login_post` | ログイン |
| GET | `/webadmin/logout` | ログアウト |
| GET | `/webadmin/auth` | 認証状態確認 |

### コンテンツAPI

| メソッド | URL | 説明 |
|----------|-----|------|
| GET | `/webadmin/getcontent?id=XXX&mode=YYY` | コンテンツ取得 |
| POST | `/webadmin/update_post` | コンテンツ作成・更新 |
| POST | `/webadmin/delete_post` | コンテンツ削除 |
| GET | `/webadmin/getlist?mode=XXX&sort=YYY&page=ZZZ` | 一覧取得 |
| GET | `/webadmin/urlmatches?url=XXX&myId=YYY` | URL重複チェック |
| GET | `/webadmin/preview` | プレビュー表示 |

### ユーザーAPI

| メソッド | URL | 説明 |
|----------|-----|------|
| GET | `/webadmin/getuser?id=XXX` | ユーザー取得 |
| POST | `/webadmin/user_post` | ユーザー作成・更新 |
| GET | `/webadmin/getlist-user` | ユーザー一覧取得 |

### ファイルAPI

| メソッド | URL | 説明 |
|----------|-----|------|
| POST | `/webadmin/image_upload` | 画像アップロード |
| POST | `/webadmin/file_upload` | ファイルアップロード |
| POST | `/webadmin/movie_register` | 動画登録 |

---

## 設定項目

### application.properties

| 設定キー | 説明 | デフォルト値 |
|----------|------|-------------|
| `server.port` | サーバーポート | 8080 |
| `server.servlet.session.timeout` | セッションタイムアウト | 30m |
| `spring.datasource.url` | DB接続URL | - |
| `spring.datasource.username` | DBユーザー名 | root |
| `spring.datasource.password` | DBパスワード | - |
| `app.pagination.page-size` | 1ページの表示件数 | 20 |
| `app.file.upload-dir` | 画像アップロード先 | uploads/images |
| `app.file.file-upload-dir` | ファイルアップロード先 | uploads/files |
| `spring.servlet.multipart.max-file-size` | 最大ファイルサイズ | 10MB |

---

## ライセンス

このプロジェクトは内部使用を目的としています。
