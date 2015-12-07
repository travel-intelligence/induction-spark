import AssemblyKeys._

assemblySettings

name := "induction"

organization := "nbu-tiu-sds"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.specs2" %% "specs2" % "2.3.9" % "test"

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

publishTo := Some("Nice Maven Repo" at "http://orinet.nce.amadeus.net/artifacts/mavenrepo/")

packageArchetype.java_application
