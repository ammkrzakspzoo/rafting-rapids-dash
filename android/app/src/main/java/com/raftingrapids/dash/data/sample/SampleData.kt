package com.raftingrapids.dash.data.sample

import com.raftingrapids.dash.domain.model.Course
import com.raftingrapids.dash.domain.model.RaftDesign

object SampleData {
    val courses = listOf(
        "springwater_start", "pebble_bend", "willow_wake", "otter_crossing", "fernbank_float", "split_stone", "foam_ribbon", "clearwater_gate",
        "pine_needle_drift", "fallen_cedar", "kingfisher_run", "twin_log_bend", "mossy_channel", "paddle_pocket", "amber_eddy", "pinebend_passage",
        "slate_steps", "boulder_ballet", "echo_narrows", "whirlpool_window", "silver_spray", "crooked_canyon", "rock_garden", "gorge_escape",
        "misty_morning", "rainbow_reach", "hidden_current", "drifting_giants", "spray_spiral", "lantern_bend", "cloudwater_chute", "mistfall_crossing",
        "thunderhead_run", "five_rock_rush", "roaring_channel", "copper_current", "double_eddy", "last_log_leap", "goldwater_sprint", "rapids_crown"
    ).mapIndexed { index, name -> Course(index + 1, name) }
    val rafts = RaftDesign.entries.toList()
    val challengeKeys = listOf("finish_featured_river", "collect_daily_paddles", "use_daily_boosts")
}
