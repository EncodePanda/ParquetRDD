package pr

import org.scalatest.Matchers

import org.apache.spark._
import org.apache.spark.rdd._
import org.scalatest.FreeSpec

import org.apache.parquet.schema.MessageTypeParser
import org.apache.parquet.schema.MessageType
import org.apache.parquet.hadoop.api.ReadSupport.ReadContext
import org.apache.parquet.hadoop.example.GroupReadSupport
import org.apache.hadoop.conf.Configuration
import org.apache.parquet.example.data.Group

class SerializableGroupReadSupport extends GroupReadSupport with Serializable

class ProjectableGroupReadSupport(private val projectionStr: String)
    extends GroupReadSupport
    with Serializable {
  override def init(configuration: Configuration,
                    keyValueMetaData: java.util.Map[String, String],
                    fileSchema: MessageType): ReadContext =
    new ReadContext(MessageTypeParser.parseMessageType(projectionStr))
}

class ParquetRDDSpec extends FreeSpec with Matchers {

  val config =
    new SparkConf().setMaster("local[*]").setAppName("ParquetRDDSpec")

  val path = "src/test/resources/example-data.parquet"

  import ParquetRDD._

  "ParquetRDD" - {
    "should read parquet files by using sc.parquet" in {
      withinSpark(sc => {
        // when
        val rdd: RDD[Group] =
          sc.parquet(path, new SerializableGroupReadSupport())
        // then
        val login =
          rdd.filter(_.getLong("id", 0) == 20).map(_.getString("login", 0)).first()
        login should equal("login20")
      })
    }

    "should be returned from sc.parquet with schema projection" in {
      withinSpark(sc => {
        // given
        val projection = "message User {\n" +
            "   required int32 age;\n" +
            "}"
        //when
        val rdd: RDD[Group] =
          sc.parquet(path, new ProjectableGroupReadSupport(projection))
        // then
        val result: String =
          rdd.filter(_.getInteger("age", 0) == 30).map(_.toString).first()
        result should equal("age: 30\n")
      })
    }
  }
 
  def withinSpark(runnable: SparkContext => Unit): Unit = {
    val sc = new SparkContext(config)
    runnable(sc)
    sc.stop()
  }
}
