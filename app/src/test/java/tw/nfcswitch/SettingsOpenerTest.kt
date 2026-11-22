package tw.nfcswitch

import android.app.Application
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalTime

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsOpenerTest {
    private val ctx get() = ApplicationProvider.getApplicationContext<Application>()

    @Test fun stopsAtFirstSuccessfulIntent() {
        val started = ArrayList<String>()
        val result = SettingsOpener.open(
            ctx,
            SettingsIntents.KIND_PAYMENT,
            SettingsIntents.PAYMENT_CHAIN,
            starter = { i ->
                started += i.action!!
                if (i.action == "android.settings.NFC_PAYMENT_SETTINGS") Unit
                else error("should not continue")
            },
            clock = LocalTime.of(17, 15, 32),
        )
        assertTrue(result.ok)
        assertEquals(listOf("android.settings.NFC_PAYMENT_SETTINGS"), started)
        assertEquals(1, result.steps.size)
        assertEquals("上次=17:15:32 預設感應付款 1.預設感應付款→成功", result.diagnosticsLine())
        assertEquals(result.diagnosticsLine(), Prefs(ctx).lastLaunch)
    }

    @Test fun fallsThroughWhenFirstThrows() {
        val started = ArrayList<String>()
        val result = SettingsOpener.open(
            ctx,
            SettingsIntents.KIND_PAYMENT,
            SettingsIntents.PAYMENT_CHAIN,
            starter = { i ->
                started += i.action!!
                if (i.action == "android.settings.NFC_PAYMENT_SETTINGS") throw android.content.ActivityNotFoundException("none")
                Unit
            },
            clock = LocalTime.of(10, 0, 0),
        )
        assertTrue(result.ok)
        assertEquals(2, started.size)
        assertFalse(result.steps[0].ok)
        assertTrue(result.steps[1].ok)
        assertTrue(result.diagnosticsLine().contains("1.預設感應付款→失敗"))
        assertTrue(result.diagnosticsLine().contains("2.NFC 設定→成功"))
    }

    @Test fun allFailLeavesOkFalse() {
        val result = SettingsOpener.open(
            ctx,
            SettingsIntents.KIND_NFC,
            SettingsIntents.NFC_CHAIN,
            starter = { throw android.content.ActivityNotFoundException("none") },
            clock = LocalTime.of(1, 2, 3),
        )
        assertFalse(result.ok)
        assertEquals(SettingsIntents.NFC_CHAIN.size, result.steps.size)
        assertTrue(result.steps.all { !it.ok })
        assertTrue(result.diagnosticsLine().startsWith("上次=01:02:03 NFC設定 "))
    }
}
