package tw.nfcswitch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

/**
 * 透明跳板：小工具、快捷開關、通知、主畫面按鈕都先開這個（自己 App 的 activity，Android 12+ 允許），
 * 再打開系統設定。等設定頁回來後讀取目前預設、更新小工具，然後結束。
 */
class PaySettingsActivity : Activity() {
    private var opened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        opened = savedInstanceState?.getBoolean(STATE_OPENED) ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_OPENED, opened)
    }

    override fun onResume() {
        super.onResume()
        if (!opened) {
            opened = true
            val kind = intent.getStringExtra(EXTRA_KIND) ?: SettingsIntents.KIND_PAYMENT
            val result = if (kind == SettingsIntents.KIND_NFC) SettingsOpener.openNfc(this)
            else SettingsOpener.openPayment(this)
            if (!result.ok) {
                Toast.makeText(this, "打不開設定，請到 App 裡看診斷資訊", Toast.LENGTH_LONG).show()
                PaymentDefaults.syncUi(this)
                finishQuiet()
            }
            return
        }
        PaymentDefaults.onReturnedFromSettings(this)
        finishQuiet()
    }

    private fun finishQuiet() {
        finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    companion object {
        const val EXTRA_KIND = "kind"
        private const val STATE_OPENED = "opened"

        fun intent(ctx: Context, kind: String = SettingsIntents.KIND_PAYMENT): Intent {
            val data = Uri.parse("nfcswitch://open/$kind")
            return Intent(ctx, PaySettingsActivity::class.java)
                .setData(data)
                .putExtra(EXTRA_KIND, kind)
        }
    }
}
