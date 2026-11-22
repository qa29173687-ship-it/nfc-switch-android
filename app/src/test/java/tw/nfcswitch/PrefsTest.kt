package tw.nfcswitch

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PrefsTest {
    private val ctx get() = ApplicationProvider.getApplicationContext<Application>()

    @Test fun remindDefaultsAndClamps() {
        val p = Prefs(ctx)
        assertFalse(p.remindEnabled)
        assertEquals(15, p.remindMinutes)
        p.remindMinutes = 7
        assertEquals(15, p.remindMinutes)
        p.remindMinutes = 30
        assertEquals(30, p.remindMinutes)
        p.remindEnabled = true
        assertEquals(true, Prefs(ctx).remindEnabled)
    }

    @Test fun lastSeenKindRoundTrip() {
        val p = Prefs(ctx)
        assertNull(p.lastSeenKind())
        p.setLastSeen("com.finshell.wallet", PaymentKind.OPPO_WALLET)
        assertEquals(PaymentKind.OPPO_WALLET, Prefs(ctx).lastSeenKind())
        assertEquals("com.finshell.wallet", Prefs(ctx).lastSeenPackage)
        p.setLastSeen("com.google.android.apps.walletnfcrel", PaymentKind.GOOGLE_WALLET)
        assertEquals(PaymentKind.GOOGLE_WALLET, Prefs(ctx).lastSeenKind())
    }

    @Test fun diagnosticsTextIncludesLaunchAndCurrentComponent() {
        val p = Prefs(ctx)
        p.lastLaunch = "上次=17:15:32 預設感應付款 1.預設感應付款→成功"
        val current = PaymentDefaults.fromRaw("com.finshell.wallet/.PayService", "錢包")
        val text = p.diagnosticsText(current)
        assertTrue(text.startsWith("上次=17:15:32"))
        assertTrue(text.contains("nfc_payment_default_component=com.finshell.wallet/.PayService"))
    }
}
