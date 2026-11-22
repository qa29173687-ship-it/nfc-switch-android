package tw.nfcswitch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentDefaultTest {
    @Test fun parsesFlattenedComponent() {
        val cn = PaymentDefaults.parseComponent("com.finshell.wallet/.NfcPaymentService")
        assertEquals("com.finshell.wallet", cn!!.packageName)
        assertTrue(cn.className.contains("NfcPaymentService"))
        assertNull(PaymentDefaults.parseComponent(null))
        assertNull(PaymentDefaults.parseComponent(""))
        assertNull(PaymentDefaults.parseComponent("not-a-component"))
    }

    @Test fun mapsKnownOppoWalletPackages() {
        for (pkg in listOf("com.finshell.wallet", "com.coloros.wallet", "com.heytap.wallet")) {
            assertEquals("OPPO 錢包（門禁）", PaymentDefaults.friendlyLabel(pkg, "x.Service", "錢包"))
            assertEquals(PaymentKind.OPPO_WALLET, PaymentDefaults.kindOf(pkg, "x.Service", "錢包"))
        }
    }

    @Test fun mapsVendorPackageNamedWallet() {
        assertTrue(PaymentDefaults.isOppoWalletPackage("com.oplus.something", "錢包"))
        assertEquals("OPPO 錢包（門禁）", PaymentDefaults.friendlyLabel("com.oppo.foo", "svc", "Wallet"))
        assertFalse(PaymentDefaults.isOppoWalletPackage("com.oplus.pay", "安全支付"))
    }

    @Test fun mapsGoogleWalletPackage() {
        assertEquals(
            "Google 錢包",
            PaymentDefaults.friendlyLabel("com.google.android.apps.walletnfcrel", "svc", "Google Wallet")
        )
        assertEquals(
            PaymentKind.GOOGLE_WALLET,
            PaymentDefaults.kindOf("com.google.android.apps.walletnfcrel", "svc", "Google Wallet")
        )
    }

    @Test fun mapsGmsTapAndPayAsGoogleWallet() {
        assertTrue(PaymentDefaults.isGoogleWallet("com.google.android.gms", "com.google.android.gms.tapandpay.hce.service.TpHceService"))
        assertEquals(
            "Google 錢包",
            PaymentDefaults.friendlyLabel("com.google.android.gms", "com.google.android.gms.tapandpay.hce.service.TpHceService", "Google Play 服務")
        )
        assertFalse(PaymentDefaults.isGoogleWallet("com.google.android.gms", "com.google.android.gms.other.Service"))
    }

    @Test fun fallsBackToAppLabelOrPackage() {
        assertEquals("支付寶", PaymentDefaults.friendlyLabel("com.eg.android.AlipayGphone", "svc", "支付寶"))
        assertEquals("com.unknown.app", PaymentDefaults.friendlyLabel("com.unknown.app", "svc", null))
        assertEquals(PaymentKind.OTHER, PaymentDefaults.kindOf("com.unknown.app", "svc", "某某"))
    }

    @Test fun fromRawUnreadableWhenEmptyOrBad() {
        val empty = PaymentDefaults.fromRaw(null, null)
        assertTrue(empty is PaymentDefault.Unreadable)
        assertEquals("目前：無法讀取", empty.statusText)
        assertTrue(empty.diagnosticsLine.startsWith("nfc_payment_default_component=無法讀取"))

        val bad = PaymentDefaults.fromRaw("???", null)
        assertTrue(bad is PaymentDefault.Unreadable)
        assertTrue(bad.diagnosticsLine.contains("格式無法解析"))
    }

    @Test fun fromRawKnownIncludesRawComponentInDiagnostics() {
        val raw = "com.finshell.wallet/.PayService"
        val d = PaymentDefaults.fromRaw(raw, "錢包") as PaymentDefault.Known
        assertEquals("目前：OPPO 錢包（門禁）", d.statusText)
        assertEquals(raw, d.raw)
        assertTrue(d.diagnosticsLine.contains(raw))
        assertTrue(d.diagnosticsLine.contains("OPPO 錢包（門禁）"))
    }
}
