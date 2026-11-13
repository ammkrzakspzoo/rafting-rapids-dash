package com.raftingrapids.dash.domain.model

data class CourseRecord(val flag: Flag, val bestMillis: Long, val mostPaddles: Int)

data class Progress(
    val loading: Boolean = true,
    val error: Boolean = false,
    val unreadable: Boolean = false,
    val unlockedThrough: Int = 1,
    val selectedCourse: Int = 1,
    val selectedRaft: RaftDesign = RaftDesign.Sunrise,
    val records: Map<Int, CourseRecord> = emptyMap(),
    val processedRuns: Set<String> = emptySet(),
    val daily: Map<String, DailyGoal> = emptyMap(),
    val settings: ControlSettings = ControlSettings()
) {
    val goldCount: Int get() = records.values.count { it.flag == Flag.Gold }
    val dailyBadges: Int get() = daily.values.count { it.completed }
}
