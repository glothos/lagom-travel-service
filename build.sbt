organization in ThisBuild := "com.marcel"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "4.0.0"

lazy val jsonformats = (project in file("json-formats"))
  .settings(
    libraryDependencies ++= Seq(
      playJsonDerivedCodecs
    )
  )

lazy val `trip-services` = (project in file("."))
  .aggregate(
    `tracker-api`,
    `tracker-impl`,
    `trip-api`,
    `trip-impl`,
    `trip-stream-api`,
    `trip-stream-impl`,
    `matching-api`,
    `matching-impl`,
  )

lazy val `matching-api` = (project in file("matching-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
    )
  ).dependsOn(jsonformats, `tracker-api`, `trip-api`)

lazy val `matching-impl` = (project in file("matching-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslKafkaBroker,
      lagomScaladslPersistenceCassandra,
      macwire,
      scalaTest
    )
  ).settings(lagomForkedTestSettings).dependsOn(jsonformats, `trip-impl`, `tracker-impl`, `matching-api`)

lazy val `trip-stream-api` = (project in file("trip-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
    )
  ).dependsOn(`trip-api`)
lazy val `trip-stream-impl` = (project in file("trip-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
    )
  ).settings(lagomForkedTestSettings)
  .dependsOn(jsonformats, `trip-api`, `trip-stream-api`)
lazy val `trip-api` = (project in file ("trip-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
    )
  ).dependsOn(jsonformats, `tracker-api`)
lazy val `trip-impl` = (project in file("trip-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
    )
  ).settings(lagomForkedTestSettings).dependsOn(jsonformats, `trip-api`, `tracker-impl`, `tracker-api`)
lazy val `tracker-api` = (project in file("tracker-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      "com.uber" % "h3" % "3.6.0",
    )
  )
  .dependsOn(jsonformats)
lazy val `tracker-impl` = (project in file("tracker-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPubSub,
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
      "com.uber" % "h3" % "3.6.0",
      "com.pauldijou" %% "jwt-play-json" % "4.2.0"
    )
  ).settings(lagomForkedTestSettings)
  .dependsOn(`tracker-api`, jsonformats)



lagomCassandraCleanOnStart in ThisBuild := true
import scala.concurrent.duration._
lagomCassandraMaxBootWaitingTime in ThisBuild := 180.seconds
