package org.fizmo.classmate.scala.pickle

sealed trait AnnotInfoBody

case class ResolvedAnnotInfoBody(infoRef: Int, args: Seq[Int], assocs: Seq[(Int, Int)]) extends AnnotInfoBody

case class DeferredAnnotInfoBody(infoRef: Int, bytes: Seq[Byte]) extends AnnotInfoBody
