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

    primaryRole match {
      case "seed" => startSharding()
      case "buffer" => startSharding(Some(Props(classOf[Buffer])))
      case "frontend" =>
        startSharding()
        system.actorOf(Props(classOf[Frontend]), "frontend")
      case role => fatal(s"Unexpected role $role")
    }
  }

  def startSharding(entryProps: Option[Props] = None)(implicit system: ActorSystem): Unit = {
    ClusterSharding(system).start(
      typeName = Buffer.shardingName,
      entryProps = entryProps,
      roleOverride = None,
      rememberEntries = true,
      idExtractor = idExtractor,
      shardResolver = shardResolver)
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
