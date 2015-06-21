package countercluster
import akka.actor._
import kamon.Kamon
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object Main {
  val roles = ConfigFactory.load.getConfig("akka.cluster").getStringList("roles").asScala
  val primaryRole: String = roles.headOption.getOrElse(sys.error("akka.cluster.roles is empty"))

  def main(args: Array[String]): Unit = {
    Kamon.start
    implicit val system = ActorSystem("cluster")
    primaryRole match {
      case "seed" => Counter.startSharding()
      case "counter" => Counter.startSharding(proxyOnlyMode = false)
      case "frontend" =>
        Counter.startSharding()
        system.actorOf(Props(classOf[Frontend]), "frontend")
      case role => sys.error(s"Unexpected role $role")
    }
  }
}
