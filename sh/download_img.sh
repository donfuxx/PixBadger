#!/bin/bash

LABEL="food"
TF_PATH="../tf_files"
PHOTO_DIR="flower_photos"

cd ${TF_PATH}

mkdir html
mkdir html/${LABEL}
mkdir ${PHOTO_DIR}/${LABEL}

echo "get all pages"
curl "https://pixabay.com/en/photos/${LABEL}/?pagi=[1-100]" -o "html/${LABEL}/#1.html"

cd html/${LABEL}

echo "get all images"
grep -oh 'https://cdn.pixabay.com/photo/\S*340.jpg' *.html >${LABEL}-urls.txt

echo "download all images"
sort -u ${LABEL}-urls.txt | wget -i- -N -P ../../${PHOTO_DIR}/${LABEL}

cd ../../${PHOTO_DIR}/${LABEL}

echo "resize images"

find . -name '*.jpg' -execdir mogrify -verbose -resize 224x {} \;

find . -name '*.jpg' -execdir mogrify -compress JPEG -quality 50 {} \;

$SHELL
