
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val scalaVer = "2.13.3"

lazy val commonSettings = Seq(
  name         := "coursier-trees",
  description  := "Querying support for Coursier dependency trees and module trees",
  organization := "eu.cdevreeze.coursier-extra",
  version      := "0.1.0-SNAPSHOT",

  scalaVersion       := scalaVer,

  scalacOptions ++= Seq("-Wconf:cat=unused-imports:w,cat=unchecked:w,cat=deprecation:w,cat=feature:w,cat=lint:w"),

  Test / publishArtifact := false,
  publishMavenStyle := true,

  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },

  pomExtra := pomData,
  pomIncludeRepository := { _ => false },

  libraryDependencies += "io.get-coursier" %%% "coursier-core" % "2.0.2",

  libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.1" % "test",

  libraryDependencies += "org.scalatestplus" %%% "scalacheck-1-14" % "3.1.1.1" % "test"
)

lazy val root = project.in(file("."))
  .aggregate(coursierTreesJVM, coursierTreesJS)
  .settings(commonSettings: _*)
  .settings(
    name                 := "coursierTrees",
    // Thanks, scala-java-time, for showing us how to prevent any publishing of root level artifacts:
    // No, SBT, we don't want any artifacts for root. No, not even an empty jar.
    publish              := {},
    publishLocal         := {},
    publishArtifact      := false,
    Keys.`package`       := file(""))

lazy val coursierTrees = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(commonSettings: _*)
  .jvmSettings(
    libraryDependencies += "org.scalacheck" %%% "scalacheck" % "1.14.3" % "test"
  )
  .jsSettings(
    // Add support for the DOM in `run` and `test`
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),

    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",

    Test / parallelExecution := false
  )

lazy val coursierTreesJVM = coursierTrees.jvm
lazy val coursierTreesJS = coursierTrees.js

lazy val pomData =
  <url>https://github.com/dvreeze/coursier-trees</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
      <comments>Coursier-trees is licensed under Apache License, Version 2.0</comments>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:git@github.com:dvreeze/coursier-trees.git</connection>
    <url>https://github.com/dvreeze/coursier-trees.git</url>
    <developerConnection>scm:git:git@github.com:dvreeze/coursier-trees.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>dvreeze</id>
      <name>Chris de Vreeze</name>
      <email>chris.de.vreeze@caiway.net</email>
    </developer>
  </developers>
