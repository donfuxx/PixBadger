#!/bin/bash

TF_TRAINING_PATH="../tf_files/training_reports"
TF_BOTTLENECKS_PATH="../tf_files/bottlenecks"

echo "copy all misclassified images"

input=${TF_TRAINING_PATH}/misclassified_test_images.txt
while IFS= read -r var
do
  
  array=($var)
  img_path=${array[0]}
  real_label=${array[1]}
  err_label=${array[2]}
  echo "${img_path} ${real_label} ${err_label}"

  dest_folder=${TF_TRAINING_PATH}"/misclassified_img/"${real_label}"/"${err_label}
  mkdir -p ${dest_folder}
  mv -v "../"${img_path} ${dest_folder}
  
  img_file=$(echo "${img_path}" | grep -oP '(?<=/)[^/]*\.jpg\b')
  echo "delete bottleneck: ${real_label}/${img_file}"
  find "${TF_BOTTLENECKS_PATH}/${real_label}" -name "${img_file}*" -delete

done < "$input"

rm -f "${input}"

#$SHELL
