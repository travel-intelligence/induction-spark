import com.amadeus.ti.induction.Introduction
import org.specs2.mutable._

class IntroductionIntroSpec extends Specification {
  "The mean value of the 10,000-sample Normal-distributed vector" should {
    "be lower than or equal to 5.2" in {
      (Introduction.vectorStats (Introduction.generateVector (5.0, 10000)) <= 5.2) must beTrue
    }
  }

  "The mean value of the 10,000-sample Normal-distributed vector" should {
    "be greater than or equal to 4.8" in {
      (Introduction.vectorStats (Introduction.generateVector (5.0, 10000)) >= 4.8) must beTrue
    }
  }
  
  "The mean value of the 100x100-sample Normal-distributed matrix" should {
    "be lower than or equal to 5.2" in {
      (Introduction.matrixStats (Introduction.generateMatrix (5.0, 100)) <= 5.2) must beTrue
    }
  }

  "The mean value of the 100x100-sample Normal-distributed matrix" should {
    "be greater than or equal to 4.8" in {
      (Introduction.matrixStats (Introduction.generateMatrix (5.0, 100)) >= 4.8) must beTrue
    }
  }

  "The comparison of two generated 100x100-sample Normal-distributed matrices, one being written on and read from disk" should {
    "have a mean value between -0.2 and +0.2" in {
      (Introduction.fullCycleMatrix (5.0, 100, "tmp-normal-distributed-matrix.csv")) must beTrue
    }
  }

}

