#!/bin/bash

# サーバーにテンプレートファイルをデプロイするスクリプト
# 使用方法: ./deploy_template_files.sh [サーバーホスト] [ユーザー名] [パスワード]

# 色の定義
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}テンプレートファイルのデプロイ${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""

# パラメータの確認
if [ -z "$1" ] || [ -z "$2" ]; then
    echo "使用方法: $0 <サーバーホスト> <ユーザー名> [パスワード]"
    echo "例: $0 example.com ubuntu"
    exit 1
fi

SERVER_HOST=$1
SERVER_USER=$2
SERVER_PASS=$3

# SSH接続してファイルを作成
echo "サーバーに接続中: ${SERVER_USER}@${SERVER_HOST}"

if [ -z "$SERVER_PASS" ]; then
    ssh ${SERVER_USER}@${SERVER_HOST} << 'EOF'
        echo "テンプレートファイルを作成中..."
        
        # original.html
        cat > /opt/skygarden/original.html << 'FILEEOF'
<!DOCTYPE html>
<html lang="japanese">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>###title###</title>
    ###head###
  </head>
  <body>
    ###content###
  </body>
</html>
FILEEOF

        # original.stylesheet.html
        cat > /opt/skygarden/original.stylesheet.html << 'FILEEOF'
###content###
FILEEOF

        # original.script.html
        cat > /opt/skygarden/original.script.html << 'FILEEOF'
###content###
FILEEOF

        # preview.html
        cat > /opt/skygarden/preview.html << 'FILEEOF'
<!DOCTYPE html>
<html lang="japanese">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>プレビュー</title>
  </head>
  <body>
    ###content###
  </body>
</html>
FILEEOF

        # ファイルの権限を設定
        chmod 644 /opt/skygarden/*.html
        
        echo "ファイルの作成が完了しました:"
        ls -lh /opt/skygarden/*.html
EOF
else
    sshpass -p "${SERVER_PASS}" ssh ${SERVER_USER}@${SERVER_HOST} << 'EOF'
        echo "テンプレートファイルを作成中..."
        
        # original.html
        cat > /opt/skygarden/original.html << 'FILEEOF'
<!DOCTYPE html>
<html lang="japanese">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>###title###</title>
    ###head###
  </head>
  <body>
    ###content###
  </body>
</html>
FILEEOF

        # original.stylesheet.html
        cat > /opt/skygarden/original.stylesheet.html << 'FILEEOF'
###content###
FILEEOF

        # original.script.html
        cat > /opt/skygarden/original.script.html << 'FILEEOF'
###content###
FILEEOF

        # preview.html
        cat > /opt/skygarden/preview.html << 'FILEEOF'
<!DOCTYPE html>
<html lang="japanese">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>プレビュー</title>
  </head>
  <body>
    ###content###
  </body>
</html>
FILEEOF

        # ファイルの権限を設定
        chmod 644 /opt/skygarden/*.html
        
        echo "ファイルの作成が完了しました:"
        ls -lh /opt/skygarden/*.html
EOF
fi

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ テンプレートファイルのデプロイが完了しました${NC}"
else
    echo "✗ デプロイに失敗しました"
    exit 1
fi
