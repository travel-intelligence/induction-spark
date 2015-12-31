package com.amadeus.ti.induction

//import scala.reflect.runtime.universe
import com.databricks.spark.csv._
import scala.collection.JavaConverters._

// Employee
case class Employee (id:Int, name:String)

object Introduction extends App {
  
  // Configure a local Spark 'cluster' with two cores
  val sparkConf = new org.apache.spark.SparkConf().setAppName ("csvDataFrame").setMaster ("local[2]")
  
  // Initialize Spark context with the Spark configuration
  val sparkContext = new org.apache.spark.SparkContext (sparkConf)
  
  // Query Spark thanks to the SQL language
  val sqlContext = new org.apache.spark.sql.SQLContext (sparkContext)
  
  // //////////// First way //////////////
  // Fill a (org.apache.spark.sql.)DataFrame with the content of the CSV file
  val studentsDF = sqlContext.csvFile (filePath = "StudentData.csv", useHeader = true, delimiter = '|')
  
  // Print the schema of this input
  studentsDF.printSchema
  
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
  
  emailDataFrame.show(3)
  
  // Select more than one column and create a different DataFrame
  val studentEmailDF = studentsDF.select ("studentName", "email")
  
  studentEmailDF.show(3)
  
  // Print the first 5 records, which have student ID over 5
  studentsDF.filter ("id > 5").show(7)
  
  // Records with no student name
  studentsDF.filter ("studentName =''").show(7)
  
  // Show all records, for which student names are empty or null
  studentsDF.filter ("studentName ='' OR studentName = 'NULL'").show(7)
  
  // Get all students, for which names start with the letter 'M'
  studentsDF.filter ("SUBSTR(studentName,0,1) ='M'").show(7)
  
  // The real power of DataFrames lies in the way we could treat it like a relational table and use SQL to query
  // Step 1. Register the students DataFrame as a table with name "students" (or any name)
  studentsDF.registerTempTable ("students")
  
  //Step 2. Query it away
  val dfFilteredBySQL = sqlContext.sql ("select * from students where studentName != '' order by email desc")
  
  dfFilteredBySQL.show(7)
  
  // You could also optionally order the DataFrame by column without registering it as a table.
  // Order by descending order
  studentsDF.sort (studentsDF ("studentName").desc).show(10)
  
  // Order by a list of column names - without using SQL
  studentsDF.sort ("studentName", "id").show(10)
  
  // Now, let's save the modified dataframe with a new name
  val options = Map ("header" -> "true", "path" -> "ModifiedStudent.csv")
  
  // Modify DataFrame - pick 'studentName' and 'email' columns, change 'studentName' column name to just 'name' 
  val copyOfStudentsDF = studentsDF.select (studentsDF ("studentName").as("name"), studentsDF ("email"))
  
  copyOfStudentsDF.show()

  // Save this new dataframe with headers and with file name "ModifiedStudent.csv"
  // copyOfStudents.save("com.databricks.spark.csv", SaveMode.Overwrite, options)
  copyOfStudentsDF.write.format ("com.databricks.spark.csv").mode (org.apache.spark.sql.SaveMode.Overwrite).options(options).save
  
  // Load the saved data and verify the schema and list some records
  // Instead of using the csvFile, you could do a 'load' 
  //val newStudents = sqlContext.load("com.databricks.spark.csv",options)
  val newStudentsDF = sqlContext.read.format("com.databricks.spark.csv").options(options).load
  newStudentsDF.printSchema()
  println ("new Students")
  newStudentsDF.show()

  // //////////// Second way //////////////
  // Create a container, aimed at receiving the data to be read from the CSV file
  val listOfEmployees = List (Employee (1,"Arun"), Employee (2, "Jason"), Employee (3, "Abhi"))
  
  // Read the CSV file and fill the corresponding DataFrame
  val empFrame = sqlContext.createDataFrame (listOfEmployees)
  
  empFrame.printSchema
  
  empFrame.show(3)
  
  // The withColumnRenamed() function allows to control the names of the columns
  val empFrameWithRenamedColumns = sqlContext.createDataFrame (listOfEmployees).withColumnRenamed ("id", "empId")
  
  empFrameWithRenamedColumns.printSchema

  // Watch out for the columns with a "." in them. This is an open bug and needs to be fixed as of 1.3.0
  empFrameWithRenamedColumns.registerTempTable("employeeTable")
  
  val sortedByNameEmployees = sqlContext.sql ("select * from employeeTable order by name desc")
  
  sortedByNameEmployees.show()
  
  // Create dataframe from Tuple. Of course, you could rename the column using the withColumnRenamed
  val empFrame1 = sqlContext.createDataFrame (Seq ((1,"Android"), (2, "iPhone")))
  empFrame1.printSchema
  empFrame1.show()
}
