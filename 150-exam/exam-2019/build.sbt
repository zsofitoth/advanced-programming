name := "Simple Set Up for Exam Questions"

version := "0.3"

scalaVersion := "2.12.6"

scalacOptions += "-deprecation"

scalacOptions += "-feature"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" 

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" 

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.22"

val libraryVersion = "1.5.0"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-generic" % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-macro"   % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-state"   % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-law"     % libraryVersion % "test"
)
