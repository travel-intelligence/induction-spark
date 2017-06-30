
# coding: utf-8

# # Resilient Distributed Datasets (RDDs)

# In this tutorial, we are going to introduce the *resilient distributed datasets* (RDDs) which is Spark's core abstraction for working with data. An RDD is a distributed collection of elements which can be operated on in parallel. Users can create RDD in two ways: *parallelizing* an existing collection in your driver program, or loading a dataset in an external storage system. RDDs support two types of operations: *transformations* and *actions*. *Transformations* construct a new RDD from a previous one and *actions* compute a result based on an RDD,. We will introduce the basic opeartions of RDDs by a simple word count example:

# ## Word Count

# In[1]:

text_file = sc.parallelize(["hello","hello world"])
counts = text_file.flatMap(lambda line: line.split(" "))              .map(lambda word: (word, 1))              .reduceByKey(lambda a, b: a + b)
for line in counts.collect():
    print line


# The first line defines a base RDD by *parallelizing* an existing Python list. The second line defines *counts* as the result of a few transformations. In the third line and fourth line, the program print all elements from *counts* by calling *collect()*. *Collect()* is used to retrieve the entire RDD if the data are expected to fit in memory. For more RDD APIs, you can refer to the website [RDD APIs](http://spark.apache.org/docs/latest/programming-guide.html#resilient-distributed-datasets-rdds)
