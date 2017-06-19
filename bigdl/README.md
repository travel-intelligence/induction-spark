# References
* Install on Linux and MacOS: https://github.com/intel-analytics/BigDL/wiki/Build-Page
* Downloads:
 * BigDL: https://github.com/intel-analytics/BigDL/wiki/Downloads
 * CIFAR-10 data: https://www.cs.toronto.edu/~kriz/cifar.html

# Cloning of the project

```bash
$ mkdir -p ~/dev/bi
$ cd ~/dev/bi
$ git clone git@github.com:travel-intelligence/induction-spark.git tiinductionsparkgit
$ cd ~/dev/bi/tiinductionsparkgit/bigdl
```

# Installation of BigDL

## CentOS / Fedora

```bash
$ export SPARK_VERSION=2.1
$ export BIGDL_VERSION=0.1.1
$ cat >> build.sbt << _EOF

libraryDependencies += "com.intel.analytics.bigdl" % "bigdl-SPARK_${SPARK_VERSION}" % "${BIGDL_VERSION}"

_EOF
```

### Binary package

```bash 
$ su -
$ mkdir -p /opt/bigdl
$ export SPARK_VERSION=2.1.0
$ export BIGDL_VERSION=0.1.1
$ export SCALA_VERSION=2.11.8
$ wget https://repo1.maven.org/maven2/com/intel/analytics/bigdl/dist-spark-S{SPARK_VERSION}-scala-${SCALA_VERSION}-linux64/${BIGDL_VERSION}/dist-spark-${SPARK_VERSION}-scala-${SCALA_VERSION}-linux64-${BIGDL_VERSION}-dist.zip
$ mkdir bigdl-${BIGDL_VERSION}
$ ln -s bigdl-${BIGDL_VERSION} current
$ cd current
$ unzip ../dist-spark-${SPARK_VERSION}-scala-${SCALA_VERSION}-linux64-${BIGDL_VERSION}-dist.zip
$ cd ~
```

## MacOS

### Binary package

```bash
$ su -
$ mkdir -p /opt/bigdl
$ cd /opt/bigdl
$ export SPARK_VERSION=2.1.0
$ export BIGDL_VERSION=0.1.1
$ export SCALA_VERSION=2.11.8
$ wget https://repo1.maven.org/maven2/com/intel/analytics/bigdl/dist-spark-${SPARK_VERSION}-scala-${SCALA_VERSION}-mac/${BIGDL_VERSION}/dist-spark-${SPARK_VERSION}-scala-${SCALA_VERSION}-mac-${BIGDL_VERSION}-dist.zip
$ mkdir bigdl-${BIGDL_VERSION}
$ ln -s bigdl-${BIGDL_VERSION} current
$ cd current
$ unzip ../dist-spark-${SPARK_VERSION}-scala-${SCALA_VERSION}-mac-${BIGDL_VERSION}-dist.zip
$ cd ~

```

### SBT

```bash
$ export SPARK_VERSION=2.1
$ export BIGDL_VERSION=0.1.1
$ cat >> build.sbt << _EOF

libraryDependencies += "com.intel.analytics.bigdl.native" % "mkl-java-mac" % "${BIGDL_VERSION}" from "http://repo1.maven.org/maven2/com/intel/analytics/bigdl/native/mkl-java-mac/${BIGDL_VERSION}/mkl-java-mac-${BIGDL_VERSION}.jar"

```

## Testing with Spark Shell

```bash
$ export BIGDL_HOME=/opt/bigdl/current
$ source ${BIGDL_HOME}/bin/bigdl.sh
$ ${SPARK_HOME}/bin/spark-shell --properties-file ${BIGDL_HOME}/conf/spark-bigdl.conf --jars ${BIGDL_HOME}/lib/bigdl-SPARK_2.1-${BIGDL_VERSION}-jar-with-dependencies.jar
```
```scala
scala> import com.intel.analytics.bigdl.tensor.Tensor
scala> Tensor[Double](2,2).fill(1.0)
```

## Data
### CIFAR
```bash
$ cd data
$ wget https://www.cs.toronto.edu/~kriz/cifar-10-binary.tar.gz
$ tar zxf cifar-10-binary.tar.gz && rm -f cifar-10-binary.tar.gz
$ cd ..
```

# Running the application
## Build the Jar artefact
```bash
$ cd ~/dev/bi/tiinductionsparkgit/bigdl
$ sbt clean package
```

## Source the BigDL environment
```bash
$ export BIGDL_HOME=/opt/bigdl/current
$ source ${BIGDL_HOME}/bin/bigdl.sh
```

## Local stand-alone Spark

```bash
$ spark-submit --master local[physical_core_number] \
               --class com.intel.analytics.bigdl.models.vgg.Train \
               dist/lib/bigdl-VERSION-jar-with-dependencies.jar \
               -f data \
               -b 1 \
               --checkpoint ./model

```

## Yarn cluster client mode

```bash

$ spark-submit --master yarn --deploy-mode client \
               --executor-cores cores_per_executor \
               --num-executors executors_number \
               --driver-class-path dist/lib/bigdl-VERSION-jar-with-dependencies.jar \
               --class com.intel.analytics.bigdl.models.vgg.Train \
               dist/lib/bigdl-VERSION-jar-with-dependencies.jar \
               -f data \
               -b 1 \
               --checkpoint ./model
```

