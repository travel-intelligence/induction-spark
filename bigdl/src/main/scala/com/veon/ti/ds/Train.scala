/*
 * Copyright 2016 The BigDL Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.veon.ti.ds

import com.intel.analytics.bigdl._
import com.intel.analytics.bigdl.dataset.DataSet
import com.intel.analytics.bigdl.dataset.image.{BGRImgNormalizer, BytesToBGRImg, BGRImgToBatch}
import com.intel.analytics.bigdl.nn.{ClassNLLCriterion, Module}
import com.intel.analytics.bigdl.optim.{SGD, OptimMethod}
import com.intel.analytics.bigdl.tensor.TensorNumericMath.TensorNumeric._
import com.intel.analytics.bigdl.utils.{Engine, LoggerFilter, T, Table}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

object Train {
  LoggerFilter.redirectSparkInfoLogs()
  Logger.getLogger("com.intel.analytics.bigdl.optim").setLevel(Level.INFO)

  import Utils._

  def main(args: Array[String]): Unit = {
    trainParser.parse(args, new TrainParams()).map(param => {
      // Spark session
      val spark = org.apache.spark.sql.SparkSession
        .builder()
        .appName("Train VGG on CIFAR-10")
        .config("spark.master", "local")
        .enableHiveSupport()
        .getOrCreate()

      // Will throw exception without this config when has only one executor
      spark.conf.set ("spark.rpc.message.maxSize", "200")

      val trainDataSet =
        DataSet.array(Utils.loadTrain(param.folder), spark.sparkContext) ->
        BytesToBGRImg() -> BGRImgNormalizer(trainMean, trainStd) ->
        BGRImgToBatch(param.batchSize)

      val model = if (param.modelSnapshot.isDefined) {
        Module.load[Float](param.modelSnapshot.get)
      } else {
        VggForCifar10(classNum = 10)
      }

      val optimMtd = if (param.stateSnapshot.isDefined) {
        OptimMethod.load[Float](param.stateSnapshot.get)
      } else {
        new SGD[Float](learningRate = 0.01, learningRateDecay = 0.0,
          weightDecay = 0.0005, momentum = 0.9, dampening = 0.0, nesterov = false,
          learningRateSchedule = SGD.EpochStep(25, 0.5))
      }

      val optimizer = Optimizer(
        model = model,
        dataset = trainDataSet,
        criterion = new ClassNLLCriterion[Float]()
      )

      val validateSet = DataSet.array(Utils.loadTest(param.folder), spark.sparkContext) ->
        BytesToBGRImg() -> BGRImgNormalizer(testMean, testStd) ->
        BGRImgToBatch(param.batchSize)

      if (param.checkpoint.isDefined) {
        optimizer.setCheckpoint(param.checkpoint.get, Trigger.everyEpoch)
      }
      if(param.overWriteCheckpoint) {
        optimizer.overWriteCheckpoint()
      }
      optimizer
        .setValidation(Trigger.everyEpoch, validateSet, Array(new Top1Accuracy[Float]))
        .setOptimMethod(optimMtd)
        .setEndWhen(Trigger.maxEpoch(param.maxEpoch))
        .optimize()

      spark.stop()
    })
  }
}

