package countercluster
import org.specs2.mutable._

class SMASpec extends SpecificationLike {
  "SMA" >> {
    "#serialize, #parse" >> {
      "it serializes and parses" in {
        val avg = SMA(1, 5)
        (1 to 300).foreach(_ => avg.increment)
        Thread.sleep(1)
        (1 to 200).foreach(_ => avg.increment)
        Thread.sleep(1)
        avg.refresh

        val serialized = avg.serialize
        val avg2 = SMA.parse(serialized)
        serialized must be_==(avg2.serialize)
      }
    }
  }
}
