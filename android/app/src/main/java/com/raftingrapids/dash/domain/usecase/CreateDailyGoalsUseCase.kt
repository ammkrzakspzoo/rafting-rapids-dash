package com.raftingrapids.dash.domain.usecase

import com.raftingrapids.dash.domain.model.DailyGoal
import com.raftingrapids.dash.domain.repository.ProgressRepository
import java.time.LocalDate

class CreateDailyGoalsUseCase(private val progress: ProgressRepository) {
    suspend operator fun invoke(date: LocalDate): Boolean = progress.update { current ->
        val key = date.toString()
        if (current.daily.containsKey(key)) current else {
            val day = date.toEpochDay()
            val goal = DailyGoal(key, Math.floorMod(day, current.unlockedThrough.toLong()).toInt() + 1, 20 + 5 * Math.floorMod(day, 3L).toInt(), 2 + Math.floorMod(day, 2L).toInt())
            current.copy(daily = current.daily + (key to goal))
        }
    }
}
