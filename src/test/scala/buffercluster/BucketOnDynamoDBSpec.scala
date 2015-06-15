package buffercluster

import org.specs2.mutable._

class BucketOnDynamoDBSpec extends SpecificationLike {
  "BucketOnDynamoDB" >> {
    "#save" >> {
      "it saves content" in {
        ok
      }
    }
  }
}
