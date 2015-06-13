name := "buffer-cluster"

version       := s"0.1-SNAPSHOT"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-cluster_2.11" % "2.4-M1",
  "com.typesafe.akka" % "akka-cluster-sharding_2.11" % "2.4-M1"
)
