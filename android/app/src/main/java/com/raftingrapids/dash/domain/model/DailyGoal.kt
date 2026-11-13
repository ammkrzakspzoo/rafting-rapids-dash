package com.raftingrapids.dash.domain.model

data class DailyGoal(
    val date: String,
    val courseId: Int,
    val paddleTarget: Int,
    val boostTarget: Int,
    val finishes: Int = 0,
    val paddles: Int = 0,
    val boosts: Int = 0
) {
    val completed: Boolean get() = finishes > 0 && paddles >= paddleTarget && boosts >= boostTarget
    val completeCount: Int get() = listOf(finishes > 0, paddles >= paddleTarget, boosts >= boostTarget).count { it }
}
