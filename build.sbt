name := "ParquetRDD"
organization := "com.slamdata"
version := "0.1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark"   %% "spark-core"    % "2.1.0",
  "org.apache.parquet" % "parquet-hadoop" % "1.9.0",
  "org.apache.hadoop"  % "hadoop-client"  % "2.7.3",
  "org.scalatest"      %% "scalatest"     % "3.0.1" % "test"
)
