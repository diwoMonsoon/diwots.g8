import sbt._
object Dependencies {
  lazy val kafka = "org.apache.kafka" %% "kafka" % "2.2.1"
  lazy val lazylogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  lazy val scalahttp = "org.scalaj" %%"scalaj-http" % "2.4.1"
  lazy val diwocommon = "com.diwo" %% "servicesbase" % "1.0"
  lazy val orgjson = "org.json" % "json" % "20180130"
  lazy val memsql = "com.memsql" % "memsql-connector_2.11" % "2.0.6"
  lazy val mysqlconnector = "mysql" % "mysql-connector-java" % "5.1.16"
  lazy val akkaspray = "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.3"
  lazy val gson ="com.google.code.gson" % "gson" % "2.8.0"
  lazy val json4s =  "org.json4s" %% "json4s-core" % "3.6.6"
  lazy val json4sNative = "org.json4s" %% "json4s-native" % "3.6.6"
  lazy val akkatracing = "com.github.levkhomich" %% "akka-tracing-http" % "0.6.1"
  lazy val playzipkin = "io.zipkin.brave.play" %% "play-zipkin-tracing-akka" % "3.0.1"
  lazy val akkatracingcore = "com.github.levkhomich" %% "akka-tracing-core" % "0.6.1"
}
