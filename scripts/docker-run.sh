#!/bin/sh
docker run -it --rm \
  --env="AKKA_HOSTNAME=seed1" \
  --env="CONFIG=/seed1.conf" \
  --env="SEED0=akka.tcp://cluster@127.0.0.1:2551" \
  --add-host="seed1:127.0.0.1" \
  -p 2551 \
  --name=seed1 \
  $(whoami)/counter-cluster
