package net.lasalette

import com.kizitonwose.time.milliseconds
import com.kizitonwose.time.minutes
import com.kizitonwose.time.seconds
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

typealias TimerListener = (minutes: Int, seconds: Int, milli: Int) -> Unit
typealias StatusListener = (from: Clock.Status, current: Clock) -> Unit

class Clock(minutes: Int = 0, seconds: Int = 3, milliseconds: Int = 0) {

  private val timeNotifier = { _: KProperty<*>, _: Long, _: Long ->
    listeners.forEach {
      it(this.minutes, this.seconds, this.milli)
    }
  }

  private var timeLeft: Long by Delegates.observable(getTimeInMilliseconds(
      minutes,
      seconds,
      milliseconds
  ), timeNotifier)

  private val timer = Timer()

  val minutes: Int get() = timeLeft.div(60000).toInt()
  val seconds: Int get() = ((timeLeft - (minutes * 60_000)) / 1000).toInt()
  val milli: Int get() = (timeLeft % 1000).toInt()

  private val statusNotifier = { _: KProperty<*>, old: Status, _: Status ->
    statusListeners.forEach {
      it(old, this)
    }
  }

  var status: Status by Delegates.observable(Status.STOPPED, statusNotifier)

  private val listeners = mutableListOf<TimerListener>()

  private val statusListeners = mutableListOf<StatusListener>()

  private fun TimerTask.execute() {
    if (status == Status.STARTED) {
      if (timeLeft > 0) {
        timeLeft -= 100
      } else {
        if (timeLeft < 0)
          timeLeft = 0
        pause()
      }
    }
  }

  fun start() {
    if (status != Status.STARTED)
      this.status = Status.STARTED
  }

  fun pause() {
    if (timeLeft == 0L && status == Status.STARTED) {
      status = Status.COMPLETE
    }
    status = Status.STOPPED
  }

  fun kill() {
    timer.cancel()
  }

  fun restart(minutes: Int, seconds: Int, milli: Int = 0) {
    status = Status.COMPLETE
    timeLeft = getTimeInMilliseconds(minutes, seconds, milli)
    start()
  }

  private fun getTimeInMilliseconds(minutes: Int, seconds: Int, milli: Int = 0): Long {
    val sMin = if (minutes > 0) minutes else 0
    val sSec = if (seconds > 0) seconds else 0
    val sMilli = if (milli > 0) milli else 0
    return sMin.minutes.inMilliseconds.longValue +
        sSec.seconds.inMilliseconds.longValue +
        sMilli.milliseconds.longValue
  }

  fun setTime(minutes: Int, seconds: Int, milli: Int = 0) {
    timeLeft = getTimeInMilliseconds(minutes, seconds, milli)
  }

  fun subscribe(listener: TimerListener) {
    synchronized(listeners) {
      if (!listeners.contains(listener))
        listeners.add(listener)
    }
  }

  fun listen(listener: StatusListener) {
    synchronized(statusListeners) {
      if (!statusListeners.contains(listener))
        statusListeners.add(listener)
    }
  }

  override fun toString(): String = "${
  if (minutes < 10) "0$minutes" else "$minutes"
  }:${
  if (seconds < 10) "0$seconds" else "$seconds"
  }${if (minutes == 0) ".${milli / 100}" else ""}"

  enum class Status {
    STOPPED, RESET, STARTED, COMPLETE
  }

  init {
    timer.scheduleAtFixedRate(object : TimerTask() {
      override fun run() = execute()
    }, 0L, 100L)
  }

}


