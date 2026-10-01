#!/bin/bash
# Download prebuilt proot for Android arm64
set -e
ASSETS_DIR="../app/src/main/assets"
mkdir -p $ASSETS_DIR

echo "Downloading proot aarch64..."
URL="https://github.com/ahmed-alnassif/proot/releases/latest/download/proot-aarch64.zip"
curl -L -o /tmp/proot.zip $URL
unzip -o /tmp/proot.zip -d /tmp/proot_extract
cp /tmp/proot_extract/proot $ASSETS_DIR/proot
cp -r /tmp/proot_extract/loader $ASSETS_DIR/ || true
chmod +x $ASSETS_DIR/proot
echo "✓ proot binary ready in $ASSETS_DIR"
ls -lh $ASSETS_DIR/
