package pr

import org.apache.spark._
import org.apache.spark.rdd._
import scala.reflect.ClassTag

class ParquetRDD[T : ClassTag](_sc: SparkContext) extends RDD[T](_sc, Nil) {

  override def compute(split: Partition, context: TaskContext): Iterator[T] = ???
  
  override protected def getPartitions: Array[Partition] = ???

}

object ParquetRDD {
  implicit class SparkContextOps(sc: SparkContext) {
    def parquet[T : ClassTag]: ParquetRDD[T] =
      new ParquetRDD[T](sc)
  }
}
