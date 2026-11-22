package tw.nfcswitch

/** 這個 App 自己記下的選擇；不是系統目前的預設感應付款應用程式。 */
object Choice {
    const val WALLET = "wallet"
    const val GOOGLE = "google"

    val REMIND_MINUTES = listOf(5, 15, 30)
    const val DEFAULT_MINUTES = 15

    fun isKnown(value: String?): Boolean = value == WALLET || value == GOOGLE

    fun label(value: String?): String = when (value) {
        WALLET -> "錢包（門禁）"
        GOOGLE -> "Google 錢包（付款）"
        else -> "尚未選擇"
    }

    fun clampMinutes(value: Int): Int = if (value in REMIND_MINUTES) value else DEFAULT_MINUTES
}
