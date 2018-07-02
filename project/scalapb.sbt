//addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.18")

//libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.7.4"

addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.18" exclude ("com.trueaccord.scalapb", "protoc-bridge_2.10"))

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin-shaded" % "0.7.1"

