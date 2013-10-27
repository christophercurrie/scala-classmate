package org.fizmo.classmate.scala

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class PickleBuffer(buffer: ByteBuffer)
{
  def this(buffer: Seq[Byte]) = this(ByteBuffer.wrap(buffer.toArray).asReadOnlyBuffer())

  def position: Int = buffer.position()

  def position_=(pos: Int): Unit = buffer.position(pos)

  def peekByte(): Byte = buffer.get(buffer.position())

  def byteAt(index: Int) = buffer.get(index)

  def readByte(): Byte = buffer.get()

  def readBytes(b: Array[Byte], offset: Int, length: Int) = buffer.get(b, offset, length)

  def readBytes(b: Array[Byte]) = buffer.get(b)

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

  def readName(len: Int): String = {
    val bytes = new Array[Byte](len)
    readBytes(bytes)
    new String(bytes, StandardCharsets.UTF_8)
  }
}