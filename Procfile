seed0: NODE_ID=seed0 java -Dconfig.resource=/seed0.conf -javaagent:aspectjweaver-1.8.6.jar -jar target/scala-2.11/counter-cluster-assembly-0.1-SNAPSHOT.jar|tee log/seed0.log
counter1: NODE_ID=counter1 java -Dconfig.resource=/counter.aws.conf -javaagent:aspectjweaver-1.8.6.jar -jar target/scala-2.11/counter-cluster-assembly-0.1-SNAPSHOT.jar|tee log/counter1.log
counter2: NODE_ID=counter2 java -Dconfig.resource=/counter.aws.conf -javaagent:aspectjweaver-1.8.6.jar -jar target/scala-2.11/counter-cluster-assembly-0.1-SNAPSHOT.jar
frontend: NODE_ID=frontend java -Dconfig.resource=/frontend.conf -javaagent:aspectjweaver-1.8.6.jar -jar target/scala-2.11/counter-cluster-assembly-0.1-SNAPSHOT.jar|tee log/frontend.log
