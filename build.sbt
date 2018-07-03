
// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

import com.typesafe.sbt.pgp.PgpKeys.publishSigned

import ReleaseTransformations._

lazy val fpassembly = crossProject(JVMPlatform, JSPlatform)
//    .withoutSuffixFor(JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("."))
    .settings(commonSettings: _*)
    .settings(publishingSettings: _*)
    .jsSettings(
      Seq(
        EclipseKeys.skipProject := true
      )
    )
    
lazy val fpassemblyJVM = fpassembly.jvm

lazy val fpassemblyJS = fpassembly.js

val organizationGlobal = "org.fpassembly"

val scalaVersionGlobal = "2.12.3"

val crossScalaVersionsGlobal = Seq("2.11.11", scalaVersionGlobal)

crossScalaVersions := crossScalaVersionsGlobal

EclipseKeys.withSource := true
  
val commonSettings = Seq(
  organization := organizationGlobal,
  name         := "fpassembly",
  scalaVersion := scalaVersionGlobal,
  scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8", "-Xlint:_", "-Ywarn-unused-import"),
  crossScalaVersions := crossScalaVersionsGlobal,
  EclipseKeys.useProjectId := true
//  ,
//  unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil,
//  unmanagedSourceDirectories in Test := (scalaSource in Test).value :: Nil
)

val publishingSettings = Seq(
//useGpg := true,
  homepage   := Some(url("https://github.com/isomorf-org/scala-fpassembly")),
  scmInfo    := Some(ScmInfo(url("https://github.com/isomorf-org/scala-fpassembly"),
                              "git@github.com:isomorf-org/scala-fpassembly.git")),
  developers := List(Developer("bdkent", "Brian Kent", "brian.kent@isomorf.io", url("https://github.com/bdkent"))),

  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),

  pomIncludeRepository := { _ => false },

  publishMavenStyle := true,
  
  // Add sonatype repository settings
  publishTo := Some(
    if (isSnapshot.value) {
      Opts.resolver.sonatypeSnapshots
    }
    else {
      Opts.resolver.sonatypeStaging
    }
  ),
  
  
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    //publishArtifacts,
    releaseStepCommandAndRemaining("+publishArtifacts"),
    releaseStepCommand("makeDocs"),
    setNextVersion,
    commitNextVersion,
    releaseStepCommand(s"sonatypeReleaseAll ${organizationGlobal}"),
    pushChanges
  )
)

publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))

commands += Command.command("releaser") {
  "release cross" :: 
  //s"sonatypeReleaseAll ${organizationGlobal}" ::
   _
}

commands += Command.command("makeDocs") {
  "makeSite" :: "ghdvCopyReadme" :: "ghdvCopyScaladocs" ::  _
}

enablePlugins(SiteScaladocPlugin)

siteSubdirName in SiteScaladoc := "scaladocs/api/" + version.value

enablePlugins(PreprocessPlugin)

enablePlugins(SbtGhDocVerPlugin)

preprocessVars in Preprocess := Map("VERSION" -> version.value)

