package net.lasalette

import org.testng.annotations.Test
import kotlin.test.assertTrue

class TestClockTimes {
  @Test fun testClockTimesSetCorrectly() {
    val clock = Clock(minutes = 0, seconds = 130, milliseconds = 0)
    println(clock)
    assertTrue(clock.minutes == 2)
    assertTrue(clock.seconds == 10)
  }

  @Test fun testClockTimesSetCorrectlyWithMilli() {
    val clock = Clock(minutes = 0, seconds = 130, milliseconds = 2500)
    println(clock)
    assertTrue(clock.minutes == 2)
    assertTrue(clock.seconds == 12)
    assertTrue(clock.milli == 500)
  }

  @Test fun testTimer() {
    val foo = Clock(1, 10, 100)
    foo.start()
    foo.subscribe {minutes, seconds, milli -> println("$minutes:$seconds:$milli") }
    //assertTrue(foo.minutes == 1 && foo.seconds == 10 && foo.milli == 100)
  }
}