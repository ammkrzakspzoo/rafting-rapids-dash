package com.raftingrapids.dash.domain.model

data class RunResult(
    val runId: String,
    val course: Course,
    val date: String,
    val completed: Boolean,
    val elapsedMillis: Long,
    val paddles: Int,
    val distance: Double,
    val boosts: Int,
    val collisions: Int,
    val flag: Flag,
    val previousBestMillis: Long? = null
)

enum class SaveStatus { Saving, Saved, Error }
