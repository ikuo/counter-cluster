A playground of Akka Cluster/ClusterSharding (on DynamoDB)

Used in this experiment: https://adtech.cyberagent.io/scalablog/2015/08/07/akka-cluster-sharding-dynamodb/

# Setup

```
$ brew install forego
$ pushd /tmp && git clone git@github.com:kamon-io/Kamon.git && git checkout release-akka-2.4 && sbt publishLocal
$ wget http://central.maven.org/maven2/org/aspectj/aspectjweaver/1.8.6/aspectjweaver-1.8.6.jar
```

Create table:

```
sbt console
> countercluster.DynamoDB.createTables
```

Update read/write throuput to 50 units.

# Running in sbt

```
$ sbt assembly
$ forego start
```

# Running in Docker

```
$ ./scripts/docker-build.sh
$ ./scripts/docker-run-seed0.sh
```

# Using DynamoDB Local

```
$ brew install dynamodb-local
$ dynamodb-local
$ open http://localhost:8000/shell/
```

Set `counter-cluster.counter.dynamodb` to `http://localhost:8000` in application.conf.
