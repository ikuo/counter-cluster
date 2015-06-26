A playground of Akka Cluster/ClusterSharding (on DynamoDB)

# Setup

```
$ brew install forego
$ brew install dynamodb-local
$ pushd /tmp && git clone git@github.com:kamon-io/Kamon.git && git checkout release-akka-2.4 && sbt publishLocal
$ wget http://central.maven.org/maven2/org/aspectj/aspectjweaver/1.8.6/aspectjweaver-1.8.6.jar
```

Confirm that `dynamodb-local` should work:

```
$ dynamodb-local
$ open http://localhost:8000/shell/
```

Create table:

```
sbt console
> countercluster.createTables
```


# Running

```
$ sbt assembly
$ forego start
```

# Development

```
sbt> ~re-start --- -Dconfig.resource=/frontend.conf
```
