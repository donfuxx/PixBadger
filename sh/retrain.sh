#!/bin/bash

TF_PATH=".."
IMAGE_SIZE=224
ARCHITECTURE="mobilenet_1.0_${IMAGE_SIZE}"
STEPS=100000
#LEARNING_RATE=0.00001
LEARNING_RATE=$1
TRAIN_SIZE=32

source ~/venv/bin/activate

echo "retraining model ${TF_PATH} ${ARCHITECTURE} ${STEPS} ${LEARNING_RATE} ${TRAIN_SIZE}"

cd ${TF_PATH}

mkdir -p training_reports/misclassified_img

ls

python -um scripts.retrain \
  --bottleneck_dir=tf_files/bottlenecks \
  --how_many_training_steps=${STEPS} \
  --learning_rate=${LEARNING_RATE} \
  --train_batch_size=${TRAIN_SIZE} \
  --validation_batch_size=128 \
  --model_dir=tf_files/models/ \
  --summaries_dir=tf_files/training_summaries/"${ARCHITECTURE}"/LR_"${LEARNING_RATE}"/TS_"${TRAIN_SIZE}"/MV_L1 \
  --output_graph=tf_files/retrained_graph_LR_${LEARNING_RATE}.pb \
  --output_labels=tf_files/retrained_labels_LR_${LEARNING_RATE}.txt \
  --architecture="${ARCHITECTURE}" \
  --image_dir=tf_files/flower_photos \
  --print_misclassified_test_images

sleep 1

#$SHELL

 
