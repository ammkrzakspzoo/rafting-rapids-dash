package com.raftingrapids.dash.domain.model

import kotlin.math.ceil

data class Course(val id: Int, val nameKey: String) {
    val chapter: Int get() = (id - 1) / 8
    val length: Double get() = 7200.0 + 120.0 * (id - 1)
    val goldMillis: Long get() = ceil((42.0 + 17.0 * (id - 1) / 30.0) * 10.0).toLong() * 100L
    val goldPaddles: Int get() = 6 + chapter
}

enum class Flag { None, Bronze, Silver, Gold }
