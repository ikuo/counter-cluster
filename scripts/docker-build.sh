#/bin/sh
cp ./target/scala-2.11/counter-cluster-assembly-0.1-SNAPSHOT.jar ./docker/
docker build -t $(whoami)/counter-cluster docker
