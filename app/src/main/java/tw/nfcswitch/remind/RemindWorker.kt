package tw.nfcswitch.remind

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import tw.nfcswitch.Choice
import tw.nfcswitch.Prefs

class RemindWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val prefs = Prefs(applicationContext)
        if (prefs.remindEnabled && prefs.lastChoice == Choice.GOOGLE) {
            Notifier.showSwitchBack(applicationContext)
        }
        return Result.success()
    }
}
