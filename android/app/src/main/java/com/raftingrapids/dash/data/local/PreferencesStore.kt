package com.raftingrapids.dash.data.local

import android.content.Context
import com.raftingrapids.dash.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PreferencesStore(private val context: Context) {
    private val preferences by lazy { context.getSharedPreferences("rafting_rapids_dash", Context.MODE_PRIVATE) }

    suspend fun read(): Progress = withContext(Dispatchers.IO) {
        val raw = preferences.getString("snapshot", null) ?: return@withContext Progress(loading = false)
        val json = JSONObject(raw)
        require(json.getInt("schemaVersion") == 1)
        val settings = json.getJSONObject("settings")
        val records = mutableMapOf<Int, CourseRecord>()
        val recordJson = json.getJSONObject("records")
        recordJson.keys().forEach { key ->
            val id = key.toInt()
            require(id in 1..40)
            val item = recordJson.getJSONObject(key)
            records[id] = CourseRecord(Flag.valueOf(item.getString("flag")), item.getLong("time").also { require(it > 0) }, item.getInt("paddles").also { require(it >= 0) })
        }
        val daily = mutableMapOf<String, DailyGoal>()
        val dailyJson = json.getJSONObject("daily")
        dailyJson.keys().forEach { date ->
            java.time.LocalDate.parse(date)
            val item = dailyJson.getJSONObject(date)
            daily[date] = DailyGoal(date, item.getInt("course").also { require(it in 1..40) }, item.getInt("paddleTarget").also { require(it > 0) }, item.getInt("boostTarget").also { require(it > 0) }, item.getInt("finishes").also { require(it >= 0) }, item.getInt("paddles").also { require(it >= 0) }, item.getInt("boosts").also { require(it >= 0) })
        }
        val processed = json.getJSONArray("processed")
        val unlocked = json.getInt("unlocked").also { require(it in 1..40) }
        val selected = json.getInt("selected").also { require(it in 1..unlocked) }
        Progress(loading = false, unlockedThrough = unlocked, selectedCourse = selected,
            selectedRaft = RaftDesign.valueOf(json.getString("raft")), records = records.toMap(),
            processedRuns = (0 until processed.length()).map { processed.getString(it) }.toSet(), daily = daily.toMap(),
            settings = ControlSettings(ControlMode.valueOf(settings.getString("mode")), settings.getInt("sensitivity").also { require(it in 0..2) }, settings.getBoolean("sound"), settings.getBoolean("haptics"), settings.getBoolean("motion")))
    }

    suspend fun write(progress: Progress): Boolean = withContext(Dispatchers.IO) {
        val records = JSONObject()
        progress.records.forEach { (id, value) -> records.put(id.toString(), JSONObject().put("flag", value.flag.name).put("time", value.bestMillis).put("paddles", value.mostPaddles)) }
        val daily = JSONObject()
        progress.daily.forEach { (date, goal) -> daily.put(date, JSONObject().put("course", goal.courseId).put("paddleTarget", goal.paddleTarget).put("boostTarget", goal.boostTarget).put("finishes", goal.finishes).put("paddles", goal.paddles).put("boosts", goal.boosts)) }
        val settings = progress.settings
        val json = JSONObject().put("schemaVersion", 1).put("unlocked", progress.unlockedThrough).put("selected", progress.selectedCourse).put("raft", progress.selectedRaft.name)
            .put("records", records).put("daily", daily).put("processed", JSONArray(progress.processedRuns.toList()))
            .put("settings", JSONObject().put("mode", settings.mode.name).put("sensitivity", settings.sensitivity).put("sound", settings.sound).put("haptics", settings.haptics).put("motion", settings.reducedMotion))
        preferences.edit().putString("snapshot", json.toString()).commit()
    }
}
