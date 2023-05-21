name := "Project2_PTR"

version := "0.1"

scalaVersion := "2.13.10"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.13" % "2.7.0"

libraryDependencies += "com.lihaoyi" %% "requests" % "0.8.0"

libraryDependencies += "org.jsoup" % "jsoup" % "1.15.4"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.5.0"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.7.0"

libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.41.2.1"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.5.1"

libraryDependencies += "com.google.code.gson" % "gson" % "2.10.1"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.14"

libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.4.2"

val AkkaVersion = "2.8.2"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test
