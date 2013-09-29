package org.fizmo.classmate.scala

class PickleBuffer(bytes: Array[Byte]) {

  private var _index = 0

  def peekByte() = bytes(_index)

  def readByte(): Byte = {
    val b = bytes(_index); _index += 1; b
  }

  def readNatural(): Long = {
    var b = 0L
    var x = 0L
    do {
      b = readByte()
      x = (x << 7) + (b & 0x7f)
    } while ((b & 0x80) != 0L)
    x
  }

  def readLong(len: Int): Long = {
    var x = 0L
    var i = 0
    while (i < len) {
      x = (x << 8) + (readByte() & 0xff)
      i += 1
    }
    val leading = 64 - (len << 3)
    x << leading >> leading
  }
}
