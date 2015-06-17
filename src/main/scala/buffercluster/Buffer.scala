package buffercluster
import akka.actor._
import akka.cluster._
import akka.cluster.sharding._
import akka.persistence._
import ClusterEvent._

class Buffer extends PersistentActor with ActorLogging with Buckets {
  import Buffer._
  implicit val ec = context.dispatcher

  override val persistenceId: String = shardingName + "-" + self.path.name
  override def preStart: Unit = loadBuckets.map(_ => self ! Recover())

  val receiveCommand: Receive = {
    case Buffer.Post(key, value) =>
      createOrUpdateBucketAndPiece(key, value)
      log.info(s"Post $key=$value")

    case Buffer.Get(key) => sender ! pieces.get(key)
  }

  val receiveRecover: Receive = {
    case msg => log.info(s"receiveRecover $msg")
  }

  override def postStop: Unit = {
    saveBuckets
    super.postStop()
  }
}

object Buffer {
  def shardKey(id: String) = (id.hashCode % numOfShards).toString
  def entryKey(id: String) = (id.hashCode % (numOfShards * 4)).toString
  case class Post(id: String, value: String)
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
