package com.raftingrapids.dash.data.repository

import com.raftingrapids.dash.data.local.PreferencesStore
import com.raftingrapids.dash.data.sample.SampleData
import com.raftingrapids.dash.domain.model.Progress
import com.raftingrapids.dash.domain.repository.ProgressRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ProgressRepositoryImpl(private val store: PreferencesStore) : ProgressRepository {
    override val courses = SampleData.courses
    override val rafts = SampleData.rafts
    private val mutex = Mutex()
    private val _progress = MutableStateFlow(Progress())
    override val progress: StateFlow<Progress> = _progress.asStateFlow()

    override suspend fun load(): Boolean = mutex.withLock {
        try {
            val loaded = store.read()
            _progress.update { loaded }
            true
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            _progress.update { it.copy(loading = false, error = true, unreadable = true) }
            false
        }
    }

    override suspend fun update(transform: (Progress) -> Progress): Boolean = mutex.withLock {
        if (_progress.value.unreadable || _progress.value.loading) return@withLock false
        val next = transform(_progress.value).copy(error = false)
        if (next == _progress.value) true else write(next)
    }

    override suspend fun reset(): Boolean = mutex.withLock {
        write(Progress(loading = false, settings = _progress.value.settings))
    }

    private suspend fun write(next: Progress): Boolean {
        return try {
            if (store.write(next)) {
                _progress.update { next }
                true
            } else {
                _progress.update { it.copy(error = true) }
                false
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            _progress.update { it.copy(error = true) }
            false
        }
    }
}
