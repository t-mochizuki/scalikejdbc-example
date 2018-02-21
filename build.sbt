scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream"             % "2.5.6",
  "org.scalikejdbc"   %% "scalikejdbc"             % "3.2.0",
  "org.scalikejdbc"   %% "scalikejdbc-config"      % "3.2.0",
  "org.scalikejdbc"   %% "scalikejdbc-streams"     % "3.2.0",
  "com.h2database"    %  "h2"                      % "1.4.196",
  "ch.qos.logback"    %  "logback-classic"         % "1.2.3"
)
