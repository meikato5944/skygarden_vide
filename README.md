# Skygarden CMS（Content Management System）

Skygarden CMSは、Webコンテンツの作成・管理・公開を行うためのコンテンツ管理システムです。
Spring Bootを基盤とし、テンプレートエンジンThymeleafを使用したサーバーサイドレンダリング型のWebアプリケーションです。

## 目次

- [技術スタック](#技術スタック)
- [システム構成](#システム構成)
- [完全な機能仕様](#完全な機能仕様)
- [処理フロー詳細](#処理フロー詳細)
- [プロジェクト構造](#プロジェクト構造)
- [詳細なクラス説明](#詳細なクラス説明)
- [データベース構造](#データベース構造)
- [API仕様詳細](#api仕様詳細)
- [セットアップ手順](#セットアップ手順)
- [使用方法](#使用方法)
- [設定項目](#設定項目)
- [セキュリティ](#セキュリティ)
- [エラーハンドリング](#エラーハンドリング)
- [テスト要件](#テスト要件)

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
| **メール送信** | Spring Mail | - |
| **HTTP クライアント** | Java HTTP Client | - |

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
│  │  ・UserController（ユーザー管理API）                    │  │
│  │  ・SettingController（設定API）                         │  │
│  │  ・ImageController（画像API）                          │  │
│  │  ・FileController（ファイルAPI）                        │  │
│  │  ・MovieController（動画API）                          │  │
│  │  ・PreviewController（プレビューAPI）                   │  │
│  │  ・BatchController（バッチAPI）                        │  │
│  │  ・ElementItemController（構成要素API）                │  │
│  └───────────────────────────────────────────────────────┘  │
│                           │                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                  Logic Layer (Service)                 │  │
│  │  ・Content（コンテンツ操作）                           │  │
│  │  ・Login（認証処理）                                   │  │
│  │  ・User（ユーザー管理）                                │  │
│  │  ・Setting（設定管理）                                 │  │
│  │  ・Batch（バッチ処理）                                 │  │
│  │  ・CommonProc（共通処理）                              │  │
│  └───────────────────────────────────────────────────────┘  │
│                           │                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │               Service Layer                            │  │
│  │  ・OpenAIService（AI生成）                              │  │
│  │  ・EmailService（メール送信）                          │  │
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

## 完全な機能仕様

### 1. 認証機能

#### 1.1 ログイン機能
- **エンドポイント**: `POST /webadmin/login_post`
- **パラメータ**: `name`（ユーザー名）、`password`（パスワード）
- **処理フロー**:
  1. ユーザー名でデータベースからユーザー情報を取得
  2. ユーザーが存在しない場合: エラーメッセージと共にログイン画面へリダイレクト
  3. パスワードを比較（現状は平文比較）
  4. 認証成功: セッションに以下を保存し、トップページへリダイレクト
     - `loginName`: ログインユーザー名（表示用）
     - `name`: ユーザー名（識別用）
     - `admin`: 管理者フラグ（"1":管理者、"0":一般ユーザー）
  5. 認証失敗: エラーメッセージと共にログイン画面へリダイレクト
- **セッション管理**: HTTPセッションを使用（タイムアウト: 30分）
- **セキュリティ注意**: パスワードは現状平文で保存・比較（本番環境ではハッシュ化を推奨）

#### 1.2 ログアウト機能
- **エンドポイント**: `GET /webadmin/logout`
- **処理**: セッションを無効化し、ログイン画面へリダイレクト

#### 1.3 認証状態確認
- **エンドポイント**: `GET /webadmin/auth`
- **戻り値**: 認証済みの場合`true`、未認証の場合`false`

### 2. コンテンツ管理機能

#### 2.1 コンテンツタイプ
| タイプ | 説明 | 用途 | データベース格納 |
|--------|------|------|-----------------|
| **コンテンツ** | 通常のWebページ | ページ本文、記事など | `type = ''`（空文字列） |
| **テンプレート** | ページのレイアウト定義 | ヘッダー・フッター・サイドバーの配置 | `type = 'template'` |
| **構成要素** | 再利用可能なパーツ | ヘッダー、フッター、ナビゲーションなど | `type = 'element'` |
| **CSS** | スタイルシート | ページのスタイル定義 | `type = 'stylesheet'` |
| **JS** | JavaScript | ページの動的機能 | `type = 'script'` |
| **画像** | 画像ファイル | コンテンツで使用する画像 | `type = 'image'` |
| **ファイル** | ダウンロードファイル | PDF、ドキュメント等 | `type = 'file'` |
| **動画** | YouTube動画 | YouTube埋め込み | `type = 'movie'` |

#### 2.2 コンテンツ作成・更新
- **エンドポイント**: `POST /webadmin/update_post`
- **パラメータ**:
  - `id`: コンテンツID（空の場合は新規作成）
  - `url`: URLパス（先頭スラッシュは自動除去）
  - `title`: タイトル
  - `head`: ヘッダー部分HTML
  - `content`: コンテンツ本文
  - `type`: コンテンツタイプ
  - `template`: テンプレートID（コンテンツタイプが空文字列の場合のみ）
  - `elementcolor`: 構成要素の色コード（構成要素の場合のみ）
  - `published`: 公開フラグ（"1":公開、"0":非公開）
  - `schedule_published`: 公開予定日時（形式: "yyyy-MM-dd HH:mm"）
  - `schedule_unpublished`: 非公開予定日時（形式: "yyyy-MM-dd HH:mm"）
- **処理フロー**:
  1. URLの先頭スラッシュを除去
  2. スケジュール日時の形式変換（"/"を"-"に、"T"を" "に）
  3. スケジュール日時が過去または現在の場合、空文字列に設定
  4. 新規作成の場合:
     - `content`テーブルに登録
     - 公開フラグが"1"かつスケジュール公開日時が空の場合、`content_public`テーブルにも登録
     - 初回公開の場合、メール通知を送信（設定されている場合）
  5. 更新の場合:
     - `content`テーブルを更新
     - 公開フラグが"1"かつスケジュール公開日時が空の場合:
       - `content_public`テーブルに既に存在する場合は更新
       - 存在しない場合は新規作成（初回公開としてメール通知）
  6. リダイレクト先URLを決定（コンテンツタイプに応じて）
- **エラーハンドリング**: 例外発生時はセッションにエラーメッセージを保存

#### 2.3 コンテンツ取得
- **エンドポイント**: `GET /webadmin/getcontent?id=XXX&mode=YYY`
- **パラメータ**:
  - `id`: コンテンツID
  - `mode`: モード（空文字列:コンテンツ、template:テンプレート、element:構成要素、stylesheet:CSS、script:JS）
- **戻り値**: `ContentBean`（コンテンツ情報、テンプレート選択肢、色選択肢など）

#### 2.4 コンテンツ削除
- **エンドポイント**: `POST /webadmin/delete_post`
- **パラメータ**: `id`（削除するコンテンツID）、`mode`（リダイレクト先決定用）
- **処理**: `content`テーブルと`content_public`テーブルから削除

#### 2.5 コンテンツ一覧取得
- **エンドポイント**: `GET /webadmin/getlist?mode=XXX&sort=YYY&page=ZZZ&keyword=AAA`
- **パラメータ**:
  - `mode`: コンテンツタイプ（フィルタリング用）
  - `sort`: ソート順（"updated desc", "id", "title"など）
  - `page`: ページ番号（デフォルト: 1）
  - `keyword`: 検索キーワード（オプション、タイトル・URL・本文を対象に部分一致検索）
- **戻り値**: `ListBean`（コンテンツ一覧、ページネーションHTML、ソート選択肢など）
- **ページネーション**: 1ページあたりの表示件数は`app.pagination.page-size`で設定（デフォルト: 20）

#### 2.6 URL重複チェック
- **エンドポイント**: `GET /webadmin/urlmatches?url=XXX&myId=YYY`
- **戻り値**: URLが重複している場合`true`、重複していない場合`false`
- **用途**: コンテンツ編集画面でURLの重複をチェック

#### 2.7 キーワード検索
- **対象フィールド**: タイトル、URL、コンテンツ本文
- **検索方法**: 部分一致検索（LIKE）
- **ページネーション**: 通常の一覧取得と同様に対応

### 3. テンプレートシステム

#### 3.1 テンプレートの構造
- **保存形式**: テンプレートの`content`フィールドに構成要素の配置がカンマ区切りで保存
- **形式**: `"###element(ID),###content###,###element(ID)"`
  - `###element(ID)`: 構成要素のID
  - `###content###`: コンテンツ本文の挿入位置（必須）
- **例**: `"###element(1),###content###,###element(2)"`
  - 構成要素1（ヘッダー）→ コンテンツ本文 → 構成要素2（フッター）

#### 3.2 テンプレート適用処理
1. コンテンツに紐づくテンプレートIDを取得
2. テンプレートの`content`フィールドをカンマで分割
3. 各要素を処理:
   - `###content###`の場合: コンテンツ本文を挿入
   - `###element(ID)`の場合: 構成要素の`content`を取得して挿入
4. 最終的なHTMLを生成

#### 3.3 テンプレートヘッダー
- テンプレートの`head`フィールドは、コンテンツの`head`と結合されて使用される
- 結合順序: テンプレートヘッダー + コンテンツヘッダー

### 4. 構成要素システム

#### 4.1 構成要素の色設定
- **設定形式**: `config`テーブルの`elements-color-value`に`"name=code*name=code*"`形式で保存
- **例**: `"header=#000000*footer=#333333*"`
- **用途**: テンプレート編集画面で構成要素を視覚的に区別

#### 4.2 構成要素の取得
- **エンドポイント**: `GET /webadmin/getElementItem`
- **戻り値**: すべての構成要素の一覧

### 5. 画像管理機能

#### 5.1 画像アップロード
- **エンドポイント**: `POST /webadmin/image_upload`
- **パラメータ**:
  - `file`: 画像ファイル（MultipartFile）
  - `id`: コンテンツID（空の場合は新規作成）
  - `title`: 画像タイトル
  - `url`: 画像のURL（物理パス）
  - `imageWidth`: 画像の幅（オプション）
  - `imageHeight`: 画像の高さ（オプション）
  - `published`: 公開フラグ
  - `schedule_published`: 公開予定日時
  - `schedule_unpublished`: 非公開予定日時
- **処理フロー**:
  1. 画像ファイルのMIMEタイプをチェック（`image/*`のみ許可）
  2. ファイル名を生成（UUID + 元の拡張子）
  3. アップロードディレクトリ（`app.file.upload-dir`）に保存
  4. サイズ情報をJSON形式で`head`フィールドに保存（例: `{"width":"560px","height":"315px"}`）
  5. データベースに登録（`content`フィールドに保存されたファイル名を格納）
  6. 公開フラグが"1"の場合、`content_public`テーブルにも登録
- **保存先**: `uploads/images/`（デフォルト）

#### 5.2 画像配信
- **処理**: `RequestRoutingFilter`が`content_public`テーブルから画像情報を取得し、ファイルシステムから画像を配信
- **MIMEタイプ**: ファイル拡張子から自動判定
- **キャッシュ**: 1日間（`Cache-Control: public, max-age=86400`）

### 6. ファイル管理機能

#### 6.1 ファイルアップロード
- **エンドポイント**: `POST /webadmin/file_upload`
- **パラメータ**:
  - `file`: ファイル（MultipartFile、任意の形式）
  - `id`: コンテンツID（空の場合は新規作成）
  - `title`: ファイルタイトル
  - `url`: ファイルのURL（物理パス）
  - `published`: 公開フラグ
  - `schedule_published`: 公開予定日時
  - `schedule_unpublished`: 非公開予定日時
- **処理フロー**:
  1. ファイル名を生成（UUID + 元の拡張子）
  2. アップロードディレクトリ（`app.file.file-upload-dir`）に保存
  3. データベースに登録:
     - `head`フィールド: 元のファイル名（ダウンロード時のファイル名として使用）
     - `content`フィールド: 保存されたファイル名（UUID形式）
  4. 公開フラグが"1"の場合、`content_public`テーブルにも登録
- **保存先**: `uploads/files/`（デフォルト）

#### 6.2 ファイルダウンロード
- **処理**: `RequestRoutingFilter`が`content_public`テーブルからファイル情報を取得し、ファイルシステムからファイルを配信
- **Content-Disposition**: `attachment`として設定（ダウンロード）
- **日本語ファイル名対応**: RFC 5987形式でエンコード

### 7. 動画管理機能

#### 7.1 動画登録
- **エンドポイント**: `POST /webadmin/movie_register`
- **パラメータ**:
  - `id`: コンテンツID（空の場合は新規作成）
  - `title`: 動画タイトル
  - `youtubeUrl`: YouTube動画のURL
  - `movieWidth`: 動画の幅（オプション）
  - `movieHeight`: 動画の高さ（オプション）
  - `published`: 公開フラグ
  - `schedule_published`: 公開予定日時
  - `schedule_unpublished`: 非公開予定日時
- **処理フロー**:
  1. YouTube URLからビデオIDを抽出
     - 対応形式:
       - `https://www.youtube.com/watch?v=VIDEO_ID`
       - `https://youtu.be/VIDEO_ID`
       - `https://www.youtube.com/embed/VIDEO_ID`
       - ビデオIDのみ（11文字の英数字）
  2. サイズ情報をJSON形式で`head`フィールドに保存
  3. データベースに登録:
     - `url`フィールド: YouTube URL（フルURL）
     - `content`フィールド: YouTubeビデオID
     - `head`フィールド: サイズ情報（JSON形式）
  4. 公開フラグが"1"の場合、`content_public`テーブルにも登録

#### 7.2 動画埋め込みタグ変換
- **タグ形式**: `[movie id=XXX]` または `[movie id=XXX, width=XXX, height=XXX]`
- **変換処理**: `Content.convertMovieTags()`メソッドで処理
- **変換結果**:
  - サイズ指定がない場合: レスポンシブ（16:9比率）のiframe
  - サイズ指定がある場合: 固定サイズのiframe
- **使用場所**: コンテンツ本文、プレビュー、公開ページ

### 8. スケジュール公開・非公開機能

#### 8.1 スケジュール公開
- **設定方法**: コンテンツ作成・更新時に`schedule_published`に未来の日時を指定
- **処理**: `Batch.publishedBatch()`メソッドで実行
- **処理フロー**:
  1. `schedule_published`が現在時刻以前のコンテンツを検索
  2. 該当コンテンツを`content_public`テーブルに登録（既存の場合は更新）
  3. `content`テーブルの`schedule_published`をクリア
  4. 初回公開の場合、メール通知を送信（設定されている場合）
- **実行方法**: `GET /webadmin/batch`エンドポイントを呼び出す（cronジョブなどから定期実行）

#### 8.2 スケジュール非公開
- **設定方法**: コンテンツ作成・更新時に`schedule_unpublished`に未来の日時を指定
- **処理**: `Batch.unPublishedBatch()`メソッドで実行
- **処理フロー**:
  1. `schedule_unpublished`が現在時刻以前のコンテンツを検索
  2. 該当コンテンツを`content_public`テーブルから削除
  3. `content`テーブルの`schedule_unpublished`をクリア
- **実行方法**: `GET /webadmin/batch`エンドポイントを呼び出す（cronジョブなどから定期実行）

#### 8.3 バッチ処理実行
- **エンドポイント**: `GET /webadmin/batch`
- **処理**: スケジュール公開とスケジュール非公開の両方を実行
- **推奨実行頻度**: 1分ごと（cron: `0 * * * * curl http://localhost:8080/webadmin/batch`）

### 9. プレビュー機能

#### 9.1 プレビュー生成
- **エンドポイント**: `GET /webadmin/preview` または `POST /webadmin/preview`
- **パラメータ**:
  - `template`: テンプレートID
  - `title`: タイトル
  - `head`: ヘッダー部分のHTML
  - `content`: コンテンツ本文
- **処理フロー**:
  1. テンプレートが指定されている場合、テンプレートのヘッダー情報を取得
  2. コンテンツ本文にテンプレートと構成要素を適用（`Content.previewContent()`）
  3. `[movie id=XXX]`タグをYouTube埋め込みコードに変換
  4. `preview.html`テンプレートに以下を挿入:
     - `###title###`: タイトル
     - `###head###`: ヘッダー部分（テンプレートヘッダー + コンテンツヘッダー）
     - `###content###`: コンテンツ本文（テンプレート・構成要素適用後）
  5. 完成したHTMLをレスポンスとして返却

#### 9.2 プレビュー変換API
- **エンドポイント**: `POST /webadmin/convert_preview`
- **パラメータ**: `contentText`（変換対象のコンテンツ）
- **戻り値**: 変換後のHTML（動画タグ変換のみ）

### 10. ユーザー管理機能

#### 10.1 ユーザー作成・更新
- **エンドポイント**: `POST /webadmin/user_post`
- **パラメータ**:
  - `id`: ユーザーID（空の場合は新規作成）
  - `name`: ユーザー名
  - `password`: パスワード
  - `email`: メールアドレス
  - `admin`: 管理者フラグ（"1":管理者、"0":一般ユーザー）
- **処理**: `content`テーブルに登録・更新

#### 10.2 ユーザー取得
- **エンドポイント**: `GET /webadmin/getuser?id=XXX`
- **戻り値**: `UserBean`（ユーザー情報）

#### 10.3 ユーザー一覧取得
- **エンドポイント**: `GET /webadmin/getlist-user?sort=XXX&page=YYY`
- **パラメータ**:
  - `sort`: ソート順（"id", "id desc", "name", "name desc"）
  - `page`: ページ番号（デフォルト: 1）
- **戻り値**: `UserListBean`（ユーザー一覧、ページネーションHTML、ソート選択肢など）

### 11. 設定管理機能

#### 11.1 設定更新
- **エンドポイント**: `POST /webadmin/setting_post`
- **パラメータ**:
  - `elements-color-value`: 構成要素の色設定（"name=code*name=code*"形式）
  - `default-publish-on`: デフォルト公開設定（"1":オン、"0":オフ）
  - `openai-api-key`: OpenAI APIキー
  - `openai-model`: OpenAIモデル（デフォルト: "gpt-3.5-turbo"）
  - `openai-prompt-title`: タイトル生成用プロンプト
  - `openai-prompt-content`: 本文生成用プロンプト
  - `ai-generation-visible`: AI生成ボタンの表示/非表示（"1":表示、"0":非表示）
  - `email-enabled`: メール機能の有効/無効（"1":有効、"0":無効）
  - `email-to`: 送信先メールアドレス（カンマ区切りで複数指定可能）
  - `email-from`: 送信元メールアドレス
  - `email-body-template`: メール本文テンプレート
  - `email-base-url`: メール内のURL生成用ベースURL
- **処理**: `config`テーブルに保存

#### 11.2 設定取得
- **エンドポイント**: `GET /webadmin/get-setting`
- **戻り値**: `SettingColorElementsBean`（構成要素の色設定リスト）

### 12. AI生成機能

#### 12.1 タイトル生成
- **エンドポイント**: `POST /webadmin/api/generate-title`
- **パラメータ**: `userInput`（ユーザーが入力した情報）
- **処理フロー**:
  1. 設定からOpenAI APIキー、モデル、プロンプトを取得
  2. プロンプトの`{userInput}`を実際の入力値に置換
  3. OpenAI APIを呼び出し
  4. レスポンスから生成されたタイトルを抽出
  5. マークダウンのコードブロック記号（```）を前後から削除
  6. 結果を返却
- **エラーハンドリング**:
  - APIキー未設定: エラーメッセージを返却
  - タイムアウト（60秒）: エラーメッセージを返却
  - HTTPエラー: エラーメッセージを返却
  - JSON解析エラー: エラーメッセージを返却

#### 12.2 本文生成
- **エンドポイント**: `POST /webadmin/api/generate-content`
- **パラメータ**: `userInput`（ユーザーが入力した情報）
- **処理フロー**: タイトル生成と同様（プロンプトが異なる）

#### 12.3 OpenAI API呼び出し詳細
- **エンドポイント**: `https://api.openai.com/v1/chat/completions`
- **タイムアウト**: 60秒
- **モデル対応**:
  - 新モデル（o-series、GPT-5など）: `max_completion_tokens`を使用
  - 従来モデル（GPT-3.5、GPT-4など）: `max_tokens`と`temperature`を使用
- **リクエスト形式**: JSON（`model`, `messages`, `max_tokens`/`max_completion_tokens`, `temperature`）

### 13. メール通知機能

#### 13.1 コンテンツ公開通知
- **トリガー**: コンテンツが初回公開された時
- **送信タイミング**:
  - コンテンツ作成時に即時公開
  - コンテンツ更新時に初回公開
  - バッチ処理でスケジュール公開
- **処理フロー**:
  1. メール機能が有効かチェック（`email-enabled`設定）
  2. 送信先メールアドレスを取得（`email-to`設定）
  3. メールアドレスの形式を検証
  4. メール本文テンプレートを取得（`email-body-template`設定）
  5. プレースホルダーを置換:
     - `###title###`: コンテンツタイトル
     - `###url###`: コンテンツのフルURL（`email-base-url` + URL）
     - `###publish_date###`: 公開日時（"yyyy-MM-dd HH:mm"形式）
  6. メールを送信
  7. エラー発生時はエラーメッセージを返却（コンテンツ登録は成功）
- **エラーハンドリング**:
  - メール機能が無効: 送信をスキップ
  - 送信先未設定: エラーメッセージを返却
  - メールアドレス形式不正: エラーメッセージを返却
  - 通信エラー: エラーメッセージを返却
  - その他のエラー: エラーメッセージを返却

### 14. URLディレクトリ機能

#### 14.1 URL階層構造表示
- **エンドポイント**: `/url-directory`（画面）
- **処理**: コンテンツをURL階層構造のツリーとして取得
- **処理フロー**:
  1. 指定タイプのコンテンツをURLでソートして取得
  2. URLが空のコンテンツは除外
  3. URLをパス部分に分割（"/"で区切る）
  4. ツリー構造を構築:
     - 中間のパート: ディレクトリノード
     - 最後のパート: ファイルノード（コンテンツIDとタイトルを含む）
  5. ルートノードから再帰的にツリーを構築
- **表示タイプ**: コンテンツ、テンプレート、画像、ファイル、CSS、JS、動画

### 15. 公開ページ配信機能

#### 15.1 リクエストルーティング
- **処理**: `RequestRoutingFilter`が全てのHTTPリクエストをインターセプト
- **処理フロー**:
  1. `/webadmin/**`パスは常にコントローラーへ転送（早期リターン）
  2. Spring MVCのハンドラーマッピングをチェック
     - マッピングが存在する場合: フィルターチェーンを継続
  3. `content_public`テーブルでURLを検索
     - コンテンツが見つかった場合: タイプに応じてレスポンスを生成
     - 見つからない場合: 404エラー処理へ

#### 15.2 コンテンツタイプ別の配信処理
- **通常コンテンツ（空文字列）**:
  - `original.html`をテンプレートとして使用
  - プレースホルダーを置換:
    - `###title###`: コンテンツタイトル
    - `###head###`: ヘッダー部分（テンプレートヘッダー + コンテンツヘッダー）
    - `###content###`: コンテンツ本文（テンプレート・構成要素適用後、動画タグ変換後）
  - Content-Type: `text/html; charset=UTF-8`
- **CSS（stylesheet）**:
  - `original.stylesheet.html`をテンプレートとして使用
  - Content-Type: `text/css`
- **JavaScript（script）**:
  - `original.script.html`をテンプレートとして使用
  - Content-Type: `application/javascript`
- **画像（image）**:
  - ファイルシステムから画像を読み込み
  - MIMEタイプを自動判定
  - キャッシュヘッダーを設定（1日間）
- **ファイル（file）**:
  - ファイルシステムからファイルを読み込み
  - Content-Disposition: `attachment`（ダウンロード）
  - 日本語ファイル名対応（RFC 5987）

---

## 処理フロー詳細

### コンテンツ作成フロー

```
1. ユーザーがコンテンツ編集画面で情報を入力
   ↓
2. POST /webadmin/update_post を送信
   ↓
3. ContentController.update() が呼び出される
   ↓
4. Content.doCreate() が実行される
   ↓
5. URLの先頭スラッシュを除去
   ↓
6. スケジュール日時の形式変換・検証
   ↓
7. ContentMapper.create() で content テーブルに登録
   ↓
8. 公開フラグが"1"かつスケジュール公開日時が空の場合:
   - ContentMapper.createPublic() で content_public テーブルに登録
   - 初回公開の場合、EmailService.sendContentPublishedNotification() を呼び出し
   ↓
9. リダイレクト先URLを決定してリダイレクト
```

### コンテンツ表示フロー

```
1. ユーザーがブラウザでURLにアクセス
   ↓
2. RequestRoutingFilter がリクエストをインターセプト
   ↓
3. /webadmin/** パスでないことを確認
   ↓
4. Spring MVCのハンドラーマッピングをチェック
   - マッピングが存在する場合: 通常のコントローラー処理へ
   ↓
5. ContentMapper.searchByUrl() で content_public テーブルからコンテンツを検索
   ↓
6. コンテンツが見つかった場合:
   - タイプに応じて処理を分岐
   - 通常コンテンツの場合:
     * Content.getTemplateHead() でテンプレートヘッダーを取得
     * Content.getHead() でコンテンツヘッダーを取得
     * Content.displayContent() でコンテンツ本文を取得（テンプレート・構成要素適用、動画タグ変換）
     * original.html を読み込み
     * プレースホルダーを置換
     * HTMLをレスポンスとして返却
   - 画像・ファイルの場合:
     * ファイルシステムからファイルを読み込み
     * 適切なContent-Typeとヘッダーを設定
     * ファイルをレスポンスとして返却
```

### バッチ処理フロー

```
1. cronジョブが /webadmin/batch を呼び出す
   ↓
2. BatchController が呼び出される
   ↓
3. Batch.publishedBatch() が実行される:
   - ContentMapper.getSchedulePublishedIds() で公開予定コンテンツを取得
   - 各コンテンツについて:
     * ContentMapper.search() でコンテンツ情報を取得
     * ContentMapper.search() で公開テーブルの存在確認
     * 初回公開の場合: ContentMapper.createPublic() で登録、メール通知
     * 既存の場合: ContentMapper.updatePublic() で更新
     * ContentMapper.clearSchedulePublished() でスケジュール公開日時をクリア
   ↓
4. Batch.unPublishedBatch() が実行される:
   - ContentMapper.getScheduleUnpublishedIds() で非公開予定コンテンツを取得
   - 各コンテンツについて:
     * ContentMapper.delete() で content_public テーブルから削除
     * ContentMapper.clearScheduleUnpublished() でスケジュール非公開日時をクリア
```

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
│       │       ├── service/                   # サービス層
│       │       │   ├── EmailService.java     # メール送信サービス
│       │       │   └── OpenAIService.java    # OpenAI API呼び出しサービス
│       │       │
│       │       └── util/                     # ユーティリティ
│       │           ├── PaginationUtil.java   # ページネーション計算
│       │           └── ScreenNameConverter.java # 画面名変換
│       │
│       └── resources/
│           ├── application.properties        # アプリケーション設定
│           ├── application-prod.properties   # 本番環境設定
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
| `HomeController` | 画面ルーティング（Thymeleafテンプレートを返す） | `/`, `/login`, `/content`, `/user-list`, `/setting`, `/url-directory` |
| `ContentController` | コンテンツのCRUD操作API | `/webadmin/update_post`, `/webadmin/getcontent`, `/webadmin/getlist`, `/webadmin/delete_post`, `/webadmin/urlmatches`, `/webadmin/api/generate-title`, `/webadmin/api/generate-content` |
| `LoginController` | 認証処理API | `/webadmin/login_post`, `/webadmin/logout`, `/webadmin/auth` |
| `UserController` | ユーザー管理API | `/webadmin/user_post`, `/webadmin/getuser`, `/webadmin/getlist-user` |
| `SettingController` | 設定管理API | `/webadmin/setting_post`, `/webadmin/get-setting`, `/webadmin/get-session` |
| `ImageController` | 画像アップロードAPI | `/webadmin/image_upload` |
| `FileController` | ファイルアップロードAPI | `/webadmin/file_upload` |
| `MovieController` | 動画登録API | `/webadmin/movie_register` |
| `PreviewController` | プレビュー生成API | `/webadmin/preview` |
| `BatchController` | バッチ処理実行API | `/webadmin/batch` |
| `ElementItemController` | 構成要素アイテム取得API | `/webadmin/getElementItem` |

### Logic層（ビジネスロジック）

| クラス | 説明 | 主な機能 |
|--------|------|----------|
| `Content` | コンテンツ管理ロジック | 作成、更新、削除、検索、テンプレート適用、動画タグ変換、URLディレクトリツリー生成 |
| `Login` | 認証ロジック | ユーザー認証、セッション管理 |
| `User` | ユーザー管理ロジック | ユーザーCRUD、一覧取得、ページネーション |
| `Setting` | 設定管理ロジック | 構成要素色設定の更新・取得、OpenAI設定、メール設定、デフォルト公開設定 |
| `Batch` | バッチ処理ロジック | スケジュール公開・非公開処理 |
| `CommonProc` | 共通ユーティリティ | ファイル読み込み、日時生成、パス取得 |

### Service層

| クラス | 説明 | 主な機能 |
|--------|------|----------|
| `OpenAIService` | OpenAI API呼び出しサービス | タイトル生成、本文生成、エラーハンドリング |
| `EmailService` | メール送信サービス | コンテンツ公開通知メール送信、メールアドレス検証 |

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
  created VARCHAR(19),             -- 作成日時（形式: "yyyy-MM-dd HH:mm"）
  updated VARCHAR(19),             -- 更新日時（形式: "yyyy-MM-dd HH:mm"）
  created_by VARCHAR(255),         -- 作成者
  updated_by VARCHAR(255),         -- 更新者
  url VARCHAR(255),                -- URLパス
  title VARCHAR(255),              -- タイトル
  head TEXT,                       -- ヘッダー部分HTML
  content TEXT,                    -- コンテンツ本文
  type VARCHAR(50),                -- コンテンツタイプ
  elementcolor VARCHAR(50),        -- 構成要素の色コード
  template VARCHAR(50),            -- テンプレートID
  schedule_published VARCHAR(19),  -- 公開予定日時（形式: "yyyy-MM-dd HH:mm"）
  schedule_unpublished VARCHAR(19),-- 非公開予定日時（形式: "yyyy-MM-dd HH:mm"）
  publishflg_keep VARCHAR(1)       -- 公開フラグ（"1":公開、"0":非公開）
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

## API仕様詳細

### 認証API

| メソッド | URL | 説明 | パラメータ | 戻り値 |
|----------|-----|------|-----------|--------|
| POST | `/webadmin/login_post` | ログイン | `name`, `password` | リダイレクト |
| GET | `/webadmin/logout` | ログアウト | なし | リダイレクト |
| GET | `/webadmin/auth` | 認証状態確認 | なし | `boolean` |

### コンテンツAPI

| メソッド | URL | 説明 | パラメータ | 戻り値 |
|----------|-----|------|-----------|--------|
| GET | `/webadmin/getcontent` | コンテンツ取得 | `id`, `mode` | `ContentBean` |
| POST | `/webadmin/update_post` | コンテンツ作成・更新 | `id`, `url`, `title`, `head`, `content`, `type`, `template`, `elementcolor`, `published`, `schedule_published`, `schedule_unpublished` | リダイレクト |
| POST | `/webadmin/delete_post` | コンテンツ削除 | `id`, `mode` | リダイレクト |
| GET | `/webadmin/getlist` | 一覧取得 | `mode`, `sort`, `page`, `keyword` | `ListBean` |
| GET | `/webadmin/urlmatches` | URL重複チェック | `url`, `myId` | `boolean` |
| GET/POST | `/webadmin/preview` | プレビュー表示 | `template`, `title`, `head`, `content` | HTML |
| POST | `/webadmin/convert_preview` | プレビュー変換 | `contentText` | HTML |
| GET | `/webadmin/api/images` | 画像一覧取得 | なし | `List<HashMap>` |
| GET | `/webadmin/api/files` | ファイル一覧取得 | なし | `List<HashMap>` |
| GET | `/webadmin/api/movies` | 動画一覧取得 | なし | `List<HashMap>` |
| GET | `/webadmin/api/contents` | コンテンツ一覧取得（リンク挿入用） | `type`, `keyword`, `sort`, `page` | `ListBean` |
| POST | `/webadmin/api/generate-title` | タイトル生成 | `userInput` | `Map<String, Object>` |
| POST | `/webadmin/api/generate-content` | 本文生成 | `userInput` | `Map<String, Object>` |

### ユーザーAPI

| メソッド | URL | 説明 | パラメータ | 戻り値 |
|----------|-----|------|-----------|--------|
| GET | `/webadmin/getuser` | ユーザー取得 | `id` | `UserBean` |
| POST | `/webadmin/user_post` | ユーザー作成・更新 | `id`, `name`, `password`, `email`, `admin` | リダイレクト |
| GET | `/webadmin/getlist-user` | ユーザー一覧取得 | `sort`, `page` | `UserListBean` |

### ファイルAPI

| メソッド | URL | 説明 | パラメータ | 戻り値 |
|----------|-----|------|-----------|--------|
| POST | `/webadmin/image_upload` | 画像アップロード | `file`, `id`, `title`, `url`, `imageWidth`, `imageHeight`, `published`, `schedule_published`, `schedule_unpublished` | リダイレクト |
| POST | `/webadmin/file_upload` | ファイルアップロード | `file`, `id`, `title`, `url`, `published`, `schedule_published`, `schedule_unpublished` | リダイレクト |
| POST | `/webadmin/movie_register` | 動画登録 | `id`, `title`, `youtubeUrl`, `movieWidth`, `movieHeight`, `published`, `schedule_published`, `schedule_unpublished` | リダイレクト |

### 設定API

| メソッド | URL | 説明 | パラメータ | 戻り値 |
|----------|-----|------|-----------|--------|
| POST | `/webadmin/setting_post` | 設定更新 | `elements-color-value`, `default-publish-on`, `openai-api-key`, `openai-model`, `openai-prompt-title`, `openai-prompt-content`, `ai-generation-visible`, `email-enabled`, `email-to`, `email-from`, `email-body-template`, `email-base-url` | リダイレクト |
| GET | `/webadmin/get-setting` | 設定取得 | なし | `SettingColorElementsBean` |
| GET | `/webadmin/get-session` | セッション属性取得 | `attribute` | `String` |

### バッチAPI

| メソッド | URL | 説明 | パラメータ | 戻り値 |
|----------|-----|------|-----------|--------|
| GET | `/webadmin/batch` | バッチ処理実行 | なし | なし |

### 構成要素API

| メソッド | URL | 説明 | パラメータ | 戻り値 |
|----------|-----|------|-----------|--------|
| GET | `/webadmin/getElementItem` | 構成要素一覧取得 | なし | `ElementItemBean` |

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
| `app.file.preview-file-name` | プレビューファイル名 | preview.html |
| `spring.servlet.multipart.max-file-size` | 最大ファイルサイズ | 10MB |

---

## セキュリティ

### 現在の実装

- **認証**: セッションベースの認証
- **パスワード**: 平文で保存・比較（本番環境ではハッシュ化を推奨）
- **セッションタイムアウト**: 30分
- **CORS**: `CorsConfig`で設定可能

### 推奨事項

- パスワードのハッシュ化（BCryptなど）
- HTTPSの使用
- SQLインジェクション対策（MyBatisのパラメータ化クエリを使用）
- XSS対策（入力値のサニタイズ）
- CSRF対策（Spring Securityの導入を検討）

---

## エラーハンドリング

### グローバル例外ハンドラー

- `GlobalExceptionHandler`でアプリケーション全体の例外を処理
- 適切なエラーメッセージを返却

### エラーメッセージ

- セッションにエラーメッセージを保存
- 一覧画面でエラーメッセージを表示
- メール送信エラーはコンテンツ登録を妨げない

---

## テスト要件

### 単体テスト

以下の機能について単体テストを作成する必要があります:

1. **認証機能**
   - ログイン成功
   - ログイン失敗（ユーザー不存在）
   - ログイン失敗（パスワード不一致）
   - ログアウト
   - 認証状態確認

2. **コンテンツ管理機能**
   - コンテンツ作成（新規）
   - コンテンツ更新（既存）
   - コンテンツ削除
   - コンテンツ取得
   - コンテンツ一覧取得（ページネーション）
   - キーワード検索
   - URL重複チェック
   - テンプレート適用
   - 動画タグ変換
   - URLディレクトリツリー生成

3. **ユーザー管理機能**
   - ユーザー作成
   - ユーザー更新
   - ユーザー取得
   - ユーザー一覧取得（ページネーション）

4. **設定管理機能**
   - 設定更新
   - 設定取得
   - 構成要素色設定のパース

5. **画像管理機能**
   - 画像アップロード
   - 画像更新
   - 画像配信

6. **ファイル管理機能**
   - ファイルアップロード
   - ファイル更新
   - ファイルダウンロード

7. **動画管理機能**
   - 動画登録
   - YouTube URL解析
   - 動画タグ変換

8. **バッチ処理機能**
   - スケジュール公開処理
   - スケジュール非公開処理

9. **プレビュー機能**
   - プレビュー生成
   - プレビュー変換

10. **AI生成機能**
    - タイトル生成（成功）
    - タイトル生成（エラー）
    - 本文生成（成功）
    - 本文生成（エラー）

11. **メール通知機能**
    - メール送信（成功）
    - メール送信（エラー）
    - メールアドレス検証

12. **公開ページ配信機能**
    - 通常コンテンツの配信
    - CSSの配信
    - JavaScriptの配信
    - 画像の配信
    - ファイルのダウンロード

### 統合テスト

- エンドツーエンドの処理フロー
- データベースとの連携
- ファイルシステムとの連携

### テストカバレッジ

- 目標: 100%のカバレッジ
- すべてのメソッド、分岐、エッジケースをテスト

---

## ライセンス

このプロジェクトは内部使用を目的としています。
