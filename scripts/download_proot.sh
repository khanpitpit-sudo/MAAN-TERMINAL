#!/bin/bash
# Download proot and Alpine Linux rootfs for MaAn Terminal

set -e

PROOT_VERSION="5.3.0"
ALPINE_VERSION="3.19"
ARCH="aarch64"

DOWNLOAD_DIR="app/src/main/assets"
mkdir -p "$DOWNLOAD_DIR"

echo "Downloading proot..."
curl -L "https://github.com/proot-me/proot/releases/download/v${PROOT_VERSION}/proot-v${PROOT_VERSION}-${ARCH}-static" \
    -o "$DOWNLOAD_DIR/proot"
chmod +x "$DOWNLOAD_DIR/proot"

echo "Downloading Alpine Linux rootfs..."
curl -L "https://dl-cdn.alpinelinux.org/alpine/v${ALPINE_VERSION}/releases/${ARCH}/alpine-minirootfs-${ALPINE_VERSION}.0-${ARCH}.tar.gz" \
    -o "$DOWNLOAD_DIR/alpine-rootfs.tar.gz"

echo "Done! Files saved to $DOWNLOAD_DIR"
ls -la "$DOWNLOAD_DIR"