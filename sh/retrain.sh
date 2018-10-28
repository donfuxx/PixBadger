#!/bin/bash

TF_PATH=".."
IMAGE_SIZE=224
ARCHITECTURE="mobilenet_1.0_${IMAGE_SIZE}"
STEPS=30000
LEARNING_RATE=0.00025

source ~/venv/bin/activate

echo "retraining model ${TF_PATH} ${ARCHITECTURE} ${STEPS} ${LEARNING_RATE}"

cd ${TF_PATH}

ls

python -m scripts.retrain \
  --bottleneck_dir=tf_files/bottlenecks \
  --how_many_training_steps=${STEPS} \
  --learning_rate=${LEARNING_RATE} \
  --model_dir=tf_files/models/ \
  --summaries_dir=tf_files/training_summaries/"${ARCHITECTURE}"/LR_"${LEARNING_RATE}" \
  --output_graph=tf_files/retrained_graph.pb \
  --output_labels=tf_files/retrained_labels.txt \
  --architecture="${ARCHITECTURE}" \
  --image_dir=tf_files/flower_photos

# $SHELL

 
