package com.raftingrapids.dash.domain.game

import com.raftingrapids.dash.core.config.GameConfig
import com.raftingrapids.dash.domain.model.*
import kotlin.math.*

class RapidsEngine {
    fun steer(state: RunState, target: Double): RunState = state.copy(targetX = target.coerceIn(40.0, 320.0))

    fun lane(state: RunState, direction: Int): RunState {
        val lane = ((state.targetX - 60.0) / 60.0).roundToInt()
        return steer(state, 60.0 + (lane + direction).coerceIn(0, 4) * 60.0)
    }

    fun boost(state: RunState): RunState = if (state.phase == RunPhase.Running && state.charge >= 99.99 && state.boostRemaining <= 0.0) {
        state.copy(boostRemaining = 2.0, boosts = state.boosts + 1)
    } else state

    fun entityX(entity: RiverEntity, millis: Long): Double = entity.x + if (entity.type == EntityType.Log) 10.0 * sin(millis / 2400.0 * 2.0 * PI + entity.phase) else 0.0

    fun step(old: RunState): RunState {
        if (old.phase != RunPhase.Running) return old
        val dt = GameConfig.STEP_SECONDS
        val spin = old.spinRemaining > 0.0
        val response = (old.targetX - old.x) * (1.0 - exp(-12.0 * dt)) * if (spin) 0.7 else 1.0
        val spinDelta = if (spin) 18.0 * (sin((1.2 - old.spinRemaining + dt) / 1.2 * 2 * PI) - sin((1.2 - old.spinRemaining) / 1.2 * 2 * PI)) else 0.0
        val x = (old.x + response.coerceIn(-300 * dt, 300 * dt) + spinDelta).coerceIn(40.0, 320.0)
        val factor = ((if (old.slowRemaining > 0) old.slowFactor else 1.0) * (if (spin) 0.85 else 1.0) * (if (old.boostRemaining > 0) 1.4 else 1.0)).coerceAtLeast(0.5)
        val travel = GameConfig.BASE_SPEED * factor * dt
        var state = old.copy(
            x = x, ticks = old.ticks + 1, distance = old.distance + travel,
            immunity = (old.immunity - dt).coerceAtLeast(0.0),
            slowRemaining = (old.slowRemaining - dt).coerceAtLeast(0.0),
            spinRemaining = (old.spinRemaining - dt).coerceAtLeast(0.0),
            boostRemaining = (old.boostRemaining - dt).coerceAtLeast(0.0),
            charge = if (old.boostRemaining > 0) ((old.boostRemaining - dt).coerceAtLeast(0.0) / 2.0 * 100.0) else old.charge
        )
        fun damage(factorValue: Double, duration: Double): RunState = state.copy(
            hull = state.hull - 1, collisions = state.collisions + 1, immunity = 0.85,
            slowFactor = factorValue, slowRemaining = duration
        )
        for (entity in old.entities) {
            if (entity.id in state.consumed) continue
            val ex = entityX(entity, state.elapsedMillis)
            val forwardRadius = when (entity.type) { EntityType.Log -> 23.0; EntityType.Whirlpool -> 46.0; EntityType.Rock -> 36.0; EntityType.Paddle -> 34.0 }
            if (entity.distance + forwardRadius < old.distance || entity.distance - forwardRadius > state.distance) continue
            val nearX = ex.coerceIn(min(old.x, state.x), max(old.x, state.x))
            val dx = abs(ex - nearX)
            val hit = if (entity.type == EntityType.Log) dx <= 46.0 else {
                val nearD = entity.distance.coerceIn(old.distance, state.distance)
                hypot(dx, entity.distance - nearD) <= forwardRadius
            }
            if (!hit) continue
            when (entity.type) {
                EntityType.Paddle -> state = state.copy(paddles = state.paddles + 1, charge = if (state.boostRemaining > 0) state.charge else (state.charge + 20).coerceAtMost(100.0), consumed = state.consumed + entity.id, pickupTicks = state.pickupTicks + (entity.id to state.ticks))
                EntityType.Whirlpool -> {
                    state = state.copy(spinRemaining = if (state.spinRemaining <= 0) 1.2 else state.spinRemaining, consumed = state.consumed + entity.id)
                }
                EntityType.Rock, EntityType.Log -> if (state.immunity <= 0.0) {
                    state = damage(if (entity.type == EntityType.Rock) 0.65 else 0.8, if (entity.type == EntityType.Rock) 0.6 else 0.4)
                    if (entity.type == EntityType.Log) {
                        val sign = if (state.x > ex) 1 else if (state.x < ex) -1 else if (state.x < 180) 1 else if (state.x > 180) -1 else 1
                        state = state.copy(x = (state.x + 36 * sign).coerceIn(40.0, 320.0))
                    }
                    state = state.copy(consumed = state.consumed + entity.id)
                }
            }
        }
        if (state.x <= 42 || state.x >= 318) {
            state = state.copy(bankTime = old.bankTime + dt)
            if (state.bankTime + 0.000001 >= state.nextBankDamage && state.immunity <= 0) {
                state = damage(0.65, 0.6).copy(nextBankDamage = state.nextBankDamage + 1.0)
            }
        } else state = state.copy(bankTime = 0.0, nextBankDamage = 0.2)
        if (state.hull <= 0 || state.elapsedMillis >= 90000 || state.distance >= state.course.length) {
            val completed = state.hull > 0 && state.distance >= state.course.length
            val flag = when {
                !completed -> Flag.None
                state.elapsedMillis <= state.course.goldMillis && state.paddles >= state.course.goldPaddles -> Flag.Gold
                state.elapsedMillis <= state.course.goldMillis + 8000 && state.paddles >= 3 + state.course.chapter -> Flag.Silver
                else -> Flag.Bronze
            }
            val result = RunResult(state.runId, state.course, state.date, completed, state.elapsedMillis, state.paddles, state.distance.coerceAtMost(state.course.length), state.boosts, state.collisions, flag, state.previousBestMillis)
            state = state.copy(phase = if (completed) RunPhase.Finished else RunPhase.Retired, result = result)
        }
        return state
    }
}
