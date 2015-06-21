name := "buffer-cluster"

version       := s"0.1-SNAPSHOT"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

resolvers += "bseibel at bintray" at "http://dl.bintray.com/bseibel/release"

resolvers += ("jdgoldie at bintray" at "http://dl.bintray.com/jdgoldie/maven")

resolvers += Resolver.file("ivy-local", file(Path.userHome.absolutePath + "/.ivy2/local"))(Resolver.ivyStylePatterns)

libraryDependencies ++= {
  val akkaVersion = "2.4-M1"
  val awsSdkVersion = "1.9.40"
  val kamonVersion = "0.4.1-35bb09838d1b0a2a1e36cd68c2db158b728a2981"
  val kamonSuffix = "_akka-2.4"
  Seq(
    "com.typesafe.akka" % "akka-cluster_2.11" % akkaVersion,
    "com.typesafe.akka" % "akka-cluster-sharding_2.11" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence-experimental" % akkaVersion,
    "com.github.jdgoldie" %% "akka-persistence-shared-inmemory" % "1.0.16",
    "com.amazonaws" % "aws-java-sdk-dynamodb" % awsSdkVersion,
    "com.amazonaws" % "aws-java-sdk-core" % awsSdkVersion,
    "io.kamon" %% s"kamon-core$kamonSuffix" % kamonVersion,
    "io.kamon" %% s"kamon-scala$kamonSuffix" % kamonVersion,
    "io.kamon" %% s"kamon-akka$kamonSuffix" % kamonVersion,
    "io.kamon" %% s"kamon-akka-remote$kamonSuffix" % kamonVersion,
    "io.kamon" %% s"kamon-datadog$kamonSuffix" % kamonVersion,
    "org.specs2" %% "specs2-core" % "3.6" % "test"
  )
}

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName.endsWith("-javadoc.jar")}
}

test in assembly := {}

Revolver.settings
