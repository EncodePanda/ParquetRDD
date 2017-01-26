package pr

import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapred.FileSplit

import org.apache.spark._
import org.apache.spark.rdd._

import scala.reflect.ClassTag

class ParquetRDDPartition(val index: Int, split: FileSplit) extends Partition {

}

class ParquetRDD[T : ClassTag](
  _sc: SparkContext,
  path: Path
) extends RDD[T](_sc, Nil) {

  override def compute(split: Partition, context: TaskContext): Iterator[T] = ???
  
  override protected def getPartitions: Array[Partition] = {
    val fs = path.getFileSystem(_sc.hadoopConfiguration)
    val fileStatus = fs.getFileStatus(path)
    val blocks = fs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen())
    blocks.zipWithIndex.map {
      case (b, i) =>
        val split = new FileSplit(path,b.getOffset(),b.getLength(),b.getHosts())
        new ParquetRDDPartition(i, split)
    }
  }

}

object ParquetRDD {
  implicit class SparkContextOps(sc: SparkContext) {
    def parquet[T : ClassTag](path: String): ParquetRDD[T] =
      new ParquetRDD[T](sc, new Path(path))
  }
}
