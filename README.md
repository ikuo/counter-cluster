A playground of Akka Cluster/ClusterSharding (on DynamoDB)

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

# Running in sbt

```
$ sbt assembly
$ forego start
```

# Running in Docker

```
$ ./scripts/docker-build.sh
$ docker run -it --rm --env="CONFIG=/seed1.conf" $(whoami)/counter-cluster
```

# Using DynamoDB Local

```
$ brew install dynamodb-local
$ dynamodb-local
$ open http://localhost:8000/shell/
```

Set `counter-cluster.counter.dynamodb` to `http://localhost:8000` in application.conf.

# Development

```
sbt> ~re-start --- -Dconfig.resource=/seed1.conf
```
