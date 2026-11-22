package tw.nfcswitch

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PrefsTest {
    private val ctx get() = ApplicationProvider.getApplicationContext<Application>()

    @Test fun lastChoiceAndLabelRoundTrip() {
        val p = Prefs(ctx)
        assertNull(p.lastChoice)
        assertEquals("尚未選擇", p.lastChoiceLabel())
        p.lastChoice = Choice.WALLET
        assertEquals(Choice.WALLET, Prefs(ctx).lastChoice)
        assertEquals("錢包（門禁）", Prefs(ctx).lastChoiceLabel())
        p.lastChoice = Choice.GOOGLE
        assertEquals("Google 錢包（付款）", Prefs(ctx).lastChoiceLabel())
    }

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
}
