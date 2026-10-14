package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SecurityApp Medellín", appName)
  }

  @Test
  fun `verify leaflet assets exist and are non-empty`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val css = context.assets.open("leaflet.css").bufferedReader().use { it.readText() }
    val js = context.assets.open("leaflet.js").bufferedReader().use { it.readText() }
    org.junit.Assert.assertTrue("leaflet.css should not be empty", css.isNotEmpty())
    org.junit.Assert.assertTrue("leaflet.js should not be empty", js.isNotEmpty())
  }

  @Test
  fun `verify zone signal codes conform to ESP32 specification`() {
    assertEquals(1, com.example.core.location.ZoneDangerColor.RED.code)
    assertEquals(2, com.example.core.location.ZoneDangerColor.YELLOW.code)
    assertEquals(3, com.example.core.location.ZoneDangerColor.GREEN.code)
    assertEquals(4, com.example.core.location.ZoneDangerColor.NO_GPS.code)

    // When GPS is null, returns 4 (NO_GPS)
    val noGpsEval = com.example.core.location.ZoneColorClassifier.evaluateGpsPosition(null, null, emptyList())
    assertEquals(4, noGpsEval.signalCodeToSend)
  }
}
