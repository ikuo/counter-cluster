package buffercluster;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "entries")
public class BucketOnDynamoDB {
  private String key;
  private int bucketId;
  private String content;

  public BucketOnDynamoDB() { this("", -1, ""); }

  public BucketOnDynamoDB(String key, int bucketId, String content) {
    this.key = key;
    this.bucketId = bucketId;
    this.content = content;
  }

  @DynamoDBHashKey
  public String getKey() { return key; }
  public void setKey(String v) { this.key = v; }

  @DynamoDBRangeKey(attributeName= "bucket-id")
  public int getBucketId() { return bucketId; }
  public void setBucketId(int v) { this.bucketId = v; }

  @DynamoDBAttribute
  public String getContent() { return content; }
  public void setContent(String v) { this.content = v; }
}
