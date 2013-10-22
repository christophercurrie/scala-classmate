package org.fizmo.classmate.scala

import java.nio.ByteBuffer

class PickleBuffer(buffer: ByteBuffer)
{
  def this(buffer: Seq[Byte]) = this(ByteBuffer.wrap(buffer.toArray).asReadOnlyBuffer())

  def position: Int = buffer.position()

  def position_=(pos: Int): Unit = buffer.position(pos)

  def peekByte(): Byte = buffer.get(buffer.position())

  def readByte(): Byte = buffer.get()

  def readNatural(): Int = {
    val l = readLongNatural()
    if (l < Int.MinValue || Int.MaxValue < l) {
      throw new IllegalStateException()
    }
    l.toInt
  }

  def readLongNatural(): Long = {
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