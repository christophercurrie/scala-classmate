package org.fizmo.classmate.scala

import pickle._
import Tags._

import java.nio.charset.StandardCharsets
import scala.annotation.tailrec

class ParsedSignature(bytes: Iterator[Byte]) {
  private val zippedBytes = bytes.zipWithIndex
  private def index() = zippedBytes.idx

  case class Version(major: Int, minor: Int)

  val version = Version(readNatural(), readNatural())
  val entries = {
    val length = readNatural()
    val entryBuffer = new Array[Entry](length)
    for (i <- 0 until length) {
      entryBuffer(i) = readEntry()
    }
    entryBuffer
  }

  private def readByte(): Byte = zippedBytes.next()._1

  private def readNatural(): Int = readLongNatural().toInt

  private def readLongNatural(): Long = {
    var b = 0L
    var n = 0L
    do {
      b = readByte()
      n = (n << 7) + (b & 0x7f)
    } while ((b & 0x80) != 0L)
    n
  }

  private def readEntry(): Entry = {
    val tag = readByte()
    val length = readNatural()
    val start = index()

    def current() = index() - start
    def done() = !(current() < length)
    def remaining() = length - current()
    def reflist = {
      def gen: Stream[Int] = if (done()) Stream.empty else readNatural() #:: gen
      gen.force
    }

    val entry: Entry = tag match {
      case TERMname => TermName(readName(length))
      case TYPEname => TypeName(readName(length))
      case NONEsym => NoneSymbolEntry()
      //case TYPEsym => TypeEntry(readSymbolInfo(length))
      case MODULEsym => ModuleEntry(readSymbolInfo(length))
      case VALsym => AmbiguousValEntry(for (i <- 0 until length) yield readByte())
      case EXTref => ExtRefEntry(readNatural(), if (done()) None else Some(readNatural()))
      case CLASSsym => {
        // CLASSsym entries are ambiguous, as there are two optional fields
        val symbolInfo = readSymbolInfo(length)
        if (!done()) {
          ClassEntry(symbolInfo, Some(readNatural()))
        }
        else if (symbolInfo.privateWithinRef.isEmpty) {
          ClassEntry(symbolInfo, None)
        }
        else {
          AmbiguousClassEntry(symbolInfo)
        }
      }
      case EXTMODCLASSref => ExtModClassRefEntry(readNatural(), if (done()) None else Some(readNatural()))
      case NOPREFIXtpe => NoPrefixEntry()
      case THIStpe => ThisTypeEntry(readNatural())
      case TYPEREFtpe => TypeRefEntry(readNatural(), readNatural(), reflist)
      case TYPEBOUNDStpe => TypeBoundsEntry(readNatural(), readNatural())
      case CLASSINFOtpe => ClassInfoEntry(readNatural(), reflist)
      case METHODtpe => MethodEntry(readNatural(), reflist)
      case POLYtpe => PolyEntry(readNatural(), reflist)

      case _ => UnknownEntry(tag, length, for (i <- 0 until length) yield readByte())
    }
    assert(current() == length, "tag " + tag + " current " + current() + " != length " + length)
    assert(remaining() == 0)
    entry
  }

  private def readName(len: Int): String = {
    val bytes = (for (i <- 0 until len) yield readByte()).toArray
    new String(bytes, StandardCharsets.UTF_8)
  }

  private def readSymbolInfo(len: Int): SymbolInfo = {
    val start = index()
    val nameRef = readNatural()
    val ownerRef = readNatural()
    val flags = readLongNatural()
    val (privateWithinRef, infoRef) = {
      val temp = readNatural()
      if (index() - start < len)
        (Some(temp), readNatural())
      else
        (None, temp)
    }
    SymbolInfo(nameRef, ownerRef, flags, privateWithinRef, infoRef)
  }
}
