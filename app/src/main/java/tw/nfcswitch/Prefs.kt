package tw.nfcswitch

import android.content.Context
import tw.nfcswitch.widget.WidgetRefresher

class Prefs(private val ctx: Context) {
    private val sp = ctx.getSharedPreferences("nfc_switch", Context.MODE_PRIVATE)

    var lastChoice: String?
        get() = sp.getString(KEY_CHOICE, null)
        set(value) {
            sp.edit().putString(KEY_CHOICE, value).apply()
            WidgetRefresher.refresh(ctx)
            tw.nfcswitch.tile.NfcSwitchTileService.requestUpdate(ctx)
        }

    var remindEnabled: Boolean
        get() = sp.getBoolean(KEY_REMIND, false)
        set(value) { sp.edit().putBoolean(KEY_REMIND, value).apply() }

    var remindMinutes: Int
        get() = Choice.clampMinutes(sp.getInt(KEY_MINS, Choice.DEFAULT_MINUTES))
        set(value) { sp.edit().putInt(KEY_MINS, Choice.clampMinutes(value)).apply() }

    var lastLaunch: String
        get() = sp.getString(KEY_LAUNCH, "") ?: ""
        set(value) { sp.edit().putString(KEY_LAUNCH, value).apply() }

    fun lastChoiceLabel(): String = Choice.label(lastChoice)

    companion object {
        private const val KEY_CHOICE = "last_choice"
        private const val KEY_REMIND = "remind_on"
        private const val KEY_MINS = "remind_mins"
        private const val KEY_LAUNCH = "last_launch"
    }
}
