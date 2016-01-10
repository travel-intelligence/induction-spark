# Induction for Spark

## Setup
### On Fedora
#### For the Hadoop clients
```bash
$ dnf -y install hadoop-common-native hadoop-client parquet-format libhdfs hadoop-hdfs-fuse parquet-format hadoop-maven-plugin
```
#### To run a stand-alone Hadoop cluster
```bash
$ dnf -y install hadoop-mapreduce hadoop-yarn hadoop-httpfs hive
```
#### Setup HDFS (as root)
See also http://fedoraproject.org/wiki/User:Denisarnaud/Hadoop

* Install the Hadoop packages
```bash
$ dnf install hadoop-common hadoop-hdfs hadoop-mapreduce \
 hadoop-mapreduce-examples hadoop-yarn maven-* xmvn*
```
* Set the JAVA_HOME environment variable within the Hadoop configuration
file (the default does not seem to work)
```bash
$ vi /etc/hadoop/hadoop-env.sh
```
For instance, with the OpenJDK on Fedora 23, the line should read something like:
```bash
$ export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.65-15.b17.fc23.x86_64
```
Or with Oracle Java JDK 8, the line would become:
```bash
$ export JAVA_HOME=/usr/java/jdk1.8.0_51
```

* You may want to adjust the amount of memory and the number of cores
for the YARN cluster, by adding the following lines to /etc/hadoop/yarn-site.xml
(derived from http://hadoop.apache.org/docs/r2.4.1/hadoop-yarn/hadoop-yarn-common/yarn-default.xml):
```xml
 <property>
   <description>Number of CPU cores that can be allocated for containers.</description>
   <name>yarn.nodemanager.resource.cpu-vcores</name>
   <value>2</value>
 </property>
 <property>
   <description>Amount of physical memory, in MB, that can be allocated for containers.</description>
   <name>yarn.nodemanager.resource.memory-mb</name>
   <value>2048</value>
 </property>
 <property>
   <description>The maximum allocation for every container request at the RM, in MBs. Memory requests higher than this won't take effect, and will get capped to this value.</description>
   <name>yarn.scheduler.maximum-allocation-mb</name>
   <value>2048</value>
 </property>
```

* A few parameters need to be adjusted for the examples with YARN and the
stand-alone Hadoop cluster (distinct from the Spark cluster embedded in
the JVM launched by sbt)
  * /etc/hadoop/core-site.xml (add 20000 to the port number):
```xml
  <property>
    <name>fs.default.name</name>
    <value>hdfs://localhost:28020</value>
  </property>
```
  * /etc/hadoop/mapred-site.xml (add 20000 to the port number):
```xml
  <property>
    <name>mapred.job.tracker</name>
    <value>localhost:28021</value>
  </property>
```
  * /etc/hadoop/hdfs-site.xml:
```xml
  <property>
     <name>dfs.safemode.min.datanodes</name>
     <value>1</value>
  </property>
```

* Format the name-node:
```bash
$ runuser hdfs -s /bin/bash /bin/bash -c "hdfs namenode -format"
```

* Start the Hadoop services:
```bash
$ systemctl start hadoop-namenode hadoop-datanode hadoop-nodemanager hadoop-resourcemanager tomcat@httpfs
```

- Check that the Hadoop services have been started:
```bash
$ systemctl status hadoop-namenode hadoop-datanode hadoop-nodemanager hadoop-resourcemanager tomcat@httpfs
```
Note that, as of January 2016, tomcat@httpfs will not start, due to a bug
on Fedora 23+ (http://bugzilla.redhat.com/show_bug.cgi?id=1295968).
A work around is described into the bug report. Basically, the configuration
files of the Tomcat version delivered with Fedora 23 (/etc/tomcat/), should be
copied in place of the tomcat@https ones (/etc/hadoop/tomcat/).

* Enable the Hadoop services permanently, in case everything went smoothly:
```bash
$ systemctl enable hadoop-namenode hadoop-datanode hadoop-nodemanager hadoop-resourcemanager tomcat@httpfs
```


* Create the default HDFS directories:
```bash
$ hdfs-create-dirs
```

* Create the HDFS home directory for your Unix user:
```bash
$ runuser hdfs -s /bin/bash /bin/bash -c "hadoop fs -mkdir /user/<username>"
$ runuser hdfs -s /bin/bash /bin/bash -c "hadoop fs -chown build /user/<username>"
```

* Create a data directory for the data, and give the write access to the Unix user:
```bash
$ runuser hdfs -s /bin/bash /bin/bash -c "hadoop fs -mkdir -p /data/induction/student"
$ runuser hdfs -s /bin/bash /bin/bash -c "hadoop fs -chown -R build /data"
```

## Examples

### Go Through a Basic Example

#### Overview
That example instanciates (Spark) DataFrames through RDD structures.
Those latter may retrieve data from CSV files.
Two ways are explored in that example:
* The RDD retrieves the full content of the CSV file, and then creates
the DataFrame.
That way seems more direct and easier, but it implies that the whole data set
fits in memory. So, it does not scale to big data cases.
* The RDD retrieves one row at a time, converts it to a RDD thanks to a case
class (Student), and then adds the RDD to the DataFrame.


#### Setup the Project
```bash
$ mkdir -p ~/dev/bi
$ cd ~/dev/bi
$ git clone git@github.com:travel-intelligence/induction-spark.git tiinductionsparkgit
$ cd ~/dev/bi/tiinductionsparkgit
$ sbt compile
[info] Loading global plugins from ~/.sbt/0.13/plugins
[info] Loading project definition from ~/dev/bi/tiinductionsparkgit/project
[info] Set current project to ti-induction-spark (in build file:~/dev/bi/tiinductionsparkgit/)
[success] Total time: 0 s, completed Dec 28, 2015 8:54:18 PM
```

#### Run the project
```bash
$ sbt run 2>&1 | grep -v "error"
 [...]
[info] Loading project definition from ~/dev/bi/tiinductionsparkgit/project
[info] Set current project to induction-spark (in build file:~/dev/bi/tiinductionsparkgit/)
[info] Running com.amadeus.ti.induction.Introduction 
[info] //////////// First way: without case classes //////////////
[info] studentsDF:
[info] root
[info]  |-- id: string (nullable = true)
[info]  |-- studentName: string (nullable = true)
[info]  |-- phone: string (nullable = true)
[info]  |-- email: string (nullable = true)
[info] 
[info] +--+-----------+--------------+--------------------+
[info] |id|studentName|         phone|               email|
[info] +--+-----------+--------------+--------------------+
[info] | 1|      Burke|1-300-746-8446|ullamcorper.velit...|
[info] | 2|      Kamal|1-668-571-5046|pede.Suspendisse@...|
[info] | 3|       Olga|1-956-311-1686|Aenean.eget.metus...|
[info] +--+-----------+--------------+--------------------+
[info] 
[info] +--+-----------+--------------+--------------------+
[info] |id|studentName|         phone|               email|
[info] +--+-----------+--------------+--------------------+
[info] | 1|      Burke|1-300-746-8446|ullamcorper.velit...|
[info] | 2|      Kamal|1-668-571-5046|pede.Suspendisse@...|
[info] | 3|       Olga|1-956-311-1686|Aenean.eget.metus...|
[info] | 4|      Belle|1-246-894-6340|vitae.aliquet.nec...|
[info] | 5|     Trevor|1-300-527-4967|dapibus.id@acturp...|
[info] | 6|     Laurel|1-691-379-9921|adipiscing@consec...|
[info] | 7|       Sara|1-608-140-1995|Donec.nibh@enimEt...|
[info] | 8|     Kaseem|1-881-586-2689|cursus.et.magna@e...|
[info] | 9|        Lev|1-916-367-5608|Vivamus.nisi@ipsu...|
[info] |10|       Maya|1-271-683-2698|accumsan.convalli...|
[info] |11|        Emi|1-467-270-1337|        est@nunc.com|
[info] |12|      Caleb|1-683-212-0896|Suspendisse@Quisq...|
[info] |13|   Florence|1-603-575-2444|sit.amet.dapibus@...|
[info] |14|      Anika|1-856-828-7883|euismod@ligulaeli...|
[info] |15|      Tarik|1-398-171-2268|turpis@felisorci.com|
[info] |16|      Amena|1-878-250-3129|lorem.luctus.ut@s...|
[info] |17|    Blossom|1-154-406-9596|Nunc.commodo.auct...|
[info] |18|        Guy|1-869-521-3230|senectus.et.netus...|
[info] |19|    Malachi|1-608-637-2772|Proin.mi.Aliquam@...|
[info] |20|     Edward|1-711-710-6552|lectus@aliquetlib...|
[info] +--+-----------+--------------+--------------------+
[info] 
[info] [1,Burke,1-300-746-8446,ullamcorper.velit.in@ametnullaDonec.co.uk]
[info] [2,Kamal,1-668-571-5046,pede.Suspendisse@interdumenim.edu]
[info] [3,Olga,1-956-311-1686,Aenean.eget.metus@dictumcursusNunc.edu]
[info] [4,Belle,1-246-894-6340,vitae.aliquet.nec@neque.co.uk]
[info] [5,Trevor,1-300-527-4967,dapibus.id@acturpisegestas.net]
[info] [1,Burke,1-300-746-8446,ullamcorper.velit.in@ametnullaDonec.co.uk]
[info] [2,Kamal,1-668-571-5046,pede.Suspendisse@interdumenim.edu]
[info] [3,Olga,1-956-311-1686,Aenean.eget.metus@dictumcursusNunc.edu]
[info] [4,Belle,1-246-894-6340,vitae.aliquet.nec@neque.co.uk]
[info] [5,Trevor,1-300-527-4967,dapibus.id@acturpisegestas.net]
[info] emailDataFrame:
[info] +--------------------+
[info] |               email|
[info] +--------------------+
[info] |ullamcorper.velit...|
[info] |pede.Suspendisse@...|
[info] |Aenean.eget.metus...|
[info] +--------------------+
[info] 
[info] studentEmailDF:
[info] +-----------+--------------------+
[info] |studentName|               email|
[info] +-----------+--------------------+
[info] |      Burke|ullamcorper.velit...|
[info] |      Kamal|pede.Suspendisse@...|
[info] |       Olga|Aenean.eget.metus...|
[info] +-----------+--------------------+
[info] 
[info] +--+-----------+--------------+--------------------+
[info] |id|studentName|         phone|               email|
[info] +--+-----------+--------------+--------------------+
[info] | 6|     Laurel|1-691-379-9921|adipiscing@consec...|
[info] | 7|       Sara|1-608-140-1995|Donec.nibh@enimEt...|
[info] | 8|     Kaseem|1-881-586-2689|cursus.et.magna@e...|
[info] | 9|        Lev|1-916-367-5608|Vivamus.nisi@ipsu...|
[info] |10|       Maya|1-271-683-2698|accumsan.convalli...|
[info] |11|        Emi|1-467-270-1337|        est@nunc.com|
[info] |12|      Caleb|1-683-212-0896|Suspendisse@Quisq...|
[info] +--+-----------+--------------+--------------------+
[info] 
[info] +--+-----------+--------------+--------------------+
[info] |id|studentName|         phone|               email|
[info] +--+-----------+--------------+--------------------+
[info] |21|           |1-598-439-7549|consectetuer.adip...|
[info] |32|           |1-184-895-9602|accumsan.laoreet@...|
[info] |45|           |1-245-752-0481|Suspendisse.eleif...|
[info] |83|           |1-858-810-2204|sociis.natoque@eu...|
[info] |94|           |1-443-410-7878|Praesent.eu.nulla...|
[info] +--+-----------+--------------+--------------------+
[info] 
[info] +--+-----------+--------------+--------------------+
[info] |id|studentName|         phone|               email|
[info] +--+-----------+--------------+--------------------+
[info] |21|           |1-598-439-7549|consectetuer.adip...|
[info] |32|           |1-184-895-9602|accumsan.laoreet@...|
[info] |33|       NULL|1-105-503-0141|Donec@Inmipede.co.uk|
[info] |45|           |1-245-752-0481|Suspendisse.eleif...|
[info] |83|           |1-858-810-2204|sociis.natoque@eu...|
[info] |94|           |1-443-410-7878|Praesent.eu.nulla...|
[info] +--+-----------+--------------+--------------------+
[info] 
[info] +--+-----------+--------------+--------------------+
[info] |id|studentName|         phone|               email|
[info] +--+-----------+--------------+--------------------+
[info] |10|       Maya|1-271-683-2698|accumsan.convalli...|
[info] |19|    Malachi|1-608-637-2772|Proin.mi.Aliquam@...|
[info] |24|    Marsden|1-477-629-7528|Donec.dignissim.m...|
[info] |37|      Maggy|1-910-887-6777|facilisi.Sed.nequ...|
[info] |61|     Maxine|1-422-863-3041|aliquet.molestie....|
[info] |77|      Maggy|1-613-147-4380| pellentesque@mi.net|
[info] |97|    Maxwell|1-607-205-1273|metus.In@musAenea...|
[info] +--+-----------+--------------+--------------------+
[info] 
[info] dfFilteredBySQL:
[info] +--+-----------+--------------+--------------------+
[info] |id|studentName|         phone|               email|
[info] +--+-----------+--------------+--------------------+
[info] |87|      Selma|1-601-330-4409|vulputate.velit@p...|
[info] |96|   Channing|1-984-118-7533|viverra.Donec.tem...|
[info] | 4|      Belle|1-246-894-6340|vitae.aliquet.nec...|
[info] |78|       Finn|1-213-781-6969|vestibulum.massa@...|
[info] |53|     Kasper|1-155-575-9346|velit.eget@pedeCu...|
[info] |63|      Dylan|1-417-943-8961|vehicula.aliquet@...|
[info] |35|     Cadman|1-443-642-5919|ut.lacus@adipisci...|
[info] +--+-----------+--------------+--------------------+
[info] 
[info] +--+-----------+--------------+--------------------+
[info] |id|studentName|         phone|               email|
[info] +--+-----------+--------------+--------------------+
[info] |50|      Yasir|1-282-511-4445|eget.odio.Aliquam...|
[info] |52|       Xena|1-527-990-8606|in.faucibus.orci@...|
[info] |86|     Xandra|1-677-708-5691|libero@arcuVestib...|
[info] |43|     Wynter|1-440-544-1851|amet.risus.Donec@...|
[info] |31|    Wallace|1-144-220-8159| lorem.lorem@non.net|
[info] |66|      Vance|1-268-680-0857|pellentesque@netu...|
[info] |41|     Tyrone|1-907-383-5293|non.bibendum.sed@...|
[info] | 5|     Trevor|1-300-527-4967|dapibus.id@acturp...|
[info] |65|      Tiger|1-316-930-7880|nec@mollisnoncurs...|
[info] |15|      Tarik|1-398-171-2268|turpis@felisorci.com|
[info] +--+-----------+--------------+--------------------+
[info] 
[info] +--+-----------+--------------+--------------------+
[info] |id|studentName|         phone|               email|
[info] +--+-----------+--------------+--------------------+
[info] |21|           |1-598-439-7549|consectetuer.adip...|
[info] |32|           |1-184-895-9602|accumsan.laoreet@...|
[info] |45|           |1-245-752-0481|Suspendisse.eleif...|
[info] |83|           |1-858-810-2204|sociis.natoque@eu...|
[info] |94|           |1-443-410-7878|Praesent.eu.nulla...|
[info] |91|       Abel|1-530-527-7467|    urna@veliteu.edu|
[info] |69|       Aiko|1-682-230-7013|turpis.vitae.puru...|
[info] |47|       Alma|1-747-382-6775|    nec.enim@non.org|
[info] |26|      Amela|1-526-909-2605| in@vitaesodales.edu|
[info] |16|      Amena|1-878-250-3129|lorem.luctus.ut@s...|
[info] +--+-----------+--------------+--------------------+
[info] 
[info] copyOfStudentsDF:
[info] +--------+--------------------+
[info] |    name|               email|
[info] +--------+--------------------+
[info] |   Burke|ullamcorper.velit...|
[info] |   Kamal|pede.Suspendisse@...|
[info] |    Olga|Aenean.eget.metus...|
[info] |   Belle|vitae.aliquet.nec...|
[info] |  Trevor|dapibus.id@acturp...|
[info] |  Laurel|adipiscing@consec...|
[info] |    Sara|Donec.nibh@enimEt...|
[info] |  Kaseem|cursus.et.magna@e...|
[info] |     Lev|Vivamus.nisi@ipsu...|
[info] |    Maya|accumsan.convalli...|
[info] |     Emi|        est@nunc.com|
[info] |   Caleb|Suspendisse@Quisq...|
[info] |Florence|sit.amet.dapibus@...|
[info] |   Anika|euismod@ligulaeli...|
[info] |   Tarik|turpis@felisorci.com|
[info] |   Amena|lorem.luctus.ut@s...|
[info] | Blossom|Nunc.commodo.auct...|
[info] |     Guy|senectus.et.netus...|
[info] | Malachi|Proin.mi.Aliquam@...|
[info] |  Edward|lectus@aliquetlib...|
[info] +--------+--------------------+
[info] 
[info] newStudentsDF:
[info] root
[info]  |-- name: string (nullable = true)
[info]  |-- email: string (nullable = true)
[info] 
[info] +--------+--------------------+
[info] |    name|               email|
[info] +--------+--------------------+
[info] |   Burke|ullamcorper.velit...|
[info] |   Kamal|pede.Suspendisse@...|
[info] |    Olga|Aenean.eget.metus...|
[info] |   Belle|vitae.aliquet.nec...|
[info] |  Trevor|dapibus.id@acturp...|
[info] |  Laurel|adipiscing@consec...|
[info] |    Sara|Donec.nibh@enimEt...|
[info] |  Kaseem|cursus.et.magna@e...|
[info] |     Lev|Vivamus.nisi@ipsu...|
[info] |    Maya|accumsan.convalli...|
[info] |     Emi|        est@nunc.com|
[info] |   Caleb|Suspendisse@Quisq...|
[info] |Florence|sit.amet.dapibus@...|
[info] |   Anika|euismod@ligulaeli...|
[info] |   Tarik|turpis@felisorci.com|
[info] |   Amena|lorem.luctus.ut@s...|
[info] | Blossom|Nunc.commodo.auct...|
[info] |     Guy|senectus.et.netus...|
[info] | Malachi|Proin.mi.Aliquam@...|
[info] |  Edward|lectus@aliquetlib...|
[info] +--------+--------------------+
[info] 
[info] //////////// Second way: with case classes //////////////
[info] empFrame:
[info] root
[info]  |-- id: integer (nullable = false)
[info]  |-- name: string (nullable = true)
[info] 
[info] +--+-----+
[info] |id| name|
[info] +--+-----+
[info] | 1| Arun|
[info] | 2|Jason|
[info] | 3| Abhi|
[info] +--+-----+
[info] 
[info] empFrameWithRenamedColumns:
[info] root
[info]  |-- empId: integer (nullable = false)
[info]  |-- name: string (nullable = true)
[info] 
[info] +-----+-----+
[info] |empId| name|
[info] +-----+-----+
[info] |    2|Jason|
[info] |    1| Arun|
[info] |    3| Abhi|
[info] +-----+-----+
[info] 
[info] root
[info]  |-- _1: integer (nullable = false)
[info]  |-- _2: string (nullable = true)
[info] 
[info] +--+-------+
[info] |_1|     _2|
[info] +--+-------+
[info] | 1|Android|
[info] | 2| iPhone|
[info] +--+-------+
[info] 
[success] Total time: 9 s, completed Jan 3, 2016 5:25:13 PM
```

### Further Creations of DataFrame Structures

#### Setup of the Project

Some parts of the code use data files on HDFS.

```bash
$ cd ~/dev/bi/tiinductionsparkgit/student
$ hadoop fs -mkdir -p /data/induction/student
$ hadoop fs -put data/profiles.json /data/induction/student
$ hadoop fs -ls /data/induction/student
-rw-r--r--   1 <username> supergroup     161820 2016-01-05 23:05 /data/induction/student/profiles.json
```
#### Run the project
```bash
$ sbt run 2>&1 | grep -v "error"
 [...]
[info] Loading project definition from ~/dev/bi/tiinductionsparkgit/student/project
[info] Set current project to induction-spark (in build file:~/dev/bi/tiinductionsparkgit/student/)
[info] Running com.amadeus.ti.induction.Introduction 
[info] /////////// First way: with a class extending Product /////////////
[info] root
[info]  |-- school: string (nullable = true)
[info]  |-- sex: string (nullable = true)
[info]  |-- age: integer (nullable = false)
[info]  |-- address: string (nullable = true)
[info]  |-- famsize: string (nullable = true)
[info]  |-- pstatus: string (nullable = true)
[info]  |-- medu: integer (nullable = false)
[info]  |-- fedu: integer (nullable = false)
[info]  |-- mjob: string (nullable = true)
[info]  |-- fjob: string (nullable = true)
[info]  |-- reason: string (nullable = true)
[info]  |-- guardian: string (nullable = true)
[info]  |-- traveltime: integer (nullable = false)
[info]  |-- studytime: integer (nullable = false)
[info]  |-- failures: integer (nullable = false)
[info]  |-- schoolsup: string (nullable = true)
[info]  |-- famsup: string (nullable = true)
[info]  |-- paid: string (nullable = true)
[info]  |-- activities: string (nullable = true)
[info]  |-- nursery: string (nullable = true)
[info]  |-- higher: string (nullable = true)
[info]  |-- internet: string (nullable = true)
[info]  |-- romantic: string (nullable = true)
[info]  |-- famrel: integer (nullable = false)
[info]  |-- freetime: integer (nullable = false)
[info]  |-- goout: integer (nullable = false)
[info]  |-- dalc: integer (nullable = false)
[info]  |-- walc: integer (nullable = false)
[info]  |-- health: integer (nullable = false)
[info]  |-- absences: integer (nullable = false)
[info]  |-- g1: integer (nullable = false)
[info]  |-- g2: integer (nullable = false)
[info]  |-- g3: integer (nullable = false)
[info] 
[info] For input string: "age"
[info] +------+---+---+-------+-------+-------+----+----+--------+--------+----------+--------+----------+---------+--------+---------+------+----+----------+-------+------+--------+--------+------+--------+-----+----+----+------+--------+--+--+--+
[info] |school|sex|age|address|famsize|pstatus|medu|fedu|    mjob|    fjob|    reason|guardian|traveltime|studytime|failures|schoolsup|famsup|paid|activities|nursery|higher|internet|romantic|famrel|freetime|goout|dalc|walc|health|absences|g1|g2|g3|
[info] +------+---+---+-------+-------+-------+----+----+--------+--------+----------+--------+----------+---------+--------+---------+------+----+----------+-------+------+--------+--------+------+--------+-----+----+----+------+--------+--+--+--+
[info] |    GP|  F| 18|      U|    GT3|      A|   4|   4| at_home| teacher|    course|  mother|         2|        2|       0|      yes|    no|  no|        no|    yes|   yes|      no|      no|     4|       3|    4|   1|   1|     3|       6| 5| 6| 6|
[info] |    GP|  F| 17|      U|    GT3|      T|   1|   1| at_home|   other|    course|  father|         1|        2|       0|       no|   yes|  no|        no|     no|   yes|     yes|      no|     5|       3|    3|   1|   1|     3|       4| 5| 5| 6|
[info] |    GP|  F| 15|      U|    LE3|      T|   1|   1| at_home|   other|     other|  mother|         1|        2|       3|      yes|    no| yes|        no|    yes|   yes|     yes|      no|     4|       3|    2|   2|   3|     3|      10| 7| 8|10|
[info] |    GP|  F| 15|      U|    GT3|      T|   4|   2|  health|services|      home|  mother|         1|        3|       0|       no|   yes| yes|       yes|    yes|   yes|     yes|     yes|     3|       2|    2|   1|   1|     5|       2|15|14|15|
[info] |    GP|  F| 16|      U|    GT3|      T|   3|   3|   other|   other|      home|  father|         1|        2|       0|       no|   yes| yes|        no|    yes|   yes|      no|      no|     4|       3|    2|   1|   2|     5|       4| 6|10|10|
[info] |    GP|  M| 16|      U|    LE3|      T|   4|   3|services|   other|reputation|  mother|         1|        2|       0|       no|   yes| yes|       yes|    yes|   yes|     yes|      no|     5|       4|    2|   1|   2|     5|      10|15|15|15|
[info] |    GP|  M| 16|      U|    LE3|      T|   2|   2|   other|   other|      home|  mother|         1|        2|       0|       no|    no|  no|        no|    yes|   yes|     yes|      no|     4|       4|    4|   1|   1|     3|       0|12|12|11|
[info] |    GP|  F| 17|      U|    GT3|      A|   4|   4|   other| teacher|      home|  mother|         2|        2|       0|      yes|   yes|  no|        no|    yes|   yes|      no|      no|     4|       1|    4|   1|   1|     1|       6| 6| 5| 6|
[info] |    GP|  M| 15|      U|    LE3|      A|   3|   2|services|   other|      home|  mother|         1|        2|       0|       no|   yes| yes|        no|    yes|   yes|     yes|      no|     4|       2|    2|   1|   1|     1|       0|16|18|19|
[info] |    GP|  M| 15|      U|    GT3|      T|   3|   4|   other|   other|      home|  mother|         1|        2|       0|       no|   yes| yes|       yes|    yes|   yes|     yes|      no|     5|       5|    1|   1|   1|     5|       0|14|15|15|
[info] |    GP|  F| 15|      U|    GT3|      T|   4|   4| teacher|  health|reputation|  mother|         1|        2|       0|       no|   yes| yes|        no|    yes|   yes|     yes|      no|     3|       3|    3|   1|   2|     2|       0|10| 8| 9|
[info] |    GP|  F| 15|      U|    GT3|      T|   2|   1|services|   other|reputation|  father|         3|        3|       0|       no|   yes|  no|       yes|    yes|   yes|     yes|      no|     5|       2|    2|   1|   1|     4|       4|10|12|12|
[info] |    GP|  M| 15|      U|    LE3|      T|   4|   4|  health|services|    course|  father|         1|        1|       0|       no|   yes| yes|       yes|    yes|   yes|     yes|      no|     4|       3|    3|   1|   3|     5|       2|14|14|14|
[info] |    GP|  M| 15|      U|    GT3|      T|   4|   3| teacher|   other|    course|  mother|         2|        2|       0|       no|   yes| yes|        no|    yes|   yes|     yes|      no|     5|       4|    3|   1|   2|     3|       2|10|10|11|
[info] |    GP|  M| 15|      U|    GT3|      A|   2|   2|   other|   other|      home|   other|         1|        3|       0|       no|   yes|  no|        no|    yes|   yes|     yes|     yes|     4|       5|    2|   1|   1|     3|       0|14|16|16|
[info] |    GP|  F| 16|      U|    GT3|      T|   4|   4|  health|   other|      home|  mother|         1|        1|       0|       no|   yes|  no|        no|    yes|   yes|     yes|      no|     4|       4|    4|   1|   2|     2|       4|14|14|14|
[info] |    GP|  F| 16|      U|    GT3|      T|   4|   4|services|services|reputation|  mother|         1|        3|       0|       no|   yes| yes|       yes|    yes|   yes|     yes|      no|     3|       2|    3|   1|   2|     2|       6|13|14|14|
[info] |    GP|  F| 16|      U|    GT3|      T|   3|   3|   other|   other|reputation|  mother|         3|        2|       0|      yes|   yes|  no|       yes|    yes|   yes|      no|      no|     5|       3|    2|   1|   1|     4|       4| 8|10|10|
[info] |    GP|  M| 17|      U|    GT3|      T|   3|   2|services|services|    course|  mother|         1|        1|       3|       no|   yes|  no|       yes|    yes|   yes|     yes|      no|     5|       5|    5|   2|   4|     5|      16| 6| 5| 5|
[info] |    GP|  M| 16|      U|    LE3|      T|   4|   3|  health|   other|      home|  father|         1|        1|       0|       no|    no| yes|       yes|    yes|   yes|     yes|      no|     3|       1|    3|   1|   3|     5|       4| 8|10|10|
[info] +------+---+---+-------+-------+-------+----+----+--------+--------+----------+--------+----------+---------+--------+---------+------+----+----------+-------+------+--------+--------+------+--------+-----+----+----+------+--------+--+--+--+
[info] 
[info] /////////// Second way: from JSON schema /////////////
[info] DataFrame made directly from a JSON schema:
[info] root
[info]  |-- _id: string (nullable = true)
[info]  |-- about: string (nullable = true)
[info]  |-- address: string (nullable = true)
[info]  |-- age: long (nullable = true)
[info]  |-- company: string (nullable = true)
[info]  |-- email: string (nullable = true)
[info]  |-- eyeColor: string (nullable = true)
[info]  |-- favoriteFruit: string (nullable = true)
[info]  |-- gender: string (nullable = true)
[info]  |-- name: string (nullable = true)
[info]  |-- phone: string (nullable = true)
[info]  |-- registered: string (nullable = true)
[info]  |-- tags: array (nullable = true)
[info]  |    |-- element: string (containsNull = true)
[info] 
[info] +--------------------+--------------------+--------------------+---+---------+--------------------+--------+-------------+------+------------------+-----------------+-------------------+--------------------+
[info] |                 _id|               about|             address|age|  company|               email|eyeColor|favoriteFruit|gender|              name|            phone|         registered|                tags|
[info] +--------------------+--------------------+--------------------+---+---------+--------------------+--------+-------------+------+------------------+-----------------+-------------------+--------------------+
[info] |55578ccb0cc5b350d...|Eu excepteur esse...|694 Oriental Cour...| 30|  ENDIPIN|tracynguyen@endip...|   brown|        apple|female|      Tracy Nguyen|+1 (971) 504-2050|2014-07-14 11:36:28|List(laboris, fug...|
[info] |55578ccb6975c4e2a...|Proident exercita...|267 Amber Street,...| 23|  WARETEL|leannagarrett@war...|   brown|   strawberry|female|    Leanna Garrett|+1 (827) 480-2869|2014-05-13 17:36:02|List(sunt, offici...|
[info] |55578ccb33399a615...|Aute proident Lor...|243 Bridgewater S...| 24| IMPERIUM|blairwhite@imperi...|   brown|       banana|  male|       Blair White|+1 (987) 458-2435|2014-03-13 16:47:44|List(officia, non...|
[info] |55578ccb0f1d5ab09...|Officia cillum nu...|647 Loring Avenue...| 24|  BEADZZA|andrearay@beadzza...|    blue|        apple|female|        Andrea Ray|+1 (992) 473-2206|2014-06-08 11:16:28|List(sint, repreh...|
[info] |55578ccb591a45d4e...|Sit fugiat mollit...|721 Bijou Avenue,...| 27|  AUSTECH|penningtongilbert...|   green|        apple|  male|Pennington Gilbert|+1 (857) 533-3476|2015-04-14 08:54:56|List(eiusmod, vel...|
[info] |55578ccb9f0cd20c4...|Minim do eiusmod ...|694 Llama Court, ...| 21|  PYRAMIA|shelleyburns@pyra...|   green|       banana|female|     Shelley Burns|+1 (965) 409-2401|2014-11-28 19:08:22|List(ea, et, veni...|
[info] |55578ccb8d0accc28...|Qui proident ulla...|498 Perry Terrace...| 40|  EDECINE|nicolefigueroa@ed...|   green|        apple|female|   Nicole Figueroa|+1 (944) 445-3666|2014-08-26 08:59:55|List(ut, enim, en...|
[info] |55578ccbd682cca21...|Labore exercitati...|243 Stillwell Ave...| 32|SINGAVERA|galealvarado@sing...|    blue|       banana|female|     Gale Alvarado|+1 (984) 410-3690|2014-12-17 12:02:42|List(do, laborum,...|
[info] |55578ccb0d9025ddd...|Velit cillum Lore...|649 Beard Street,...| 36|FURNITECH|melindaparker@fur...|    blue|   strawberry|female|    Melinda Parker|+1 (860) 401-3246|2014-03-14 17:30:44|List(officia, fug...|
[info] |55578ccb5be70de0d...|Laborum tempor mi...|972 Marconi Place...| 36|   DIGIAL|byerscarson@digia...|    blue|        apple|  male|      Byers Carson|+1 (807) 591-3568|2014-01-04 07:54:01|List(non, veniam,...|
[info] |55578ccbc5a1050a5...|Duis fugiat Lorem...|483 Hanson Place,...| 31| ASSURITY|kristiemckinney@a...|   green|       banana|female|  Kristie Mckinney|+1 (905) 511-3302|2014-06-07 05:23:58|List(culpa, fugia...|
[info] |55578ccb07fa02369...|Consequat fugiat ...|540 Woodpoint Roa...| 40|MICROLUXE|salazarburks@micr...|   brown|   strawberry|  male|     Salazar Burks|+1 (939) 455-3286|2014-10-02 22:33:54|List(ipsum, adipi...|
[info] |55578ccb809e55bf0...|Lorem culpa Lorem...|442 Ainslie Stree...| 32| VIOCULAR|hopkinspatterson@...|   green|        apple|  male| Hopkins Patterson|+1 (998) 499-2682|2015-04-16 12:18:35|List(dolore, offi...|
[info] |55578ccb204ff8ee6...|Qui ad cillum mag...|444 Argyle Road, ...| 23|    IMKAN|maysrosario@imkan...|   green|        apple|  male|      Mays Rosario|+1 (869) 589-3296|2014-02-03 13:44:13|List(quis, nulla,...|
[info] |55578ccb4b062fc61...|Duis ex velit dui...|571 Sunnyside Ave...| 38|   HELIXO|atkinshancock@hel...|    blue|   strawberry|  male|    Atkins Hancock|+1 (949) 582-3230|2014-01-13 09:04:34|List(dolore, nisi...|
[info] |55578ccba5ff361a9...|Et magna laboris ...|385 Meeker Avenue...| 40|  SLOFAST|edwinarobertson@s...|    blue|   strawberry|female|  Edwina Robertson|+1 (830) 409-2817|2015-01-08 15:02:38|List(excepteur, c...|
[info] |55578ccb386940ac3...|Labore sit mollit...|936 Cheever Place...| 37| FLEETMIX|elsienoel@fleetmi...|    blue|        apple|female|        Elsie Noel|+1 (880) 439-2520|2015-04-14 15:29:40|List(voluptate, e...|
[info] |55578ccbfc41ff7fe...|Consequat eiusmod...|406 Lake Place, M...| 36| EVENTAGE|mirandamarsh@even...|   green|        apple|female|     Miranda Marsh|+1 (836) 586-2989|2014-10-31 19:51:46|List(pariatur, du...|
[info] |55578ccbfa6b6c300...|Duis fugiat conse...|364 Metropolitan ...| 31|  BALOOBA|sharronmcconnell@...|   brown|        apple|female| Sharron Mcconnell|+1 (947) 432-2612|2014-05-30 08:51:01|List(enim, veniam...|
[info] |55578ccbdd6650d81...|Consequat et magn...|113 Applegate Cou...| 29|    EURON|mcdowellwelch@eur...|    blue|   strawberry|  male|    Mcdowell Welch|+1 (866) 439-3371|2014-10-17 21:30:54|List(ipsum, do, c...|
[info] +--------------------+--------------------+--------------------+---+---------+--------------------+--------+-------------+------+------------------+-----------------+-------------------+--------------------+
[info] 
[info] DataFrame made directly from a RDD, itself made from a JSON schema:
[info] root
[info]  |-- _id: string (nullable = true)
[info]  |-- about: string (nullable = true)
[info]  |-- address: string (nullable = true)
[info]  |-- age: long (nullable = true)
[info]  |-- company: string (nullable = true)
[info]  |-- email: string (nullable = true)
[info]  |-- eyeColor: string (nullable = true)
[info]  |-- favoriteFruit: string (nullable = true)
[info]  |-- gender: string (nullable = true)
[info]  |-- name: string (nullable = true)
[info]  |-- phone: string (nullable = true)
[info]  |-- registered: string (nullable = true)
[info]  |-- tags: array (nullable = true)
[info]  |    |-- element: string (containsNull = true)
[info] 
[info] +--------------------+--------------------+--------------------+---+---------+--------------------+--------+-------------+------+------------------+-----------------+-------------------+--------------------+
[info] |                 _id|               about|             address|age|  company|               email|eyeColor|favoriteFruit|gender|              name|            phone|         registered|                tags|
[info] +--------------------+--------------------+--------------------+---+---------+--------------------+--------+-------------+------+------------------+-----------------+-------------------+--------------------+
[info] |55578ccb0cc5b350d...|Eu excepteur esse...|694 Oriental Cour...| 30|  ENDIPIN|tracynguyen@endip...|   brown|        apple|female|      Tracy Nguyen|+1 (971) 504-2050|2014-07-14 11:36:28|List(laboris, fug...|
[info] |55578ccb6975c4e2a...|Proident exercita...|267 Amber Street,...| 23|  WARETEL|leannagarrett@war...|   brown|   strawberry|female|    Leanna Garrett|+1 (827) 480-2869|2014-05-13 17:36:02|List(sunt, offici...|
[info] |55578ccb33399a615...|Aute proident Lor...|243 Bridgewater S...| 24| IMPERIUM|blairwhite@imperi...|   brown|       banana|  male|       Blair White|+1 (987) 458-2435|2014-03-13 16:47:44|List(officia, non...|
[info] |55578ccb0f1d5ab09...|Officia cillum nu...|647 Loring Avenue...| 24|  BEADZZA|andrearay@beadzza...|    blue|        apple|female|        Andrea Ray|+1 (992) 473-2206|2014-06-08 11:16:28|List(sint, repreh...|
[info] |55578ccb591a45d4e...|Sit fugiat mollit...|721 Bijou Avenue,...| 27|  AUSTECH|penningtongilbert...|   green|        apple|  male|Pennington Gilbert|+1 (857) 533-3476|2015-04-14 08:54:56|List(eiusmod, vel...|
[info] |55578ccb9f0cd20c4...|Minim do eiusmod ...|694 Llama Court, ...| 21|  PYRAMIA|shelleyburns@pyra...|   green|       banana|female|     Shelley Burns|+1 (965) 409-2401|2014-11-28 19:08:22|List(ea, et, veni...|
[info] |55578ccb8d0accc28...|Qui proident ulla...|498 Perry Terrace...| 40|  EDECINE|nicolefigueroa@ed...|   green|        apple|female|   Nicole Figueroa|+1 (944) 445-3666|2014-08-26 08:59:55|List(ut, enim, en...|
[info] |55578ccbd682cca21...|Labore exercitati...|243 Stillwell Ave...| 32|SINGAVERA|galealvarado@sing...|    blue|       banana|female|     Gale Alvarado|+1 (984) 410-3690|2014-12-17 12:02:42|List(do, laborum,...|
[info] |55578ccb0d9025ddd...|Velit cillum Lore...|649 Beard Street,...| 36|FURNITECH|melindaparker@fur...|    blue|   strawberry|female|    Melinda Parker|+1 (860) 401-3246|2014-03-14 17:30:44|List(officia, fug...|
[info] |55578ccb5be70de0d...|Laborum tempor mi...|972 Marconi Place...| 36|   DIGIAL|byerscarson@digia...|    blue|        apple|  male|      Byers Carson|+1 (807) 591-3568|2014-01-04 07:54:01|List(non, veniam,...|
[info] |55578ccbc5a1050a5...|Duis fugiat Lorem...|483 Hanson Place,...| 31| ASSURITY|kristiemckinney@a...|   green|       banana|female|  Kristie Mckinney|+1 (905) 511-3302|2014-06-07 05:23:58|List(culpa, fugia...|
[info] |55578ccb07fa02369...|Consequat fugiat ...|540 Woodpoint Roa...| 40|MICROLUXE|salazarburks@micr...|   brown|   strawberry|  male|     Salazar Burks|+1 (939) 455-3286|2014-10-02 22:33:54|List(ipsum, adipi...|
[info] |55578ccb809e55bf0...|Lorem culpa Lorem...|442 Ainslie Stree...| 32| VIOCULAR|hopkinspatterson@...|   green|        apple|  male| Hopkins Patterson|+1 (998) 499-2682|2015-04-16 12:18:35|List(dolore, offi...|
[info] |55578ccb204ff8ee6...|Qui ad cillum mag...|444 Argyle Road, ...| 23|    IMKAN|maysrosario@imkan...|   green|        apple|  male|      Mays Rosario|+1 (869) 589-3296|2014-02-03 13:44:13|List(quis, nulla,...|
[info] |55578ccb4b062fc61...|Duis ex velit dui...|571 Sunnyside Ave...| 38|   HELIXO|atkinshancock@hel...|    blue|   strawberry|  male|    Atkins Hancock|+1 (949) 582-3230|2014-01-13 09:04:34|List(dolore, nisi...|
[info] |55578ccba5ff361a9...|Et magna laboris ...|385 Meeker Avenue...| 40|  SLOFAST|edwinarobertson@s...|    blue|   strawberry|female|  Edwina Robertson|+1 (830) 409-2817|2015-01-08 15:02:38|List(excepteur, c...|
[info] |55578ccb386940ac3...|Labore sit mollit...|936 Cheever Place...| 37| FLEETMIX|elsienoel@fleetmi...|    blue|        apple|female|        Elsie Noel|+1 (880) 439-2520|2015-04-14 15:29:40|List(voluptate, e...|
[info] |55578ccbfc41ff7fe...|Consequat eiusmod...|406 Lake Place, M...| 36| EVENTAGE|mirandamarsh@even...|   green|        apple|female|     Miranda Marsh|+1 (836) 586-2989|2014-10-31 19:51:46|List(pariatur, du...|
[info] |55578ccbfa6b6c300...|Duis fugiat conse...|364 Metropolitan ...| 31|  BALOOBA|sharronmcconnell@...|   brown|        apple|female| Sharron Mcconnell|+1 (947) 432-2612|2014-05-30 08:51:01|List(enim, veniam...|
[info] |55578ccbdd6650d81...|Consequat et magn...|113 Applegate Cou...| 29|    EURON|mcdowellwelch@eur...|    blue|   strawberry|  male|    Mcdowell Welch|+1 (866) 439-3371|2014-10-17 21:30:54|List(ipsum, do, c...|
[info] +--------------------+--------------------+--------------------+---+---------+--------------------+--------+-------------+------+------------------+-----------------+-------------------+--------------------+
[info] 
[info] DataFrame made from a DataType:
[info] root
[info]  |-- id: string (nullable = true)
[info]  |-- about: string (nullable = true)
[info]  |-- address: string (nullable = true)
[info]  |-- age: integer (nullable = true)
[info]  |-- company: string (nullable = true)
[info]  |-- email: string (nullable = true)
[info]  |-- eyeColor: string (nullable = true)
[info]  |-- favoriteFruit: string (nullable = true)
[info]  |-- gender: string (nullable = true)
[info]  |-- name: string (nullable = true)
[info]  |-- phone: string (nullable = true)
[info]  |-- registered: timestamp (nullable = true)
[info]  |-- tags: array (nullable = true)
[info]  |    |-- element: string (containsNull = true)
[info] 
[info] +----+--------------------+--------------------+---+---------+--------------------+--------+-------------+------+------------------+-----------------+--------------------+--------------------+
[info] |  id|               about|             address|age|  company|               email|eyeColor|favoriteFruit|gender|              name|            phone|          registered|                tags|
[info] +----+--------------------+--------------------+---+---------+--------------------+--------+-------------+------+------------------+-----------------+--------------------+--------------------+
[info] |null|Eu excepteur esse...|694 Oriental Cour...| 30|  ENDIPIN|tracynguyen@endip...|   brown|        apple|female|      Tracy Nguyen|+1 (971) 504-2050|2014-07-14 11:36:...|List(laboris, fug...|
[info] |null|Proident exercita...|267 Amber Street,...| 23|  WARETEL|leannagarrett@war...|   brown|   strawberry|female|    Leanna Garrett|+1 (827) 480-2869|2014-05-13 17:36:...|List(sunt, offici...|
[info] |null|Aute proident Lor...|243 Bridgewater S...| 24| IMPERIUM|blairwhite@imperi...|   brown|       banana|  male|       Blair White|+1 (987) 458-2435|2014-03-13 16:47:...|List(officia, non...|
[info] |null|Officia cillum nu...|647 Loring Avenue...| 24|  BEADZZA|andrearay@beadzza...|    blue|        apple|female|        Andrea Ray|+1 (992) 473-2206|2014-06-08 11:16:...|List(sint, repreh...|
[info] |null|Sit fugiat mollit...|721 Bijou Avenue,...| 27|  AUSTECH|penningtongilbert...|   green|        apple|  male|Pennington Gilbert|+1 (857) 533-3476|2015-04-14 08:54:...|List(eiusmod, vel...|
[info] |null|Minim do eiusmod ...|694 Llama Court, ...| 21|  PYRAMIA|shelleyburns@pyra...|   green|       banana|female|     Shelley Burns|+1 (965) 409-2401|2014-11-28 19:08:...|List(ea, et, veni...|
[info] |null|Qui proident ulla...|498 Perry Terrace...| 40|  EDECINE|nicolefigueroa@ed...|   green|        apple|female|   Nicole Figueroa|+1 (944) 445-3666|2014-08-26 08:59:...|List(ut, enim, en...|
[info] |null|Labore exercitati...|243 Stillwell Ave...| 32|SINGAVERA|galealvarado@sing...|    blue|       banana|female|     Gale Alvarado|+1 (984) 410-3690|2014-12-17 12:02:...|List(do, laborum,...|
[info] |null|Velit cillum Lore...|649 Beard Street,...| 36|FURNITECH|melindaparker@fur...|    blue|   strawberry|female|    Melinda Parker|+1 (860) 401-3246|2014-03-14 17:30:...|List(officia, fug...|
[info] |null|Laborum tempor mi...|972 Marconi Place...| 36|   DIGIAL|byerscarson@digia...|    blue|        apple|  male|      Byers Carson|+1 (807) 591-3568|2014-01-04 07:54:...|List(non, veniam,...|
[info] |null|Duis fugiat Lorem...|483 Hanson Place,...| 31| ASSURITY|kristiemckinney@a...|   green|       banana|female|  Kristie Mckinney|+1 (905) 511-3302|2014-06-07 05:23:...|List(culpa, fugia...|
[info] |null|Consequat fugiat ...|540 Woodpoint Roa...| 40|MICROLUXE|salazarburks@micr...|   brown|   strawberry|  male|     Salazar Burks|+1 (939) 455-3286|2014-10-02 22:33:...|List(ipsum, adipi...|
[info] |null|Lorem culpa Lorem...|442 Ainslie Stree...| 32| VIOCULAR|hopkinspatterson@...|   green|        apple|  male| Hopkins Patterson|+1 (998) 499-2682|2015-04-16 12:18:...|List(dolore, offi...|
[info] |null|Qui ad cillum mag...|444 Argyle Road, ...| 23|    IMKAN|maysrosario@imkan...|   green|        apple|  male|      Mays Rosario|+1 (869) 589-3296|2014-02-03 13:44:...|List(quis, nulla,...|
[info] |null|Duis ex velit dui...|571 Sunnyside Ave...| 38|   HELIXO|atkinshancock@hel...|    blue|   strawberry|  male|    Atkins Hancock|+1 (949) 582-3230|2014-01-13 09:04:...|List(dolore, nisi...|
[info] |null|Et magna laboris ...|385 Meeker Avenue...| 40|  SLOFAST|edwinarobertson@s...|    blue|   strawberry|female|  Edwina Robertson|+1 (830) 409-2817|2015-01-08 15:02:...|List(excepteur, c...|
[info] |null|Labore sit mollit...|936 Cheever Place...| 37| FLEETMIX|elsienoel@fleetmi...|    blue|        apple|female|        Elsie Noel|+1 (880) 439-2520|2015-04-14 15:29:...|List(voluptate, e...|
[info] |null|Consequat eiusmod...|406 Lake Place, M...| 36| EVENTAGE|mirandamarsh@even...|   green|        apple|female|     Miranda Marsh|+1 (836) 586-2989|2014-10-31 19:51:...|List(pariatur, du...|
[info] |null|Duis fugiat conse...|364 Metropolitan ...| 31|  BALOOBA|sharronmcconnell@...|   brown|        apple|female| Sharron Mcconnell|+1 (947) 432-2612|2014-05-30 08:51:...|List(enim, veniam...|
[info] |null|Consequat et magn...|113 Applegate Cou...| 29|    EURON|mcdowellwelch@eur...|    blue|   strawberry|  male|    Mcdowell Welch|+1 (866) 439-3371|2014-10-17 21:30:...|List(ipsum, do, c...|
[info] +----+--------------------+--------------------+---+---------+--------------------+--------+-------------+------+------------------+-----------------+--------------------+--------------------+
[info] 
[info] All records count (should be 200): 200
[info] Filtered based on timestamp count (should be 106): 106
[info] Retrieved JSON schema:
[info] {
[info]   "type" : "struct",
[info]   "fields" : [ {
[info]     "name" : "id",
[info]     "type" : "string",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "about",
[info]     "type" : "string",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "address",
[info]     "type" : "string",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "age",
[info]     "type" : "integer",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "company",
[info]     "type" : "string",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "email",
[info]     "type" : "string",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "eyeColor",
[info]     "type" : "string",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "favoriteFruit",
[info]     "type" : "string",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "gender",
[info]     "type" : "string",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "name",
[info]     "type" : "string",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "phone",
[info]     "type" : "string",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "registered",
[info]     "type" : "timestamp",
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   }, {
[info]     "name" : "tags",
[info]     "type" : {
[info]       "type" : "array",
[info]       "elementType" : "string",
[info]       "containsNull" : true
[info]     },
[info]     "nullable" : true,
[info]     "metadata" : { }
[info]   } ]
[info] }
[info] /////////// Third way: with Parquet Storage /////////////
[success] Total time: 8 s, completed Jan 10, 2016 11:56:40 PM
```
