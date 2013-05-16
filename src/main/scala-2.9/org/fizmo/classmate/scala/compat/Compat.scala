package org.fizmo.classmate.scala.compat

object Compat {
  implicit def manifestTo[T](m: Manifest[T]) = new {
    def runtimeClass = m.erasure
  }
}
