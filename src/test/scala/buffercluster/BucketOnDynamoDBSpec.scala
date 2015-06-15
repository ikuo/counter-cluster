package buffercluster
import org.specs2.mutable._

class BucketOnDynamoDBSpec extends SpecificationLike {
  "BucketOnDynamoDB" >> {
    "#save" >> {
      "it saves content" in {
        val mapper = DynamoDB.mapper
        val bucket = new BucketOnDynamoDB("key001", 0, "key001=value001,key002=value002")
        mapper.save(bucket)
        ok
      }
    }
  }
}
