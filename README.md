### Induction for Spark

## Setup
# On Fedora
dnf -y install hadoop-common-native hadoop-client parquet-format libhdfs

## Examples

# Go Through a Basic Example
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
$ sbt run 2>&1 | grep -v "error"
 [...]
[info] Loading project definition from ~/dev/bi/tiinductionsparkgit/project
[info] Set current project to induction-scala-data-science (in build file:~/dev/bi/tiinductionsparkgit/)
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

