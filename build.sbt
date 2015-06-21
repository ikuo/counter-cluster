name := "buffer-cluster"

version       := s"0.1-SNAPSHOT"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

resolvers += "bseibel at bintray" at "http://dl.bintray.com/bseibel/release"

resolvers += ("jdgoldie at bintray" at "http://dl.bintray.com/jdgoldie/maven")

libraryDependencies ++= {
  val akkaVersion = "2.4-M1"
  val awsSdkVersion = "1.9.40"
  val kamonVersion = "0.4.0"
  Seq(
    "com.typesafe.akka" % "akka-cluster_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-cluster-sharding_2.11" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence-experimental" % akkaVersion,
    "com.github.jdgoldie" %% "akka-persistence-shared-inmemory" % "1.0.16",
    "com.amazonaws" % "aws-java-sdk-dynamodb" % awsSdkVersion,
    "com.amazonaws" % "aws-java-sdk-core" % awsSdkVersion,
    "io.kamon" %% "kamon-core" % kamonVersion,
    "io.kamon" %% "kamon-scala" % kamonVersion,
    "io.kamon" %% "kamon-akka" % kamonVersion,
    "io.kamon" %% "kamon-datadog" % kamonVersion,
    "org.specs2" %% "specs2-core" % "3.6" % "test"
  )
}

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName.endsWith("-javadoc.jar")}
}

test in assembly := {}

Revolver.settings
