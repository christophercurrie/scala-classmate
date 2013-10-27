package org.fizmo.classmate.scala

object ReplTest {
  def apply(cls: Class[_]) = {
    val locator = new SignatureLocator
    val sigLocation = locator.locate(cls)
    new SignatureTable(sigLocation._1)
  }
}
