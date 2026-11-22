package tw.nfcswitch.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tw.nfcswitch.NfcStatus
import tw.nfcswitch.PaymentDefault
import tw.nfcswitch.PaymentKind
import tw.nfcswitch.RemindPolicy

@Composable
fun MainScreen(
    nfc: NfcStatus,
    payment: PaymentDefault,
    showReminder: Boolean,
    remindEnabled: Boolean,
    remindMinutes: Int,
    canNotify: Boolean,
    diagnostics: String,
    onOpenNfc: () -> Unit,
    onOpenPayment: () -> Unit,
    onRemindChange: (Boolean) -> Unit,
    onMinutesChange: (Int) -> Unit,
    onPinWidget: () -> Unit,
    message: String?,
    onMessageShown: () -> Unit,
) {
    val ctx = LocalContext.current
    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()
            onMessageShown()
        }
    }
    val statusColor = when (payment.kind) {
        PaymentKind.OPPO_WALLET -> GreenDeep
        PaymentKind.GOOGLE_WALLET -> BlueDeep
        PaymentKind.UNREADABLE -> Amber
        PaymentKind.OTHER -> Ink
    }
    Scaffold(
        containerColor = Sheet,
        topBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Sheet)
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
            ) {
                Text("NFC 付款切換", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink)
            }
        }
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Banner("這個 App 不能直接改系統預設，只會打開設定頁，由你點一下切換。")

            Card {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (nfc.available && nfc.enabled) Green else Color(0xFFC45B4A))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("NFC", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink)
                        Text(nfc.label, color = Muted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    OutlinedButton(onClick = onOpenNfc, shape = RoundedCornerShape(50)) {
                        Text("開啟 NFC 設定", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Ink)
                    }
                }
            }

            Card {
                Text("目前的預設", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    payment.statusText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = statusColor,
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )
                Surface(
                    onClick = onOpenPayment,
                    shape = RoundedCornerShape(20.dp),
                    color = GreenSoft,
                    modifier = Modifier.fillMaxWidth().height(88.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("切換 NFC 付款", color = GreenDeep, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                }
            }

            Card {
                Text("使用教學", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Ink)
                Spacer(Modifier.height(8.dp))
                val tips = listOf(
                    "到「感應付款」設定裡，開啟「需要解鎖才能付款」。螢幕鎖著時就不會跳出付款。",
                    "刷門禁時，把手機背面上方貼住讀卡機，停 1 到 2 秒再拿開。",
                    "這個 App 不能直接改系統預設，只會打開設定頁，由你點一下切換。",
                    "要把快捷開關加到下拉選單：下拉兩次→編輯→拖曳「NFC 付款切換」。",
                    "桌面小工具：長按桌面空白處→小工具→NFC 付款切換。也可以按下面的「加到桌面」。",
                )
                tips.forEachIndexed { i, t ->
                    Row(Modifier.padding(bottom = 8.dp)) {
                        Text("${i + 1}. ", fontWeight = FontWeight.Bold, color = Ink, fontSize = 13.sp)
                        Text(t, color = Color(0xFF3D424C), fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
                OutlinedButton(onClick = onPinWidget, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text("加到桌面", fontWeight = FontWeight.Bold, color = Ink)
                }
            }

            Card {
                if (showReminder) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("提醒", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Ink)
                            Text("切到 Google 錢包後，提醒我切回門禁", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
                        }
                        Switch(
                            checked = remindEnabled,
                            onCheckedChange = onRemindChange,
                            colors = SwitchDefaults.colors(checkedTrackColor = Green, checkedThumbColor = Color.White)
                        )
                    }
                    if (remindEnabled) {
                        Spacer(Modifier.height(8.dp))
                        Text("提醒時間", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RemindPolicy.MINUTES.forEach { m ->
                                val on = m == remindMinutes
                                Surface(
                                    onClick = { onMinutesChange(m) },
                                    shape = RoundedCornerShape(50),
                                    color = if (on) GreenSoft else Sheet,
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            "$m 分鐘",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (on) GreenDeep else Ink
                                        )
                                    }
                                }
                            }
                        }
                        if (!canNotify) {
                            Text(
                                "目前沒有通知權限，提醒時間到了也不會出現通知。請到系統設定允許通知。",
                                color = Amber,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }
                } else {
                    Text("提醒", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Ink)
                    Text(
                        "目前讀不到系統的預設付款應用程式，所以無法在切到 Google 錢包後自動提醒。若之後讀得到，這裡會再出現。",
                        color = Muted,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Text(
                "診斷（系統有沒有接下這個動作，以及讀到的預設付款元件）",
                color = Muted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
            Text(
                diagnostics,
                color = Color(0xFF4A5060),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun Banner(text: String) {
    Text(
        text = text,
        color = Amber,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AmberBg)
            .border(1.dp, Color(0xFFF0D58A), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
private fun Card(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), content = { content() })
    }
}
