
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.2.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")

addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.8.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")

addSbtPlugin("net.vonbuchholtz" % "sbt-dependency-check" % "2.0.0")

addSbtPlugin("ch.epfl.scala" % "sbt-missinglink" % "0.3.1")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.0.0"
