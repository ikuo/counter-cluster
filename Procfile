dynamodb: dynamodb-local
seed1: java -Dconfig.resource=/seed1.conf -javaagent:aspectjweaver-1.8.6.jar -jar target/scala-2.11/counter-cluster-assembly-0.1-SNAPSHOT.jar|tee log/seed1.log
counter1: java -Dconfig.resource=/counter.aws.conf -javaagent:aspectjweaver-1.8.6.jar -jar target/scala-2.11/counter-cluster-assembly-0.1-SNAPSHOT.jar|tee log/counter1.log
counter2: java -Dconfig.resource=/counter.aws.conf -javaagent:aspectjweaver-1.8.6.jar -jar target/scala-2.11/counter-cluster-assembly-0.1-SNAPSHOT.jar
frontend: java -Dconfig.resource=/frontend.conf -javaagent:aspectjweaver-1.8.6.jar -jar target/scala-2.11/counter-cluster-assembly-0.1-SNAPSHOT.jar|tee log/frontend.log
