package tw.nfcswitch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import tw.nfcswitch.remind.RemindScheduler

/**
 * 透明跳板：小工具、快捷開關、通知都先開這個（自己 App 的 activity，Android 12+ 允許），
 * 必要時記下「上次選擇」，再依序嘗試打開系統設定，然後馬上結束。
 */
class PaySettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val choice = intent.getStringExtra(EXTRA_CHOICE)
        if (Choice.isKnown(choice)) {
            Prefs(this).lastChoice = choice
            if (choice == Choice.GOOGLE) RemindScheduler.arm(this) else RemindScheduler.cancel(this)
        }
        val kind = intent.getStringExtra(EXTRA_KIND) ?: SettingsIntents.KIND_PAYMENT
        val result = if (kind == SettingsIntents.KIND_NFC) SettingsOpener.openNfc(this)
        else SettingsOpener.openPayment(this)
        if (!result.ok) {
            Toast.makeText(this, "打不開設定，請到 App 裡看診斷資訊", Toast.LENGTH_LONG).show()
        }
        finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    companion object {
        const val EXTRA_CHOICE = "choice"
        const val EXTRA_KIND = "kind"

        fun intent(ctx: Context, choice: String? = null, kind: String = SettingsIntents.KIND_PAYMENT): Intent {
            val data = Uri.parse("nfcswitch://open/${kind}/${choice ?: "none"}")
            return Intent(ctx, PaySettingsActivity::class.java)
                .setData(data)
                .putExtra(EXTRA_CHOICE, choice)
                .putExtra(EXTRA_KIND, kind)
        }
    }
}
