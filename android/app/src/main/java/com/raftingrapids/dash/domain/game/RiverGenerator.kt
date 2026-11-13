package com.raftingrapids.dash.domain.game

import com.raftingrapids.dash.domain.model.*
import kotlin.math.*

class RiverGenerator {
    fun generate(course: Course): List<RiverEntity> {
        var seed = 4100L + 97L * course.id
        fun next(count: Int): Int {
            seed = seed * 48271L % 2147483647L
            return (seed % count).toInt()
        }
        val result = mutableListOf<RiverEntity>()
        var safe = 2
        var row = 720.0
        var id = 0
        while (row <= course.length - 360.0) {
            val previous = safe
            safe = (safe + next(3) - 1).coerceIn(0, 4)
            val corridorMin = 60.0 + min(previous, safe) * 60.0 - 32.0
            val corridorMax = 60.0 + max(previous, safe) * 60.0 + 32.0
            result += RiverEntity(id++, EntityType.Paddle, 60.0 + safe * 60.0, row - 90.0)
            val lanes = (0..4).filter { it != safe }.toMutableList()
            val used = mutableSetOf<Int>()
            repeat(listOf(1, 1, 2, 2, 3)[course.chapter]) {
                val lane = lanes.removeAt(next(lanes.size))
                val roll = next(100)
                val rockCut = listOf(80, 60, 50, 45, 40)[course.chapter]
                val logCut = listOf(100, 85, 80, 75, 70)[course.chapter]
                val kind = when { roll < rockCut -> EntityType.Rock; roll < logCut -> EntityType.Log; else -> EntityType.Whirlpool }
                val phase = next(2400) / 2400.0 * 2.0 * PI
                fun fits(candidate: Int, type: EntityType): Boolean {
                    if (candidate == safe || candidate in used) return false
                    val extent = when (type) { EntityType.Log -> 56.0; EntityType.Whirlpool -> 46.0; else -> 36.0 }
                    val x = 60.0 + candidate * 60.0
                    return x + extent < corridorMin || x - extent > corridorMax
                }
                val chosen = when {
                    fits(lane, kind) -> lane to kind
                    fits(lane, EntityType.Rock) -> lane to EntityType.Rock
                    else -> (0..4).firstOrNull { fits(it, EntityType.Rock) }?.let { it to EntityType.Rock }
                }
                if (chosen != null) {
                    used += chosen.first
                    result += RiverEntity(id++, chosen.second, 60.0 + chosen.first * 60.0, row, phase)
                }
            }
            row += 360.0
        }
        return result.toList()
    }
}
