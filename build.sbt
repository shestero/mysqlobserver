import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

resolvers += "repo.spring.io" at "https://repo.spring.io/ui/native/libs-milestone/"
resolvers += "r2dbc-client repo" at "https://repo.spring.io/artifactory/libs-milestone/"

val AkkaVersion = "2.6.19"
val AkkaHttpVersion = "10.2.9"

lazy val root = (project in file("."))
  .settings(
    name := "mysqlobserver",

    //resolvers += "repo.spring.io" at "https://repo.spring.io/ui/native/libs-milestone",
    //resolvers += "r2dbc-client repo" at "https://mvnrepository.com/artifact",

    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2"     % Compile, // ?
      "io.monix"               %% "monix-reactive"     % "3.4.0" % Compile,
      "io.r2dbc"               % "r2dbc-client"        % "1.0.0.M7"  % Compile,
      "io.r2dbc"               % "r2dbc-spi"        % "1.0.0.RELEASE"  % Compile,
      "dev.miku"               % "r2dbc-mysql"    % "0.8.+"  % Compile,

      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion  % Compile,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion  % Compile,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion  % Compile
    )
  )

