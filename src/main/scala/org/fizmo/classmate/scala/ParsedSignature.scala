package org.fizmo.classmate.scala

import pickle._
import Tags._

import java.nio.charset.StandardCharsets
import scala.annotation.tailrec

class ParsedSignature(buffer: PickleBuffer) {

  case class Version(major: Int, minor: Int)

  val version = Version(buffer.readNatural(), buffer.readNatural())
  val entries = {
    val length = buffer.readNatural()
    val entryBuffer = new Array[Entry](length)
    for (i <- 0 until length) {
      entryBuffer(i) = readEntry()
    }
    entryBuffer
  }

  private def readEntry(): Entry = {
    val tag = buffer.readByte()
    val length = buffer.readNatural()
    val start = buffer.position

    def current() = buffer.position - start
    def done() = !(current() < length)
    def remaining() = length - current()
    def reflist = {
      def gen: Stream[Int] = if (done()) Stream.empty else buffer.readNatural #:: gen
      gen.force
    }

    val entry: Entry = tag match {
      case TERMname => TermName(readName(length))
      case TYPEname => TypeName(readName(length))
      case NONEsym => NoneSymbolEntry()
      case TYPEsym => TypeEntry(readSymbolInfo(length))
      case ALIASsym => AliasEntry(readSymbolInfo(length))
      case MODULEsym => ModuleEntry(readSymbolInfo(length))
      //case VALsym => AmbiguousValEntry(for (i <- 0 until length) yield readByte())
      case VALsym => ValEntry(readSymbolInfo(length), if (done()) None else Some(buffer.readNatural()))
      case EXTref => ExtRefEntry(buffer.readNatural(), if (done()) None else Some(buffer.readNatural()))
      case CLASSsym => {
        // CLASSsym entries are ambiguous, as there are two optional fields
        val symbolInfo = readSymbolInfo(length)
        if (!done()) {
          ClassEntry(symbolInfo, Some(buffer.readNatural()))
        }
        else if (symbolInfo.privateWithinRef.isEmpty) {
          ClassEntry(symbolInfo, None)
        }
        else {
          AmbiguousClassEntry(symbolInfo)
        }
      }
      case EXTMODCLASSref => ExtModClassRefEntry(buffer.readNatural(), if (done()) None else Some(buffer.readNatural()))
      case NOPREFIXtpe => NoPrefixEntry()
      case THIStpe => ThisTypeEntry(buffer.readNatural())
      case SINGLEtpe => SingleEntry(buffer.readNatural(), buffer.readNatural())
      case TYPEREFtpe => TypeRefEntry(buffer.readNatural(), buffer.readNatural(), reflist)
      case TYPEBOUNDStpe => TypeBoundsEntry(buffer.readNatural(), buffer.readNatural())
      case CLASSINFOtpe => ClassInfoEntry(buffer.readNatural(), reflist)
      case METHODtpe => MethodEntry(buffer.readNatural(), reflist)
      case POLYtpe => PolyEntry(buffer.readNatural(), reflist)
      case LITERALstring => LiteralStringEntry(buffer.readNatural())
      case SYMANNOT => SymAnnotEntry(buffer.readNatural(), DeferredAnnotInfoBody(buffer.readNatural(), for (i <- 0 until remaining()) yield buffer.readByte()))
      case CHILDREN => ChildrenEntry(reflist)

      case _ => UnknownEntry(tag, length, for (i <- 0 until length) yield buffer.readByte())
    }
    assert(current() == length, "tag " + tag + " current " + current() + " != length " + length)
    assert(remaining() == 0)
    entry
  }

  private def readName(len: Int): String = {
    val bytes = (for (i <- 0 until len) yield buffer.readByte()).toArray
    new String(bytes, StandardCharsets.UTF_8)
  }

  private def readSymbolInfo(len: Int): SymbolInfo = {
    val start = buffer.position
    val nameRef = buffer.readNatural()
    val ownerRef = buffer.readNatural()
    val flags = buffer.readLongNatural()
    val (privateWithinRef, infoRef) = {
      val temp = buffer.readNatural()
      if (buffer.position - start < len)
        (Some(temp), buffer.readNatural())
      else
        (None, temp)
    }
    SymbolInfo(nameRef, ownerRef, flags, privateWithinRef, infoRef)
  }
}
