package com.vi5hnu.curvykids.data.content

import kotlin.random.Random

/**
 * Chooses the handful of topics shown on the Home screen.
 *
 * Home used to list all 44 topics in one uncategorised two-column wall — 22 rows a four-year-old
 * has to scroll and visually parse before touching anything. A short, changing set is easier to
 * act on, and every topic stays reachable from the grouped Play tab, so nothing is hidden.
 *
 * The order is seeded by the day, so the picks are stable for a whole day (a child pointing at
 * "the lion one" finds it still there a minute later) but different tomorrow.
 *
 * @param lastTopicId The topic already offered by the Continue card, excluded to avoid repeating it.
 * @param seed        Usually the local epoch day.
 */
fun dailyPicks(
    all: List<Topic> = TOPICS,
    lastTopicId: String?,
    seed: Long,
    count: Int = 6,
): List<Topic> {
    if (all.size <= count) return all

    // The order depends only on the day. Excluding the Continue topic is done by filtering the
    // already-shuffled order, never by reshuffling — otherwise every visit to Home dealt a new
    // set of cards, and a child who was reaching for "the lion one" found it gone.
    val random = Random(seed)

    // A tracing topic is always offered: writing is the app's core skill, and a purely random
    // draw regularly produced a Home screen with no writing practice on it at all.
    val writing = all.filter { it.kind == TopicKind.TRACE }.shuffled(random)
    val rest = all.filter { it.kind != TopicKind.TRACE }.shuffled(random)
    val ordered = listOfNotNull(writing.firstOrNull()) + rest + writing.drop(1)

    return ordered.filter { it.id != lastTopicId }.take(count)
}
