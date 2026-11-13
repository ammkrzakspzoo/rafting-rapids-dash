package com.raftingrapids.dash.core.input

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import kotlin.math.abs
import kotlin.math.sign

class TiltInputAdapter(context: Context) : SensorEventListener {
    private val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val available: Boolean get() = sensor != null
    private var callback: ((Double) -> Unit)? = null
    private var rotation = Surface.ROTATION_0
    private var neutral: Double? = null
    private var filtered = 0.0
    private var owner: String? = null

    fun start(runId: String, displayRotation: Int, onTilt: (Double) -> Unit) {
        stop()
        owner = runId
        rotation = displayRotation
        callback = onTilt
        neutral = null
        filtered = 0.0
        sensor?.let { manager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    fun stop(runId: String? = null) {
        if (runId != null && owner != runId) return
        manager.unregisterListener(this)
        callback = null
        owner = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        val axis = when (rotation) {
            Surface.ROTATION_90 -> -event.values[1].toDouble()
            Surface.ROTATION_180 -> event.values[0].toDouble()
            Surface.ROTATION_270 -> event.values[1].toDouble()
            else -> -event.values[0].toDouble()
        }
        if (neutral == null) neutral = axis
        filtered += 0.15 * (axis - (neutral ?: axis) - filtered)
        val value = if (abs(filtered) < 0.4) 0.0 else sign(filtered) * (abs(filtered) - 0.4) / 3.0
        callback?.invoke(value.coerceIn(-1.0, 1.0))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
