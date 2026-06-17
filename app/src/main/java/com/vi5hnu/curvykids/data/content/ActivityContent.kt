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
    ColorItem("Brown",  0xFF8B5E3C),
    ColorItem("Gray",   0xFF9AA7B2),
    ColorItem("Black",  0xFF2B3A4A),
)

// ── Counting ──────────────────────────────────────────────────────────────────

val COUNT_ITEMS = listOf("🍎", "🐥", "🎈", "⭐", "🐠", "🍓", "🦋", "🌸", "🚗", "🐸")

// ── First Words ───────────────────────────────────────────────────────────────

data class WordEntry(val word: String, val emoji: String)

/**
 * 100+ short words (3–5 letters) covering A–Z, several per letter where possible, each with an
 * emoji. Kept short so the spell-it tiles stay tappable. Grouped by first letter for easy upkeep.
 */
val WORD_LIST = listOf(
    // A
    WordEntry("ANT", "🐜"), WordEntry("APE", "🦍"), WordEntry("ARM", "💪"),
    // B
    WordEntry("BAT", "🦇"), WordEntry("BEE", "🐝"), WordEntry("BUS", "🚌"),
    WordEntry("BED", "🛏️"), WordEntry("BOW", "🎀"), WordEntry("BALL", "⚽"),
    WordEntry("BIRD", "🐦"), WordEntry("BOOK", "📚"), WordEntry("BEAR", "🐻"),
    // C
    WordEntry("CAT", "🐱"), WordEntry("COW", "🐮"), WordEntry("CAR", "🚗"),
    WordEntry("CUP", "🥤"), WordEntry("CAKE", "🍰"), WordEntry("CORN", "🌽"),
    WordEntry("CRAB", "🦀"),
    // D
    WordEntry("DOG", "🐶"), WordEntry("DUCK", "🦆"), WordEntry("DOOR", "🚪"),
    WordEntry("DRUM", "🥁"),
    // E
    WordEntry("EGG", "🥚"), WordEntry("EAR", "👂"), WordEntry("EYE", "👁️"),
    // F
    WordEntry("FOX", "🦊"), WordEntry("FISH", "🐟"), WordEntry("FROG", "🐸"),
    WordEntry("FAN", "🪭"), WordEntry("FIRE", "🔥"), WordEntry("FOOT", "🦶"),
    // G
    WordEntry("GOAT", "🐐"), WordEntry("GIFT", "🎁"), WordEntry("GRAPE", "🍇"),
    // H
    WordEntry("HAT", "🎩"), WordEntry("HEN", "🐔"), WordEntry("HAND", "✋"),
    WordEntry("HOME", "🏠"), WordEntry("HOOK", "🪝"),
    // I
    WordEntry("ICE", "🧊"), WordEntry("INK", "🖋️"),
    // J
    WordEntry("JAM", "🍯"), WordEntry("JET", "✈️"), WordEntry("JAR", "🫙"),
    // K
    WordEntry("KEY", "🔑"), WordEntry("KITE", "🪁"), WordEntry("KING", "🤴"),
    WordEntry("KOALA", "🐨"),
    // L
    WordEntry("LION", "🦁"), WordEntry("LEAF", "🍃"), WordEntry("LEG", "🦵"),
    WordEntry("LAMP", "💡"), WordEntry("LOCK", "🔒"),
    // M
    WordEntry("MAP", "🗺️"), WordEntry("MOON", "🌙"), WordEntry("MILK", "🥛"),
    WordEntry("MASK", "🎭"), WordEntry("MOUSE", "🐭"),
    // N
    WordEntry("NET", "🥅"), WordEntry("NOSE", "👃"), WordEntry("NUT", "🥜"),
    // O
    WordEntry("OWL", "🦉"), WordEntry("OX", "🐂"), WordEntry("ONE", "1️⃣"),
    // P
    WordEntry("PIG", "🐷"), WordEntry("PEN", "🖊️"), WordEntry("PIE", "🥧"),
    WordEntry("PEAR", "🍐"), WordEntry("PLANE", "✈️"),
    // Q
    WordEntry("QUEEN", "👑"),
    // R
    WordEntry("RAT", "🐀"), WordEntry("RING", "💍"), WordEntry("ROSE", "🌹"),
    WordEntry("RAIN", "🌧️"), WordEntry("RICE", "🍚"),
    // S
    WordEntry("SUN", "☀️"), WordEntry("STAR", "⭐"), WordEntry("SOCK", "🧦"),
    WordEntry("SHIP", "🚢"), WordEntry("SNAKE", "🐍"), WordEntry("SHOE", "👟"),
    // T
    WordEntry("TREE", "🌳"), WordEntry("TENT", "⛺"), WordEntry("TAXI", "🚕"),
    WordEntry("TRAIN", "🚂"),
    // U
    WordEntry("UFO", "🛸"), WordEntry("UP", "⬆️"),
    // V
    WordEntry("VAN", "🚐"), WordEntry("VASE", "🏺"),
    // W
    WordEntry("WEB", "🕸️"), WordEntry("WOLF", "🐺"), WordEntry("WAVE", "🌊"),
    WordEntry("WHALE", "🐳"),
    // X / extra
    WordEntry("BOX", "📦"), WordEntry("SIX", "6️⃣"),
    // Y
    WordEntry("YAK", "🐃"), WordEntry("YARN", "🧶"),
    // Z
    WordEntry("ZIP", "🤐"), WordEntry("ZEBRA", "🦓"),
)

