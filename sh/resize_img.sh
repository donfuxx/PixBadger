#!/bin/bash

TF_PATH="../tf_files/flower_photos"

cd ${TF_PATH}

echo "resize images in ${TF_PATH}"

find . -name '*.jpg' -execdir mogrify -verbose -resize 224x {} \;

find . -name '*.jpg' -execdir mogrify -compress JPEG -quality 50 {} \;

$SHELL

