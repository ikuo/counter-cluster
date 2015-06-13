package buffercluster

import akka.actor._
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object Main {
  val roles = ConfigFactory.load.getConfig("akka.cluster").getStringList("roles").asScala
  val primaryRole: Option[String] = roles.headOption
  val configResource = System.getProperty("config.resource")
  implicit val system = ActorSystem("frontend")

  def main(args: Array[String]): Unit = {
    primaryRole match {
      case Some("frontend") =>
        val frontend = system.actorOf(Props(classOf[Frontend]), "frontend")
        frontend ! (("key1", "value1"))
      case Some("seed") =>
        println(s"Starting seed with $configResource")
      case role => fatal(s"Unexpected role $role")
    }
  }

  private def fatal(msg: String): Unit = {
    println(msg)
    system.terminate
  }
}
