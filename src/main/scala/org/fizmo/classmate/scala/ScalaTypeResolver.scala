package org.fizmo.classmate.scala

import com.fasterxml.classmate.{ResolvedType, TypeResolver, GenericType}
import org.fizmo.classmate.scala.compat.Compat._

object ScalaTypeResolver {
  private [this] val _javaResolver = new TypeResolver

  def resolve(cls: Class[_]) = _javaResolver.resolve(cls)
  def resolve(tp: GenericType[_]) = _javaResolver.resolve(tp)
  def resolve(base: Class[_], tp1: Class[_], tpr: Class[_]*) = _javaResolver.resolve(base, (tp1 +: tpr): _*)
  def resolve[T](implicit m: Manifest[T]): ResolvedType = resolve(m.runtimeClass)
}
