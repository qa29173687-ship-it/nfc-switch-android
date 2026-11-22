package tw.nfcswitch

import android.content.Intent
import android.provider.Settings
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 打開系統設定的候選清單。真正能不能打開，要靠 startActivity 的結果，
 * 成功只代表系統接下這個動作，不代表預設已經改好。
 */
object SettingsIntents {
    data class Target(val label: String, val action: String)

    val PAYMENT_CHAIN = listOf(
        Target("預設感應付款", Settings.ACTION_NFC_PAYMENT_SETTINGS),
        Target("NFC 設定", Settings.ACTION_NFC_SETTINGS),
        Target("無線與網路", Settings.ACTION_WIRELESS_SETTINGS),
        Target("系統設定", Settings.ACTION_SETTINGS),
    )

    val NFC_CHAIN = listOf(
        Target("NFC 設定", Settings.ACTION_NFC_SETTINGS),
        Target("無線與網路", Settings.ACTION_WIRELESS_SETTINGS),
        Target("系統設定", Settings.ACTION_SETTINGS),
    )

    const val KIND_PAYMENT = "預設感應付款"
    const val KIND_NFC = "NFC設定"

    data class Step(val label: String, val action: String, val ok: Boolean, val reason: String? = null)

    data class LaunchResult(
        val kind: String,
        val ok: Boolean,
        val steps: List<Step>,
        val timeText: String,
    ) {
        fun diagnosticsLine(): String = formatDiagnostics(timeText, kind, steps)
    }

    fun intentFor(action: String): Intent =
        Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

    fun nowText(clock: LocalTime = LocalTime.now()): String =
        clock.format(TIME)

    fun formatDiagnostics(timeText: String, kind: String, steps: List<Step>): String {
        if (steps.isEmpty()) return "上次=$timeText $kind 還沒打開過"
        val body = steps.mapIndexed { i, s ->
            val tail = if (s.ok) "成功" else "失敗(${s.reason ?: "未知"})"
            "${i + 1}.${s.label}→$tail"
        }.joinToString("；")
        return "上次=$timeText $kind $body"
    }

    fun reasonOf(e: Throwable): String {
        val name = e.javaClass.simpleName
        val msg = e.message?.take(80)?.replace('\n', ' ')
        return if (msg.isNullOrBlank()) name else "$name: $msg"
    }

    private val TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
}
