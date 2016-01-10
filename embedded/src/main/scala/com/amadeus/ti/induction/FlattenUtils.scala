package com.amadeus.ti.induction

import scala.reflect.ClassTag

//
object FlattenUtils {
  
  implicit class FlattenOps[V: ClassTag](rdd: org.apache.spark.rdd.RDD[Option[V]]) {
    def flatten: org.apache.spark.rdd.RDD[V] = {
      rdd.flatMap (x => x)
    }
  }

}
