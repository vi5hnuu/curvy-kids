package com.vi5hnu.curvykids.billing

import com.vi5hnu.curvykids.audio.packs.PackState
import com.vi5hnu.curvykids.audio.packs.VoiceReadiness
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/** Something a free user may find locked: an activity, a Barakhadi letter, or a story. */
sealed interface Lockable {
    /** Stable id, used to remember what was opened for today. */
    val key: String

    data class Activity(val topicId: String) : Lockable {
        override val key get() = "topic:$topicId"
    }

    /** A consonant in the Barakhadi strip, by its place in HINDI_CONSONANTS. */
    data class BarakhadiLetter(val index: Int) : Lockable {
        override val key get() = "consonant:$index"
    }

    /** A story, by its place in STORIES. */
    data class Story(val index: Int) : Lockable {
        override val key get() = "story:$index"
    }
}

/** The mark a screen draws on an item, from [ContentLocks.markOf]. */
sealed interface LockMark {
    /** Free, or Pro is owned, and its voices are here: no mark. */
    data object NONE : LockMark

    /** Whether Pro is owned isn't known yet: a shimmer where the mark will go, never a guess. */
    data object LOADING : LockMark

    /** Part of Pro and not opened: a padlock. */
    data object LOCKED : LockMark

    /** Opened for today by a video: a play mark. */
    data object OPEN_TODAY : LockMark

    /** Open, and its voices are downloading: a ring filling up ([progress] 0–1, 0 until known). */
    data class GettingReady(val progress: Float) : LockMark

    /** Open, but its voices can't download without a grown-up (offline, Play asks, it failed). */
    data object NEEDS_GROWN_UP : LockMark
}

/**
 * What is open right now, as one value screens can read without knowing why.
 *
 * @param owned       CurvyKids Pro is unlocked: everything is open.
 * @param openedToday Keys of items a free user opened just for today (see [Lockable.key]).
 * @param ready       Whether Pro is owned is known. Before that (the first moments after start)
 *                    Pro items count as locked, so nothing opens by mistake, but no padlock is
 *                    shown, so a Pro owner never sees one flash.
 */
data class AccessState(
    val owned: Boolean = false,
    val openedToday: Set<String> = emptySet(),
    val ready: Boolean = false,
) {
    fun isLocked(item: Lockable): Boolean = !owned && !ProCatalog.isFree(item) && item.key !in openedToday

    /**
     * The one rule for what to draw on [item]. Until [ready], a Pro item shows [LockMark.LOADING],
     * so a Pro owner never sees a padlock (or a "today" mark) flash before ownership is read.
     */
    fun markOf(item: Lockable): LockMark = when {
        owned || ProCatalog.isFree(item) -> LockMark.NONE
        !ready -> LockMark.LOADING
        item.key in openedToday -> LockMark.OPEN_TODAY
        else -> LockMark.LOCKED
    }
}

/**
 * The one place that decides whether something is locked, combining the free/Pro split
 * ([ProCatalog]), whether Pro is owned ([ProRepository]) and what was opened for today. Screens
 * read [state] and never ask billing (or anything else) themselves.
 *
 * @param openedToday Keys opened for today; nothing by default.
 */
class Access(
    pro: ProRepository,
    scope: CoroutineScope,
    openedToday: Flow<Set<String>> = flowOf(emptySet()),
) {
    val state: StateFlow<AccessState> = combine(pro.state, openedToday) { p, today ->
        AccessState(owned = p.owned, openedToday = today, ready = p.known)
    }.stateIn(scope, SharingStarted.Eagerly, AccessState())
}

/**
 * Items as a screen sees them: whether one is locked, whether its voices are here, and what to do
 * on a tap. A locked item goes to [onLocked] (the caller shows the "this one is for Pro" sheet);
 * an open one whose voices aren't downloaded yet goes to [onNeedsVoices], which fetches them in
 * the background and runs the item's opening once they're in. So Pro recordings are only ever
 * fetched for an item the child may open. [NONE] leaves everything open and ready.
 *
 * Kept for as long as [access] and [voices] don't change (the caller remembers it), so screens
 * given the same one skip redrawing.
 */
class ContentLocks(
    val access: AccessState,
    val voices: VoiceReadiness = VoiceReadiness.ALL_READY,
    val onNeedsVoices: (item: Lockable, open: () -> Unit) -> Unit = { _, open -> open() },
    val onLocked: (Lockable) -> Unit,
) {
    /**
     * What to draw on [item]: the lock's mark ([AccessState.markOf]); for an item the child may
     * open, a download in progress or one needing a grown-up shows instead. An item whose voices
     * simply haven't been fetched yet looks as usual until it is tapped.
     */
    fun markOf(item: Lockable): LockMark {
        val lock = access.markOf(item)
        if (lock != LockMark.NONE && lock != LockMark.OPEN_TODAY) return lock
        return when (val v = voices.stateOf(item)) {
            is PackState.Downloading -> LockMark.GettingReady(v.progress)
            is PackState.Failed, PackState.NeedsConfirmation -> LockMark.NEEDS_GROWN_UP
            else -> lock
        }
    }

    /**
     * Runs [open] when [item] is open and its voices are here; fetches them first when they
     * aren't ([onNeedsVoices]); reports a locked one. While ownership is still being read, a tap
     * on a Pro item does nothing rather than guess.
     */
    fun open(item: Lockable, open: () -> Unit) {
        when {
            access.isLocked(item) -> if (access.ready) onLocked(item)
            !voices.isReady(item) -> onNeedsVoices(item, open)
            else -> open()
        }
    }

    companion object {
        val NONE = ContentLocks(AccessState(owned = true, ready = true)) { }
    }
}
