package tw.nfcswitch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChoiceTest {
    @Test fun knownValues() {
        assertTrue(Choice.isKnown(Choice.WALLET))
        assertTrue(Choice.isKnown(Choice.GOOGLE))
        assertFalse(Choice.isKnown(null))
        assertFalse(Choice.isKnown(""))
        assertFalse(Choice.isKnown("oppo"))
    }

    @Test fun labelsAreTraditionalChineseAndDoNotClaimSystemState() {
        assertEquals("錢包（門禁）", Choice.label(Choice.WALLET))
        assertEquals("Google 錢包（付款）", Choice.label(Choice.GOOGLE))
        assertEquals("尚未選擇", Choice.label(null))
        assertEquals("尚未選擇", Choice.label("other"))
    }

    @Test fun clampMinutesOnlyAllowsFiveFifteenThirty() {
        assertEquals(15, Choice.clampMinutes(0))
        assertEquals(15, Choice.clampMinutes(10))
        assertEquals(5, Choice.clampMinutes(5))
        assertEquals(15, Choice.clampMinutes(15))
        assertEquals(30, Choice.clampMinutes(30))
        assertEquals(15, Choice.clampMinutes(-1))
        assertEquals(listOf(5, 15, 30), Choice.REMIND_MINUTES)
    }
}
