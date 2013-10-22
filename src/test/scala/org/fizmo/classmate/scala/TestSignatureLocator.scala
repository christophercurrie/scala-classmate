package org.fizmo.classmate.scala

import org.scalatest.{Inside, fixture, ShouldMatchers}

class PackageLevelSignature

class TestSignatureLocator extends fixture.FlatSpec with ShouldMatchers with Inside {

  type FixtureParam = SignatureLocator

  def withFixture(test: OneArgTest)
  {
    test(new SignatureLocator)
  }

  behavior of "SignatureLocator"

  it should "find a signature on a top-level class" in { f =>
    val result = f.locate(classOf[PackageLevelSignature])
    inside (result) { case (sig, xs) =>
      sig.length should be > 0
      xs should have length 1
      xs.head should be (classOf[PackageLevelSignature])
    }
  }

  it should "find a signature for a nested class" in { f =>
    class NestedSignature

    val result = f.locate(classOf[NestedSignature])
    inside (result) { case (sig, xs) =>
      sig.length should be > 0
      xs should have length 2
      xs.head should be (classOf[TestSignatureLocator])
      xs.last should be (classOf[NestedSignature])
    }
  }
}
