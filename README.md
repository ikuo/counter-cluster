# Setup

```
$ brew install forego
$ brew install dynamodb-local
sbt assembly
forego start
```

# Running

```
sbt> ~re-start --- -Dconfig.resource=/frontend.conf
```
