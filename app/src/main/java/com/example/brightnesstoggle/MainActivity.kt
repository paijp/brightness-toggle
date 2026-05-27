package com.example.brightnesstoggle

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager

class MainActivity : Activity() {

    companion object {
        // 輝度の4段階 (0〜255スケール)
        // 0%=1, 10%=26, 30%=77, 100%=255
        val BRIGHTNESS_STEPS = intArrayOf(1, 26, 77, 255)
        val BRIGHTNESS_LABELS = arrayOf("0%", "10%", "30%", "100%")

        const val PREFS_NAME = "BrightnessTogglePrefs"
        const val KEY_LAST_BRIGHTNESS = "last_brightness"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ロック画面上でも起動できるようにする
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }

        // WRITE_SETTINGS 権限チェック
        if (!Settings.System.canWrite(this)) {
            val intent = android.content.Intent(
                Settings.ACTION_MANAGE_WRITE_SETTINGS,
                android.net.Uri.parse("package:$packageName")
            )
            startActivity(intent)
            finish()
            return
        }

        try {
            // 自動輝度をオフにする
            Settings.System.putInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )

            // 現在の輝度値を読む (0〜255)
            // 読み出し失敗時はSharedPreferencesの最終保存値を使う
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val currentBrightness = try {
                Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            } catch (e: Settings.SettingNotFoundException) {
                prefs.getInt(KEY_LAST_BRIGHTNESS, BRIGHTNESS_STEPS[0])
            }

            // 現在値より大きい最初のステップへ進む（常に増加方向）
            // ステップぴったりの場合も「より大きい」条件から外れるので自動的に次へ進む
            val nextStepIndex = BRIGHTNESS_STEPS.indices.firstOrNull { i ->
                BRIGHTNESS_STEPS[i] > currentBrightness
            } ?: 0  // 最大値以上なら最初(0%)へ折り返す

            val newBrightness = BRIGHTNESS_STEPS[nextStepIndex]

            // 輝度を設定
            Settings.System.putInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                newBrightness
            )

            // 設定値をSharedPreferencesにも常に保存（次回のフォールバック用）
            prefs.edit().putInt(KEY_LAST_BRIGHTNESS, newBrightness).apply()

        } catch (e: Exception) {
            // 失敗時は何もしない
        }

        // UIを表示せず即終了
        finish()
    }
}

