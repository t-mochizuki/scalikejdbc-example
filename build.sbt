scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc"             % "3.2.0",
  "com.h2database"  %  "h2"                      % "1.4.196",
  "ch.qos.logback"  %  "logback-classic"         % "1.2.3"
)

enablePlugins(ScalikejdbcPlugin)
