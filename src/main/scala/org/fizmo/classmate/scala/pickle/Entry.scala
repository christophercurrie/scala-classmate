package org.fizmo.classmate.scala
package pickle

import Tags._

/***************************************************
  * Symbol table attribute format:
  *   Symtab         = nentries_Nat {Entry}
  *   Entry          = 1 TERMNAME len_Nat NameInfo
  *                  | 2 TYPENAME len_Nat NameInfo
  *                  | 3 NONEsym len_Nat
  *                  | 4 TYPEsym len_Nat SymbolInfo
  *                  | 5 ALIASsym len_Nat SymbolInfo
  *                  | 6 CLASSsym len_Nat SymbolInfo [thistype_Ref]
  *                  | 7 MODULEsym len_Nat SymbolInfo
  *                  | 8 VALsym len_Nat [defaultGetter_Ref /* no longer needed*/] SymbolInfo [alias_Ref]
  *                  | 9 EXTref len_Nat name_Ref [owner_Ref]
  *                  | 10 EXTMODCLASSref len_Nat name_Ref [owner_Ref]
  *                  | 11 NOtpe len_Nat
  *                  | 12 NOPREFIXtpe len_Nat
  *                  | 13 THIStpe len_Nat sym_Ref
  *                  | 14 SINGLEtpe len_Nat type_Ref sym_Ref
  *                  | 15 CONSTANTtpe len_Nat constant_Ref
  *                  | 16 TYPEREFtpe len_Nat type_Ref sym_Ref {targ_Ref}
  *                  | 17 TYPEBOUNDStpe len_Nat tpe_Ref tpe_Ref
  *                  | 18 REFINEDtpe len_Nat classsym_Ref {tpe_Ref}
  *                  | 19 CLASSINFOtpe len_Nat classsym_Ref {tpe_Ref}
  *                  | 20 METHODtpe len_Nat tpe_Ref {sym_Ref}
  *                  | 21 POLYTtpe len_Nat tpe_Ref {sym_Ref}
  *                  | 22 IMPLICITMETHODtpe len_Nat tpe_Ref {sym_Ref} /* no longer needed */
  *                  | 52 SUPERtpe len_Nat tpe_Ref tpe_Ref
  *                  | 24 LITERALunit len_Nat
  *                  | 25 LITERALboolean len_Nat value_Long
  *                  | 26 LITERALbyte len_Nat value_Long
  *                  | 27 LITERALshort len_Nat value_Long
  *                  | 28 LITERALchar len_Nat value_Long
  *                  | 29 LITERALint len_Nat value_Long
  *                  | 30 LITERALlong len_Nat value_Long
  *                  | 31 LITERALfloat len_Nat value_Long
  *                  | 32 LITERALdouble len_Nat value_Long
  *                  | 33 LITERALstring len_Nat name_Ref
  *                  | 34 LITERALnull len_Nat
  *                  | 35 LITERALclass len_Nat tpe_Ref
  *                  | 36 LITERALenum len_Nat sym_Ref
  *                  | 40 SYMANNOT len_Nat sym_Ref AnnotInfoBody
  *                  | 41 CHILDREN len_Nat sym_Ref {sym_Ref}
  *                  | 42 ANNOTATEDtpe len_Nat [sym_Ref /* no longer needed */] tpe_Ref {annotinfo_Ref}
  *                  | 43 ANNOTINFO len_Nat AnnotInfoBody
  *                  | 44 ANNOTARGARRAY len_Nat {constAnnotArg_Ref}
  *                  | 47 DEBRUIJNINDEXtpe len_Nat level_Nat index_Nat
  *                  | 48 EXISTENTIALtpe len_Nat type_Ref {symbol_Ref}
  *                  | 49 TREE len_Nat 1 EMPTYtree
  *                  | 49 TREE len_Nat 2 PACKAGEtree type_Ref sym_Ref mods_Ref name_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 3 CLASStree type_Ref sym_Ref mods_Ref name_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 4 MODULEtree type_Ref sym_Ref mods_Ref name_Ref tree_Ref
  *                  | 49 TREE len_Nat 5 VALDEFtree type_Ref sym_Ref mods_Ref name_Ref tree_Ref tree_Ref
  *                  | 49 TREE len_Nat 6 DEFDEFtree type_Ref sym_Ref mods_Ref name_Ref numtparams_Nat {tree_Ref} numparamss_Nat {numparams_Nat {tree_Ref}} tree_Ref tree_Ref
  *                  | 49 TREE len_Nat 7 TYPEDEFtree type_Ref sym_Ref mods_Ref name_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 8 LABELtree type_Ref sym_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 9 IMPORTtree type_Ref sym_Ref tree_Ref {name_Ref name_Ref}
  *                  | 49 TREE len_Nat 11 DOCDEFtree type_Ref sym_Ref string_Ref tree_Ref
  *                  | 49 TREE len_Nat 12 TEMPLATEtree type_Ref sym_Ref numparents_Nat {tree_Ref} tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 13 BLOCKtree type_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 14 CASEtree type_Ref tree_Ref tree_Ref tree_Ref
  *                  | 49 TREE len_Nat 15 SEQUENCEtree type_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 16 ALTERNATIVEtree type_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 17 STARtree type_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 18 BINDtree type_Ref sym_Ref name_Ref tree_Ref
  *                  | 49 TREE len_Nat 19 UNAPPLYtree type_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 20 ARRAYVALUEtree type_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 21 FUNCTIONtree type_Ref sym_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 22 ASSIGNtree type_Ref tree_Ref tree_Ref
  *                  | 49 TREE len_Nat 23 IFtree type_Ref tree_Ref tree_Ref tree_Ref
  *                  | 49 TREE len_Nat 24 MATCHtree type_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 25 RETURNtree type_Ref sym_Ref tree_Ref
  *                  | 49 TREE len_Nat 26 TREtree type_Ref tree_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 27 THROWtree type_Ref tree_Ref
  *                  | 49 TREE len_Nat 28 NEWtree type_Ref tree_Ref
  *                  | 49 TREE len_Nat 29 TYPEDtree type_Ref tree_Ref tree_Ref
  *                  | 49 TREE len_Nat 30 TYPEAPPLYtree type_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 31 APPLYtree type_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 32 APPLYDYNAMICtree type_Ref sym_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 33 SUPERtree type_Ref sym_Ref tree_Ref name_Ref
  *                  | 49 TREE len_Nat 34 THIStree type_Ref sym_Ref  name_Ref
  *                  | 49 TREE len_Nat 35 SELECTtree type_Ref sym_Ref tree_Ref name_Ref
  *                  | 49 TREE len_Nat 36 IDENTtree type_Ref sym_Ref name_Ref
  *                  | 49 TREE len_Nat 37 LITERALtree type_Ref constant_Ref
  *                  | 49 TREE len_Nat 38 TYPEtree type_Ref
  *                  | 49 TREE len_Nat 39 ANNOTATEDtree type_Ref tree_Ref tree_Ref
  *                  | 49 TREE len_Nat 40 SINGLETONTYPEtree type_Ref tree_Ref
  *                  | 49 TREE len_Nat 41 SELECTFROMTYPEtree type_Ref tree_Ref name_Ref
  *                  | 49 TREE len_Nat 42 COMPOUNDTYPEtree type_Ref tree_Ref
  *                  | 49 TREE len_Nat 43 APPLIEDTYPEtree type_Ref tree_Ref {tree_Ref}
  *                  | 49 TREE len_Nat 44 TYPEBOUNDStree type_Ref tree_Ref tree_Ref
  *                  | 49 TREE len_Nat 45 EXISTENTIALTYPEtree type_Ref tree_Ref {tree_Ref}
  *                  | 50 MODIFIERS len_Nat flags_Long privateWithin_Ref
  *   SymbolInfo     = name_Ref owner_Ref flags_LongNat [privateWithin_Ref] info_Ref
  *   NameInfo       = <character sequence of length len_Nat in Utf8 format>
  *   NumInfo        = <len_Nat-byte signed number in big endian format>
  *   Ref            = Nat
  *   AnnotInfoBody  = info_Ref {annotArg_Ref} {name_Ref constAnnotArg_Ref}
  *   AnnotArg       = Tree | Constant
  *   ConstAnnotArg  = Constant | AnnotInfo | AnnotArgArray
  *
  *   len is remaining length after `len'.
  */


trait Entry {
  def tag: Byte
  def isSymbol: Boolean = false
}

case class UnknownEntry(tag: Byte, length: Int, data: Seq[Byte]) extends Entry

// *   Entry          = 1 TERMNAME len_Nat NameInfo
case class TermName(name: String) extends Entry
{
  val tag = TERMname
}

// *                  | 2 TYPENAME len_Nat NameInfo
case class TypeName(name: String) extends Entry
{
  val tag = TYPEname
}

// *                  | 3 NONEsym len_Nat
case class NoneSymbolEntry() extends Entry
{
  val tag = NONEsym
  override val isSymbol = true
}

//*                  | 4 TYPEsym len_Nat SymbolInfo
case class TypeEntry(symbolInfo: SymbolInfo) extends Entry {
  val tag = TYPEsym
  override val isSymbol = true
}

//*                  | 5 ALIASsym len_Nat SymbolInfo
case class AliasEntry(symbolInfo: SymbolInfo) extends Entry {
  val tag = ALIASsym
  override val isSymbol = true
}

//*                  | 6 CLASSsym len_Nat SymbolInfo [thistype_Ref]
case class ClassEntry(symbolInfo: SymbolInfo, thisRef: Option[Int]) extends Entry
{
  val tag = CLASSsym
  override val isSymbol = true
}

case class AmbiguousClassEntry(symbolInfo: SymbolInfo) extends Entry
{
  val tag = CLASSsym
  override val isSymbol = true
}

//*                  | 7 MODULEsym len_Nat SymbolInfo
case class ModuleEntry(symbolInfo: SymbolInfo) extends Entry
{
  val tag = MODULEsym
  override val isSymbol = true
}

//*                  | 8 VALsym len_Nat [defaultGetter_Ref /* no longer needed*/] SymbolInfo [alias_Ref]
case class ValEntry(defaultGetterRef: Option[Int], symbolInfo: SymbolInfo, aliasRef: Option[Int]) extends Entry
{
  val tag = VALsym
  override val isSymbol = true
}

case class AmbiguousValEntry(bytes: Seq[Byte]) extends Entry {
  val tag = VALsym
  override val isSymbol = true
}

//*                  | 9 EXTref len_Nat name_Ref [owner_Ref]
case class ExtRefEntry(nameRef: Int, ownerRef: Option[Int]) extends Entry
{
  val tag = EXTref
}

//*                  | 10 EXTMODCLASSref len_Nat name_Ref [owner_Ref]
case class ExtModClassRefEntry(nameRef: Int, ownerRef: Option[Int]) extends Entry
{
  val tag = EXTMODCLASSref
}

//*                  | 11 NOtpe len_Nat
//*                  | 12 NOPREFIXtpe len_Nat
case class NoPrefixEntry() extends Entry
{
  val tag = NOtpe
}


//*                  | 13 THIStpe len_Nat sym_Ref
case class ThisTypeEntry(symRef: Int) extends Entry
{
  val tag = THIStpe
}

//*                  | 14 SINGLEtpe len_Nat type_Ref sym_Ref
case class SingleEntry(typeRef: Int, symRef: Int) extends Entry
{
  val tag = SINGLEtpe
}

//*                  | 15 CONSTANTtpe len_Nat constant_Ref

//*                  | 16 TYPEREFtpe len_Nat type_Ref sym_Ref {targ_Ref}
case class TypeRefEntry(typeRef: Int, symRef: Int, targRefs: Seq[Int]) extends Entry
{
  val tag = TYPEREFtpe
}

//*                  | 17 TYPEBOUNDStpe len_Nat tpe_Ref tpe_Ref
case class TypeBoundsEntry(tpeRefA: Int, tpeRefB: Int) extends Entry
{
  val tag = TYPEBOUNDStpe
}

//*                  | 18 REFINEDtpe len_Nat classsym_Ref {tpe_Ref}
//*                  | 19 CLASSINFOtpe len_Nat classsym_Ref {tpe_Ref}
case class ClassInfoEntry(classSymRef: Int, tpeRefs: Seq[Int]) extends Entry
{
  val tag = CLASSINFOtpe
}

//*                  | 20 METHODtpe len_Nat tpe_Ref {sym_Ref}
case class MethodEntry(tpeRef: Int, symRefs: Seq[Int]) extends Entry
{
  val tag = METHODtpe
}

//*                  | 21 POLYTtpe len_Nat tpe_Ref {sym_Ref}
case class PolyEntry(tpeRef: Int, symRefs: Seq[Int]) extends Entry
{
  val tag = POLYtpe
}

//*                  | 22 IMPLICITMETHODtpe len_Nat tpe_Ref {sym_Ref} /* no longer needed */
//*                  | 52 SUPERtpe len_Nat tpe_Ref tpe_Ref
//*                  | 24 LITERALunit len_Nat
//*                  | 25 LITERALboolean len_Nat value_Long
//*                  | 26 LITERALbyte len_Nat value_Long
//*                  | 27 LITERALshort len_Nat value_Long
//*                  | 28 LITERALchar len_Nat value_Long
//*                  | 29 LITERALint len_Nat value_Long
//*                  | 30 LITERALlong len_Nat value_Long
//*                  | 31 LITERALfloat len_Nat value_Long
//*                  | 32 LITERALdouble len_Nat value_Long

//*                  | 33 LITERALstring len_Nat name_Ref
case class LiteralStringEntry(nameRef: Int) extends Entry
{
  val tag = LITERALstring
}

//*                  | 34 LITERALnull len_Nat
//*                  | 35 LITERALclass len_Nat tpe_Ref
//*                  | 36 LITERALenum len_Nat sym_Ref

//*                  | 40 SYMANNOT len_Nat sym_Ref AnnotInfoBody
case class SymAnnotEntry(symRef: Int, annotInfo: AnnotInfoBody) extends Entry
{
  val tag = SYMANNOT
}

//*                  | 41 CHILDREN len_Nat sym_Ref {sym_Ref}
case class ChildrenEntry(syms: Seq[Int]) extends Entry
{
  val tag = CHILDREN
}

//*                  | 42 ANNOTATEDtpe len_Nat [sym_Ref /* no longer needed */] tpe_Ref {annotinfo_Ref}
//*                  | 43 ANNOTINFO len_Nat AnnotInfoBody
//*                  | 44 ANNOTARGARRAY len_Nat {constAnnotArg_Ref}
//*                  | 47 DEBRUIJNINDEXtpe len_Nat level_Nat index_Nat
//*                  | 48 EXISTENTIALtpe len_Nat type_Ref {symbol_Ref}
//*                  | 49 TREE len_Nat 1 EMPTYtree
