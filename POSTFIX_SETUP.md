# macOSでpostfixを有効にする手順

## 1. postfixの状態確認

```bash
sudo /usr/sbin/postfix status
```

## 2. postfixの起動

```bash
# postfixを起動
sudo /usr/sbin/postfix start

# 起動確認
sudo /usr/sbin/postfix status
```

## 3. ポート25がリッスンしているか確認

```bash
sudo lsof -i :25
```

## 4. 自動起動設定（macOSのバージョンによる）

### macOS Big Sur以降の場合

macOS Big Sur以降では、`launchctl load`が非推奨になっています。
以下の方法を試してください：

#### 方法1: launchctl bootstrapを使用（推奨）

```bash
sudo launchctl bootstrap system /System/Library/LaunchDaemons/com.apple.postfix.master.plist
```

#### 方法2: 手動起動スクリプトを作成

自動起動が難しい場合は、アプリケーション起動時にpostfixを起動するスクリプトを作成します。

`start_postfix.sh`を作成：

```bash
#!/bin/bash
if ! sudo /usr/sbin/postfix status > /dev/null 2>&1; then
    sudo /usr/sbin/postfix start
fi
```

#### 方法3: 手動起動（最もシンプル）

必要に応じて手動で起動：
```bash
sudo /usr/sbin/postfix start
```

## 5. 動作確認

### ポート25がリッスンしているか確認

```bash
# ポート25が開いているか確認
sudo lsof -i :25
```

または

```bash
netstat -an | grep 25
```

### テストメール送信（オプション）

```bash
# ローカルメールボックスにテストメールを送信
echo "Test message" | mail -s "Test Subject" $(whoami)

# メールを確認（macOSの場合）
# Mail.appを開くか、ターミナルで:
mail
```

## 6. トラブルシューティング

### postfixが起動しない場合

```bash
# ログを確認
sudo tail -f /var/log/mail.log
# または
sudo tail -f /var/log/postfix.log
```

### ポート25が使用できない場合

macOSでは、セキュリティ設定によりポート25が制限されている場合があります。
その場合は、postfixの設定を変更して別のポートを使用するか、
システム設定で許可する必要があります。

### launchctl bootstrapが失敗する場合

macOSの新しいバージョンでは、システムのLaunchDaemonを直接ロードできない場合があります。
その場合は、手動起動（方法3）を使用してください。

## 7. 開発環境での推奨設定

開発環境では、実際にメールを送信せず、ローカルのメールキューに保存する設定が推奨されます。

`/etc/postfix/main.cf` の設定例：

```
# ローカルホストからのみメールを受け付ける
inet_interfaces = localhost
# 外部へのメール送信を無効化（開発環境の場合）
relayhost = 
```

## 8. アプリケーション起動時にpostfixを確認する方法

Spring Bootアプリケーション起動時にpostfixが起動しているか確認するスクリプト例：

```bash
#!/bin/bash
# start_app_with_postfix.sh

# postfixが起動していない場合は起動
if ! sudo /usr/sbin/postfix status > /dev/null 2>&1; then
    echo "Starting postfix..."
    sudo /usr/sbin/postfix start
fi

# アプリケーションを起動
./gradlew bootRun
```

## 注意事項

- macOSのpostfixは、システム全体のメール送信に使用されます
- 開発環境では、実際のメール送信を避けるため、設定画面でメール機能をOFFにすることを推奨します
- 本番環境（サクラサーバー）では、postfixが適切に設定されていることを確認してください
- macOS Big Sur以降では、システムのLaunchDaemonの自動起動設定が制限される場合があります

## 参考リンク

- [Postfix公式ドキュメント](http://www.postfix.org/documentation.html)
- [macOSでのpostfix設定](https://developer.apple.com/documentation/macos-release-notes/macos-big-sur-11_0_1-release-notes)
