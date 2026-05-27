package com.example.brightnesstoggle

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    companion object {
        // 輝度の5段階 (0〜255スケール)
        // 最低値は1(完全オフは避ける), 25%=64, 50%=128, 75%=191, 100%=255
        val BRIGHTNESS_STEPS = intArrayOf(1, 64, 128, 191, 255)
        val BRIGHTNESS_LABELS = arrayOf("0%", "25%", "50%", "75%", "100%")

        const val PREFS_NAME = "BrightnessTogglePrefs"
        const val KEY_LAST_BRIGHTNESS = "last_brightness"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // WRITE_SETTINGS 権限チェック
        if (!Settings.System.canWrite(this)) {
            val intent = android.content.Intent(
                Settings.ACTION_MANAGE_WRITE_SETTINGS,
                android.net.Uri.parse("package:$packageName")
            )
            startActivity(intent)
            Toast.makeText(this, "「設定の変更を許可」をONにしてください", Toast.LENGTH_LONG).show()
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
            // ステップ値ぴったりの場合も「より大きい」条件から外れるので自動的に次へ進む
            // 例: 現在=64(25%) → 次は128(50%)、現在=26 → 次は64(25%)
            val nextStepIndex = BRIGHTNESS_STEPS.indices.firstOrNull { i ->
                BRIGHTNESS_STEPS[i] > currentBrightness
            } ?: 0  // 最大値以上なら最初(0%)へ折り返す

            val newBrightness = BRIGHTNESS_STEPS[nextStepIndex]
            val label = BRIGHTNESS_LABELS[nextStepIndex]

            // 輝度を設定
            Settings.System.putInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                newBrightness
            )

            // 設定値をSharedPreferencesにも常に保存（次回のフォールバック用）
            prefs.edit().putInt(KEY_LAST_BRIGHTNESS, newBrightness).apply()

            // ウィンドウの輝度にも即座に反映
            val lp = window.attributes
            lp.screenBrightness = newBrightness / 255f
            window.attributes = lp

            // ラベル表示
            val tv = findViewById<TextView>(R.id.tv_brightness)
            tv.text = "輝度: $label"

            Toast.makeText(this, "輝度: $label", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "輝度変更に失敗しました: ${e.message}", Toast.LENGTH_LONG).show()
        }

        // 少し待ってから終了（Toast表示のため）
        window.decorView.postDelayed({ finish() }, 800)
    }
}
