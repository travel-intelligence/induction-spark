package com.amadeus.ti.induction

//
object Introduction extends App {

  // Configure a local Spark 'cluster' with two cores
  val sparkConf =
    new org.apache.spark.SparkConf()
      .setAppName ("Spark-Induction")
      .setMaster ("local[2]")

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

  // JSON data file, from the local filesystem
  // val dataFilepath = "data/profiles.json"
  // JSON data file, from HDFS
  // (check the fs.default.name property in the /etc/hadoop/core-site.xml file)
  val dataFilepath = "data/profiles.json"
  //val dataFilepath = "hdfs://${HDFS_URL}/user/${USER}/data/induction/embedded/profiles.json"
  val dFrame : org.apache.spark.sql.DataFrame =
    sqlContext.read.json (dataFilepath)

  // DEBUG
  println ("DataFrame made directly from a JSON schema:")
  dFrame.printSchema()
  dFrame.show()

  // Using JSONRDD
  val strRDD : org.apache.spark.rdd.RDD[String] =
    sparkContext.textFile (dataFilepath)
  val jsonRDD : org.apache.spark.sql.DataFrame = sqlContext.read.json (strRDD)

  // DEBUG
  println ("DataFrame made directly from a RDD, itself made from a JSON schema:")
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

  //
  val jsonRDDWithSchema : org.apache.spark.sql.DataFrame =
    sqlContext.read.schema (profilesSchema).json (strRDD)

  // DEBUG
  println ("DataFrame made from a DataType:")
  jsonRDDWithSchema.printSchema()
  jsonRDDWithSchema.show()

  //
  jsonRDDWithSchema.registerTempTable ("profilesTable")

  // Filter based on timestamp
  val filterCount = sqlContext
    .sql ("select * from profilesTable where registered > CAST('2014-08-26 00:00:00' AS TIMESTAMP)")
    .count

  val fullCount = sqlContext.sql ("select * from profilesTable").count

  // Checks
  println ("All records count (should be 200): " + fullCount)
  println ("Filtered based on timestamp count (should be 106): " + filterCount)

  // Write schema as JSON to file
  val profileSchemaFilepath = "data/schema/profileSchema.json"
  scala.reflect.io.File (profileSchemaFilepath).writeAll (profilesSchema.json)

  // Retrieve the JSON schema, just as a sanity check
  val loadedSchema =
    org.apache.spark.sql.types.DataType.fromJson (
      scala.io.Source
        .fromFile (profileSchemaFilepath)
        .mkString
    )

  // Print loaded schema
  println ("Retrieved JSON schema:")
  println (loadedSchema.prettyJson)


  // //////////// Third way: with Parquet Storage //////////////
  println ("/////////// Third way: with Parquet Storage /////////////")

  // Treat Parquet binary encoded values as Strings
  sqlContext.setConf ("spark.sql.parquet.binaryAsString", "true")
  sqlContext.setConf ("spark.sql.parquet.compression.codec", "snappy")
  // sqlContext.setConf ("spark.sql.parquet.compression.codec", "gzip")
  // sqlContext.setConf ("spark.sql.parquet.compression.codec", "lzo")
  
  import sqlContext.implicits._
  
  // Fill a Spark RDD structure with the content of the CSV file
  // (available from http://archive.ics.uci.edu/ml/datasets/Student+Performance)
  val rddOfStudentDetails =
    extractCSVToStudents ("data/student-details.csv", sparkContext)
  
  // Convert RDD[Student] to a Dataframe using sqlContext.implicits
  // val studentDetailsDFrame = rddOfStudentDetails.toDF()
  val studentDetailsDFrame = sqlContext.createDataFrame (rddOfStudentDetails)
  
  // Save DataFrame as Parquet
  // Commented as it triggers an error like 'NoSuchFieldError: DECIMAL')
  // studentDetailsDFrame.saveAsParquetFile ("data/studentPq.parquet")
  // studentDetailsDFrame.write.format ("parquet").mode (org.apache.spark.sql.SaveMode.Overwrite).save ("data/studentPq.parquet")
  
  // Read data for confirmation
  // val pqDFrame = sqlContext.parquetFile ("data/studentPq.parquet")
  // val pqDFrame = sqlContext.read.parquet ("data/studentPq.parquet")

  // DEBUG
  // pqDFrame.show()
    
  // The CSV has a header row.  Zipping with index and skipping the first row
  def extractCSVToStudents (filePath: String, sc: org.apache.spark.SparkContext)
      : org.apache.spark.rdd.RDD[model.StudentShort] = {
    val rddOfStudents: org.apache.spark.rdd.RDD[model.StudentShort] =
      sc.textFile (filePath)
        .zipWithIndex()
        .filter (_._2 > 0)
        .map (eachLineAndNum => {
          val data = eachLineAndNum._1.split ("\\|")
          model.StudentShort (data(0), data(1), data(2), data(3))
        })
    
    rddOfStudents
  }

}
