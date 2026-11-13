package com.raftingrapids.dash.core.feedback

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import com.raftingrapids.dash.domain.model.ControlSettings

class PlatformFeedback(context: Context) {
    private val vibrator = context.getSystemService(Vibrator::class.java)
    private var tone: ToneGenerator? = null
    private var lastPickup = 0L

    fun play(kind: Int, settings: ControlSettings) {
        val now = SystemClock.elapsedRealtime()
        if (kind == 0 && now - lastPickup < 150) return
        if (kind == 0) lastPickup = now
        if (settings.sound) {
            try {
                val generator = tone ?: ToneGenerator(AudioManager.STREAM_MUSIC, 45).also { tone = it }
                generator.startTone(if (kind == 2) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_BEEP, when (kind) { 0 -> 40; 1 -> 70; else -> 120 })
            } catch (_: RuntimeException) { tone?.release(); tone = null }
        }
        if (kind == 3 && settings.haptics && vibrator?.hasVibrator() == true) {
            vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    fun release() { tone?.release(); tone = null; vibrator?.cancel() }
}
