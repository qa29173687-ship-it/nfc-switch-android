package tw.nfcswitch

import android.content.Context

class Prefs(private val ctx: Context) {
    private val sp = ctx.getSharedPreferences("nfc_switch", Context.MODE_PRIVATE)

    var remindEnabled: Boolean
        get() = sp.getBoolean(KEY_REMIND, false)
        set(value) { sp.edit().putBoolean(KEY_REMIND, value).apply() }

    var remindMinutes: Int
        get() = RemindPolicy.clampMinutes(sp.getInt(KEY_MINS, RemindPolicy.DEFAULT_MINUTES))
        set(value) { sp.edit().putInt(KEY_MINS, RemindPolicy.clampMinutes(value)).apply() }

    var lastLaunch: String
        get() = sp.getString(KEY_LAUNCH, "") ?: ""
        set(value) { sp.edit().putString(KEY_LAUNCH, value).apply() }

    var lastComponent: String
        get() = sp.getString(KEY_COMPONENT, "") ?: ""
        set(value) { sp.edit().putString(KEY_COMPONENT, value).apply() }

    var lastSeenPackage: String?
        get() = sp.getString(KEY_SEEN_PKG, null)
        set(value) { sp.edit().putString(KEY_SEEN_PKG, value).apply() }

    fun lastSeenKind(): PaymentKind? {
        val raw = sp.getString(KEY_SEEN_KIND, null) ?: return null
        return runCatching { PaymentKind.valueOf(raw) }.getOrNull()
    }

    fun setLastSeen(packageName: String?, kind: PaymentKind) {
        sp.edit()
            .putString(KEY_SEEN_PKG, packageName)
            .putString(KEY_SEEN_KIND, kind.name)
            .apply()
    }

    fun diagnosticsText(current: PaymentDefault): String {
        val launch = lastLaunch.ifBlank { "還沒打開過設定" }
        return launch + "\n" + current.diagnosticsLine
    }

    companion object {
        private const val KEY_REMIND = "remind_on"
        private const val KEY_MINS = "remind_mins"
        private const val KEY_LAUNCH = "last_launch"
        private const val KEY_COMPONENT = "last_component"
        private const val KEY_SEEN_PKG = "last_seen_pkg"
        private const val KEY_SEEN_KIND = "last_seen_kind"
    }
}
