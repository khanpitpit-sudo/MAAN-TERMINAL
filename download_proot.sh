#!/bin/bash
# Download prebuilt proot for Android arm64 - FIXED
set -e
ASSETS_DIR="app/src/main/assets"
TMP_DIR="./tmp_download"
mkdir -p $ASSETS_DIR $TMP_DIR

echo "Downloading proot aarch64..."
URL="https://github.com/ahmed-alnassif/proot/releases/latest/download/proot-aarch64.zip"
curl -L -o $TMP_DIR/proot.zip $URL
unzip -o $TMP_DIR/proot.zip -d $TMP_DIR/proot_extract
cp $TMP_DIR/proot_extract/proot $ASSETS_DIR/proot
cp -r $TMP_DIR/proot_extract/loader $ASSETS_DIR/ 2>/dev/null || true
chmod +x $ASSETS_DIR/proot
echo "✓ proot binary ready in $ASSETS_DIR"
ls -lh $ASSETS_DIR/
rm -rf $TMP_DIR
echo "Cleaned temp files"
