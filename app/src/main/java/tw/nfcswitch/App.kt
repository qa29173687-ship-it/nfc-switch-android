package tw.nfcswitch

import android.app.Application
import tw.nfcswitch.remind.Notifier

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Notifier.ensureChannel(this)
    }
}
