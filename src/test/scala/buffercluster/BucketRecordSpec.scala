package buffercluster
import org.specs2.mutable._

class BucketRecord extends SpecificationLike {
  "BucketRecord" >> {
    "it saves and scans" in {
      val mapper = DynamoDB.mapper
      val bucket = new BucketRecord("key001", 0, "key001=value001,key002=value002")
      mapper.save(bucket)

      mapper.query(classOf[BucketRecord])
      ok
    }
  }
}
