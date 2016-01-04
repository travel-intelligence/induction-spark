package com.amadeus.ti.induction

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

}
