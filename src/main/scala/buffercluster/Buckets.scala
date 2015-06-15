package buffercluster

import scala.concurrent._
import scala.collection.mutable.{ HashSet, ArrayBuffer, Buffer => SBuffer, HashMap }
import Buckets._

trait Buckets {
  implicit val ec: ExecutionContext
  val persistenceId: String
  val buckets = ArrayBuffer[Bucket]()
  val pieces = HashMap[String, Tuple2[Piece, Bucket]]()

  protected def createOrUpdateBucketAndPiece(key: String, value: String): Unit = {
    val (piece, bucket) = pieces.getOrElseUpdate(key, newPiece(key, value))
    piece.value = value
    bucket.isDirty = true
  }

  protected def saveBuckets: Seq[Future[Unit]] =
    buckets.zipWithIndex.withFilter(_._1.isDirty).map {
      case (bucket, index) => bucket.save(persistenceId, index)
    }

  protected def loadBuckets(implicit ec: ExecutionContext): Future[Unit] =
    Future(()) // TODO

  private def newPiece(key: String, value: String) = {
    val piece = Piece(key, value)
    val bucket: Bucket = buckets.lastOption.filterNot(_.isFull).getOrElse(newBucket)
    bucket.pieces.add(piece)
    (piece, bucket)
  }

  private def newBucket = {
    val bkt = Bucket()
    buckets.append(bkt)
    bkt
  }
}

object Buckets {
  val mapper = DynamoDB.mapper

  case class Piece(key: String, var value: String) {
    val serialized = List(key, value).mkString(":")
  }
  object Piece {
    def apply(string: String): Piece = string.split(":").toList match {
      case key :: value :: Nil => Piece(key, value)
      case _ => sys.error(s"Malformed piece $string")
    }
  }

  case class Bucket(pieces: HashSet[Piece] = HashSet.empty[Piece], var isDirty: Boolean = false) {
    def isFull = pieces.size >= 10
    def serialized = pieces.map(_.serialized).mkString(",")
    def save(persistenceId: String, bucketId: Int)(implicit ec: ExecutionContext): Future[Unit] = {
      val entity = new BucketRecord(persistenceId, bucketId, s"dummy")
      Future {
        mapper.save(entity)
        this.isDirty = false
      }
    }
  }
  object Bucket {
    def apply(string: String): Bucket = Bucket(pieces = HashSet(string.split(",").map(Piece(_)): _*))
  }
}
