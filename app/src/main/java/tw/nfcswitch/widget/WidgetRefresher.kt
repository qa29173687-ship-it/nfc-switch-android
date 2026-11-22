package tw.nfcswitch.widget

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import androidx.glance.appwidget.updateAll

object WidgetRefresher {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun refresh(ctx: Context) {
        val app = ctx.applicationContext
        scope.launch {
            runCatching { NfcSwitchWidget().updateAll(app) }
        }
    }
}
