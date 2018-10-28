#!/bin/bash

source ~/venv/bin/activate

cd ..

echo "Starting tensorboard"

tensorboard --logdir tf_files/training_summaries &

$SHELL
