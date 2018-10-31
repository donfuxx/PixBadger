#!/bin/bash

TF_TRAINING_PATH="../tf_files/training_reports"

echo "copy all misclassified images"

input=${TF_TRAINING_PATH}/misclassified_test_images.txt
while IFS= read -r var
do
  cp -v "../"$(echo "$var" | grep -oh '.*tf_files.*jpg') ${TF_TRAINING_PATH}"/misclassified_img"
done < "$input"

$SHELL
