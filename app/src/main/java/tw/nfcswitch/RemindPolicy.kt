package tw.nfcswitch

/** 純邏輯：什麼時候該排程「切回門禁」提醒。方便單元測試。 */
object RemindPolicy {
    fun shouldArm(remindEnabled: Boolean, lastChoice: String?): Boolean =
        remindEnabled && lastChoice == Choice.GOOGLE
}
