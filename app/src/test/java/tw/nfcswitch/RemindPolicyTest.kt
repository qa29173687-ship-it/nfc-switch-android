package tw.nfcswitch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RemindPolicyTest {
    @Test fun hidesReminderWhenUnreadable() {
        assertFalse(RemindPolicy.shouldShowReminder(PaymentKind.UNREADABLE))
        assertTrue(RemindPolicy.shouldShowReminder(PaymentKind.OPPO_WALLET))
        assertTrue(RemindPolicy.shouldShowReminder(PaymentKind.GOOGLE_WALLET))
        assertTrue(RemindPolicy.shouldShowReminder(PaymentKind.OTHER))
    }

    @Test fun armsOnlyWhenDefaultBecomesGoogleWallet() {
        assertTrue(RemindPolicy.shouldArmOnReturn(true, PaymentKind.OPPO_WALLET, PaymentKind.GOOGLE_WALLET))
        assertTrue(RemindPolicy.shouldArmOnReturn(true, PaymentKind.OTHER, PaymentKind.GOOGLE_WALLET))
        assertFalse(RemindPolicy.shouldArmOnReturn(false, PaymentKind.OPPO_WALLET, PaymentKind.GOOGLE_WALLET))
        assertFalse(RemindPolicy.shouldArmOnReturn(true, PaymentKind.GOOGLE_WALLET, PaymentKind.GOOGLE_WALLET))
        assertFalse(RemindPolicy.shouldArmOnReturn(true, null, PaymentKind.GOOGLE_WALLET))
        assertFalse(RemindPolicy.shouldArmOnReturn(true, PaymentKind.OPPO_WALLET, PaymentKind.OPPO_WALLET))
        assertFalse(RemindPolicy.shouldArmOnReturn(true, PaymentKind.OPPO_WALLET, PaymentKind.UNREADABLE))
    }

    @Test fun cancelsWhenReadableAndNotGoogle() {
        assertTrue(RemindPolicy.shouldCancelOnReturn(PaymentKind.OPPO_WALLET))
        assertTrue(RemindPolicy.shouldCancelOnReturn(PaymentKind.OTHER))
        assertFalse(RemindPolicy.shouldCancelOnReturn(PaymentKind.GOOGLE_WALLET))
        assertFalse(RemindPolicy.shouldCancelOnReturn(PaymentKind.UNREADABLE))
    }

    @Test fun notifyIfStillGoogleOrUnreadableAtFireTime() {
        assertTrue(RemindPolicy.shouldNotify(true, PaymentKind.GOOGLE_WALLET))
        assertTrue(RemindPolicy.shouldNotify(true, PaymentKind.UNREADABLE))
        assertFalse(RemindPolicy.shouldNotify(true, PaymentKind.OPPO_WALLET))
        assertFalse(RemindPolicy.shouldNotify(false, PaymentKind.GOOGLE_WALLET))
    }

    @Test fun clampMinutesOnlyAllowsFiveFifteenThirty() {
        assertEquals(15, RemindPolicy.clampMinutes(0))
        assertEquals(5, RemindPolicy.clampMinutes(5))
        assertEquals(15, RemindPolicy.clampMinutes(15))
        assertEquals(30, RemindPolicy.clampMinutes(30))
        assertEquals(15, RemindPolicy.clampMinutes(10))
        assertEquals(listOf(5, 15, 30), RemindPolicy.MINUTES)
    }
}
