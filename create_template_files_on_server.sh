#!/bin/bash

# サーバー上で実行するスクリプト
# サーバーにSSH接続して、このスクリプトを実行してください

echo "テンプレートファイルを作成中..."

# original.html
cat > /opt/skygarden/original.html << 'EOF'
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
EOF

# original.stylesheet.html
cat > /opt/skygarden/original.stylesheet.html << 'EOF'
###content###
EOF

# original.script.html
cat > /opt/skygarden/original.script.html << 'EOF'
###content###
EOF

# preview.html
cat > /opt/skygarden/preview.html << 'EOF'
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
EOF

# ファイルの権限を設定
chmod 644 /opt/skygarden/*.html

echo "ファイルの作成が完了しました:"
ls -lh /opt/skygarden/*.html
