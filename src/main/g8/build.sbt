organization in ThisBuild := "$organization$"
version in ThisBuild := "$version$"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test


import com.typesafe.sbt.packager.MappingsHelper
import com.lightbend.lagom.core.LagomVersion
import sbt.Keys.libraryDependencies

scalacOptions ++=Seq(
  "-deprecation",
  "-unchecked",
  "-encoding",
  "UTF-8",
  "-Xlint"
)


lazy val `$name;format="norm"$` = (project in file("."))
  .aggregate(`$name;format="norm"$-api`, `$name;format="norm"$-impl`)

lazy val `$name;format="norm"$-api` = (project in file("$name;format="norm"$-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      Dependencies.lazylogging,
      Dependencies.scalahttp,
      Dependencies.gson,
      Dependencies.json4s,
      Dependencies.json4sNative,
      Dependencies.playzipkin
    )
  )

lazy val `$name;format="norm"$-impl` = (project in file("$name;format="norm"$-impl"))
  .enablePlugins(LagomScala)
  .settings(
    crossScalaVersions := Seq("2.11.11", "2.12.8"),
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest  ,
      Dependencies.lazylogging,
      Dependencies.scalahttp,
      Dependencies.kafka,
      Dependencies.gson,
      Dependencies.json4s,
      Dependencies.json4sNative,
      "org.json" % "json" % "20180130",
      Dependencies.playzipkin
    )
  )
  .settings(lagomForkedTestSettings)
  .settings(lagomServiceHttpPort := 58208)
  .dependsOn(`$name;format="norm"$-api`)


val lagomScaladslAkkaDiscovery = "com.lightbend.lagom" %% "lagom-scaladsl-akka-discovery-service-locator" % LagomVersion.current

def dockerSettings = Seq(
  dockerUpdateLatest := true,
  dockerBaseImage := "adoptopenjdk/openjdk8",
  dockerUsername := sys.props.get("docker.username"),
  dockerRepository := sys.props.get("docker.registry")
)

lagomServiceLocatorPort in ThisBuild := $locatorport$
lagomServiceGatewayPort in ThisBuild := $gatewayport$

