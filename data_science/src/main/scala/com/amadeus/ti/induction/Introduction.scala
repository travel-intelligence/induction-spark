package com.amadeus.ti.induction

object Introduction extends App {

  def generateVector (meanValue: Double, size: Int) = {
    // Normal distribution with mean value given as parameter.
    val normalDist = breeze.stats.distributions.Gaussian (meanValue, meanValue)
    
    // Fill a vector with samples drawn from the Normal random distribution
    breeze.linalg.DenseVector.rand (size, normalDist)
  }

  def vectorStats (vector: breeze.linalg.DenseVector[Double]) = {
    // Return the mean value
    breeze.stats.mean (vector)
  }

  def readVector (filename: String) = {
    // Un-serialize the vector from a CSV file
    println ("Read a vector from the '" + filename + "' file.")
    breeze.linalg.csvread (file = new java.io.File(filename), separator = ';', skipLines = 0)(0, ::).t
  }

  def writeVector (vector: breeze.linalg.DenseVector[Double], filename: String) = {
    // Serialize the vector into a CSV, made of just one line
    breeze.linalg.csvwrite (file = new java.io.File (filename), mat = vector.toDenseMatrix,
      separator = ';')
    println ("Written the " + vector.size + "-vector into the '" + filename + "' file.")
  }

  def generateMatrix (meanValue: Double, size: Int) = {
    // Normal distribution with mean value given as parameter.
    val normalDist = breeze.stats.distributions.Gaussian (meanValue, meanValue)
    
    // Fill a size x size matrix with samples drawn from the Normal random distribution
    breeze.linalg.DenseMatrix.rand (size, size, normalDist)
  }

  def matrixStats (matrix: breeze.linalg.DenseMatrix[Double]) = {
    // Return the mean value
    breeze.stats.mean (matrix)
  }

  def readMatrix (filename: String) = {
    // Un-serialize the matrix from a CSV file
    println ("Read a matrix from the '" + filename + "' file.")
    breeze.linalg.csvread (file = new java.io.File(filename), separator = ';', skipLines = 0)
  }

  def writeMatrix (matrix: breeze.linalg.DenseMatrix[Double], filename: String) = {
    // Serialize the matrix into a CSV file
    breeze.linalg.csvwrite (file = new java.io.File (filename), mat = matrix, separator = ';')
    println ("Written the " + matrix.rows + "x" + matrix.cols + "-matrix into the '" + filename + "' file.")
  }

  // Play with vectors
  // writeVector (generateVector (5, 10000), "data/normal-distributed-vector.csv")
  val normalVectorRetrieved = readVector ("data/normal-distributed-vector.csv")
  val sizeVector = normalVectorRetrieved.size
  println ("Mean of the retrieved " + sizeVector + "-sample vector (should be around 5): " + vectorStats (normalVectorRetrieved))
  val normalVectorGenerated = generateVector (5, sizeVector)
  println ("Mean of the generated " + sizeVector + "-sample vector (should be around 5): " + vectorStats (normalVectorGenerated))
  // The difference between two vectors needs an hardware-optimized implementation of BLAS,
  // for instance OpenBLAS
  // val normalVectorDiff = normalVectorRetrieved - normalVectorGenerated
  // println ("Mean of the diff " + sizeVector + "-sample vector (should be around 0): " + vectorStats (normalVectorDiff))

  // Play with matrices
  // writeMatrix (generateMatrix (5, 100), "data/normal-distributed-matrix.csv")
  val normalMatrixRetrieved = readMatrix ("data/normal-distributed-matrix.csv")
  val rowsMatrix = normalMatrixRetrieved.rows
  val colsMatrix = normalMatrixRetrieved.cols
  println ("Mean of the retrieved " + rowsMatrix + "x" + colsMatrix + "-matrix (should be around 5): " + matrixStats (normalMatrixRetrieved))
  val normalMatrixGenerated = generateMatrix (5, rowsMatrix)
  println ("Mean of the generated " + rowsMatrix + "x" + colsMatrix + "-matrix (should be around 5): " + matrixStats (normalMatrixGenerated))
  // The difference between two matrices needs an hardware-optimized implementation of BLAS,
  // for instance OpenBLAS
  // val normalMatrixDiff = normalMatrixRetrieved - normalMatrixGenerated
  // println ("Mean of the diff " + rowsMatrix + "x" + colsMatrix + "-matrix (should be around 0): " + matrixStats (normalMatrixDiff))
}

