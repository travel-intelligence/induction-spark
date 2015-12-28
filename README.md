# Simple (Yet Complete) Example for Scala and SBT 

# Setup SBT and Scala
See http://www.scala-sbt.org/0.13/tutorial/index.html. SBT will then pull all the dependencies, including Scala.

# Go Through the Example
```bash
$ mkdir -p ~/dev/bi
$ cd ~/dev/bi
$ git clone git@github.com:travel-intelligence/induction-scala.git tiinductionscalagit
$ cd ~/dev/bi/tiinductionscalagit
$ sbt compile
[info] Loading global plugins from ~/.sbt/0.13/plugins
[info] Loading project definition from ~/dev/bi/tiinductionscalagit/project
[info] Set current project to ti-induction-scala (in build file:~/dev/bi/tiinductionscalagit/)
[success] Total time: 0 s, completed Dec 28, 2015 8:54:18 PM
$ sbt run
 [...]
[info] Running TIInductionScala.Introduction
Is 'Herculaneum' a palindrome? false
[success] Total time: 0 s, completed Dec 28, 2015 8:54:30 PM
$ sbt test
 [...]
[info] IntroductionIntroSpec
[info]
[info] The phrase 'Never odd or even' should
[info] + be a palindrome
 [...]
[info] The word 'Palindrome' should
[info] + not be a palindrome
[info]
[info] Total for specification IntroductionIntroSpec
[info] Finished in 18 ms
[info] 4 examples, 0 failure, 0 error
[info] Passed: Total 4, Failed 0, Errors 0, Passed 4
[success] Total time: 1 s, completed Dec 28, 2015 8:58:38 PM
```
