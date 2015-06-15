package buffercluster

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.{document => aws}
import com.amazonaws.services.dynamodbv2.model._
import scala.collection.JavaConversions._

object DynamoDB {
  private val provider = new DefaultAWSCredentialsProviderChain()
  private val configuration = new ClientConfiguration() {
    override def getMaxErrorRetry: Int = 3
    override def getConnectionTimeout: Int = 2000
  }
  val client: AmazonDynamoDBClient = new AmazonDynamoDBClient(provider.getCredentials, configuration)
  client.setEndpoint("http://localhost:8000", "", "ap-northeast-1");
  val mapper: DynamoDBMapper = new DynamoDBMapper(client);
  val db: aws.DynamoDB = new aws.DynamoDB(client)

  def createTables: Unit = {
    val keySchema:List[KeySchemaElement] = List(
      new KeySchemaElement().withAttributeName("key").withKeyType(KeyType.HASH)
    )
    val result = DynamoDB.client.createTable(
      new CreateTableRequest("entries", keySchema)
        .withAttributeDefinitions(
          new AttributeDefinition().withAttributeName("key").withAttributeType(ScalarAttributeType.N)
        )
        .withProvisionedThroughput(
          new ProvisionedThroughput().withReadCapacityUnits(4l).withWriteCapacityUnits(2l)
        )
    )
    println(result)
  }
}
