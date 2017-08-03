
// See https://mvnrepository.com

// //// Java libraries
// Date-Time
//val libjoda = "joda-time" % "joda-time" % "2.9.7"
val libnscala = "com.github.nscala-time" %% "nscala-time" % "2.16.0"

// JSON
// Note: version 3.2.11 not compatible with Spark 1.4+
//val libjson4score = "org.json4s" %% "json4s-core" % "3.2.10"
val libjson4sjackson = "org.json4s" %% "json4s-jackson" % "3.2.11"

// MySQL
val libmysql = "mysql" % "mysql-connector-java" % "6.0.6"

// //// Scala libraries
// Test
val libspecs2 = "org.specs2" %% "specs2-core" % "3.9.4" % "test"

// Breeze (linear algebra and numerical algorithms)
val libbreeze = "org.scalanlp" %% "breeze" % "0.13.2"
val libbreezenative = "org.scalanlp" %% "breeze-natives" % "0.13.2"

// Apache Avro
val libavro = "org.apache.avro" % "avro" % "1.8.2"
val libavroparquet = "org.apache.parquet" % "parquet-avro" % "1.9.0"
val libavrochill = "com.twitter" %% "chill-avro" % "0.8.0"

// Jets3t
val libjets3t = "net.java.dev.jets3t" % "jets3t" % "0.9.3"

// Google Guava
val libguava = "com.google.guava" % "guava" % "22.0"

// Netty
val libnetty = "io.netty" % "netty-all" % "4.1.13.Final"

// Hadoop
val libhadoopcommon = "org.apache.hadoop" % "hadoop-common" % "2.7.3"
val libhadoophdfs = "org.apache.hadoop" % "hadoop-hdfs" % "2.7.3"
val libhadoopyarn = "org.apache.hadoop" % "hadoop-yarn-client" % "2.7.3"

// Spark
val sparkVersion="2.2.0"
val libsparkcore = "org.apache.spark" %% "spark-core" % sparkVersion
val libsparksql = "org.apache.spark" %% "spark-sql" % sparkVersion
val libsparkmllib = "org.apache.spark" %% "spark-mllib" % sparkVersion
//val libsparkcsv = "com.databricks" %% "spark-csv" % "1.5.0"
val libsparkhive = "org.apache.spark" %% "spark-hive" % sparkVersion
val libsparkcassandra = "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.1"

lazy val commonSettings = Seq (
	organization := "com.amadeus.ti",
	version := "0.2.0",
	scalaVersion := "2.11.8"
)

lazy val libSettings = Seq (
  libraryDependencies += libnscala,
  libraryDependencies += libspecs2,
  libraryDependencies += libbreeze,
  libraryDependencies += libbreezenative,
  libraryDependencies += libjets3t,
  libraryDependencies += libguava,
  libraryDependencies += libnetty,
  libraryDependencies += libhadoopcommon,
  libraryDependencies += libhadoophdfs,
  libraryDependencies += libhadoopyarn,
  libraryDependencies += libsparkcore,
  libraryDependencies += libsparksql,
  libraryDependencies += libsparkmllib,
  libraryDependencies += libsparkhive,
  libraryDependencies += libsparkcassandra,
  libraryDependencies += libmysql,
  libraryDependencies += libjson4sjackson,
  libraryDependencies += libavro,
  libraryDependencies += libavroparquet,
  libraryDependencies += libavrochill
)

lazy val displayVersion = taskKey[Unit]("Display the version of the project")

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(libSettings: _*)
  .settings(
    name := "induction-spark-yarn",
    displayVersion := { println (version.value.toString)}
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

testOptions in Test += Tests.Argument (TestFrameworks.Specs2, "console", "junitxml")

packageArchetype.java_application


