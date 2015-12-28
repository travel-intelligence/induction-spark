import TIInductionScala.Introduction
import org.specs2.mutable._

class IntroductionIntroSpec extends Specification {
  "The phrase 'Never odd or even'" should {
    "be a palindrome" in {
      Introduction.isPalindrome("Never odd or even") must beTrue
    }
  }

  "The phrase 'Mr. Owl ate my metal worm'" should {
    "be a palindrome" in {
      Introduction.isPalindrome("Mr. Owl ate my metal worm") must beTrue
    }
  }

  "The date '20:02 02/20 2002'" should {
    "be a palindrome" in {
      Introduction.isPalindrome("20:02 02/20 2002") must beTrue
    }
  }

  "The word 'Palindrome'" should {
    "not be a palindrome" in {
      Introduction.isPalindrome("Palindrome") must beFalse
    }
  }

}
