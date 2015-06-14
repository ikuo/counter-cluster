# Setup

```
$ brew install forego
$ brew install dynamodb-local
```

Create `ids` table from [DynamoDB local JavaScript console](http://localhost:8000/shell/) as follows:

```
var params = {
    TableName: 'ids',
    KeySchema: [
        { // Required HASH type attribute
            AttributeName: 'key',
            KeyType: 'HASH',
        }
    ],
    AttributeDefinitions: [
        {
            AttributeName: 'key',
            AttributeType: 'S',
        }
    ],
    ProvisionedThroughput: {
        ReadCapacityUnits: 1,
        WriteCapacityUnits: 1,
    }
};
dynamodb.createTable(params, function(err, data) {
    if (err) print(err);
    else print(data);
});
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
