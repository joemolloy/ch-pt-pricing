#!/bin/sh

T603_PATH=$1
TARGET_DIRECTORY=$2

for page in $(seq 20 64); do
    echo "Converting page ${page}..."
    pdf-stapler sel $T603_PATH ${page} $TARGET_DIRECTORY/${page}.pdf
    pdftotext -layout $TARGET_DIRECTORY/${page}.pdf $TARGET_DIRECTORY/${page}.txt
done
