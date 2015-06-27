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

class Frontend extends Actor with ActorLogging {
  implicit val ec = context.dispatcher
  val counter = ClusterSharding(context.system).shardRegion(Counter.shardingName)
  var count = 0
  val random = new Random(0)
  implicit val timeout = Timeout(1.seconds)
  val numOfKeys = 1000

  override def preStart: Unit = {
    context.system.scheduler.schedule(0.millis, 20.millis) {
      val trace = Kamon.tracer.newContext("frontend")
      counter.ask(Counter.Post(s"key${random.nextInt(numOfKeys)}")).
        recover { case err => err.printStackTrace; log.error(err.getMessage) }.
        map { i => println(i) }.
        onComplete { _ => trace.finish() }
    }
    super.preStart
  }

  def receive = {
    case Frontend.GetCount => sender ! count
  }
}

object Frontend {
  object GetCount
}
