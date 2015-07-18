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
  import Frontend._
  implicit val ec = context.dispatcher
  implicit val timeout = Timeout(1.seconds)
  val counter = ClusterSharding(context.system).shardRegion(Counter.shardingName)
  val random = new Random()
  private object Metrics {
    val sent = Kamon.metrics.counter("frontend.sent")
    val recv = Kamon.metrics.counter("frontend.recv")
    val error = Kamon.metrics.counter("frontend.error")
  }

  def receive = {
    case 'Run =>
      context.system.scheduler.schedule(initialDelay, interval) { postMessage }
  }

  private def postMessage: Unit = {
    val trace = Kamon.tracer.newContext("frontend")
    val key = List("key", random.nextInt(numOfKeys)).mkString
    Metrics.sent.increment()
    counter.ask(Counter.Post(key)).
      recover {
        case err =>
          Metrics.error.increment()
          log.error(s"Error on $key(${Counter.shardKey(key)}, ${Counter.entryKey(key)}): ${err.getMessage}")
      }.
      map { i =>
        Metrics.recv.increment()
        log.info(s"Recv: $i")
      }.
      onComplete(_ => trace.finish())
  }
}

object Frontend {
  val config = ConfigFactory.load.getConfig("counter-cluster.frontend")
  val initialDelay = config.getLong("initial-delay-millis").millis
  val interval = config.getLong("interval-millis").millis
  val numOfKeys = config.getInt("num-of-keys")
  val numOfActors = config.getInt("num-of-actors")
  def run(system: ActorSystem): Unit = {
    for (i <- (1 to numOfActors)) {
      system.actorOf(Props(classOf[Frontend]), s"frontend-$i") ! 'Run
    }
  }
}
