package countercluster
import com.google.common.collect.EvictingQueue

case class MovingAverage(intervalMs: Long, numOfIntervals: Int) {
  private var startedAt = System.currentTimeMillis()
  private var _count = 0
  private val queue = EvictingQueue.create[Int](numOfIntervals)

  def increment: Unit = { refresh; this._count += 1 }

  def refresh: Unit = {
    val now = System.currentTimeMillis()
    val expiredSlots = (now - startedAt) / intervalMs
    if (expiredSlots > 0) {
      queue.add(_count)
      this.startedAt = now
      this._count = 0
    }
    (2L to expiredSlots).foreach(_ => queue.add(0))
  }

  def value: Double = {
    var result = 0
    val it = queue.iterator
    while(it.hasNext) { result += it.next }
    result / queue.size
  }

  def values = queue.toArray
  def count = _count
}
