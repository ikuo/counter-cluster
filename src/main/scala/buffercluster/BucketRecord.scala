package buffercluster
import com.amazonaws.services.dynamodbv2.datamodeling._

@DynamoDBTable(tableName = "entries")
case class BucketRecord(var key: String, var bucketId: Int, var content: String) {
  def this(key: String) = this(key, -1, "")
  def this() = this("")

  @DynamoDBHashKey
  def getKey = key
  def setKey(v: String): Unit = { this.key = v }

  @DynamoDBRangeKey(attributeName= "bucket-id")
  def getBucketId = bucketId
  def setBucketId(v: Int): Unit = { this.bucketId = v; }

  @DynamoDBAttribute
  def getContent = content
  def setContent(v: String): Unit = { this.content = v; }
}
