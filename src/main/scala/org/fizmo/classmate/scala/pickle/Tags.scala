package org.fizmo.classmate.scala.pickle

object Tags {
  final val TERMname: Byte = 1
  final val TYPEname: Byte = 2
  final val NONEsym: Byte = 3
  final val TYPEsym: Byte = 4
  final val ALIASsym: Byte = 5
  final val CLASSsym: Byte = 6
  final val MODULEsym: Byte = 7
  final val VALsym: Byte = 8
  final val EXTref: Byte = 9
  final val EXTMODCLASSref: Byte = 10
  final val NOtpe: Byte = 11
  final val NOPREFIXtpe: Byte = 12
  final val THIStpe: Byte = 13
  final val SINGLEtpe: Byte = 14
  final val CONSTANTtpe: Byte = 15
  final val TYPEREFtpe: Byte = 16
  final val TYPEBOUNDStpe: Byte = 17
  final val REFINEDtpe: Byte = 18
  final val CLASSINFOtpe: Byte = 19
  final val METHODtpe: Byte = 20
  final val POLYtpe: Byte = 21
  final val IMPLICITMETHODtpe: Byte = 22    // no longer generated

  final val LITERAL: Byte = 23   // base line for literals
  final val LITERALunit: Byte = 24
  final val LITERALboolean: Byte = 25
  final val LITERALbyte: Byte = 26
  final val LITERALshort: Byte = 27
  final val LITERALchar: Byte = 28
  final val LITERALint: Byte = 29
  final val LITERALlong: Byte = 30
  final val LITERALfloat: Byte = 31
  final val LITERALdouble: Byte = 32
  final val LITERALstring: Byte = 33
  final val LITERALnull: Byte = 34
  final val LITERALclass: Byte = 35
  final val LITERALenum: Byte = 36
  final val SYMANNOT: Byte = 40
  final val CHILDREN: Byte = 41
  final val ANNOTATEDtpe: Byte = 42
  final val ANNOTINFO: Byte = 43
  final val ANNOTARGARRAY: Byte = 44

  final val SUPERtpe: Byte = 46
  final val DEBRUIJNINDEXtpe: Byte = 47
  final val EXISTENTIALtpe: Byte = 48

  final val TREE: Byte = 49      // prefix code that means a tree is coming
  final val MODIFIERS: Byte = 50

  final val firstSymTag: Byte = NONEsym
  final val lastSymTag: Byte = VALsym
  final val lastExtSymTag: Byte = EXTMODCLASSref

}
