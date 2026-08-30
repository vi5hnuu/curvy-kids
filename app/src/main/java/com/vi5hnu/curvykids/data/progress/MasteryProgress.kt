package com.vi5hnu.curvykids.data.progress

import com.vi5hnu.curvykids.data.content.Script
import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.data.content.TopicKind

/** One progress bar's worth of mastery: [done] of [total] characters learned in [label]. */
data class TraceProgress(val label: String, val done: Int, val total: Int) {
    /** 0f..1f, safe when [total] is zero. */
    val fraction: Float get() = if (total <= 0) 0f else (done.toFloat() / total).coerceIn(0f, 1f)
}

/**
 * Splits the child's mastered characters into one bar per writing system.
 *
 * The totals are derived from [TOPICS] rather than hardcoded, because a hardcoded Latin-only
 * total (62) was being compared against a mastered list that also accumulated Hindi characters,
 * so the bar read impossible values like "80/62" and sat pinned at full.
 *
 * The Hindi bar is only returned once the child has actually practised Hindi, so an
 * English-only learner is not shown a track they will never fill.
 *
 * @param mastered Keys as stored by AppRepository, e.g. "upper:A", "hindi_vowels:अ".
 */
fun traceProgress(mastered: List<String>): List<TraceProgress> {
    val traceTopics = TOPICS.filter { it.kind == TopicKind.TRACE && it.set != null }
    val masteredIds = mastered.mapNotNull { it.substringBefore(':').takeIf { id -> id.isNotEmpty() } }

    return traceTopics
        .groupBy { it.set!!.script }
        .toSortedMap(compareBy { it.ordinal })   // LATIN first, then DEVANAGARI
        .mapNotNull { (script, topics) ->
            val ids = topics.map { it.id }.toSet()
            val done = masteredIds.count { it in ids }
            val total = topics.sumOf { it.set!!.characters.size }
            // Hide a script the child has not started, except Latin which is the default track.
            if (script != Script.LATIN && done == 0) return@mapNotNull null
            TraceProgress(
                label = when (script) {
                    Script.LATIN -> "Letters & Numbers"
                    Script.DEVANAGARI -> "Hindi Letters"
                },
                done = done.coerceAtMost(total),
                total = total,
            )
        }
}
