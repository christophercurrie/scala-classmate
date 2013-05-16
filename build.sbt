name := "scala-classmate"

// Honestly.
crossScalaVersions := Seq("2.9.0", "2.9.1", "2.9.2", "2.9.3", "2.10.0", "2.10.1")

unmanagedSourceDirectories in Compile <+= (sourceDirectory in Compile, scalaBinaryVersion) { (s,v) => v match {
  case v29 if v29.startsWith("2.9") => s / ("scala-2.9")
  case _ => s / ("scala-"+v)
} }

scalacOptions ++= Seq("-deprecation")

libraryDependencies ++= Seq(
    "com.fasterxml" % "classmate" % "0.8.0"
)