package buffercluster
import org.specs2.mutable._
import org.specs2.specification.Scope
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

class BucketsSpec extends SpecificationLike {
  "Buckets" >> {
    "#{seva, load}Buckets" >> {
      "when small data" >> {
        "it saves & load with a single bucket" in new BucketsCheck {
          val it = new TargetBucket("single-bucket-test")
          it.put("key001", "value001")
          it.put("key001", "value002")
          it.put("key003", "value003")
          val saved = it.save
          saved.size must be_==(1)
          saved.foreach(Await.result(_, 5.seconds))
          Await.result(it.load, 5.seconds)

          it.buckets.size must be_==(1)
          it.buckets(0).pieces.size must be_==(2)
          it.buckets(0).pieces must not contain(Buckets.Piece("key001", "value001"))
          it.buckets(0).pieces must contain(Buckets.Piece("key001", "value002"))
          it.buckets(0).pieces must contain(Buckets.Piece("key003", "value003"))
        }
      }

      "when large data" >> {
        "it saves and loads with bultiple buckets" in new BucketsCheck {
          val it = new TargetBucket("multi-bucket-test")
          (1 to 100).foreach(i => it.put(s"key$i", s"value$i"))
          val saved = it.save
          saved.size must be_>(1)
          saved.foreach(Await.result(_, 5.seconds))
          Await.result(it.load, 5.seconds)

          it.buckets.size must be_==(saved.size)
          it.buckets(0).pieces.size must be_>(1)
          it.buckets(1).pieces.size must be_>(0)
        }
      }
    }
  }

  trait BucketsCheck extends Scope {
    class TargetBucket(id: String) extends Buckets {
      override val persistenceId = id
      override val ec = ExecutionContext.Implicits.global
      def put(key: String, value: String) = createOrUpdateBucketAndPiece(key, value)
      def save = saveBuckets
      def load = loadBuckets
    }
  }
}
