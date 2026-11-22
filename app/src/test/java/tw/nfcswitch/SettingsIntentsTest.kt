package tw.nfcswitch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

class SettingsIntentsTest {
    @Test fun paymentChainStartsWithNfcPaymentSettings() {
        val actions = SettingsIntents.PAYMENT_CHAIN.map { it.action }
        assertEquals("android.settings.NFC_PAYMENT_SETTINGS", actions[0])
        assertEquals("android.settings.NFC_SETTINGS", actions[1])
        assertEquals("android.settings.WIRELESS_SETTINGS", actions[2])
        assertEquals("android.settings.SETTINGS", actions[3])
        assertEquals(listOf("預設感應付款", "NFC 設定", "無線與網路", "系統設定"), SettingsIntents.PAYMENT_CHAIN.map { it.label })
    }

    @Test fun nfcChainDoesNotStartAtPaymentSettings() {
        assertEquals("android.settings.NFC_SETTINGS", SettingsIntents.NFC_CHAIN.first().action)
        assertFalse(SettingsIntents.NFC_CHAIN.any { it.action == "android.settings.NFC_PAYMENT_SETTINGS" })
    }

    @Test fun diagnosticsFormatsSuccessfulFirstStep() {
        val steps = listOf(SettingsIntents.Step("預設感應付款", "android.settings.NFC_PAYMENT_SETTINGS", true))
        val line = SettingsIntents.formatDiagnostics("17:18:05", SettingsIntents.KIND_PAYMENT, steps)
        assertEquals("上次=17:18:05 預設感應付款 1.預設感應付款→成功", line)
    }

    @Test fun diagnosticsIncludesFallbackAfterFailure() {
        val steps = listOf(
            SettingsIntents.Step("預設感應付款", "a", false, "ActivityNotFoundException"),
            SettingsIntents.Step("NFC 設定", "b", true),
        )
        val line = SettingsIntents.formatDiagnostics("08:01:00", SettingsIntents.KIND_PAYMENT, steps)
        assertEquals("上次=08:01:00 預設感應付款 1.預設感應付款→失敗(ActivityNotFoundException)；2.NFC 設定→成功", line)
    }

    @Test fun nowTextUsesHms() {
        assertEquals("09:05:07", SettingsIntents.nowText(LocalTime.of(9, 5, 7)))
    }

    @Test fun reasonOfIncludesExceptionName() {
        val r = SettingsIntents.reasonOf(IllegalStateException("no activity"))
        assertTrue(r.contains("IllegalStateException"))
        assertTrue(r.contains("no activity"))
    }
}
