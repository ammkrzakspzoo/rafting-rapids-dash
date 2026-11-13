package com.raftingrapids.dash.domain.model

enum class RaftDesign { Sunrise, Lagoon, Glacier, Sunburst }

fun RaftDesign.isUnlocked(progress: Progress): Boolean = when (this) {
    RaftDesign.Sunrise -> true
    RaftDesign.Lagoon -> progress.records.size >= 5
    RaftDesign.Glacier -> progress.goldCount >= 10
    RaftDesign.Sunburst -> progress.goldCount >= 40
}
