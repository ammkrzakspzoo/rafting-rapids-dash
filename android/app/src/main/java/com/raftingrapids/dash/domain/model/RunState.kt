package com.raftingrapids.dash.domain.model

enum class RunPhase { Ready, Running, Paused, Finished, Retired }
enum class EntityType { Rock, Log, Whirlpool, Paddle }

data class RiverEntity(val id: Int, val type: EntityType, val x: Double, val distance: Double, val phase: Double = 0.0)

data class RunState(
    val runId: String,
    val course: Course,
    val date: String,
    val raft: RaftDesign,
    val settings: ControlSettings,
    val entities: List<RiverEntity>,
    val phase: RunPhase = RunPhase.Ready,
    val x: Double = 180.0,
    val targetX: Double = 180.0,
    val ticks: Int = 0,
    val distance: Double = 0.0,
    val hull: Int = 3,
    val paddles: Int = 0,
    val charge: Double = 0.0,
    val boostRemaining: Double = 0.0,
    val boosts: Int = 0,
    val collisions: Int = 0,
    val immunity: Double = 0.0,
    val slowRemaining: Double = 0.0,
    val slowFactor: Double = 1.0,
    val spinRemaining: Double = 0.0,
    val bankTime: Double = 0.0,
    val nextBankDamage: Double = 0.2,
    val consumed: Set<Int> = emptySet(),
    val result: RunResult? = null,
    val previousBestMillis: Long? = null,
    val pickupTicks: Map<Int, Int> = emptyMap()
) {
    val elapsedMillis: Long get() = ticks * 1000L / 60L
    val visibleEntities: List<RiverEntity> get() = entities.filter { (it.id !in consumed || pickupTicks[it.id]?.let { tick -> ticks - tick <= 11 } == true) && it.distance - distance in -100.0..1100.0 }
}
