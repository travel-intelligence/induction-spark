package com.amadeus.ti.induction

//import scala.reflect.runtime.universe
//import com.databricks.spark.csv._
import scala.collection.JavaConverters._

// Employee
case class Employee (id:Int, name:String)

object Introduction extends App {
  
  // Spark 2.x way, with a SparkSession
  // https://databricks.com/blog/2016/08/15/how-to-use-sparksession-in-apache-spark-2-0.html
  val spark = org.apache.spark.sql.SparkSession
    .builder()
    .appName("SparkSessionForInduction")
	.config("spark.master", "local")
    .enableHiveSupport()
    .getOrCreate()

  // Spark 1.x way, with SparkConf, SparkContext and SQLContext
  // Configure a local Spark 'cluster' with two cores
  //val sparkConf = new org.apache.spark.SparkConf()
  //  .setAppName ("Spark-Induction")
  //  .setMaster ("local[*]")
  // Initialize Spark context with the Spark configuration
  //val sparkContext = new org.apache.spark.SparkContext (sparkConf)
  // Query Spark thanks to the SQL language
  //val sqlContext = new org.apache.spark.sql.SQLContext (sparkContext)

  // ////////// Simple RDD ///////////
  // Calculate pi with Monte Carlo estimation
  import scala.math.random

  // Make a very large unique set of 1 -> n
  val partitions = 2
  val n = math.min(100000L * partitions, Int.MaxValue).toInt
  val xs = 1 until n

  // Split n into the number of partitions we can use
  val rdd = spark.sparkContext.parallelize(xs, partitions).setName("'N values rdd'")

  // Generate a random set of points within a 2x2 square
  val sample = rdd.map { i =>
    val x = random * 2 - 1
    val y = random * 2 - 1
    (x, y)
  }.setName("'Random points rdd'")

  // points w/in the square also w/in the center circle of r=1
  val inside = sample
    .filter { case (x, y) => (x * x + y * y < 1) }
    .setName("'Random points inside circle'")

  val count = inside.count()

  // Area(circle)/Area(square) = inside/n => pi=4*inside/n
  println("Pi is roughly " + 4.0 * count / n)

  // //////////// First way: without case classes //////////////
  println ("//////////// First way: without case classes //////////////")

  // CSV data file, from the local file-system
  // val dataFilepath = "data/StudentData.csv"
  // CSV data file, from HDFS
  // (check the fs.defaultFS property in the $HADOOP_CONF_DIR/core-site.xml file)
  // val dataFilepath = "hdfs://localhost:8020/data/induction/yarn/data/StudentData.csv"
  val dataFilepath = "data/StudentData.csv"

  // Spark 1.x: val df = sqlContext.read
  //val studentsDF = sqlContext.csvFile (filePath = dataFilepath,
  //                                     useHeader = true, delimiter = '|')
  // Spark 2x
  val studentsDF = spark.read
    .format("com.databricks.spark.csv")
    .option("header", "true")       // Use first line of all files as header
    .option("inferSchema", "true")  // Automatically infer data types
    .option("delimiter", "|")
    .option("timestampFormat", "yyyy-MM-dd HH:mm:ss")
    .load(dataFilepath)
  
  // Print the schema of this input
  println ("studentsDF:")
  studentsDF.printSchema()
  
  // Sample 3 records along with headers
  studentsDF.show (3)
  
  // Show all the records along with headers 
  studentsDF.show ()  
  
  // Sample the first 5 records
  studentsDF.head(5).foreach (println)
  
  // Alias of head
  studentsDF.take(5).foreach (println)
    
  // Select just the email ID to a different DataFrame
  val emailDataFrame:org.apache.spark.sql.DataFrame = studentsDF.select ("email")
  println ("emailDataFrame:")
  emailDataFrame.show(3)
  
  // Select more than one column and create a different DataFrame
  val studentEmailDF = studentsDF.select ("studentName", "email")
  println ("studentEmailDF:")
  studentEmailDF.show(3)
  
  // Print the first 5 records, which have student ID over 5
  studentsDF.filter ("id > 5").show(7)
  
  // Records with no student name
  studentsDF.filter ("studentName =''").show(7)
  
  // Show all records, for which student names are empty or null
  studentsDF.filter ("studentName ='' OR studentName = 'NULL'").show(7)
  
  // Get all students, for which names start with the letter 'M'
  studentsDF.filter ("SUBSTR(studentName,0,1) ='M'").show(7)
  
  // The real power of DataFrames lies in the way we could treat it
  // like a relational table and use SQL to query
  // Step 1. Register the students DataFrame as a table
  // with name "students" (or any name)
  studentsDF.createOrReplaceTempView ("students")
  
  // Step 2. Query it away
  val dfFilteredBySQL = spark.sql ("select * from students where studentName != '' order by email desc")
  println ("dfFilteredBySQL:")
  dfFilteredBySQL.show(7)
  
  // You could also optionally order the DataFrame by column
  // without registering it as a table.
  // Order by descending order
  studentsDF.sort (studentsDF ("studentName").desc).show(10)
  
  // Order by a list of column names - without using SQL
  studentsDF.sort ("studentName", "id").show(10)
  
  // Now, let's save the modified DataFrame with a new name
  val options = Map ("header" -> "true", "path" -> "ModifiedStudent.csv")
  
  // Modify DataFrame - pick 'studentName' and 'email' columns,
  // change 'studentName' column name to just 'name'
  val copyOfStudentsDF = studentsDF
    .select (studentsDF ("studentName").as("name"), studentsDF ("email"))
  println ("copyOfStudentsDF:")
  copyOfStudentsDF.show()

  // Save this new dataframe with headers
  // and with file name "ModifiedStudent.csv"
  copyOfStudentsDF.write
    .format ("com.databricks.spark.csv")
    .mode (org.apache.spark.sql.SaveMode.Overwrite)
    .options(options).save
  
  // Load the saved data and verify the schema and list some records
  // Instead of using the csvFile, you could do a 'load' 
  val newStudentsDF = spark.read
    .format ("com.databricks.spark.csv")
    .options (options).load
  println ("newStudentsDF:")
  newStudentsDF.printSchema()
  newStudentsDF.show()

  // //////////// Second way: with case classes //////////////
  println ("//////////// Second way: with case classes //////////////")

  // Create a container, aimed at receiving the data to be read from the CSV file
  val listOfEmployees = List (
    Employee (1,"Arun"),
    Employee (2, "Jason"),
    Employee (3, "Abhi"))
  
  // Read the CSV file and fill the corresponding DataFrame
  val empFrame = spark.createDataFrame (listOfEmployees)
  
  println ("empFrame:")
  empFrame.printSchema
  empFrame.show(3)
  
  // The withColumnRenamed() function allows to control the names of the columns
  val empFrameWithRenamedColumns = spark.createDataFrame (listOfEmployees)
    .withColumnRenamed ("id", "empId")
  
  println ("empFrameWithRenamedColumns:")
  empFrameWithRenamedColumns.printSchema

  // Watch out for the columns with a "." in them.
  // This is an open bug and needs to be fixed as of 1.3.0
  empFrameWithRenamedColumns.createOrReplaceTempView ("employeeTable")
  
  val sortedByNameEmployees = spark.sql ("select * from employeeTable order by name desc")
  
  sortedByNameEmployees.show()
  
  // Create dataframe from Tuple. Of course,
  // you could rename the column using the withColumnRenamed
  val empFrame1 = spark.createDataFrame (Seq ((1,"Android"), (2, "iPhone")))
  empFrame1.printSchema
  empFrame1.show()
}
