package org.fizmo.classmate.scala

import org.scalatest.fixture
import org.scalatest.matchers.ShouldMatchers
import org.fizmo.classmate.scala.pickle.UnknownEntry
import scala.None

object TestSignatureTable
{
  class Bean
}

class TestSignatureTable extends fixture.FlatSpec with ShouldMatchers {
  import TestSignatureTable._

  type FixtureParam = SignatureLocator

  override def withFixture(test: OneArgTest) {
    test(new SignatureLocator)
  }

  behavior of "SignatureTable"

  it should "correctly read a basic bean" in { locator =>
    val beanClass = classOf[Bean]
    val (sig, loc) = locator.locate(beanClass)
    loc should have length 2
    loc(0) should be === classOf[TestSignatureTable]
    loc(1) should be === beanClass

    val table = new SignatureTable(sig)
    table.majorVersion should be === 5
    table.minorVersion should be === 0

    val entries = table.entries
    entries should have length 83
    entries.collectFirst { case e@UnknownEntry(_,_,_) => e } should be === None
  }

}
