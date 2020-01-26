name := "scala-nats-example"
version := "0.1.0"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "io.nats" % "jnats" % "2.6.6",
  "com.typesafe.play" %% "play-json" % "2.8.1",
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0"
)
