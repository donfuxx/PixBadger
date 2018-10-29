#!/bin/bash

TF_PATH=".."
IMAGE_SIZE=224
ARCHITECTURE="mobilenet_1.0_${IMAGE_SIZE}"
STEPS=2000
LEARNING_RATE=0.0001
TRAIN_SIZE=1000

source ~/venv/bin/activate

echo "retraining model ${TF_PATH} ${ARCHITECTURE} ${STEPS} ${LEARNING_RATE} ${TRAIN_SIZE}"

cd ${TF_PATH}

ls

python -m scripts.retrain \
  --bottleneck_dir=tf_files/bottlenecks \
  --how_many_training_steps=${STEPS} \
  --learning_rate=${LEARNING_RATE} \
  --train_batch_size=${TRAIN_SIZE} \
  --model_dir=tf_files/models/ \
  --summaries_dir=tf_files/training_summaries/"${ARCHITECTURE}"/LR_"${LEARNING_RATE}"/TS_"${TRAIN_SIZE}" \
  --output_graph=tf_files/retrained_graph.pb \
  --output_labels=tf_files/retrained_labels.txt \
  --architecture="${ARCHITECTURE}" \
  --image_dir=tf_files/flower_photos \
  --print_misclassified_test_images

$SHELL

 
