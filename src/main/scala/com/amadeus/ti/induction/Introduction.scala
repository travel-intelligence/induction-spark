package com.amadeus.ti.induction

object Introduction extends App { 
  def isPalindrome(inputString: String) = {
    val strippedInputString =
      inputString.toLowerCase.replaceAll("[^a-z0-9]", "")
    val reversedInputString = strippedInputString.reverse
    strippedInputString == reversedInputString
  }

  println("Is 'Herculaneum' a palindrome? " + isPalindrome("Herculaneum"))
}

