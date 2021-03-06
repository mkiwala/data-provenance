package com.cibo.provenance

import java.io.Serializable

import io.circe.{Encoder, Json, ObjectEncoder}

/**
  * This uses the raw Java binary serialization internally, and puts the byte array into
  * simple JSON.  It handles the provenance monads that have arbitrary complex depth,
  * with types known at the base class but arbitrary types in subclasses.
  *
  * @tparam T: Some type (serializable)
  */
class BinaryEncoder[T <: Serializable] extends Encoder[T] with Serializable {
  // NOTE: This is its own class, wrapping a regular circe encoder, because the
  // real encoder isn't _itelf_ serializable, and some functions (like map), that
  // take functions inadvertently serialize the encoders themselves.  This makes
  // any case like that work seamlessly, and reconstruct with fidelity later without
  // creating a data payload.

  @transient
  private lazy val enc: ObjectEncoder[T] =
    Encoder.forProduct2("bytes", "length") {
      obj =>
        val bytes: Array[Byte] =
          try {
            Codec.getBytesAndDigestRaw(obj)._1
          } catch {
            case e: Exception =>
              val ee = e
              println(f"Error serializing: $ee")
              throw e
          }
        Tuple2(bytes, bytes.length)
    }

  def apply(a: T): Json = enc.apply(a)
}
