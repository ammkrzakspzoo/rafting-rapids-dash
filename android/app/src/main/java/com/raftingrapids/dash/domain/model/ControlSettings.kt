package com.raftingrapids.dash.domain.model

enum class ControlMode { Drag, Tilt }

data class ControlSettings(
    val mode: ControlMode = ControlMode.Drag,
    val sensitivity: Int = 1,
    val sound: Boolean = true,
    val haptics: Boolean = false,
    val reducedMotion: Boolean = false
) {
    val multiplier: Double get() = listOf(0.75, 1.0, 1.25)[sensitivity.coerceIn(0, 2)]
}
