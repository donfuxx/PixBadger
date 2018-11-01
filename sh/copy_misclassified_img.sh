#!/bin/bash

TF_TRAINING_PATH="../tf_files/training_reports"

echo "copy all misclassified images"

input=${TF_TRAINING_PATH}/misclassified_test_images.txt
while IFS= read -r var
do
  
  array=($var)
  img_path=${array[0]}
  err_label=${array[1]}
  real_label=${array[2]}

  dest_folder=${TF_TRAINING_PATH}"/misclassified_img/"${real_label}"/"${err_label}
  mkdir -p ${dest_folder}
  mv -v "../"${img_path} ${dest_folder}

done < "$input"

rm ${input}

#$SHELL
