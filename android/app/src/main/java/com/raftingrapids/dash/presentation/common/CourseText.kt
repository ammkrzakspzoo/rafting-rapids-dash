package com.raftingrapids.dash.presentation.common

import com.raftingrapids.dash.R
import com.raftingrapids.dash.domain.model.*

private val courseNames = listOf(
    R.string.course_01, R.string.course_02, R.string.course_03, R.string.course_04, R.string.course_05, R.string.course_06, R.string.course_07, R.string.course_08,
    R.string.course_09, R.string.course_10, R.string.course_11, R.string.course_12, R.string.course_13, R.string.course_14, R.string.course_15, R.string.course_16,
    R.string.course_17, R.string.course_18, R.string.course_19, R.string.course_20, R.string.course_21, R.string.course_22, R.string.course_23, R.string.course_24,
    R.string.course_25, R.string.course_26, R.string.course_27, R.string.course_28, R.string.course_29, R.string.course_30, R.string.course_31, R.string.course_32,
    R.string.course_33, R.string.course_34, R.string.course_35, R.string.course_36, R.string.course_37, R.string.course_38, R.string.course_39, R.string.course_40
)

fun Course.nameRes(): Int = courseNames[id - 1]
fun chapterRes(chapter: Int): Int = listOf(R.string.chapter_0, R.string.chapter_1, R.string.chapter_2, R.string.chapter_3, R.string.chapter_4)[chapter]
fun difficultyRes(chapter: Int): Int = listOf(R.string.difficulty_0, R.string.difficulty_1, R.string.difficulty_2, R.string.difficulty_3, R.string.difficulty_4)[chapter]
fun RaftDesign.nameRes(): Int = when (this) {
    RaftDesign.Sunrise -> R.string.raft_sunrise
    RaftDesign.Lagoon -> R.string.raft_lagoon
    RaftDesign.Glacier -> R.string.raft_glacier
    RaftDesign.Sunburst -> R.string.raft_sunburst
}
fun RaftDesign.imageRes(): Int = when (this) {
    RaftDesign.Sunrise -> R.drawable.sprite_1
    RaftDesign.Lagoon -> R.drawable.sprite_2
    RaftDesign.Glacier -> R.drawable.sprite_3
    RaftDesign.Sunburst -> R.drawable.sprite_4
}
fun Flag.labelRes(): Int = when (this) {
    Flag.None -> R.string.flag_none
    Flag.Bronze -> R.string.flag_bronze
    Flag.Silver -> R.string.flag_silver
    Flag.Gold -> R.string.flag_gold
}
