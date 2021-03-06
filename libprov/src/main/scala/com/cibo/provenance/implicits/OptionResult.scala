package com.cibo.provenance.implicits

/**
  * Created by ssmith on 11/12/17.
  *
  * OptionResult[A] is extended by the implicit class OptionResultExt[A],
  * which extends FunctionCallWithProvenance[O] when O is an Option[_].
  *
  */

import com.cibo.provenance._
import scala.language.existentials

class OptionResult[A](result: FunctionCallResultWithProvenance[Option[A]])
  (implicit cdsa: Codec[Option[A]], cda: Codec[A]) {

  def get(implicit rt: ResultTracker) = result.call.get.resolve

  def isEmpty(implicit rt: ResultTracker) = result.call.isEmpty.resolve

  def nonEmpty(implicit rt: ResultTracker) = result.call.nonEmpty.resolve

  def map[B : Codec, F <: Function1WithProvenance[A, B]](f: F)(
    implicit rt: ResultTracker,
    cdb: Codec[Option[B]],
    cdf: Codec[F]
  ) = {
    implicit val cdf2: Codec[Function1WithProvenance[A,B]] = cdf.asInstanceOf[Codec[Function1WithProvenance[A,B]]]
    result.call.map(f).resolve
  }
}
