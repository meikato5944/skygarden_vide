# Skygarden CMS統合システム

Skygarden_BackとSkygarden3.0.0_Frontを統合したCMSシステムです。

## 技術スタック

- **フレームワーク**: Spring Boot 3.4.1
- **言語**: Java 21, TypeScript
- **テンプレートエンジン**: Thymeleaf
- **データベース**: MySQL
- **ORM**: MyBatis
- **ビルドツール**: Gradle

## プロジェクト構造

```
skygarden_vide/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/skygarden/
│   │   │       ├── Application.java
│   │   │       ├── bean/          # DTOクラス
│   │   │       ├── controller/    # REST APIコントローラー
│   │   │       ├── logic/         # ビジネスロジック
│   │   │       ├── mapper/         # MyBatis Mapperインターフェース
│   │   │       └── config/        # 設定クラス
│   │   └── resources/
│   │       ├── mapper/             # MyBatis XMLマッパー
│   │       ├── templates/          # Thymeleafテンプレート
│   │       └── static/             # 静的リソース（CSS、JS、画像）
│   └── test/
├── build.gradle
└── settings.gradle
```

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
spring.datasource.url=jdbc:mysql://localhost:3306/skygarden?enabledTLSProtocols=TLSv1.2
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

## 主な変更点

1. **MyBatisの導入**: JDBCからMyBatisに変更し、SQLをXMLファイルで管理
2. **Thymeleafの導入**: React SPAからThymeleafテンプレートに変更
3. **統合**: バックエンドとフロントエンドを1つのSpring Bootアプリケーションに統合
4. **デザインの保持**: 既存のCSSとデザインをそのまま使用

## ライセンス

このプロジェクトは内部使用を目的としています。
# skygarden_vide
# skygarden_vide
