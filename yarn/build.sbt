
// import AssemblyKeys._

assemblyOption in assembly :=
  (assemblyOption in assembly).value.copy (includeScala = false)

// assemblySettings

// test in assembly := {}

// See https://mvnrepository.com

// //// Java libraries
// Date-Time
val libjoda = "joda-time" % "joda-time" % "2.9.7"

// JSON
// Note: version 3.2.11 not compatible with Spark 1.4+
//val libjson4score = "org.json4s" %% "json4s-core" % "3.2.10"
val libjson4sjackson = "org.json4s" %% "json4s-jackson" % "3.2.10"

// MySQL
val libmysql = "mysql" % "mysql-connector-java" % "6.0.5"

// //// Scala libraries
// Test
val libspecs2 = "org.specs2" %% "specs2" % "3.3.1" % "test"

// Breeze (linear algebra and numerical algorithms)
val libbreeze = "org.scalanlp" %% "breeze" % "0.13"
val libbreezenative = "org.scalanlp" %% "breeze-natives" % "0.13"

// Apache Avro
val libavro = "org.apache.avro" % "avro" % "1.8.1"
val libavroparquet = "com.twitter" % "parquet-avro" % "1.6.0"
val libavrochill = "com.twitter" %% "chill-avro" % "0.9.2"

// Spark
val sparkVersion="1.6.3"
val libsparkcore = "org.apache.spark" %% "spark-core" % sparkVersion
val libsparksql = "org.apache.spark" %% "spark-sql" % sparkVersion
val libsparkmllib = "org.apache.spark" %% "spark-mllib" % sparkVersion
val libsparkcsv = "com.databricks" %% "spark-csv" % "1.5.0"
val libsparkhive = "org.apache.spark" %% "spark-hive" % sparkVersion
val libsparkcassandra = "com.datastax.spark" %% "spark-cassandra-connector-java" % "1.6.0-M1"

lazy val commonSettings = Seq (
	organization := "com.amadeus.ti",
	version := "0.1.0",
	scalaVersion := "2.10.6",
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

javacOptions in Compile ++= Seq ("-source", "1.7",  "-target", "1.7")

scalacOptions += "-target:jvm-1.7"

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


