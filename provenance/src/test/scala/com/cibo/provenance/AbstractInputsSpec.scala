package com.cibo.provenance

import java.io.File

import com.cibo.io.s3.SyncablePath
import org.apache.commons.io.FileUtils
import org.scalatest.{FunSpec, Matchers}


class AbstractInputsSpec extends FunSpec with Matchers {

  val testOutputBaseDir: String = TestUtils.testOutputBaseDir
  implicit val buildInfo: BuildInfo = DummyBuildInfo

  describe("functions with abstract input types") {

    // Auto-fabricate implicit circe JSON encoders/decoders.
    import io.circe.generic.auto._

    val ruffers = Doggie("Ruffers")
    val fluffy = Kitty("Fluffy")

    it("work with different subclasses of input") {
      val testSubdir = f"abstract-inputs"
      val testDataDir = f"$testOutputBaseDir/$testSubdir"
      FileUtils.deleteDirectory(new File(testDataDir))
      implicit val rt = ResultTrackerSimple(SyncablePath(testDataDir))

      sayYourName(ruffers).resolve.output shouldBe "Ruffers"

      sayYourName(fluffy).resolve.output shouldBe "Fluffy"

      TestUtils.diffOutputSubdir(testSubdir)
    }
  }
}

trait PetUnsealedTrait { def name: String }
case class Kitty(name: String) extends PetUnsealedTrait
case class Doggie(name: String) extends PetUnsealedTrait

object sayYourName extends Function1WithProvenance[PetUnsealedTrait, String] {
  val currentVersion = Version("0.1")
  def impl(animal: PetUnsealedTrait): String = animal.name
}
