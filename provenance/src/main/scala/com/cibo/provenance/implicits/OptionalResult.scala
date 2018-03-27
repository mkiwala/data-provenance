package com.cibo.provenance.implicits

/**
  * Created by ssmith on 11/12/17.
  *
  * OptionResult[A] is extended by the implicit class OptionResultExt[A],
  * which extends FunctionCallWithProvenance[O] when O is an Option[_].
  *
  */

import com.cibo.provenance._
import io.circe.{Decoder, Encoder}

import scala.language.existentials
import scala.reflect.ClassTag

class OptionalResult[A](result: FunctionCallResultWithProvenance[Option[A]])
  (implicit
    ctsa: ClassTag[Option[A]],
    cta: ClassTag[A],
    csa: Codec[Option[A]],
    ca: Codec[A]
  ) {

  def get(implicit rt: ResultTracker) = result.call.get.resolve

  def isEmpty(implicit rt: ResultTracker) = result.call.isEmpty.resolve

  def nonEmpty(implicit rt: ResultTracker) = result.call.nonEmpty.resolve

  def map[B : ClassTag : Codec](
    f: ValueWithProvenance[Function1WithProvenance[A, B]]
  )(
    implicit rt: ResultTracker,
    ocb: Codec[Option[B]]
  ) =
    result.call.map(f).resolve
}
