package tw.nfcswitch.remind

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import tw.nfcswitch.PaySettingsActivity
import tw.nfcswitch.R

object Notifier {
    const val CHANNEL_ID = "remind"
    const val NOTIF_ID = 17

    fun ensureChannel(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        val ch = NotificationChannel(CHANNEL_ID, ctx.getString(R.string.notif_channel), NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "切到 Google 錢包後，提醒你切回門禁"
        }
        nm.createNotificationChannel(ch)
    }

    fun canPost(ctx: Context): Boolean =
        (Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) &&
            NotificationManagerCompat.from(ctx).areNotificationsEnabled()

    fun showSwitchBack(ctx: Context) {
        if (!canPost(ctx)) return
        ensureChannel(ctx)
        val open = PendingIntent.getActivity(
            ctx,
            18,
            PaySettingsActivity.intent(ctx).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notif = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notify)
            .setContentTitle(ctx.getString(R.string.notif_title))
            .setContentText(ctx.getString(R.string.notif_text))
            .setStyle(NotificationCompat.BigTextStyle().bigText(ctx.getString(R.string.notif_text)))
            .setContentIntent(open)
            .addAction(0, ctx.getString(R.string.notif_action), open)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(ctx).notify(NOTIF_ID, notif)
    }
}
