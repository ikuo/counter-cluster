package buffercluster
import com.amazonaws.services.dynamodbv2.datamodeling._
import com.amazonaws.services.dynamodbv2.model._
import org.specs2.mutable._
import scala.collection.mutable.ArrayBuffer

class BucketRecordSpec extends SpecificationLike {
  "BucketRecord" >> {
    "it saves and scans" in {
      val mapper = DynamoDB.mapper
      val buckets = List(
        new BucketRecord("id0a", 0, "key1:value1,key2:value2,key3:value3"),
        new BucketRecord("id0a", 1, "key4:value4,key5:value5"),
        new BucketRecord("id0b", 0, "key6:value6,key7:value7")
      )
      for (bucket <- buckets) mapper.save(bucket)

      val expr = new DynamoDBQueryExpression[BucketRecord]().
        withHashKeyValues(buckets.head).
        withSelect(Select.ALL_ATTRIBUTES)

      val it = mapper.query(classOf[BucketRecord], expr).iterator
      val results = ArrayBuffer[BucketRecord]()
      while (it.hasNext) { results.append(it.next) }
      results.size must beEqualTo(2)
      results(0).key aka "key(0)" must beEqualTo("id0a")
      results(1).key aka "key(1)" must beEqualTo("id0a")
      results(0).bucketId aka "bucketId(0)" must beEqualTo(0)
      results(1).bucketId aka "bucketId(1)" must beEqualTo(1)
      results(0).content aka "content(0)" must beEqualTo("key1:value1,key2:value2,key3:value3")
    }
  }
}
