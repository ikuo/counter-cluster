dynamodb: dynamodb-local
seed1: java -Dconfig.resource=/seed1.conf -jar target/scala-2.11/buffer-cluster-assembly-0.1-SNAPSHOT.jar|tee log/seed1.log
buffer1: java -Dconfig.resource=/buffer.conf -jar target/scala-2.11/buffer-cluster-assembly-0.1-SNAPSHOT.jar|tee log/buffer1.log
buffer2: java -Dconfig.resource=/buffer.conf -jar target/scala-2.11/buffer-cluster-assembly-0.1-SNAPSHOT.jar
frontend: java -Dconfig.resource=/frontend.conf -jar target/scala-2.11/buffer-cluster-assembly-0.1-SNAPSHOT.jar|tee log/frontend.log
