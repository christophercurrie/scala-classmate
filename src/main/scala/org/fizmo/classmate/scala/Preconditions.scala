package org.fizmo.classmate.scala

object Preconditions {
  def notNull(o: AnyRef) {
    if (o == null) throw new NullPointerException()
  }

  def notNull(o: AnyRef, message: => String) {
    if (o == null) throw new NullPointerException(message)
  }
}
