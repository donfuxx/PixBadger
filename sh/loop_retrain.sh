#!/bin/bash

BASE_DIR=$(dirname "$0")

while true
do
  ./retrain.sh
  cd ${BASE_DIR}
  ./copy_misclassified_img.sh
done

$SHELL
