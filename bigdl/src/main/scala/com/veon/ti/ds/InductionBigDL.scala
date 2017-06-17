package com.veon.ti.ds

import scala.collection.JavaConverters._
// BigDL
import com.intel.analytics.bigdl.tensor.Tensor

// Employee
case class Employee (id:Int, name:String)

//
object BigDLInduction extends App {

  // Spark session
  val spark = org.apache.spark.sql.SparkSession
    .builder()
    .appName("SparkSessionForBigDLInduction")
    .config("spark.master", "local")
    .enableHiveSupport()
    .getOrCreate()

  // CSV data file, from the local file-system
  // val dataFilepath = "/data/bigdl-induction/StudentData.csv"
  // CSV data file, from local HDFS
  // val dataFilepath = "hdfs://localhost:9000/data/zeppelin/bigdl-induction/StudentData.csv"
  // CSV data file, from remote HDFS
  // (check the fs.defaultFS property in the $HADOOP_CONF_DIR/core-site.xml file)
  // val dataFilepath = "hdfs://172.30.1.129:8020/data/zeppelin/bigdl-induction/StudentData.csv"
  val dataFilepath = "../data/StudentData.csv"

  // DataFrame
  val df = spark.read
    .format("com.databricks.spark.csv")
    .option("header", "true")       // Use first line of all files as header
    .option("charset", "utf8")    // CP-1251
    .option("inferSchema", "true")  // Automatically infer data types
    .option("delimiter", "|")
    .option("timestampFormat", "yyyy-MM-dd HH:mm:ss")
    .load(dataFilepath)

  //
  df.printSchema()

  //
  df.show(5)

  // Print the first 5 records, which have user ID greater than 5
  df.filter ("id > 5").show(7)
  
  //
  Tensor[Double](2,2).fill(1.0)

}

