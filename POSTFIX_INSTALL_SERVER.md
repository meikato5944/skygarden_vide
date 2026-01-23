# サクラサーバーでのpostfixインストール手順

## 確認結果

- ❌ postfixがインストールされていない（`Unit postfix.service could not be found.`）
- ❌ ポート25に接続できない（`Connection refused`）

## インストール手順

### 1. postfixをインストール

```bash
sudo apt-get update
sudo apt-get install postfix
```

### 2. インストール時の設定

インストール中に設定ウィザードが表示されます。以下のように設定してください：

**設定タイプの選択**:
- 「Internet Site」を選択（Enterキー）

**システムメール名**:
- ドメイン名を入力（例: `skygarden-development.com`）
- または、そのままEnterキーでデフォルトを使用

### 3. インストール後の確認

```bash
# postfixの状態を確認
sudo systemctl status postfix

# postfixを起動（自動起動は既に有効になっているはず）
sudo systemctl start postfix

# 起動確認
sudo systemctl status postfix
```

### 4. ポート25がリッスンしているか確認

```bash
# ssコマンドを使用（netstatの代わり）
sudo ss -tlnp | grep :25

# または
sudo lsof -i :25
```

期待される結果:
```
tcp   LISTEN  0  100  127.0.0.1:25  0.0.0.0:*  users:(("master",pid=12345,fd=13))
```

### 5. postfixへの接続テスト

```bash
telnet localhost 25
```

接続できた場合、以下のようなメッセージが表示されます：
```
220 hostname ESMTP Postfix
```

`quit` と入力して終了します。

### 6. 基本設定の確認

```bash
# postfixの設定を確認
sudo postfix check

# メイン設定ファイルの確認
sudo cat /etc/postfix/main.cf | grep -E "^inet_interfaces"
```

**推奨設定**:
```
inet_interfaces = localhost
```

これにより、ローカルホストからのみメールを受け付けます（セキュリティ上推奨）。

## トラブルシューティング

### インストールが失敗する場合

```bash
# パッケージリストを更新
sudo apt-get update

# 依存関係を確認
sudo apt-get install -f

# 再度インストール
sudo apt-get install postfix
```

### ポート25が使用できない場合

```bash
# 他のプロセスがポート25を使用しているか確認
sudo lsof -i :25
```

### 設定を変更した場合

```bash
# 設定ファイルの構文チェック
sudo postfix check

# postfixを再起動
sudo systemctl restart postfix
```

## インストール後の動作確認

### テストメール送信

```bash
# ローカルユーザーにテストメールを送信
echo "Test message" | mail -s "Test Subject" ubuntu

# メールを確認
mail
```

### ログの確認

```bash
# postfixのログを確認
sudo tail -f /var/log/mail.log
```

## アプリケーションでの動作確認

postfixのインストールと起動が完了したら：

1. アプリケーションを再起動（必要に応じて）
2. 設定画面でメール通知を有効にする
3. コンテンツを公開状態で登録
4. 「登録しました。（メール送信に成功しました）」と表示されることを確認

## まとめ

**実行すべきコマンド**:

```bash
# 1. postfixをインストール
sudo apt-get update
sudo apt-get install postfix

# 2. postfixの状態を確認
sudo systemctl status postfix

# 3. ポート25がリッスンしているか確認
sudo ss -tlnp | grep :25

# 4. 接続テスト
telnet localhost 25
```

これで、アプリケーションからメール送信ができるようになります。
