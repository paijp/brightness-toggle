package com.example.brightnesstoggle

import android.app.Activity
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
        // 現在値からステップを探すときの許容誤差
        const val TOLERANCE = 10
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
            val currentBrightness = Settings.System.getInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                128 // 読めない場合は中間値をデフォルトに
            )

            // 現在値より大きい最初のステップを探す（常に増加方向）
            // 例: 24% → 25%、26% → 50%
            // ステップ値ぴったりの場合(誤差TOLERANCE以内)は次のステップへ進む
            val exactMatchIndex = BRIGHTNESS_STEPS.indices.firstOrNull { i ->
                Math.abs(BRIGHTNESS_STEPS[i] - currentBrightness) <= TOLERANCE
            }
            val nextStepIndex = if (exactMatchIndex != null) {
                // ステップ値にぴったり一致 → 次のステップへ
                (exactMatchIndex + 1) % BRIGHTNESS_STEPS.size
            } else {
                // ステップ値の間にある → 現在値より大きい最初のステップへ
                BRIGHTNESS_STEPS.indices.firstOrNull { i ->
                    BRIGHTNESS_STEPS[i] > currentBrightness
                } ?: 0  // 最大値を超えていたら最初(0%)へ折り返す
            }

            val newBrightness = BRIGHTNESS_STEPS[nextStepIndex]
            val label = BRIGHTNESS_LABELS[nextStepIndex]

            // 輝度を設定
            Settings.System.putInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                newBrightness
            )

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
