package buffercluster
import org.specs2.mutable._
import org.specs2.specification.Scope
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

class BucketsSpec extends SpecificationLike {
  "Buckets" >> {
    "#loadBuckets" >> {
      "it saves content" in new BucketsCheck {
        it.put("key001", "value001")
        it.put("key001", "value002")
        it.put("key003", "value003")
        it.save.foreach(Await.result(_, 2.seconds))
        Await.result(it.load, 2.seconds)
        it.buckets.size must be_==(1)
        it.buckets(0).pieces.size must be_==(2)
        it.buckets(0).pieces must not contain(Buckets.Piece("key001", "value001"))
        it.buckets(0).pieces must contain(Buckets.Piece("key001", "value002"))
        it.buckets(0).pieces must contain(Buckets.Piece("key003", "value003"))
      }
    }
  }

  trait BucketsCheck extends Scope {
    class TargetBucket extends Buckets {
      override val persistenceId = "buckets01"
      override val ec = ExecutionContext.Implicits.global
      def put(key: String, value: String) = createOrUpdateBucketAndPiece(key, value)
      def save = saveBuckets
      def load = loadBuckets
    }
    val it = new TargetBucket
  }
}
