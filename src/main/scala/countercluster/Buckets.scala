package countercluster

import scala.concurrent._
import scala.collection.mutable.{ HashSet, ArrayBuffer, Buffer => SBuffer, HashMap }
import com.amazonaws.services.dynamodbv2.datamodeling._

trait Buckets[Value] {
  implicit val ec: ExecutionContext
  val persistenceId: String
  val buckets = ArrayBuffer[Bucket]()
  val pieces = HashMap[String, Tuple2[Piece, Bucket]]()

  def getValue(key: String): Option[Value] = pieces.get(key).map(_._1.value)

  protected def createOrUpdateBucketAndPiece(key: String, value: Value): Unit = {
    val (piece, bucket) = pieces.getOrElseUpdate(key, newPiece(key, value))
    piece.value = value
    bucket.isDirty = true
  }

  protected def saveBuckets: Seq[Future[Unit]] =
    buckets.zipWithIndex.withFilter(_._1.isDirty).map {
      case (bucket, index) => bucket.save(persistenceId, index)
    }

  protected def loadBuckets(implicit ec: ExecutionContext): Future[Unit] = Future {
    buckets.clear()
    val it = {
      val query = new DynamoDBQueryExpression().
        withHashKeyValues(new BucketRecord(persistenceId))
      val list = mapper.query(classOf[BucketRecord], query)
      list.iterator
    }
    while (it.hasNext) { buckets.append(Bucket(it.next.content)) }
  }

  private def newPiece(key: String, value: Value) = {
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

  protected def parse(key: String, value: String): Piece
  protected def serialize(piece: Piece): String

  val mapper = DynamoDB.mapper

  case class Piece(key: String, var value: Value)
  object Piece {
    def apply(string: String): Piece = string.split(":").toList match {
      case key :: value :: Nil => parse(key, value)
      case _ => sys.error(s"Malformed piece $string")
    }
  }

  case class Bucket(pieces: HashSet[Piece] = HashSet.empty[Piece], var isDirty: Boolean = false) {
    import Bucket._
    def isFull = pieces.size >= 10
    def serialized = pieces.map(serialize(_)).mkString(delimiter)
    def save(persistenceId: String, bucketId: Int)(implicit ec: ExecutionContext): Future[Unit] = {
      val entity = new BucketRecord(persistenceId, bucketId, serialized)
      Future {
        mapper.save(entity)
        this.isDirty = false
      }
    }
  }
  object Bucket {
    val delimiter = "/"
    def apply(string: String): Bucket = Bucket(pieces = HashSet(string.split(delimiter).map(Piece(_)): _*))
  }
}
