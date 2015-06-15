package buffercluster

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "entries")
class BucketRecord(key: String, bucketId: Int, content: String) {
  private var _key: String = key
  private var _bucketId: Int = bucketId
  private var _content: String = content
  def this() { this("", -1, "") }

  @DynamoDBHashKey
  def getKey = _key
  def setKey(v: String): Unit = { this._key = v }

  @DynamoDBRangeKey(attributeName= "bucket-id")
  def getBucketId = _bucketId
  def setBucketId(v: Int): Unit = { this._bucketId = v; }

  @DynamoDBAttribute
  def getContent = _content
  def setContent(v: String): Unit = { this._content = v; }
}
