package tw.nfcswitch.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import tw.nfcswitch.Choice
import tw.nfcswitch.PaySettingsActivity
import tw.nfcswitch.Prefs

class NfcSwitchWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val last = Prefs(context).lastChoice
        provideContent {
            GlanceTheme {
                WidgetContent(last)
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
private val BlueSoft = Color(0xFFE8F0FE)
private val Card = Color(0xFFF7F8FA)
private val Amber = ColorProvider(Color(0xFF8A5A00))

@Composable
private fun WidgetContent(last: String?) {
    val ctx = androidx.glance.LocalContext.current
    val lastColor = when (last) {
        Choice.WALLET -> GreenDeep
        Choice.GOOGLE -> BlueDeep
        else -> Ink
    }
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(Card)
            .cornerRadius(24.dp)
            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "NFC 付款切換",
                style = TextStyle(color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold),
                modifier = GlanceModifier.defaultWeight()
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "上次選擇 · 示意",
                    style = TextStyle(color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                )
                Text(
                    text = Choice.label(last),
                    style = TextStyle(color = lastColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
        Spacer(GlanceModifier.height(10.dp))
        Row(modifier = GlanceModifier.fillMaxWidth().defaultWeight()) {
            ChoiceButton(
                title = "刷門禁",
                sub = "OPPO 錢包",
                bg = GreenSoft,
                fg = GreenDeep,
                modifier = GlanceModifier.defaultWeight(),
                action = actionStartActivity(PaySettingsActivity.intent(ctx, Choice.WALLET))
            )
            Spacer(GlanceModifier.width(10.dp))
            ChoiceButton(
                title = "付款",
                sub = "Google 錢包",
                bg = BlueSoft,
                fg = BlueDeep,
                modifier = GlanceModifier.defaultWeight(),
                action = actionStartActivity(PaySettingsActivity.intent(ctx, Choice.GOOGLE))
            )
        }
    }
}

@Composable
private fun ChoiceButton(
    title: String,
    sub: String,
    bg: Color,
    fg: ColorProvider,
    modifier: GlanceModifier,
    action: androidx.glance.action.Action,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .cornerRadius(18.dp)
            .background(bg)
            .clickable(action)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(color = fg, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        )
        Text(
            text = sub,
            style = TextStyle(color = fg, fontSize = 11.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        )
    }
}
