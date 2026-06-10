package com.vi5hnu.curvykids.recognition

/**
 * A single sampled point of a handwriting stroke.
 *
 * @param x horizontal position in canvas pixels.
 * @param y vertical position in canvas pixels.
 * @param t capture time in milliseconds (relative timing is what ML Kit cares about).
 */
data class Point(val x: Float, val y: Float, val t: Long)

/** One continuous pen-down..pen-up stroke, made up of ordered [Point]s. */
data class Stroke(val points: List<Point>)

/**
 * The region the user drew into, in the same coordinate space as the stroke points.
 * Supplying this to the recognizer as a writing-area hint improves accuracy for
 * single-character input. See [Recognizer.recognize].
 */
data class WritingArea(val width: Float, val height: Float)
