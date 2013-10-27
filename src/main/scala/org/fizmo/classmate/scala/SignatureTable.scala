package org.fizmo.classmate.scala

import pickle._
import pickle.Tags._

import scala.collection.mutable

class SignatureTable(signature: Seq[Byte]) {
  private val buffer = new PickleBuffer(signature)

  val majorVersion = buffer.readNatural()
  val minorVersion = buffer.readNatural()

  private val index = {
    val tableSize = buffer.readNatural()
    val ind = new Array[Int](tableSize)
    for (i <- 0 until tableSize) {
      ind(i) = buffer.position
      buffer.readByte()
      val entrySize = buffer.readNatural()
      buffer.position += entrySize
    }
    ind
  }

  def tag(entry: Int) = buffer.byteAt(index(entry))

  private val entryCache = new mutable.HashMap[Int, Entry] with mutable.SynchronizedMap[Int, Entry]

  lazy val entries = for (i <- 0 until index.length) yield entry(i)
  def entry(i: Int) = entryCache.getOrElseUpdate(i, {
    buffer.position = index(i)
    val tag = buffer.readByte()
    val length = buffer.readNatural()
    val start = buffer.position
    val end = start + length
    def remaining  = start + length - buffer.position

    val result = tag match {
      case TERMname => TermName(buffer.readName(length))
      case TYPEname => TypeName(buffer.readName(length))
      case NONEsym => NoneSymbolEntry()
      case ALIASsym => AliasEntry(symbolInfo())
      case CLASSsym => {
        val si = symbolInfo()
        val thisRef = maybeRef(remaining)
        ClassEntry(si, thisRef)
      }
      case MODULEsym => ModuleEntry(symbolInfo())
      case VALsym => {
        val defaultGetterRef = if (isNameRef(buffer.peekByte())) None else Some(readRef())
        val si = symbolInfo()
        val aliasRef = maybeRef(remaining)
        ValEntry(defaultGetterRef, si, aliasRef)
      }
      case EXTref => {
        val nameRef = readRef()
        val ownerRef = maybeRef(remaining)
        ExtRefEntry(nameRef, ownerRef)
      }
      case EXTMODCLASSref => {
        val nameRef = readRef()
        val ownerRef = maybeRef(remaining)
        ExtModClassRefEntry(nameRef, ownerRef)
      }
      case THIStpe => ThisTypeEntry(readRef())
      case TYPEREFtpe => {
        val typeRef = readRef()
        val symRef = readRef()
        val targRefs = seqRef(end)
        TypeRefEntry(typeRef, symRef, targRefs)
      }
      case CLASSINFOtpe => {
        val classSymRef = readRef()
        val tpeRefs = seqRef(end)
        ClassInfoEntry(classSymRef, tpeRefs)
      }
      case METHODtpe => {
        val tpeRef = readRef()
        val symRefs = seqRef(end)
        MethodEntry(tpeRef, symRefs)
      }
      case _ => {
        val bytes = new Array[Byte](length)
        buffer.readBytes(bytes)
        UnknownEntry(tag, length, bytes)
      }
    }

    if (remaining != 0) {
      assert(remaining == 0)
    }
    result
  })

  private def readRef() = buffer.readNatural()

  private def maybeRef(remaining: Int): Option[Int] =
    if (remaining > 0) Some(buffer.readNatural()) else None

  private def seqRef(end: Int): Seq[Int] = {
    val builder = Seq.newBuilder[Int]
    while (buffer.position < end)
      builder += readRef()
    builder.result()
  }

  private def symbolInfo() = {
    val nameRef = readRef()
    val ownerRef = readRef()
    val flags = buffer.readLongNatural()
    val (privateWithinRef, infoRef) = {
      val tmp = readRef()
      if (isSymbolRef(tmp))
        (Some(tmp), readRef())
      else
        (None, tmp)
    }
    SymbolInfo(nameRef, ownerRef, flags, privateWithinRef, infoRef)
  }

  private def isSymbolRef(ref: Int) = {
    val tag = this.tag(ref)
    firstSymTag <= tag && tag <= lastExtSymTag
  }

  private def isNameRef(ref: Int) = tag(ref) match {
    case TERMname | TYPEname => true
    case _ => false
  }
}
