package tw.nfcswitch.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import tw.nfcswitch.PaySettingsActivity
import tw.nfcswitch.PaymentDefault
import tw.nfcswitch.PaymentDefaults
import tw.nfcswitch.PaymentKind

class NfcSwitchWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val current = PaymentDefaults.read(context)
        provideContent {
            GlanceTheme {
                WidgetContent(current)
            }
        }
    }
}

class NfcSwitchWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NfcSwitchWidget()
}

private val Ink = ColorProvider(Color(0xFF1C1C1E))
private val Muted = ColorProvider(Color(0xFF6B6F76))
private val GreenDeep = ColorProvider(Color(0xFF087A54))
private val BlueDeep = ColorProvider(Color(0xFF1557B0))
private val GreenSoft = Color(0xFFE7F7F0)
private val Amber = ColorProvider(Color(0xFF8A5A00))

@Composable
private fun WidgetContent(current: PaymentDefault) {
    val ctx = LocalContext.current
    val statusColor = when (current.kind) {
        PaymentKind.OPPO_WALLET -> GreenDeep
        PaymentKind.GOOGLE_WALLET -> BlueDeep
        PaymentKind.UNREADABLE -> Amber
        PaymentKind.OTHER -> Ink
    }
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(Color(0xFFF7F8FA))
            .cornerRadius(24.dp)
            .padding(14.dp)
    ) {
        Text(
            text = "NFC 付款切換",
            style = TextStyle(color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = current.statusText,
            style = TextStyle(color = statusColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        )
        Spacer(GlanceModifier.height(10.dp))
        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .defaultWeight()
                .cornerRadius(18.dp)
                .background(GreenSoft)
                .clickable(actionStartActivity(PaySettingsActivity.intent(ctx)))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "切換 NFC 付款",
                style = TextStyle(
                    color = GreenDeep,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
