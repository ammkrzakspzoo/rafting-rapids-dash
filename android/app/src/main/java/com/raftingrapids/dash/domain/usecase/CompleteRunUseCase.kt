package com.raftingrapids.dash.domain.usecase

import com.raftingrapids.dash.domain.model.*
import com.raftingrapids.dash.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

class CompleteRunUseCase(private val progress: ProgressRepository, private val daily: CreateDailyGoalsUseCase) {
    private val mutex = Mutex()
    private val pending = mutableMapOf<String, RunResult>()
    private val _status = MutableStateFlow<Map<String, SaveStatus>>(emptyMap())
    val status: StateFlow<Map<String, SaveStatus>> = _status.asStateFlow()

    suspend operator fun invoke(result: RunResult) {
        pending[result.runId] = result
        save(result)
    }

    suspend fun retry(runId: String) { pending[runId]?.let { save(it) } }

    suspend fun retryAll() { pending.values.toList().forEach { save(it) } }

    suspend fun resetProgress(): Boolean = mutex.withLock {
        val saved = progress.reset()
        if (saved) { pending.clear(); _status.update { emptyMap() } }
        saved
    }

    private suspend fun save(result: RunResult) = mutex.withLock {
        if (pending[result.runId] != result) return@withLock
        _status.update { it + (result.runId to SaveStatus.Saving) }
        val dailyReady = daily(LocalDate.parse(result.date))
        val saved = dailyReady && progress.update { current ->
            if (result.runId in current.processedRuns) current else {
                val old = current.records[result.course.id]
                val records = if (result.completed) current.records + (result.course.id to CourseRecord(
                    if (old != null && old.flag.ordinal > result.flag.ordinal) old.flag else result.flag,
                    minOf(old?.bestMillis ?: Long.MAX_VALUE, result.elapsedMillis), maxOf(old?.mostPaddles ?: 0, result.paddles)
                )) else current.records
                val goal = current.daily[result.date]
                val updated = goal?.copy(finishes = goal.finishes + if (result.completed && result.course.id == goal.courseId) 1 else 0, paddles = goal.paddles + result.paddles, boosts = goal.boosts + result.boosts)
                current.copy(records = records, processedRuns = current.processedRuns + result.runId,
                    unlockedThrough = if (result.completed) maxOf(current.unlockedThrough, (result.course.id + 1).coerceAtMost(40)) else current.unlockedThrough,
                    daily = if (updated != null) current.daily + (result.date to updated) else current.daily)
            }
        }
        _status.update { it + (result.runId to if (saved) SaveStatus.Saved else SaveStatus.Error) }
        if (saved) pending.remove(result.runId)
    }
}
