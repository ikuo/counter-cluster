package countercluster

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.{document => aws}
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.regions.Regions
import scala.collection.JavaConversions._
import com.typesafe.config.ConfigFactory

object DynamoDB {
  private val provider = new DefaultAWSCredentialsProviderChain()
  private val awsConfig = new ClientConfiguration() {
    override def getMaxErrorRetry: Int = 3
    override def getConnectionTimeout: Int = 2000
  }
  val client: AmazonDynamoDBClient = new AmazonDynamoDBClient(provider.getCredentials, awsConfig)
  val config = ConfigFactory.load.getConfig("counter-cluster.counter.dynamodb")
  val endpoint = config.getString("endpoint")
  client.setEndpoint(endpoint, "", "ap-northeast-1")
  val mapper: DynamoDBMapper = new DynamoDBMapper(client)
  val db: aws.DynamoDB = new aws.DynamoDB(client)

  def createTables: Unit = {
    val keySchema: List[KeySchemaElement] = List(
      new KeySchemaElement().withAttributeName("key").withKeyType(KeyType.HASH),
      new KeySchemaElement().withAttributeName("bucket-id").withKeyType(KeyType.RANGE)
    )
    val result = DynamoDB.client.createTable(
      new CreateTableRequest("entries", keySchema)
        .withAttributeDefinitions(
          new AttributeDefinition().withAttributeName("key").withAttributeType(ScalarAttributeType.S)
        )
        .withAttributeDefinitions(
          new AttributeDefinition().withAttributeName("bucket-id").withAttributeType(ScalarAttributeType.N)
        )
        .withProvisionedThroughput(
          new ProvisionedThroughput().withReadCapacityUnits(4l).withWriteCapacityUnits(2l)
        )
    )
    println(result)
  }

  def deleteTables: Unit = {
    DynamoDB.client.deleteTable(new DeleteTableRequest("entries"))
  }
}
