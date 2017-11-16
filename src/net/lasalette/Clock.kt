package net.lasalette
/*
import com.kizitonwose.time.milliseconds
import com.kizitonwose.time.minutes
import com.kizitonwose.time.seconds
import tornadofx.observable
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

typealias TimerListener = (minutes: Long, seconds: Long, milli: Long) -> Unit
typealias StatusListener = (from: Clock.Status, current: Clock) -> Unit

class Clock(minutes: Int = 0, seconds: Int = 3, milliseconds: Int = 0) {

  private val timeNotifier = { _: KProperty<*>, _: Long, _: Long ->
    listeners.forEach {
      it(this.minutes, this.seconds, this.milli)
    }
  }

  private var timeLeft: Long by Delegates.observable(
      (minutes.minutes.inMilliseconds.longValue
          + seconds.seconds.inMilliseconds.longValue
          + milliseconds.milliseconds.inMilliseconds.longValue), timeNotifier)

  private var timer = Timer()

  val minutes: Long get() = timeLeft.milliseconds.inMinutes.longValue
  val seconds: Long get() = (minutes - timeLeft.milliseconds.inSeconds.longValue).seconds.longValue
  val milli: Long get() = (minutes.minutes + seconds.seconds).longValue - timeLeft

  private val statusNotifier = { _: KProperty<*>, old: Status, _: Status ->
    statusListeners.forEach {
      it(old, this)
    }
  }

  var status: Status by Delegates.observable(Status.STOPPED, statusNotifier)

  private val listeners = mutableListOf<TimerListener>()

  private val statusListeners = mutableListOf<StatusListener>()

  private fun TimerTask.execute() {
    if (status == Status.STARTED && timeLeft > 0) {
      timeLeft -= 100
    } else if (timeLeft < 0) {
      timeLeft = 0
      status = Status.COMPLETE
    }
  }

  fun start() {
    if (status != Status.STARTED)
      this.status = Status.STARTED

    timer.cancel()
    timer = Timer()

    timer.scheduleAtFixedRate(object : TimerTask() {
      override fun run() = execute()
    }, 0L, 100.milliseconds.inMilliseconds.longValue)
  }

  fun pause() {
    status = Status.STOPPED
    timer.cancel()
  }

  fun restart(minutes: Int, seconds: Int, milli: Int) {
    status = Status.COMPLETE
    timer.cancel()
    start()
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

  override fun toString(): String {
    return "$minutes:$seconds:$milli ($timeLeft)"
  }


  enum class Status {
    STOPPED, RESET, STARTED, COMPLETE
  }
}

fun main(args: Array<String>) {
  val clock = Clock(minutes = 1, seconds = 5, milliseconds = 344)

  clock.subscribe { _, _, _ ->
    if (clock.status == Clock.Status.STARTED)
      println(clock)
  }

  clock.start()


  clock.listen { from, current ->
    println("From: $from To -> ${current.status}")
    if (clock.status == Clock.Status.COMPLETE) {
      println("Done. - $clock")
    }
  }
}*/