// ── Animals ───────────────────────────────────────────────────────────────────

data class Animal(val name: String, val emoji: String, val sound: String, val svg: String? = null)

val ANIMALS = listOf(
    Animal("Lion",     "🦁", "Roar!",   "kids/animals/animal-lion.svg"),     Animal("Dog",   "🐶", "Woof!",   "kids/animals/animal-dog.svg"),
    Animal("Cat",      "🐱", "Meow!",   "kids/animals/animal-cat.svg"),      Animal("Cow",   "🐮", "Moo!",    "kids/animals/animal-cow.svg"),
    Animal("Duck",     "🦆", "Quack!",  "kids/birds/bird-duck.svg"),         Animal("Frog",  "🐸", "Ribbit!", "kids/animals/animal-frog.svg"),
    Animal("Sheep",    "🐑", "Baa!",    "kids/animals/animal-sheep.svg"),    Animal("Horse", "🐴", "Neigh!",  "kids/animals/animal-horse.svg"),
    Animal("Pig",      "🐷", "Oink!",   "kids/animals/animal-pig.svg"),      Animal("Bee",   "🐝", "Buzz!",   "kids/insects/insect-bee.svg"),
    Animal("Bird",     "🐦", "Tweet!",  "kids/birds/bird-bird.svg"),         Animal("Elephant", "🐘", "Trumpet!", "kids/animals/animal-elephant.svg"),
)

// ── Body Parts ────────────────────────────────────────────────────────────────

data class BodyPart(val name: String, val emoji: String, val svg: String? = null)

