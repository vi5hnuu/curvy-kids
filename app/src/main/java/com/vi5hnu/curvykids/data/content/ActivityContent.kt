package com.vi5hnu.curvykids.data.content

import androidx.compose.ui.graphics.Color

// ── Shapes ────────────────────────────────────────────────────────────────────

data class Shape(val id: String, val name: String, val color: Color)

val SHAPES = listOf(
    Shape("circle",    "Circle",    Color(0xFF46A6F0)),
    Shape("square",    "Square",    Color(0xFFFF8B6B)),
    Shape("triangle",  "Triangle",  Color(0xFF4FCB94)),
    Shape("star",      "Star",      Color(0xFFFFC24A)),
    Shape("heart",     "Heart",     Color(0xFFFF8FB6)),
    Shape("diamond",   "Diamond",   Color(0xFFA88BF6)),
    Shape("rectangle", "Rectangle", Color(0xFF1FC2AE)),
    Shape("oval",      "Oval",      Color(0xFFFF5A52)),
    Shape("pentagon",  "Pentagon",  Color(0xFF38CFE0)),
    Shape("hexagon",   "Hexagon",   Color(0xFFFF9F1C)),
)

// ── Colors ────────────────────────────────────────────────────────────────────

data class ColorItem(val name: String, val hex: Long)

val COLOR_ITEMS = listOf(
    ColorItem("Red",    0xFFFF5A52),
    ColorItem("Blue",   0xFF46A6F0),
    ColorItem("Green",  0xFF4FCB94),
    ColorItem("Yellow", 0xFFFFC24A),
    ColorItem("Purple", 0xFFA88BF6),
    ColorItem("Pink",   0xFFFF8FB6),
    ColorItem("Orange", 0xFFFF8B6B),
    ColorItem("Teal",   0xFF1FC2AE),
)

// ── Counting ──────────────────────────────────────────────────────────────────

val COUNT_ITEMS = listOf("🍎", "🐥", "🎈", "⭐", "🐠", "🍓", "🦋", "🌸", "🚗", "🐸")

// ── First Words ───────────────────────────────────────────────────────────────

data class WordEntry(val word: String, val emoji: String)

val WORD_LIST = listOf(
    WordEntry("CAT", "🐱"), WordEntry("DOG", "🐶"), WordEntry("SUN", "☀️"),
    WordEntry("BUS", "🚌"), WordEntry("HAT", "🎩"), WordEntry("COW", "🐮"),
    WordEntry("PIG", "🐷"), WordEntry("BEE", "🐝"), WordEntry("FOX", "🦊"),
    WordEntry("OWL", "🦉"), WordEntry("BAT", "🦇"), WordEntry("HEN", "🐔"),
    WordEntry("CUP", "🥤"), WordEntry("BED", "🛏️"), WordEntry("CAR", "🚗"),
)

// ── Animals ───────────────────────────────────────────────────────────────────

data class Animal(val name: String, val emoji: String, val sound: String)

val ANIMALS = listOf(
    Animal("Lion",     "🦁", "Roar!"),     Animal("Dog",   "🐶", "Woof!"),
    Animal("Cat",      "🐱", "Meow!"),     Animal("Cow",   "🐮", "Moo!"),
    Animal("Duck",     "🦆", "Quack!"),    Animal("Frog",  "🐸", "Ribbit!"),
    Animal("Sheep",    "🐑", "Baa!"),      Animal("Horse", "🐴", "Neigh!"),
    Animal("Pig",      "🐷", "Oink!"),     Animal("Bee",   "🐝", "Buzz!"),
    Animal("Bird",     "🐦", "Tweet!"),    Animal("Elephant", "🐘", "Trumpet!"),
)

// ── Body Parts ────────────────────────────────────────────────────────────────

data class BodyPart(val name: String, val emoji: String)

val BODY_PARTS = listOf(
    BodyPart("Hand",  "✋"), BodyPart("Eye",   "👁️"),
    BodyPart("Ear",   "👂"), BodyPart("Nose",  "👃"),
    BodyPart("Foot",  "🦶"), BodyPart("Mouth", "👄"),
    BodyPart("Hair",  "💇"), BodyPart("Tooth", "🦷"),
    BodyPart("Leg",   "🦵"), BodyPart("Tongue","👅"),
)

// ── Days of the Week ──────────────────────────────────────────────────────────

val DAYS = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

val DAY_COLORS = listOf(
    Color(0xFFFF8B6B), // Sunday  – coral
    Color(0xFFFFC24A), // Monday  – sun
    Color(0xFF4FCB94), // Tuesday – green
    Color(0xFF1FC2AE), // Wednesday – teal
    Color(0xFF46A6F0), // Thursday – blue
    Color(0xFFA88BF6), // Friday  – grape
    Color(0xFFFF8FB6), // Saturday – pink
)

// ── Crayon colors for the drawing canvas ─────────────────────────────────────

val CRAYON_COLORS = listOf(
    Color(0xFF4F6BED), // indigo (default)
    Color(0xFFFF5A52), // red
    Color(0xFF4FCB94), // green
    Color(0xFFFF9F1C), // orange
    Color(0xFFA855F7), // purple
    Color(0xFF1FC2AE), // teal
)
