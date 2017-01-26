name := "ParquetRDD"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark"  %% "spark-core" % "2.1.0",
  "org.apache.parquet" % "parquet-hadoop" % "1.9.0",
  "org.apache.hadoop" % "hadoop-client" % "2.7.3"
)
