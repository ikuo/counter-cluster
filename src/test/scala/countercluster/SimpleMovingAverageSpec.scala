package countercluster
import org.specs2.mutable._

class SimpleMovingAverageSpec extends SpecificationLike {
  "SimpleMovingAverage" >> {
    "#serialize, #parse" >> {
      "it serializes and parses" in {
        val avg = SimpleMovingAverage(1, 5)
        (1 to 300).foreach(_ => avg.increment)
        Thread.sleep(1)
        (1 to 200).foreach(_ => avg.increment)
        Thread.sleep(1)
        avg.refresh

        val serialized = avg.serialize
        val avg2 = SimpleMovingAverage.parse(serialized)
        serialized must be_==(avg2.serialize)
      }
    }
  }
}
