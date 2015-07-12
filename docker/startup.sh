#!/bin/sh
: ${AKKA_HOSTNAME:=`curl http://169.254.169.254/latest/meta-data/local-ipv4`}
java \
  -Dakka.cluster.seed-nodes.0=$SEED0 \
  -Dconfig.resource=$CONFIG \
  -javaagent:aspectjweaver-1.8.6.jar \
  -jar counter-cluster-assembly-0.1-SNAPSHOT.jar
