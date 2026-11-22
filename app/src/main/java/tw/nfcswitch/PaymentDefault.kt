package tw.nfcswitch

import android.content.Context
import android.provider.Settings
import tw.nfcswitch.tile.NfcSwitchTileService
import tw.nfcswitch.widget.WidgetRefresher

enum class PaymentKind { OPPO_WALLET, GOOGLE_WALLET, OTHER, UNREADABLE }

sealed class PaymentDefault {
    abstract val kind: PaymentKind
    abstract val raw: String?
    abstract val statusText: String
    abstract val shortLabel: String
    abstract val diagnosticsLine: String

    data class Known(
        override val raw: String,
        val packageName: String,
        val className: String,
        val label: String,
        override val kind: PaymentKind,
    ) : PaymentDefault() {
        override val statusText: String = "目前：$label"
        override val shortLabel: String = label
        override val diagnosticsLine: String =
            "nfc_payment_default_component=$raw（$label / $packageName）"
    }

    data class Unreadable(val reason: String?) : PaymentDefault() {
        override val kind: PaymentKind = PaymentKind.UNREADABLE
        override val raw: String? = null
        override val statusText: String = "目前：無法讀取"
        override val shortLabel: String = "無法讀取"
        override val diagnosticsLine: String =
            "nfc_payment_default_component=無法讀取" + (reason?.let { "（$it）" } ?: "")
    }
}

object PaymentDefaults {
    const val SECURE_KEY = "nfc_payment_default_component"
    const val GOOGLE_WALLET_PKG = "com.google.android.apps.walletnfcrel"
    const val GMS_PKG = "com.google.android.gms"

    /** 已公開可查到的 OPPO／ColorOS／HeyTap「錢包」套件（門禁卡、交通卡用這個）。 */
    val OPPO_WALLET_PACKAGES = setOf(
        "com.finshell.wallet",
        "com.coloros.wallet",
        "com.heytap.wallet",
    )

    data class Comp(val packageName: String, val className: String)

    /** 解析 Settings.Secure 的元件字串（package/class 或 package/.class），不依賴 ComponentName 以便單元測試。 */
    fun parseComponent(raw: String?): Comp? {
        if (raw.isNullOrBlank()) return null
        val s = raw.trim()
        val idx = s.indexOf('/')
        if (idx <= 0 || idx == s.lastIndex) return null
        val pkg = s.substring(0, idx).trim()
        var cls = s.substring(idx + 1).trim()
        if (pkg.isEmpty() || cls.isEmpty() || pkg.contains(' ')) return null
        if (cls.startsWith('.')) cls = pkg + cls
        return Comp(pkg, cls)
    }

    fun isOppoWalletPackage(packageName: String, appLabel: String? = null): Boolean {
        if (packageName in OPPO_WALLET_PACKAGES) return true
        val vendor = packageName.startsWith("com.finshell.") ||
            packageName.startsWith("com.coloros.") ||
            packageName.startsWith("com.heytap.") ||
            packageName.startsWith("com.oplus.") ||
            packageName.startsWith("com.oppo.")
        val label = appLabel?.trim().orEmpty()
        return vendor && (label == "錢包" || label.equals("Wallet", ignoreCase = true))
    }

    fun isGoogleWallet(packageName: String, className: String?): Boolean {
        if (packageName == GOOGLE_WALLET_PKG) return true
        // 許多手機的 Google 錢包／Google Pay 感應付款服務掛在 Play 服務底下
        return packageName == GMS_PKG && (className?.contains("tapandpay", ignoreCase = true) == true)
    }

    fun friendlyLabel(packageName: String, className: String?, appLabel: String?): String = when {
        isOppoWalletPackage(packageName, appLabel) -> "OPPO 錢包（門禁）"
        isGoogleWallet(packageName, className) -> "Google 錢包"
        !appLabel.isNullOrBlank() -> appLabel
        else -> packageName
    }

    fun kindOf(packageName: String, className: String?, appLabel: String?): PaymentKind = when {
        isOppoWalletPackage(packageName, appLabel) -> PaymentKind.OPPO_WALLET
        isGoogleWallet(packageName, className) -> PaymentKind.GOOGLE_WALLET
        else -> PaymentKind.OTHER
    }

    fun fromRaw(raw: String?, appLabel: String?): PaymentDefault {
        if (raw.isNullOrBlank()) return PaymentDefault.Unreadable("空值")
        val cn = parseComponent(raw) ?: return PaymentDefault.Unreadable("格式無法解析")
        return PaymentDefault.Known(
            raw = raw.trim(),
            packageName = cn.packageName,
            className = cn.className,
            label = friendlyLabel(cn.packageName, cn.className, appLabel),
            kind = kindOf(cn.packageName, cn.className, appLabel),
        )
    }

    fun read(ctx: Context): PaymentDefault {
        val raw = try {
            Settings.Secure.getString(ctx.contentResolver, SECURE_KEY)
        } catch (e: SecurityException) {
            return PaymentDefault.Unreadable(e.javaClass.simpleName)
        } catch (e: Exception) {
            return PaymentDefault.Unreadable(e.javaClass.simpleName)
        }
        val cn = parseComponent(raw)
        val appLabel = cn?.let { labelFor(ctx, it.packageName) }
        return fromRaw(raw, appLabel)
    }

    fun labelFor(ctx: Context, packageName: String): String? = runCatching {
        val pm = ctx.packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
    }.getOrNull()

    fun diagnosticsOf(d: PaymentDefault): String = d.diagnosticsLine

    /** 只更新小工具／快捷開關顯示，不處理提醒。 */
    fun syncUi(ctx: Context) {
        WidgetRefresher.refresh(ctx)
        NfcSwitchTileService.requestUpdate(ctx)
    }

    /**
     * 從系統設定頁返回時：更新畫面，並在「目前預設變成 Google 錢包」時才排程提醒。
     * 讀不到時不假裝有選擇，也不新排程。
     */
    fun onReturnedFromSettings(ctx: Context): PaymentDefault {
        val current = read(ctx)
        val prefs = Prefs(ctx)
        val previousKind = prefs.lastSeenKind()
        if (RemindPolicy.shouldArmOnReturn(prefs.remindEnabled, previousKind, current.kind)) {
            tw.nfcswitch.remind.RemindScheduler.arm(ctx)
        } else if (RemindPolicy.shouldCancelOnReturn(current.kind)) {
            tw.nfcswitch.remind.RemindScheduler.cancel(ctx)
        }
        if (current is PaymentDefault.Known) prefs.setLastSeen(current.packageName, current.kind)
        prefs.lastComponent = current.diagnosticsLine
        syncUi(ctx)
        return current
    }
}
