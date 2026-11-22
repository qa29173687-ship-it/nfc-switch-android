package tw.nfcswitch

/** 純邏輯：什麼時候該顯示／排程「切回門禁」提醒。方便單元測試。 */
object RemindPolicy {
    val MINUTES = listOf(5, 15, 30)
    const val DEFAULT_MINUTES = 15

    fun clampMinutes(value: Int): Int = if (value in MINUTES) value else DEFAULT_MINUTES

    /** 讀不到系統預設時，不要顯示提醒設定（也無法判斷有沒有切到 Google 錢包）。 */
    fun shouldShowReminder(kind: PaymentKind): Boolean = kind != PaymentKind.UNREADABLE

    /**
     * 從設定頁回來後才檢查：目前變成 Google 錢包，且先前不是，才排程。
     * previous == null 代表還沒讀到過，不算「變成」。
     */
    fun shouldArmOnReturn(remindEnabled: Boolean, previous: PaymentKind?, current: PaymentKind): Boolean =
        remindEnabled && current == PaymentKind.GOOGLE_WALLET && previous != null && previous != PaymentKind.GOOGLE_WALLET

    /** 目前已不是 Google 錢包（且讀得到）→ 取消尚未發出的提醒。 */
    fun shouldCancelOnReturn(current: PaymentKind): Boolean =
        current == PaymentKind.OPPO_WALLET || current == PaymentKind.OTHER

    fun shouldNotify(remindEnabled: Boolean, kindAtFire: PaymentKind): Boolean =
        remindEnabled && (kindAtFire == PaymentKind.GOOGLE_WALLET || kindAtFire == PaymentKind.UNREADABLE)
}
