package buffercluster
import scala.concurrent.duration._
import akka.actor._
import akka.cluster._
import akka.cluster.sharding._
import akka.persistence._
import ClusterEvent._

class Buffer extends PersistentActor with ActorLogging with Buckets[String] {
  import Buffer._
  implicit val ec = context.dispatcher

  override val persistenceId: String = shardingName + "-" + self.path.name

  override val receiveCommand: Receive = {
    case Buffer.Post(key) =>
      createOrUpdateBucketAndPiece(key, "dummy")
      log.info(s"Post $key")
      sender ! 5

    case Buffer.Get(key) => sender ! pieces.get(key)
  }

  override val receiveRecover: Receive = {
    case msg => log.info(s"receiveRecover $msg")
  }

  override def preStart: Unit = {
    loadBuckets.map { _ =>
      context.system.scheduler.schedule(0.seconds, 10.seconds)(saveBuckets)
      self ! Recover()
    }
    super.preStart()
  }

  override def postStop: Unit = {
    saveBuckets
    super.postStop()
  }

  protected def parse(key: String, value: String) = Piece(key, value)
  protected def serialize(piece: Piece): String = List(piece.key, piece.value).mkString(":")
}

object Buffer {
  def shardKey(id: String) = (id.hashCode % numOfShards).toString
  def entryKey(id: String) = (id.hashCode % (numOfShards * 4)).toString
  case class Post(id: String)
  case class Get(key: String)

  val shardingName = "Buffer"
  val plannedMaxNodes = 6
  val numOfShards = plannedMaxNodes * 10
  val idExtractor: ShardRegion.IdExtractor = {
    case msg: Buffer.Post => (entryKey(msg.id), msg)
  }
  val shardResolver: ShardRegion.ShardResolver = {
    case msg: Buffer.Post => shardKey(msg.id)
  }

  def startSharding(proxyOnlyMode: Boolean = true)(implicit system: ActorSystem): Unit = {
    ClusterSharding(system).start(
      typeName = Buffer.shardingName,
      entryProps = if (proxyOnlyMode) None else Some(Props(classOf[Buffer])),
      roleOverride = None,
      rememberEntries = true,
      idExtractor = idExtractor,
      shardResolver = shardResolver)
  }
}
