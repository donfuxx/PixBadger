#!/bin/bash

BASE_DIR=$(dirname "$0")
START=1
DECR=1
I=1
LR=${START}

while true
do

  echo
  echo "Learning Rate phase I = ${I}"

  if [[ $I = 1 ]]
    then LR=0.001
    elif [[ $I = 2 ]]
#    then LR=0.0005
#    elif [[ $I = 3 ]]
    then LR=0.0001
#    elif [[ $I = 4 ]]
#    then LR=0.00005
#    elif [[ $I = 5 ]]
#    then LR=0.00001
#    elif [[ $I = 6 ]]
#    then LR=0.000005
#    elif [[ $I = 7 ]]
#    then LR=0.000001
    else $SHELL
  fi

  ((I++))

  #LR=$(($LR+($I*$DECR)))
  ./retrain.sh "${LR}"
  cd ${BASE_DIR}
  ./copy_misclassified_img.sh
done

$SHELL
