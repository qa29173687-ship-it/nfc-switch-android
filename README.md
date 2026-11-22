# NFC 付款切換

給 OPPO 等 Android 手機用的小工具：門禁卡存在「錢包」裡、付款用 Google 錢包時，兩者會搶同一個 NFC。這個 App **不能偷偷改系統預設**，只會一鍵打開系統「預設感應付款」頁，由你選「錢包」或「Google 錢包」。

- 桌面小工具／下拉快捷開關：一個「切換 NFC 付款」按鈕，打開同一個設定頁
- 若系統允許讀取，會顯示目前的預設付款應用程式（讀不到就顯示「目前：無法讀取」，不會假裝有「上次選擇」）
- App 主畫面：NFC 開／關狀態、使用教學、診斷資訊
- 若讀得到目前預設，可選「切到 Google 錢包後提醒切回門禁」（5／15／30 分鐘）
- 資料只存在這支手機，不需帳號、不會連網

這是側載用的 App，沒有上架 Play 商店。第一次安裝時請允許「不明來源」。

Build: `JAVA_HOME=... ANDROID_HOME=... ./gradlew test assembleRelease`。
Release signing reads `/workspace/keys/nfc-switch.properties` (outside the repo, override with `NFC_SWITCH_SIGNING`); the keystore is never committed.
