package tw.nfcswitch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RemindPolicyTest {
    @Test fun armsOnlyWhenEnabledAndLastChoiceIsGoogleWallet() {
        assertTrue(RemindPolicy.shouldArm(true, Choice.GOOGLE))
        assertFalse(RemindPolicy.shouldArm(false, Choice.GOOGLE))
        assertFalse(RemindPolicy.shouldArm(true, Choice.WALLET))
        assertFalse(RemindPolicy.shouldArm(true, null))
        assertFalse(RemindPolicy.shouldArm(false, Choice.WALLET))
    }
}
