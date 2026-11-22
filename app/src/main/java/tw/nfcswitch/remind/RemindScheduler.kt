package tw.nfcswitch.remind

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import tw.nfcswitch.Prefs
import java.util.concurrent.TimeUnit

object RemindScheduler {
    const val UNIQUE = "nfc-switch-remind"

    fun arm(ctx: Context) {
        val prefs = Prefs(ctx)
        if (!prefs.remindEnabled) {
            cancel(ctx)
            return
        }
        val req = OneTimeWorkRequestBuilder<RemindWorker>()
            .setInitialDelay(prefs.remindMinutes.toLong(), TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(ctx).enqueueUniqueWork(UNIQUE, ExistingWorkPolicy.REPLACE, req)
    }

    fun cancel(ctx: Context) {
        WorkManager.getInstance(ctx).cancelUniqueWork(UNIQUE)
    }
}
