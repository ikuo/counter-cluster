package buffercluster

import akka.actor._
import akka.cluster.sharding._
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object Main {
  val roles = ConfigFactory.load.getConfig("akka.cluster").getStringList("roles").asScala
  val primaryRole: String = roles.headOption.getOrElse(sys.error("akka.cluster.roles is empty"))
  val configResource = System.getProperty("config.resource")

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("cluster")

    ClusterSharding(system).start(
      typeName = Buffer.shardingName,
      entryProps = Some(Props[Buffer]),
      roleOverride = None,
      rememberEntries = false,
      idExtractor = idExtractor,
      shardResolver = shardResolver)

    primaryRole match {
      case "buffer" => system.actorOf(Props(classOf[Buffer]), "buffer")
      case "frontend" => system.actorOf(Props(classOf[Frontend]), "frontend")
      case "seed" => println(s"Starting seed with $configResource")
      case role => fatal(s"Unexpected role $role")
    }
    Thread.sleep(10000)
  }

  val idExtractor: ShardRegion.IdExtractor = {
    case msg: Buffer.Post => (msg.key, msg)
  }

  val shardResolver: ShardRegion.ShardResolver = {
    case msg: Buffer.Post => (msg.key.hashCode % 30).toString
  }

  private def fatal(msg: String): Unit = {
    println(msg)
    //system.terminate
  }
}
