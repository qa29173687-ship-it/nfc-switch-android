package tw.nfcswitch

import android.content.Context
import android.nfc.NfcAdapter

data class NfcStatus(val available: Boolean, val enabled: Boolean) {
    val label: String = when {
        !available -> "這支手機沒有 NFC"
        enabled -> "已開啟"
        else -> "已關閉，刷卡前請先打開"
    }
}

object NfcStatusReader {
    fun read(ctx: Context): NfcStatus {
        val adapter = NfcAdapter.getDefaultAdapter(ctx)
        return if (adapter == null) NfcStatus(available = false, enabled = false)
        else NfcStatus(available = true, enabled = adapter.isEnabled)
    }
}
