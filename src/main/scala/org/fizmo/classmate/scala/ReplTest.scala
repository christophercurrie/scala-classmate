package org.fizmo.classmate.scala

import scala.io.Codec

object ReplTest {
  def apply(cls: Class[_]) = {
    val locator = new SignatureLocator
    val sigLocation = locator.locate(cls)
    val encoded = Codec.toUTF8(sigLocation._1)
    val len = ByteCodecs.decode(encoded)
    val decoded = encoded take len
    new ParsedSignature(decoded.iterator)
  }
}
