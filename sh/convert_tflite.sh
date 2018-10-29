#!/bin/bash

TF_PATH=".."
IMAGE_SIZE=224

source ~/venv/bin/activate

echo "convert to tensorflow lite"

cd ${TF_PATH}

ls

toco \
  --graph_def_file=tf_files/retrained_graph.pb \
  --output_file=tf_files/optimized_graph.lite \
  --input_format=TENSORFLOW_GRAPHDEF \
  --output_format=TFLITE \
  --input_shape=1,${IMAGE_SIZE},${IMAGE_SIZE},3 \
  --input_array=input \
  --output_array=final_result \
  --inference_type=FLOAT \
  --input_data_type=FLOAT

ls

$SHELL
