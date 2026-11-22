package tw.nfcswitch.ui

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import tw.nfcswitch.Choice
import tw.nfcswitch.NfcStatus
import tw.nfcswitch.NfcStatusReader
import tw.nfcswitch.PaySettingsActivity
import tw.nfcswitch.Prefs
import tw.nfcswitch.SettingsOpener
import tw.nfcswitch.remind.Notifier
import tw.nfcswitch.remind.RemindScheduler
import tw.nfcswitch.widget.NfcSwitchWidgetReceiver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Notifier.ensureChannel(this)
        setContent { NfcSwitchTheme { AppRoot() } }
    }

    @androidx.compose.runtime.Composable
    private fun AppRoot() {
        val prefs = remember { Prefs(this) }
        var lastChoice by remember { mutableStateOf(prefs.lastChoice) }
        var remind by remember { mutableStateOf(prefs.remindEnabled) }
        var minutes by remember { mutableStateOf(prefs.remindMinutes) }
        var nfc by remember { mutableStateOf(NfcStatusReader.read(this)) }
        var diagnostics by remember { mutableStateOf(prefs.lastLaunch) }
        var canNotify by remember { mutableStateOf(Notifier.canPost(this)) }
        var toast by remember { mutableStateOf<String?>(null) }

        fun refresh() {
            lastChoice = prefs.lastChoice
            remind = prefs.remindEnabled
            minutes = prefs.remindMinutes
            nfc = NfcStatusReader.read(this)
            diagnostics = prefs.lastLaunch
            canNotify = Notifier.canPost(this)
        }

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { refresh() }

        val permLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            canNotify = Notifier.canPost(this)
            if (!granted) toast = "沒有通知權限的話，提醒不會出現。可到系統設定開啟。"
        }

        fun recordAndOpenPayment(choice: String) {
            prefs.lastChoice = choice
            lastChoice = choice
            RemindScheduler.applyChoice(this, choice)
            val result = SettingsOpener.openPayment(this)
            diagnostics = result.diagnosticsLine()
            if (!result.ok) toast = "打不開設定，請看下面的診斷資訊"
        }

        fun openNfcSettings() {
            val result = SettingsOpener.openNfc(this)
            diagnostics = result.diagnosticsLine()
            if (!result.ok) toast = "打不開 NFC 設定，請看下面的診斷資訊"
        }

        fun setRemind(on: Boolean) {
            prefs.remindEnabled = on
            remind = on
            if (on && Build.VERSION.SDK_INT >= 33 && !Notifier.canPost(this)) {
                permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (on) RemindScheduler.arm(this) else RemindScheduler.cancel(this)
        }

        fun pinWidget() {
            val mgr = AppWidgetManager.getInstance(this)
            val cn = ComponentName(this, NfcSwitchWidgetReceiver::class.java)
            if (mgr.isRequestPinAppWidgetSupported) {
                mgr.requestPinAppWidget(cn, null, null)
            } else {
                toast = "這支手機不支援從 App 加到桌面，請長按桌面空白處，選小工具。"
            }
        }

        MainScreen(
            nfc = nfc,
            lastChoice = lastChoice,
            lastChoiceLabel = Choice.label(lastChoice),
            remindEnabled = remind,
            remindMinutes = minutes,
            canNotify = canNotify,
            diagnostics = diagnostics.ifBlank { "還沒打開過設定" },
            onOpenNfc = { openNfcSettings() },
            onOpenPayment = {
                startActivity(PaySettingsActivity.intent(this).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
            },
            onPickWallet = { recordAndOpenPayment(Choice.WALLET) },
            onPickGoogle = { recordAndOpenPayment(Choice.GOOGLE) },
            onRemindChange = { setRemind(it) },
            onMinutesChange = {
                prefs.remindMinutes = it
                minutes = prefs.remindMinutes
                if (remind) RemindScheduler.arm(this)
            },
            onPinWidget = { pinWidget() },
            message = toast,
            onMessageShown = { toast = null }
        )
    }
}
