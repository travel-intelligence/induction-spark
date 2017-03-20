
publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://nexus-etl.vipdmp.com/repository/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "dmp-ds-snapshots")
  else
    Some("releases"  at nexus + "dmp-ds-releases")
}

pomExtra := (
  <url>https://gitlab.vipdmp.com/datascience/induction-spark</url>
  <licenses>
    <license>
      <name>GPLv3</name>
      <url>https://www.gnu.org/licenses/gpl-3.0.en.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://gitlab.vipdmp.com/datascience/induction-spark.git</url>
    <connection>scm:git:https://gitlab.vipdmp.com/datascience/induction-spark.git</connection>
  </scm>
  <developers>
    <developer>
      <id>darnaud</id>
      <name>Denis Arnaud</name>
      <url>https://vipdmpteam.slack.com/team/denis.arnaud</url>
    </developer>
  </developers>)

