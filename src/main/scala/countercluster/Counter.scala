package countercluster
import scala.concurrent.duration._
import akka.actor._
import akka.cluster._
import akka.cluster.sharding._
import akka.persistence._
import kamon.Kamon
import com.typesafe.config.ConfigFactory
import ClusterEvent._

class Counter extends PersistentActor with ActorLogging with Buckets[SimpleMovingAverage] {
  import Counter._
  implicit val ec = context.dispatcher
  private val metrics = Kamon.metrics.counter("counter.receive.command")

  override val persistenceId: String = shardingName + "-" + self.path.name

  override val receiveCommand: Receive = {
    case Counter.Post(key) =>
      metrics.increment()
      try {
        val value = getValue(key).getOrElse(SimpleMovingAverage(10, 6))
        value.increment
        createOrUpdateBucketAndPiece(key, value)
        sender ! value.value
      } catch {
        case err: Throwable => log.error("err", err)
      }

    case SaveBuckets      => saveBuckets

    case Counter.Get(key) => sender ! getValue(key).map(_.value)
  }

  override val receiveRecover: Receive = {
    case msg => log.info(s"Recovered: $buckets")
  }

  override def preStart: Unit =
    loadBuckets.map { _ =>
      context.system.scheduler.schedule(0.seconds, 10.seconds)(self ! SaveBuckets)
      self ! Recover()
    }

  override def postStop: Unit = {
    saveBuckets
    super.postStop()
  }

  protected def parse(key: String, value: String) = Piece(key, SimpleMovingAverage.parse(value))
  protected def serialize(piece: Piece): String = List(piece.key, piece.value.serialize).mkString(":")
}

object Counter {
  val shardingName = "Counter"
  val config = ConfigFactory.load.getConfig("counter-cluster.counter")
  val numOfShards = config.getInt("num-of-shards")
  val entriesPerShard = config.getInt("entries-per-shard")

  def shardKey(id: String) = (id.hashCode % numOfShards).toString
  def entryKey(id: String) = (id.hashCode % (numOfShards * entriesPerShard)).toString

  case class Post(id: String)
  case class Get(key: String)

  val idExtractor: ShardRegion.IdExtractor = {
    case msg: Counter.Post => (entryKey(msg.id), msg)
  }
  val shardResolver: ShardRegion.ShardResolver = {
    case msg: Counter.Post => shardKey(msg.id)
  }

  def startSharding(proxyOnlyMode: Boolean = true)(implicit system: ActorSystem): Unit = {
    ClusterSharding(system).start(
      typeName = Counter.shardingName,
      entryProps = if (proxyOnlyMode) None else Some(Props(classOf[Counter])),
      roleOverride = None,
      rememberEntries = true,
      idExtractor = idExtractor,
      shardResolver = shardResolver)
  }
}
