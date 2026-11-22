package tw.nfcswitch.remind

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import tw.nfcswitch.PaymentDefaults
import tw.nfcswitch.Prefs
import tw.nfcswitch.RemindPolicy

class RemindWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val prefs = Prefs(applicationContext)
        val current = PaymentDefaults.read(applicationContext)
        if (RemindPolicy.shouldNotify(prefs.remindEnabled, current.kind)) {
            Notifier.showSwitchBack(applicationContext)
        }
        return Result.success()
    }
}
