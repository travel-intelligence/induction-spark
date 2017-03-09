import AssemblyKeys._

assemblyOption in assembly :=
  (assemblyOption in assembly).value.copy (includeScala = false)

assemblySettings

test in assembly := {}

// //// Java libraries
// Date-Time
val libjoda = "joda-time" % "joda-time" % "2.2"

// JSON
// Note: version 3.2.11 not compatible with Spark 1.4+
val libjson4score = "org.json4s" % "json4s-core_2.10" % "3.2.10"
val libjson4sjackson = "org.json4s" % "json4s-jackson_2.10" % "3.2.10"

// MySQL
val libmysql = "mysql" % "mysql-connector-java" % "5.1.34"

// //// Scala libraries
// Test
val libspecs2 = "org.specs2" %% "specs2" % "2.4.2" % "test"

// Breeze (linear algebra and numerical algorithms)
val libbreeze = "org.scalanlp" %% "breeze" % "0.11.2"
val libbreezenative = "org.scalanlp" %% "breeze-natives" % "0.11.2"

// Apache Avro
val libavro = "org.apache.avro" % "avro" % "1.7.7"
val libavroparquet = "com.twitter" % "parquet-avro" % "1.6.0"
val libavrochill = "com.twitter" %% "chill-avro" % "0.6.0"

// Spark
val sparkVersion="1.4.1"
val libsparkcore = "org.apache.spark" %% "spark-core" % sparkVersion
val libsparksql = "org.apache.spark" %% "spark-sql" % sparkVersion
val libsparkmllib = "org.apache.spark" %% "spark-mllib" % sparkVersion
val libsparkcsv = "com.databricks" %% "spark-csv" % "1.0.3"
val libsparkhive = "org.apache.spark" %% "spark-hive" % sparkVersion
val libsparkcassandra = "com.datastax.spark" %% "spark-cassandra-connector-java" % "1.2.0"

lazy val commonSettings = Seq (
	organization := "com.amadeus.ti",
	version := "0.1.0",
	scalaVersion := "2.10.5",
	sbtVersion := "0.13.13"
)

lazy val libSettings = Seq (
  libraryDependencies += libjoda,
  libraryDependencies += libspecs2,
  libraryDependencies += libbreeze,
  libraryDependencies += libbreezenative,
  libraryDependencies += libsparkcore,
  libraryDependencies += libsparksql,
  libraryDependencies += libsparkmllib,
  libraryDependencies += libsparkcsv,
  libraryDependencies += libsparkhive,
  libraryDependencies += libsparkcassandra,
  libraryDependencies += libmysql,
  libraryDependencies += libjson4score,
  libraryDependencies += libjson4sjackson,
  libraryDependencies += libavro,
  libraryDependencies += libavroparquet,
  libraryDependencies += libavrochill
)

lazy val root = (project in file(".")).
	settings(commonSettings: _*).
	settings(libSettings: _*).
	settings(
		name := "induction-spark-yarn"
	)

checksums in update := Nil

javacOptions in Compile ++= Seq ("-source", "1.6",  "-target", "1.6")

scalacOptions += "-target:jvm-1.6"

scalacOptions += "-deprecation"

scalacOptions += "-feature"

parallelExecution in Test := false

resolvers ++= Seq (
  Resolver.sonatypeRepo("public"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  "Local repository" at "http://localhost/artifacts/mavenrepo/",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
  "Apache HBase" at "https://repository.apache.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Twitter" at "http://maven.twttr.com/",
  Resolver.mavenLocal
)

//
fork := true

// Avro (needs the SBT Avro plug-in from "com.cavorite")
Seq (sbtavro.SbtAvro.avroSettings: _*)

(stringType in avroConfig) := "String"

javaSource in sbtavro.SbtAvro.avroConfig := (sourceDirectory in Compile).value / "java"

// Artifacts
publishTo := Some ("Local Maven Repo" at "http://localhost/artifacts/mavenrepo/")

testOptions in Test += Tests.Argument (TestFrameworks.Specs2, "console", "junitxml")

packageArchetype.java_application

net.virtualvoid.sbt.graph.Plugin.graphSettings
