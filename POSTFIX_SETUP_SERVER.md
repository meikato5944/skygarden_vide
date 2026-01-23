# サクラサーバー側でのpostfix設定手順

## 1. postfixのインストール確認

```bash
# postfixがインストールされているか確認
which postfix
# または
dpkg -l | grep postfix
```

## 2. postfixがインストールされていない場合

```bash
# Ubuntu/Debian系の場合
sudo apt-get update
sudo apt-get install postfix

# インストール時に設定ウィザードが表示されます
# 一般的な設定:
# - 設定タイプ: "Internet Site"
# - システムメール名: ドメイン名（例: skygarden-development.com）
```

## 3. postfixの状態確認

```bash
# postfixの状態を確認
sudo systemctl status postfix

# または
sudo postfix status
```

## 4. postfixの起動

```bash
# postfixを起動
sudo systemctl start postfix

# 自動起動を有効化（推奨）
sudo systemctl enable postfix
```

## 5. postfixの設定確認

### 基本設定ファイル

```bash
# メイン設定ファイルを確認
sudo cat /etc/postfix/main.cf | grep -E "^[^#]" | head -20
```

### 重要な設定項目

`/etc/postfix/main.cf` で確認すべき項目：

```
# ローカルホストからのメールを受け付ける
inet_interfaces = localhost

# または、すべてのインターフェースで受け付ける場合
inet_interfaces = all

# リレーホスト（外部へのメール送信に必要）
# 通常は空欄（直接送信）またはSMTPリレーサーバーを指定
relayhost = 

# メールサイズ制限（必要に応じて）
message_size_limit = 10240000
```

## 6. ポート25がリッスンしているか確認

```bash
# ポート25が開いているか確認
sudo netstat -tlnp | grep :25
# または
sudo ss -tlnp | grep :25
```

## 7. ファイアウォール設定（必要に応じて）

サクラサーバーでファイアウォールを使用している場合：

```bash
# UFWを使用している場合
sudo ufw allow 25/tcp

# firewalldを使用している場合
sudo firewall-cmd --add-service=smtp --permanent
sudo firewall-cmd --reload
```

## 8. 動作確認

### テストメール送信

```bash
# ローカルユーザーにテストメールを送信
echo "Test message" | mail -s "Test Subject" root

# メールを確認
mail
```

### ログで確認

```bash
# postfixのログを確認
sudo tail -f /var/log/mail.log
# または
sudo tail -f /var/log/postfix.log
```

## 9. アプリケーションとの連携確認

Spring Bootアプリケーションの設定（`application-prod.properties`）:

```properties
# メール設定（postfix使用）
spring.mail.host=localhost
spring.mail.port=25
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
```

この設定で、アプリケーションは`localhost:25`のpostfixに接続します。

## 10. 外部へのメール送信について

### 直接送信（推奨）

postfixが直接外部にメールを送信する場合、DNSの設定が必要です：
- SPFレコードの設定
- 逆引きDNSの設定
- 必要に応じてDKIMの設定

### SMTPリレーを使用する場合

外部のSMTPサーバー（Gmail、SendGridなど）をリレーとして使用する場合：

`/etc/postfix/main.cf`:

```
relayhost = [smtp.gmail.com]:587
smtp_sasl_auth_enable = yes
smtp_sasl_password_maps = hash:/etc/postfix/sasl_passwd
smtp_sasl_security_options = noanonymous
smtp_tls_security_level = encrypt
```

## 11. トラブルシューティング

### postfixが起動しない場合

```bash
# 設定ファイルの構文チェック
sudo postfix check

# エラーログを確認
sudo tail -50 /var/log/mail.log
```

### メールが送信されない場合

```bash
# メールキューを確認
sudo postqueue -p

# メールキューを強制送信
sudo postqueue -f

# 特定のメールを削除
sudo postsuper -d QUEUE_ID
```

### ポート25が使用できない場合

```bash
# 他のプロセスがポート25を使用しているか確認
sudo lsof -i :25
```

## 12. セキュリティ設定（推奨）

### ローカルホストからのみメールを受け付ける

`/etc/postfix/main.cf`:

```
inet_interfaces = localhost
```

### リレー制限

`/etc/postfix/main.cf`:

```
mynetworks = 127.0.0.0/8 [::ffff:127.0.0.0]/104 [::1]/128
```

## まとめ

サクラサーバー側での手順：

1. ✅ postfixがインストールされているか確認
2. ✅ postfixが起動しているか確認（`sudo systemctl status postfix`）
3. ✅ 自動起動を有効化（`sudo systemctl enable postfix`）
4. ✅ ポート25がリッスンしているか確認
5. ✅ アプリケーションの設定（`application-prod.properties`）が正しいか確認

**注意**: サクラサーバーでは、通常postfixは既にインストール・設定されている可能性が高いです。まずは状態確認から始めることを推奨します。
