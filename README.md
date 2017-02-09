# ParquetRDD

Project aims to give ability to read parquet files using Apache Spark RDD API.
To get the `RDD` of type `T` (`RDD[T]`) the API requires to 
1. provide the implementation of `ReadSupport[T]` that transforms each row to a value of `T`
2. ensure that `ReadSupprot[T]` is `Serializable`

## Example usage 1

Given example file that looks like this:

| id  | login  | age |
| --- | ------ | ----|
| 1   | login1 | 11  |
| 2   | login2 | 12  |
| 3   | login3 | 13  |
|     | ....   |     |

We need to provide an instance of `ReadSupport[T]` that is serializable. For this example we will use `ReadSupprot` that is shiped with with `parquet-mr` project called `GroupReadSupport`. The minor problem is that this implementation is not serializable - something we can easily fix with a nice trick

```scala
class SerializableGroupReadSupport extends GroupReadSupport with Serializable
```

We can now read our file from HDFS (or local file system) by calling `sc.parquet`:

```scala
import ParquetRDD._
val path: Path = new Path("hdfs://localhost:9000/example.parquet")

val rdd: RDD[Group] = sc.parquet(path, new SerializableGroupReadSupport())

println(rdd.collect())
```

## Example 2 - projection with schema

You can also want to read only specific parquet columns (aka projection). Just implement `ReadSupport` that does the projection for you

```scala
class ProjectableGroupReadSupport(private val projectionStr: String)
    extends GroupReadSupport
    with Serializable {
  override def init(configuration: Configuration,
                    keyValueMetaData: java.util.Map[String, String],
                    fileSchema: MessageType): ReadContext =
    new ReadContext(MessageTypeParser.parseMessageType(projectionStr))
}

import ParquetRDD._
val path: Path = new Path("hdfs://localhost:9000/example.parquet")
val projection = "message User {\n" +
            "   required int32 age;\n" +
            "}"

val rdd: RDD[Group] = sc.parquet(path, new ProjectableGroupReadSupport(projection))

println(rdd.collect())
```

Note that ProjectableGroupReadSupport holds refernece to "serialized" `String` representation of `MessageType` (aka schema), because `MessageType` does not implement `Serializable`.
