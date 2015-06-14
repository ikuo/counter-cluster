package buffercluster

import scala.collection.mutable.{ HashSet, ArrayBuffer, Buffer => SBuffer, HashMap }
import Buckets._

trait Buckets {
  val buckets = ArrayBuffer[Bucket]()
  val pieces = HashMap[String, Tuple2[Piece, Bucket]]()

  protected def createOrUpdateBucketAndPiece(key: String, value: String): Unit = {
    val (piece, bucket) = pieces.getOrElseUpdate(key, newPiece(key, value))
    piece.value = value
    bucket.isDirty = true
  }

  protected def saveBuckets: Unit = buckets.withFilter(_.isDirty).foreach(_.save)

  protected def loadBuckets: Unit = () // TODO

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
  case class Piece(key: String, var value: String)
  case class Bucket(pieces: HashSet[Piece] = HashSet.empty[Piece], var isDirty: Boolean = false) {
    def isFull = pieces.size >= 10
    def save: Unit = {
      //TODO
      this.isDirty = false
    }
  }
}
