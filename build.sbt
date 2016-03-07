lazy val playjsontime = project in file(".")

organization := "com.beamly"
    licenses := Seq(("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")))
 description := "Play-Json typeclass instances for datetime types"
    homepage := Some(url("https://github.com/beamly/playjsontime"))
   startYear := Some(2016)

      scalaVersion := "2.11.7"
crossScalaVersions := Seq("2.11.7", "2.10.6")

scalacOptions ++= Seq("-encoding", "utf8")
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")
scalacOptions  += "-language:higherKinds"
scalacOptions  += "-language:implicitConversions"
scalacOptions  += "-language:postfixOps"
scalacOptions  += "-Xfuture"
scalacOptions  += "-Yno-adapted-args"
scalacOptions  += "-Ywarn-dead-code"
scalacOptions  += "-Ywarn-numeric-widen"
//scalacOptions ++= "-Ywarn-unused-import".ifScala211Plus.value.toSeq
scalacOptions  += "-Ywarn-value-discard"

//scalacOptions in (Compile, console) -= "-Ywarn-unused-import"
//scalacOptions in (Test,    console) -= "-Ywarn-unused-import"

maxErrors := 5
triggeredMessage := Watched.clearWhenTriggered

libraryDependencies += "com.typesafe.play" %% "play-json"   % "2.4.6"
libraryDependencies += "org.specs2"        %% "specs2-core" % "3.6.5" % "test"

parallelExecution in Test := true
fork in Test := false

bintrayOrganization := Some("beamly")

pomExtra := pomExtra.value ++ {
    <developers>
        <developer>
            <id>dwijnand</id>
            <name>Dale Wijnand</name>
            <email>dale wijnand gmail com</email>
            <url>dwijnand.com</url>
        </developer>
    </developers>
    <scm>
        <connection>scm:git:github.com/beamly/playjsontime.git</connection>
        <developerConnection>scm:git:git@github.com:beamly/playjsontime.git</developerConnection>
        <url>https://github.com/beamly/playjsontime</url>
    </scm>
}

bintrayReleaseOnPublish in ThisBuild := false

releaseCrossBuild := true

watchSources ++= (baseDirectory.value * "*.sbt").get
watchSources ++= (baseDirectory.value / "project" * "*.scala").get
