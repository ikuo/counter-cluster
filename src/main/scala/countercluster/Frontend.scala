package countercluster
import akka.cluster._
import akka.cluster.sharding._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import ClusterEvent._
import scala.concurrent.duration._
import scala.util.Random
import kamon.Kamon
import kamon.trace.Tracer
import com.typesafe.config.ConfigFactory

class Frontend extends Actor with ActorLogging {
  implicit val ec = context.dispatcher
  implicit val timeout = Timeout(1.seconds)
  val config = ConfigFactory.load.getConfig("counter-cluster.frontend")
  val initialDelay = config.getLong("initial-delay-millis").millis
  val interval = config.getLong("interval-millis").millis
  val numOfKeys = config.getInt("num-of-keys")
  val counter = ClusterSharding(context.system).shardRegion(Counter.shardingName)
  val random = new Random(0)

  override def preStart: Unit = {
    super.preStart
    context.system.scheduler.schedule(initialDelay, interval) {
      val trace = Kamon.tracer.newContext("frontend")
      val key = List("key", random.nextInt(numOfKeys)).mkString
      counter.ask(Counter.Post(key)).
        recover { case err => log.error(err.getMessage) }.
        map(i => println(i)).
        onComplete(_ => trace.finish())
    }
  }

  def receive = {
    case () => ()
  }
}
