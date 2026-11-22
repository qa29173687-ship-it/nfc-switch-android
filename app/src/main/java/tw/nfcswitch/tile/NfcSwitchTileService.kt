package tw.nfcswitch.tile

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import tw.nfcswitch.PaySettingsActivity
import tw.nfcswitch.PaymentDefaults
import tw.nfcswitch.PaymentKind

class NfcSwitchTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile ?: return
        val current = PaymentDefaults.read(this)
        tile.state = Tile.STATE_INACTIVE
        tile.label = "NFC 付款切換"
        if (Build.VERSION.SDK_INT >= 29) {
            tile.subtitle = if (current.kind == PaymentKind.UNREADABLE) {
                "目前：無法讀取"
            } else {
                current.statusText
            }
        }
        tile.updateTile()
    }

    override fun onClick() {
        val intent = PaySettingsActivity.intent(this)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pending = PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= 34) {
            startActivityAndCollapse(pending)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    companion object {
        fun requestUpdate(ctx: Context) {
            runCatching {
                requestListeningState(
                    ctx.applicationContext,
                    ComponentName(ctx, NfcSwitchTileService::class.java)
                )
            }
        }
    }
}
