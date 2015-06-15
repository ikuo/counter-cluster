name := "buffer-cluster"

version       := s"0.1-SNAPSHOT"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

resolvers += "bseibel at bintray" at "http://dl.bintray.com/bseibel/release"

resolvers += ("jdgoldie at bintray" at "http://dl.bintray.com/jdgoldie/maven")

libraryDependencies ++= {
  val awsSdkVersion = "1.9.40"
  Seq(
    "com.typesafe.akka" % "akka-cluster_2.11" % "2.4-M1",
    "com.typesafe.akka" % "akka-cluster-sharding_2.11" % "2.4-M1",
    "com.typesafe.akka" %% "akka-persistence-experimental" % "2.4-M1",
    "com.github.jdgoldie" %% "akka-persistence-shared-inmemory" % "1.0.16",
    "com.amazonaws" % "aws-java-sdk-dynamodb" % awsSdkVersion,
    "com.amazonaws" % "aws-java-sdk-core" % awsSdkVersion,
    "org.specs2" %% "specs2-core" % "3.6" % "test"
  )
}

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName.endsWith("-javadoc.jar")}
}

Revolver.settings
