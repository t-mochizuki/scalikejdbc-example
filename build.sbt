scalaVersion := "2.12.3"

val redshiftVersion = "1.1.17.1017"
val redshiftUrl = s"https://s3.amazonaws.com/redshift-downloads/drivers/RedshiftJDBC41-$redshiftVersion.jar"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc"             % "3.2.0",
  "org.scalikejdbc" %% "scalikejdbc-config"      % "3.2.0",
  "com.amazonaws"   %  "redshift.jdbc"           % redshiftVersion from redshiftUrl,
  "ch.qos.logback"  %  "logback-classic"         % "1.2.3"
)
