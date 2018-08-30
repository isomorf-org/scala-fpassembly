package org.fpassembly.model.v2

import org.fpassembly.model.v2.Reference._
import org.fpassembly.model.v2.Type._

sealed trait Element

case class Literal(value: String) extends Element

sealed trait Type extends Element

sealed trait Reference extends Element { def handle: String }
object Reference {
  case class Identifier(namespace: List[String], handle: String) extends Reference
  case class Symbol(handle: String) extends Reference
}

object Type {
  case class SimpleDataType(identifier: Identifier) extends Type
  case class DataType(identifier: Identifier, contains: List[Type]) extends Type
  case class GenericType(name: String) extends Type
  case class FunctionType(args: List[Type], ret: Type) extends Type
  case object TodoType extends Type
}

sealed trait Expression extends Element
sealed trait ReferenceExpression extends Expression
sealed trait ApplicationExpression extends Expression { def args: List[Expression] }
object Expression {
  case class ConstantExpression(literal: Literal, `type`: Type) extends Expression
  case class LocalReferenceExpression(symbol: Symbol) extends ReferenceExpression
  case class FunctionReferenceExpression(identifier: Identifier) extends ReferenceExpression
  case class ConstructorReferenceExpression(identifier: Identifier, symbol: Symbol) extends ReferenceExpression
  case class LocalApplicationExpression(symbol: Symbol, args: List[Expression]) extends ApplicationExpression
  case class FunctionApplicationExpression(identifier: Identifier, args: List[Expression]) extends ApplicationExpression
  case class ConstructorApplicationExpression(identifier: Identifier, symbol: Symbol, args: List[Expression]) extends ApplicationExpression
  case class MatchExpression(expression: Expression, cases: List[Case], otherwise: Option[Expression]) extends Expression
  case class ComplexExpression(locals: List[LocalFunctionDefinition], expression: Expression) extends Expression
  case object TodoExpression extends Expression
}

case class Case(pattern: Pattern, expression: Expression) extends Element

sealed trait Pattern extends Element
object Pattern {
  case class AnyPattern(guide: Symbol) extends Pattern
  case class ConstantPattern(literal: Literal, `type`: Type) extends Pattern
  case class TypePattern(guide: Symbol, `type`: Type) extends Pattern
  case class ConstructorPattern(guide: Option[Symbol], identifier: Identifier, symbol: Symbol, patterns: List[Pattern]) extends Pattern
  case object TodoPattern extends Pattern
  case object IgnorePattern extends Pattern
}

sealed trait FirstClassEntityDefinition extends Element { def identifier: Identifier }

case class Arg(symbol: Symbol, `type`: Type) extends Element

sealed trait FirstClassFunctionDefinition extends FirstClassEntityDefinition { def args: List[Arg]; def ret: Type }
sealed trait FirstClassTypeDefinition extends FirstClassEntityDefinition { def dataType: DataType; def identifier = dataType.identifier }
sealed trait ImplementedFunctionDefinition { def args: List[Arg]; def ret: Type; def expression: Expression }
sealed trait NativeDefinition
sealed trait UserDefinedDefinition

object FirstClassEntityDefinition {
  case class FunctionDefinition(identifier: Identifier, args: List[Arg], ret: Type, expression: Expression) extends FirstClassFunctionDefinition with ImplementedFunctionDefinition with UserDefinedDefinition
  case class NativeFunctionDefinition(identifier: Identifier, args: List[Arg], ret: Type) extends FirstClassFunctionDefinition with NativeDefinition
  case class EffectFunctionDefinition(identifier: Identifier, args: List[Arg], ret: Type) extends FirstClassFunctionDefinition with NativeDefinition
  case class TypeDefinition(dataType: DataType, constructors: List[ConstructorDefinition]) extends FirstClassTypeDefinition with UserDefinedDefinition // this should have been (Identifier, List[GenericType], constructors)
  case class NativeTypeDefinition(dataType: DataType) extends FirstClassTypeDefinition with NativeDefinition
}

case class LocalFunctionDefinition(symbol: Symbol, args: List[Arg], ret: Type, expression: Expression) extends Element with ImplementedFunctionDefinition
case class ConstructorDefinition(symbol: Symbol, args: List[Arg]) extends Element

case class Signature(identifier: Identifier, args: List[Arg], ret: Type)

case class Source(export: List[FirstClassEntityDefinition])
