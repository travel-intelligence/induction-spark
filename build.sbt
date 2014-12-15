import AssemblyKeys._

assemblySettings

test in assembly := {} // Tests for Finnair are not defined yet

name := "ti-edicsv-ay"

organization := "ti-edicsv-ay"

version := "0.6-backbone-cdh5"

scalaVersion := "2.10.4"

checksums in update := Nil

scalacOptions += "-deprecation"

parallelExecution in Test := false

libraryDependencies <++= (version) {
    case v if v.contains("cdh4") => Seq("com.nicta"  %% "scoobi" % "0.8.4-cdh4",
                                        "com.amadeus.ori" % "EdifactOnHadoop" % "2.4-cdh4")
    case v if v.contains("cdh5") => Seq("com.nicta"  %% "scoobi" % "0.8.4-cdh5" exclude ("org.apache.hadoop", "hadoop-client") exclude ("org.apache.avro", "avro"),
                                        "org.apache.hadoop" % "hadoop-client" % "2.3.0-cdh5.1.2",
                                        "com.amadeus.ori" % "EdifactOnHadoop" % "2.4-cdh5")
}

libraryDependencies += "org.specs2" %% "specs2" % "2.3.9" % "test"

libraryDependencies += "com.amadeus.ori" % "EdifactParser" % "2.8"
 
libraryDependencies += "ti-models" %% "ti-models" % "0.57"

libraryDependencies += "joda-time" % "joda-time" % "2.2"

resolvers ++= Seq(
		  Resolver.sonatypeRepo("public"),
		  Resolver.sonatypeRepo("releases"),
		  Resolver.sonatypeRepo("snapshots"),
		  "cloudera"           at "http://repository.cloudera.com/content/repositories/releases",
		  "Local repository"   at "http://orinet.nce.amadeus.net/artifacts/mavenrepo/",
		  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases")

mergeStrategy in assembly <<= (mergeStrategy in assembly) { mergeStrategy => {
    case entry => {
      val strategy = mergeStrategy(entry)
      if (strategy == MergeStrategy.deduplicate) MergeStrategy.first
      else strategy
    }
  }
}

javacOptions in Compile ++= Seq("-source", "1.6",  "-target", "1.6") 

scalacOptions += "-target:jvm-1.6"

publishTo := Some("ORI Maven Repo" at "http://orinet.nce.amadeus.net/artifacts/mavenrepo/")

testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "console", "junitxml")

packageArchetype.java_application
