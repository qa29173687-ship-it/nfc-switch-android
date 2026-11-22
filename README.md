# NFC 付款切換

給 OPPO 等 Android 手機用的小工具：門禁卡存在「錢包」裡、付款用 Google 錢包時，兩者會搶同一個 NFC。這個 App **不能偷偷改系統預設**，只會一鍵打開系統「預設感應付款」頁，由你選「錢包」或「Google 錢包」。

- 桌面小工具：兩個大按鈕「刷門禁（OPPO 錢包）」「付款（Google 錢包）」，並顯示「上次選擇」（示意；這個 App 無法可靠讀取系統目前的預設）
- 下拉快捷開關「NFC 付款切換」：點一下打開同一個設定頁（加入方式：下拉兩次→編輯→拖曳「NFC 付款切換」）
- App 主畫面：NFC 開／關狀態、開啟 NFC 設定、使用教學、可選的「切到 Google 錢包後提醒切回門禁」（5／15／30 分鐘）
- 資料只存在這支手機，不需帳號、不會連網

這是側載用的 App，沒有上架 Play 商店。第一次安裝時請允許「不明來源」。

Build: `JAVA_HOME=... ANDROID_HOME=... ./gradlew test assembleRelease`。
Release signing reads `/workspace/keys/nfc-switch.properties` (outside the repo, override with `NFC_SWITCH_SIGNING`); the keystore is never committed.
