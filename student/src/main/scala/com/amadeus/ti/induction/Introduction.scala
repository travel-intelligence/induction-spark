package com.amadeus.ti.induction

//import org.apache.spark.sql.types._

//
object Introduction extends App {

  // Configure a local Spark 'cluster' with two cores
  val sparkConf = new org.apache.spark.SparkConf().setAppName ("DataWith33Atts").setMaster("local[2]")

  // Initialize Spark context with the Spark configuration
  val sparkContext = new org.apache.spark.SparkContext (sparkConf)

  // Query Spark thanks to the SQL language
  val sqlContext = new org.apache.spark.sql.SQLContext (sparkContext)

  // //////////// First way: with a class extending Product //////////////
  println ("/////////// First way: with a class extending Product /////////////")

  // Fill a Spark RDD structure with the content of the CSV file
  // (available from http://archive.ics.uci.edu/ml/datasets/Student+Performance)
  val rddOfStudents = convertCSVToStudents ("data/student-mat.csv", sparkContext)

  // Create a DataFrame from the Spark RDD
  val studentDFrame = sqlContext.createDataFrame (rddOfStudents)

  // DEBUG
  studentDFrame.printSchema()
  studentDFrame.show()

  // Fill a Spark RDD with the content of a CSV file
  def convertCSVToStudents (filePath: String, sc: org.apache.spark.SparkContext)
      : org.apache.spark.rdd.RDD[model.Student] = {
    val rddOfStringStudents: org.apache.spark.rdd.RDD[String] =
      sc.textFile (filePath)
    val rddOfStudents: org.apache.spark.rdd.RDD[model.Student] =
      rddOfStringStudents.flatMap (eachLine => model.Student (eachLine))
    //
    rddOfStudents
  }

  // //////////// Second way: from JSON schema //////////////
  println ("/////////// Second way: from JSON schema /////////////")

  // From the local filesystem
  val schemaFilepath = "data/schema/profiles.json"
  // From HDFS
  // val schemaFilepath = "hdfs://localhost:9000/data/scalada/profiles.json"
  val dFrame : org.apache.spark.sql.DataFrame =
    sqlContext.read.json (schemaFilepath)

  // DEBUG
  dFrame.printSchema()
  dFrame.show()

  // Using JSONRDD
  val strRDD : org.apache.spark.rdd.RDD[String] =
    sparkContext.textFile (schemaFilepath)
  val jsonRDD : org.apache.spark.sql.DataFrame = sqlContext.read.json (strRDD)

  // DEBUG
  jsonRDD.printSchema()
  jsonRDD.show()

  // Explicit Schema Definition
  val profilesSchema = org.apache.spark.sql.types.StructType (
    Seq (
      org.apache.spark.sql.types.StructField ("id",
        org.apache.spark.sql.types.StringType, true),
      org.apache.spark.sql.types.StructField ("about",
        org.apache.spark.sql.types.StringType, true),
      org.apache.spark.sql.types.StructField ("address",
        org.apache.spark.sql.types.StringType, true),
      org.apache.spark.sql.types.StructField ("age",
        org.apache.spark.sql.types.IntegerType, true),
      org.apache.spark.sql.types.StructField ("company",
        org.apache.spark.sql.types.StringType, true),
      org.apache.spark.sql.types.StructField ("email",
        org.apache.spark.sql.types.StringType, true),
      org.apache.spark.sql.types.StructField ("eyeColor",
        org.apache.spark.sql.types.StringType, true),
      org.apache.spark.sql.types.StructField ("favoriteFruit",
        org.apache.spark.sql.types.StringType, true),
      org.apache.spark.sql.types.StructField ("gender",
        org.apache.spark.sql.types.StringType, true),
      org.apache.spark.sql.types.StructField ("name",
        org.apache.spark.sql.types.StringType, true),
      org.apache.spark.sql.types.StructField ("phone",
        org.apache.spark.sql.types.StringType, true),
      org.apache.spark.sql.types.StructField ("registered",
        org.apache.spark.sql.types.TimestampType, true),
      org.apache.spark.sql.types.StructField ("tags",
        org.apache.spark.sql.types.ArrayType (org.apache.spark.sql.types.StringType), true)
    )
  )

  val jsonRDDWithSchema : org.apache.spark.sql.DataFrame =
    sqlContext.jsonRDD (strRDD, profilesSchema)

  // DEBUG
  jsonRDDWithSchema.printSchema()
  jsonRDDWithSchema.show()

  //
  jsonRDDWithSchema.registerTempTable ("profilesTable")

  // Filter based on timestamp
  val filterCount = sqlContext.sql ("select * from profilesTable where registered> CAST('2014-08-26 00:00:00' AS TIMESTAMP)").count

  val fullCount = sqlContext.sql ("select * from profilesTable").count

  println ("All Records Count : " + fullCount) //200
  println ("Filtered based on timestamp count : " + filterCount) //106

  // Write schema as JSON to file
  scala.reflect.io.File ("profileSchema.json").writeAll (profilesSchema.json)

  val loadedSchema =
    org.apache.spark.sql.types.DataType.fromJson (
      scala.io.Source
        .fromFile ("profileSchema.json")
        .mkString
    )

  // Print loaded schema
  println (loadedSchema.prettyJson)

}
