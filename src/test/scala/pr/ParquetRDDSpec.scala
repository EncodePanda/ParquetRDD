package pr

import org.scalatest.Matchers

import org.apache.spark._
import org.apache.spark.rdd._
import org.scalatest.FreeSpec

import org.apache.parquet.hadoop.example.GroupReadSupport
import org.apache.parquet.example.data.Group

class AltGroupReadSupport extends GroupReadSupport with Serializable

class ParquetRDDSpec extends FreeSpec with Matchers {

  val config =
    new SparkConf().setMaster("local[*]").setAppName("ParquetRDDSpec")

  import ParquetRDD._

  "ParquetRDD" - {
    "should be returned from sc.parquet" in {
      val sc = new SparkContext(config)
      val rdd: RDD[Group] =
        sc.parquet("hdfs://localhost:9000/data3.parquet", new AltGroupReadSupport())

      val login =
        rdd.filter(_.getLong("id", 0) == 500).map(_.getString("login", 0)).first()

      login should equal("login500")

      sc.stop()
    }
  }

}