val BODY_PARTS = listOf(
    BodyPart("Hand",  "✋",  "kids/body/body-hand.svg"),  BodyPart("Eye",   "👁️", "kids/body/body-eye.svg"),
    BodyPart("Ear",   "👂",  "kids/body/body-ear.svg"),   BodyPart("Nose",  "👃", "kids/body/body-nose.svg"),
    BodyPart("Foot",  "🦶",  "kids/body/body-foot.svg"),  BodyPart("Mouth", "👄", "kids/body/body-mouth.svg"),
    BodyPart("Hair",  "💇"),                              BodyPart("Tooth", "🦷", "kids/body/body-tooth.svg"),
    BodyPart("Leg",   "🦵",  "kids/body/body-leg.svg"),   BodyPart("Tongue","👅", "kids/body/body-tongue.svg"),
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

// ── Shared emoji pool for the brain games (Memory, Odd-One-Out, Big/Small, Catch) ───────

val GAME_EMOJIS = listOf(
    // animals
    "🐶", "🐱", "🦁", "🐮", "🐸", "🦆", "🐷", "🐝", "🦋", "🐠",
    "🐢", "🦄", "🐙", "🦀", "🦉", "🐧", "🐬", "🦊", "🐼", "🐨",
    // food
    "🍎", "🍓", "🍌", "🍇", "🍉", "🍊", "🍒", "🥕", "🌽", "🍩",
    // things & nature
    "⭐", "🌸", "🌈", "🚗", "🎈", "🌙", "🌻", "🚀", "⚽", "🎵",
    "🚂", "✈️", "🏀", "🎁", "🪁", "🌼", "🍄", "🌵",
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

// ── Months of the Year ────────────────────────────────────────────────────────

data class Month(val name: String, val emoji: String, val color: Color, val svg: String? = null)

val MONTHS = listOf(
    Month("January",   "❄️", Color(0xFF46A6F0), "kids/days-months/month-january.svg"),
    Month("February",  "💝", Color(0xFFFF8FB6), "kids/days-months/month-february.svg"),
    Month("March",     "🌱", Color(0xFF4FCB94), "kids/days-months/month-march.svg"),
    Month("April",     "🌸", Color(0xFFFF8FB6), "kids/days-months/month-april.svg"),
    Month("May",       "🌻", Color(0xFFFFC24A), "kids/days-months/month-may.svg"),
    Month("June",      "☀️", Color(0xFFFF8B6B), "kids/days-months/month-june.svg"),
    Month("July",      "🏖️", Color(0xFFFF5A52), "kids/days-months/month-july.svg"),
    Month("August",    "🍦", Color(0xFFFFC24A), "kids/days-months/month-august.svg"),
    Month("September", "🍂", Color(0xFFFF9F1C), "kids/days-months/month-september.svg"),
    Month("October",   "🎃", Color(0xFFFF8B6B), "kids/days-months/month-october.svg"),
    Month("November",  "🍁", Color(0xFF8B5E3C), "kids/days-months/month-november.svg"),
    Month("December",  "🎄", Color(0xFF4FCB94), "kids/days-months/month-december.svg"),
)

// ── Seasons & Weather ─────────────────────────────────────────────────────────

data class SeasonItem(val name: String, val emoji: String, val weather: String, val weatherEmojis: List<String>, val color: Color)

val SEASONS_LIST = listOf(
    SeasonItem("Spring", "🌸", "warm and rainy",  listOf("🌧️", "🌈", "🌱"), Color(0xFF4FCB94)),
    SeasonItem("Summer", "☀️", "hot and sunny",   listOf("☀️", "🏖️", "🍦"), Color(0xFFFF8B6B)),
    SeasonItem("Autumn", "🍂", "cool and windy",  listOf("🍂", "🍁", "🌬️"), Color(0xFFFF9F1C)),
    SeasonItem("Winter", "❄️", "cold and snowy",  listOf("❄️", "⛄", "🧣"), Color(0xFF46A6F0)),
)

// ── Emotions / Feelings ───────────────────────────────────────────────────────

data class Emotion(val name: String, val emoji: String, val description: String, val svg: String? = null)

val EMOTIONS = listOf(
    Emotion("Happy",     "😊", "I feel great!", "kids/feelings/feeling-happy.svg"),
    Emotion("Sad",       "😢", "I feel upset",  "kids/feelings/feeling-sad.svg"),
    Emotion("Angry",     "😠", "I feel mad",    "kids/feelings/feeling-angry.svg"),
    Emotion("Surprised", "😲", "Wow!",          "kids/feelings/feeling-surprised.svg"),
    Emotion("Scared",    "😨", "I feel afraid", "kids/feelings/feeling-scared.svg"),
    Emotion("Tired",     "😴", "I need sleep",  "kids/feelings/feeling-sleepy.svg"),
    Emotion("Silly",     "😜", "Hehe!",         "kids/feelings/feeling-silly.svg"),
    Emotion("Excited",   "🤩", "Yay!",          "kids/feelings/feeling-excited.svg"),
)

// ── Vehicles / Transport ──────────────────────────────────────────────────────

data class Vehicle(val name: String, val emoji: String, val sound: String, val svg: String? = null)

val VEHICLES = listOf(
    Vehicle("Car",        "🚗", "Vroom!",     "kids/transport/transport-car.svg"),
    Vehicle("Bus",        "🚌", "Honk!",      "kids/transport/transport-bus.svg"),
    Vehicle("Train",      "🚂", "Choo choo!", "kids/transport/transport-steam-train.svg"),
    Vehicle("Airplane",   "✈️", "Whoosh!",    "kids/transport/transport-airplane.svg"),
    Vehicle("Rocket",     "🚀", "Blast off!", "kids/transport/transport-rocket.svg"),
    Vehicle("Ship",       "🚢", "Splash!",    "kids/transport/transport-ship.svg"),
    Vehicle("Bicycle",    "🚲", "Ring ring!", "kids/transport/transport-bicycle.svg"),
    Vehicle("Helicopter", "🚁", "Whirr!",     "kids/transport/transport-helicopter.svg"),
    Vehicle("Boat",       "⛵", "Sail away!", "kids/transport/transport-sailboat.svg"),
    Vehicle("Ambulance",  "🚑", "Wee woo!",   "kids/transport/transport-ambulance.svg"),
)

// ── Fruits & Vegetables ───────────────────────────────────────────────────────

data class FruitItem(val name: String, val emoji: String, val isFruit: Boolean, val svg: String? = null)

val FRUITS_VEGGIES = listOf(
    FruitItem("Apple",      "🍎", true,  "kids/fruits/fruit-apple.svg"),
    FruitItem("Banana",     "🍌", true,  "kids/fruits/fruit-banana.svg"),
    FruitItem("Grapes",     "🍇", true,  "kids/fruits/fruit-grapes.svg"),
    FruitItem("Strawberry", "🍓", true,  "kids/fruits/fruit-strawberry.svg"),
    FruitItem("Watermelon", "🍉", true,  "kids/fruits/fruit-watermelon.svg"),
    FruitItem("Pineapple",  "🍍", true,  "kids/fruits/fruit-pineapple.svg"),
    FruitItem("Mango",      "🥭", true,  "kids/fruits/fruit-mango.svg"),
    FruitItem("Orange",     "🍊", true,  "kids/fruits/fruit-orange.svg"),
    FruitItem("Carrot",     "🥕", false, "kids/vegetables/veg-carrot.svg"),
    FruitItem("Broccoli",   "🥦", false, "kids/vegetables/veg-broccoli.svg"),
    FruitItem("Corn",       "🌽", false, "kids/vegetables/veg-corn.svg"),
    FruitItem("Potato",     "🥔", false, "kids/vegetables/veg-potato.svg"),
    FruitItem("Tomato",     "🍅", false, "kids/fruits/fruit-tomato.svg"),
    FruitItem("Cucumber",   "🥒", false, "kids/vegetables/veg-cucumber.svg"),
)

// ── Opposites ─────────────────────────────────────────────────────────────────

data class OppositePair(val word1: String, val emoji1: String, val word2: String, val emoji2: String)

val OPPOSITES = listOf(
    OppositePair("Big",    "🐘", "Small",  "🐭"),
    OppositePair("Hot",    "🔥", "Cold",   "🧊"),
    OppositePair("Up",     "⬆️", "Down",   "⬇️"),
    OppositePair("Fast",   "🚀", "Slow",   "🐢"),
    OppositePair("Happy",  "😊", "Sad",    "😢"),
    OppositePair("Day",    "☀️", "Night",  "🌙"),
    OppositePair("Full",   "🍽️", "Empty",  "🫙"),
    OppositePair("Open",   "📖", "Closed", "📕"),
    OppositePair("Loud",   "📣", "Quiet",  "🤫"),
    OppositePair("Long",   "🐍", "Short",  "🐛"),
    OppositePair("Clean",  "🧼", "Dirty",  "🪣"),
    OppositePair("Light",  "🪶", "Heavy",  "🏋️"),
)

// ── Phonics — Letter Sounds ───────────────────────────────────────────────────

/** Each letter paired with its primary phonetic sound, a starter word, and an emoji. */
data class LetterPhonic(val letter: String, val sound: String, val example: String, val emoji: String)

val PHONICS_DATA = listOf(
    LetterPhonic("A", "ahh",  "Apple",     "🍎"),
    LetterPhonic("B", "buh",  "Ball",      "⚽"),
    LetterPhonic("C", "kuh",  "Cat",       "🐱"),
    LetterPhonic("D", "duh",  "Dog",       "🐶"),
    LetterPhonic("E", "ehh",  "Egg",       "🥚"),
    LetterPhonic("F", "fuh",  "Fish",      "🐟"),
    LetterPhonic("G", "guh",  "Goat",      "🐐"),
    LetterPhonic("H", "huh",  "Hat",       "🎩"),
    LetterPhonic("I", "ih",   "Igloo",     "🧊"),
    LetterPhonic("J", "juh",  "Jam",       "🍯"),
    LetterPhonic("K", "kuh",  "Kite",      "🪁"),
    LetterPhonic("L", "luh",  "Lion",      "🦁"),
    LetterPhonic("M", "muh",  "Moon",      "🌙"),
    LetterPhonic("N", "nuh",  "Nest",      "🪺"),
    LetterPhonic("O", "ohh",  "Orange",    "🍊"),
    LetterPhonic("P", "puh",  "Pig",       "🐷"),
    LetterPhonic("Q", "kwuh", "Queen",     "👑"),
    LetterPhonic("R", "ruh",  "Rainbow",   "🌈"),
    LetterPhonic("S", "suh",  "Sun",       "☀️"),
    LetterPhonic("T", "tuh",  "Tree",      "🌳"),
    LetterPhonic("U", "uhh",  "Umbrella",  "☂️"),
    LetterPhonic("V", "vuh",  "Van",       "🚐"),
    LetterPhonic("W", "wuh",  "Wolf",      "🐺"),
    LetterPhonic("X", "ksss", "X-ray",     "🦴"),
    LetterPhonic("Y", "yuh",  "Yarn",      "🧶"),
    LetterPhonic("Z", "zuh",  "Zebra",     "🦓"),
)

// ── Rhyming Word Groups ───────────────────────────────────────────────────────

/** A target word with several rhyming alternatives (word + emoji). */
data class RhymeGroup(val word: String, val emoji: String, val rhymes: List<Pair<String, String>>)

val RHYME_GROUPS = listOf(
    RhymeGroup("CAT",  "🐱", listOf("BAT" to "🦇", "HAT" to "🎩", "MAT" to "🧹", "RAT" to "🐀")),
    RhymeGroup("DOG",  "🐶", listOf("LOG" to "🪵", "FROG" to "🐸", "HOG" to "🐷")),
    RhymeGroup("SUN",  "☀️", listOf("RUN" to "🏃", "FUN" to "🎉", "BUN" to "🍞")),
    RhymeGroup("CAKE", "🎂", listOf("LAKE" to "🏞️", "MAKE" to "🔨", "BAKE" to "👨‍🍳")),
    RhymeGroup("TREE", "🌳", listOf("BEE" to "🐝", "SEE" to "👁️", "KEY" to "🔑")),
    RhymeGroup("STAR", "⭐", listOf("CAR" to "🚗", "JAR" to "🫙", "FAR" to "🔭")),
    RhymeGroup("FISH", "🐟", listOf("DISH" to "🍽️", "WISH" to "🌠")),
    RhymeGroup("BALL", "⚽", listOf("TALL" to "🏀", "WALL" to "🧱", "CALL" to "📞")),
    RhymeGroup("RING", "💍", listOf("KING" to "🤴", "SING" to "🎵", "WING" to "🪶")),
    RhymeGroup("BEAR", "🐻", listOf("HAIR" to "💇", "CHAIR" to "🪑", "PEAR" to "🍐")),
)
