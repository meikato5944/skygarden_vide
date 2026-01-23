# メール送信エラーの原因追究手順

## エラーメッセージ
「登録しました。（メール送信に失敗しました） メール送信に失敗しました：通信エラーが発生しました。」

このエラーは、postfixとの通信に失敗している可能性が高いです。

## 原因追究手順

### 1. postfixの状態確認（最重要）

```bash
# postfixの状態を確認
sudo systemctl status postfix
```

**確認ポイント**:
- `Active: active (running)` となっているか
- エラーメッセージがないか

**postfixが起動していない場合**:
```bash
# postfixを起動
sudo systemctl start postfix

# 起動確認
sudo systemctl status postfix
```

### 2. ポート25がリッスンしているか確認

```bash
# ポート25が開いているか確認
sudo netstat -tlnp | grep :25
# または
sudo ss -tlnp | grep :25
```

**期待される結果**:
```
tcp        0      0 127.0.0.1:25            0.0.0.0:*               LISTEN      12345/master
```

ポート25がリッスンしていない場合、postfixが起動していない可能性があります。

### 3. postfixの設定確認

```bash
# postfixの設定ファイルを確認
sudo cat /etc/postfix/main.cf | grep -E "^inet_interfaces"
```

**期待される設定**:
```
inet_interfaces = localhost
```
または
```
inet_interfaces = all
```

### 4. postfixのログを確認

```bash
# postfixのログを確認（最新50行）
sudo tail -50 /var/log/mail.log

# または
sudo tail -50 /var/log/postfix.log

# リアルタイムでログを確認（別ターミナルで実行）
sudo tail -f /var/log/mail.log
```

**確認ポイント**:
- エラーメッセージがないか
- 接続試行の記録があるか

### 5. アプリケーションのログを確認

```bash
# Spring Bootアプリケーションのログを確認
# systemdで管理している場合
sudo journalctl -u skygarden -n 100 --no-pager

# または、ログファイルがある場合
tail -100 /path/to/application.log
```

**確認ポイント**:
- `JavaMailSenderが利用できません` というメッセージ
- `メール送信に失敗しました` の詳細なエラーメッセージ

### 6. postfixへの接続テスト

```bash
# telnetでpostfixに接続できるかテスト
telnet localhost 25

# 接続できた場合、以下のようなメッセージが表示されます:
# 220 hostname ESMTP Postfix
```

接続できない場合、postfixが起動していないか、ポート25がブロックされています。

### 7. JavaMailSenderの設定確認

アプリケーションの設定ファイル（`application-prod.properties`）を確認：

```properties
spring.mail.host=localhost
spring.mail.port=25
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
```

## よくある原因と対処法

### 原因1: postfixがインストールされていない

**確認方法**:
```bash
which postfix
dpkg -l | grep postfix
```

**対処法**:
```bash
sudo apt-get update
sudo apt-get install postfix
```

### 原因2: postfixが起動していない

**確認方法**:
```bash
sudo systemctl status postfix
```

**対処法**:
```bash
sudo systemctl start postfix
sudo systemctl enable postfix  # 自動起動を有効化
```

### 原因3: ポート25がブロックされている

**確認方法**:
```bash
sudo netstat -tlnp | grep :25
```

**対処法**:
- ファイアウォールの設定を確認
- セキュリティグループの設定を確認（サクラサーバーの場合）

### 原因4: postfixの設定が間違っている

**確認方法**:
```bash
sudo postfix check
```

**対処法**:
エラーメッセージに従って設定を修正

### 原因5: JavaMailSenderが正しく初期化されていない

**確認方法**:
アプリケーションのログで `JavaMailSenderが利用できません` というメッセージを確認

**対処法**:
- `spring-boot-starter-mail` が依存関係に含まれているか確認
- `application-prod.properties` の設定が正しいか確認

## デバッグ用の詳細ログ出力

アプリケーションのログレベルを上げて、詳細なエラー情報を取得：

`application-prod.properties` に追加：

```properties
# メール関連のログレベルを上げる
logging.level.com.example.skygarden.service.EmailService=DEBUG
logging.level.org.springframework.mail=DEBUG
```

## クイックチェックリスト

サーバー側で以下を順番に確認：

- [ ] `sudo systemctl status postfix` で起動状態を確認
- [ ] `sudo netstat -tlnp | grep :25` でポート25がリッスンしているか確認
- [ ] `telnet localhost 25` でpostfixに接続できるか確認
- [ ] `sudo tail -50 /var/log/mail.log` でpostfixのログを確認
- [ ] アプリケーションのログでエラーの詳細を確認

## 次のステップ

上記の確認結果を共有していただければ、より具体的な対処法を提案できます。
