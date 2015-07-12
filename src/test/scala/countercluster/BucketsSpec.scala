package countercluster
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
          it.save
          it.buckets.size must be_==(1)
          it.buckets(0).pieces.size must be_==(2)
        }
      }

      "when large data" >> {
        "it saves and loads with bultiple buckets" in new BucketsCheck {
          val it = new TargetBucket("multi-bucket-test")
          (1 to 100).foreach(i => it.put(s"key$i", s"value$i"))
          it.save
          it.buckets.size must be_==(10)
          it.buckets(0).pieces.size must be_>(1)
          it.buckets(1).pieces.size must be_>(0)
        }
      }
    }
  }

  trait BucketsCheck extends Scope {
    class TargetBucket(id: String) extends Buckets[String] {
      override val persistenceId = id
      override val ec = ExecutionContext.Implicits.global
      def put(key: String, value: String) = createOrUpdateBucketAndPiece(key, value)
      def save = saveBuckets
      def load = loadBuckets
      def parse(key: String, value: String) = Piece(key, value)
      def serialize(piece: Piece): String = List(piece.key, piece.value).mkString(":")
    }
  }
}
