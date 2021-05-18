name := "BEAM-12359"
version := "0.1"
scalaVersion := "2.13.6"

// Versions
val beamVersion = "2.29.0"

libraryDependencies ++= Seq(
  "org.apache.beam" % "beam-runners-direct-java" % beamVersion,
  "org.apache.beam" % "beam-sdks-java-io-google-cloud-platform" % beamVersion,
)
