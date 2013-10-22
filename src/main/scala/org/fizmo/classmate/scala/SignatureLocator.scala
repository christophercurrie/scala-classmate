package org.fizmo.classmate.scala

import scala.reflect.{ScalaLongSignature, ScalaSignature}
import Preconditions._
import scala.annotation.tailrec
import scala.io.Codec

class SignatureLocator {
  def locate(cls: Class[_]): (Array[Byte], Seq[Class[_]]) = {
    notNull(cls)

    @tailrec
    def _locate(xs: Seq[Class[_]]): (String, Seq[Class[_]]) = {
      assert(xs.head != null)
      val as = xs.head.getAnnotations
      val sig = ((None: Option[String]) /: as) {
        case (_, a: ScalaLongSignature) => Some(a.bytes().mkString)
        case (None, a: ScalaSignature) => Some(a.bytes())
        case (o, _) => o
      }

      if (sig.isDefined) return (sig.get, xs)
      _locate(xs.head.getEnclosingClass +: xs)
    }

    val location = _locate(Seq(cls))
    (ByteCodecs.decode(location._1), location._2)
  }
}
