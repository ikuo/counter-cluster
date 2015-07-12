#!/bin/sh
user=${1:-`whoami`}
sudo docker run -it --rm \
  --env="AKKA_HOSTNAME=seed0" \
  --env="AKKA_PORT=2551" \
  --env="CONFIG=/seed0.conf" \
  --env="SEED0=akka.tcp://cluster@seed0:2551" \
  --add-host="seed0:127.0.0.1" \
  -p 2551 \
  --name=seed0 \
  $user/counter-cluster
