package tw.nfcswitch

import android.content.Context

object SettingsOpener {
    fun openPayment(ctx: Context): SettingsIntents.LaunchResult =
        open(ctx, SettingsIntents.KIND_PAYMENT, SettingsIntents.PAYMENT_CHAIN)

    fun openNfc(ctx: Context): SettingsIntents.LaunchResult =
        open(ctx, SettingsIntents.KIND_NFC, SettingsIntents.NFC_CHAIN)

    fun open(
        ctx: Context,
        kind: String,
        chain: List<SettingsIntents.Target>,
        starter: (android.content.Intent) -> Unit = { ctx.startActivity(it) },
        clock: java.time.LocalTime = java.time.LocalTime.now(),
    ): SettingsIntents.LaunchResult {
        val steps = ArrayList<SettingsIntents.Step>()
        var ok = false
        for (t in chain) {
            try {
                starter(SettingsIntents.intentFor(t.action))
                steps += SettingsIntents.Step(t.label, t.action, true)
                ok = true
                break
            } catch (e: Exception) {
                steps += SettingsIntents.Step(t.label, t.action, false, SettingsIntents.reasonOf(e))
            }
        }
        val result = SettingsIntents.LaunchResult(kind, ok, steps, SettingsIntents.nowText(clock))
        Prefs(ctx).lastLaunch = result.diagnosticsLine()
        return result
    }
}
