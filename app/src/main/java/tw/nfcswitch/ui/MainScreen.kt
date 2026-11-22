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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import tw.nfcswitch.Choice
import tw.nfcswitch.NfcStatus

@Composable
fun MainScreen(
    nfc: NfcStatus,
    lastChoice: String?,
    lastChoiceLabel: String,
    remindEnabled: Boolean,
    remindMinutes: Int,
    canNotify: Boolean,
    diagnostics: String,
    onOpenNfc: () -> Unit,
    onOpenPayment: () -> Unit,
    onPickWallet: () -> Unit,
    onPickGoogle: () -> Unit,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("上次選擇", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    HintTag("示意")
                }
                Text(
                    lastChoiceLabel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = when (lastChoice) {
                        Choice.WALLET -> GreenDeep
                        Choice.GOOGLE -> BlueDeep
                        else -> Ink
                    },
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChoiceBtn(
                        title = "刷門禁",
                        sub = "OPPO 錢包",
                        bg = GreenSoft,
                        fg = GreenDeep,
                        modifier = Modifier.weight(1f),
                        onClick = onPickWallet
                    )
                    ChoiceBtn(
                        title = "付款",
                        sub = "Google 錢包",
                        bg = BlueSoft,
                        fg = BlueDeep,
                        modifier = Modifier.weight(1f),
                        onClick = onPickGoogle
                    )
                }
                TextButton(onClick = onOpenPayment, modifier = Modifier.fillMaxWidth()) {
                    Text("只打開「預設感應付款」設定", fontWeight = FontWeight.Bold, color = GreenDeep)
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
                        Choice.REMIND_MINUTES.forEach { m ->
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
            }

            Text(
                "診斷（系統有沒有接下這個動作，不是設定有沒有改成功）",
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
private fun HintTag(text: String) {
    Text(
        text,
        color = Amber,
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(AmberBg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
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

@Composable
private fun ChoiceBtn(
    title: String,
    sub: String,
    bg: Color,
    fg: Color,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = bg,
        modifier = modifier.height(96.dp)
    ) {
        Column(
            Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, color = fg, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Text(sub, color = fg.copy(alpha = 0.85f), fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
        }
    }
}
