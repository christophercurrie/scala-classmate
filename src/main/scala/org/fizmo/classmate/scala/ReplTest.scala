package org.fizmo.classmate.scala

import scala.io.Codec

object ReplTest {
  def apply(cls: Class[_]) = {
    val locator = new SignatureLocator
    val sigLocation = locator.locate(cls)
//    val encoded = Codec.toUTF8(sigLocation._1) map { b => ((b.toInt-1)&0x7f).toByte }
//    //val encoded = sigLocation._1.getBytes.map { b=> ((b.toInt-1)&0x7f).toByte }
//    val len = ByteCodecs.decode7to8(encoded,encoded.length)
//    val decoded = encoded take len
    new ParsedSignature(new PickleBuffer(sigLocation._1))
  }
}
