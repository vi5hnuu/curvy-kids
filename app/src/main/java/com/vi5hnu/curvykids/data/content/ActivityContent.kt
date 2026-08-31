package com.vi5hnu.curvykids.data.content

import androidx.compose.ui.graphics.Color
import com.vi5hnu.curvykids.ui.theme.Teal
import com.vi5hnu.curvykids.ui.theme.Coral
import com.vi5hnu.curvykids.ui.theme.Grape
import com.vi5hnu.curvykids.ui.theme.Blue
import com.vi5hnu.curvykids.ui.theme.Pink
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.Sun
import com.vi5hnu.curvykids.ui.theme.Aqua
import com.vi5hnu.curvykids.ui.theme.TintTeal
import com.vi5hnu.curvykids.ui.theme.TintCoral
import com.vi5hnu.curvykids.ui.theme.TintGrape
import com.vi5hnu.curvykids.ui.theme.TintBlue
import com.vi5hnu.curvykids.ui.theme.TintPink
import com.vi5hnu.curvykids.ui.theme.TintGreen
import com.vi5hnu.curvykids.ui.theme.TintSun
import com.vi5hnu.curvykids.ui.theme.TintAqua

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

/** Things to count. Illustrated, so the child counts pictures rather than font glyphs. */
val COUNT_ITEMS: List<AssetPath> = listOf(
    KidAssets.fruit("apple"), KidAssets.bird("chick"), KidAssets.toy("balloon"),
    KidAssets.weather("star"), KidAssets.seaAnimal("tropical-fish"), KidAssets.fruit("strawberry"),
    KidAssets.insect("butterfly"), KidAssets.nature("blossom"), KidAssets.transport("car"),
    KidAssets.animal("frog"),
)

// ── First Words ───────────────────────────────────────────────────────────────

data class WordEntry(val word: String, val svg: AssetPath)

/**
 * Short words (3–5 letters) spanning A–Z, each with a hand-drawn illustration.
 *
 * Every entry is picture-backed on purpose: a word tile with no image teaches nothing to a child
 * who cannot yet read it. Words whose subject has no artwork were swapped for ones that do
 * rather than shipping a blank tile.
 */
val WORD_LIST = listOf(
    // A
    WordEntry("ANT", KidAssets.insect("ant")), WordEntry("APE", KidAssets.animal("monkey")),
    WordEntry("ARM", KidAssets.body("arm")),
    // B
    WordEntry("BAT", KidAssets.animal("bat")), WordEntry("BEE", KidAssets.insect("bee")),
    WordEntry("BUS", KidAssets.transport("bus")), WordEntry("BED", KidAssets.home("bed")),
    WordEntry("BOW", KidAssets.toy("bow")), WordEntry("BIRD", KidAssets.bird("bird")),
    WordEntry("BEAR", KidAssets.animal("bear")), WordEntry("BOOT", KidAssets.clothes("boot")),
    // C
    WordEntry("CAT", KidAssets.animal("cat")), WordEntry("COW", KidAssets.animal("cow")),
    WordEntry("CAR", KidAssets.transport("car")), WordEntry("CAKE", KidAssets.food("cake")),
    WordEntry("CORN", KidAssets.vegetable("corn")), WordEntry("CRAB", KidAssets.seaAnimal("crab")),
    WordEntry("CHAIR", KidAssets.home("chair")),
    // D
    WordEntry("DOG", KidAssets.animal("dog")), WordEntry("DUCK", KidAssets.bird("duck")),
    WordEntry("DOOR", KidAssets.home("door")), WordEntry("DRUM", KidAssets.instrument("drum")),
    WordEntry("DEER", KidAssets.animal("deer")),
    // E
    WordEntry("EGG", KidAssets.food("egg")), WordEntry("EAR", KidAssets.body("ear")),
    WordEntry("EYE", KidAssets.body("eye")), WordEntry("EAGLE", KidAssets.bird("eagle")),
    // F
    WordEntry("FOX", KidAssets.animal("fox")), WordEntry("FISH", KidAssets.seaAnimal("fish")),
    WordEntry("FROG", KidAssets.animal("frog")), WordEntry("FOOT", KidAssets.body("foot")),
    WordEntry("FIRE", KidAssets.weather("fire")),
    // G
    WordEntry("GOAT", KidAssets.animal("goat")), WordEntry("GIFT", KidAssets.toy("gift")),
    WordEntry("GRAPE", KidAssets.fruit("grapes")), WordEntry("GOOSE", KidAssets.bird("goose")),
    // H
    WordEntry("HAT", KidAssets.clothes("top-hat")), WordEntry("HEN", KidAssets.bird("hen")),
    WordEntry("HAND", KidAssets.body("hand")), WordEntry("HOUSE", KidAssets.place("house")),
    WordEntry("HORSE", KidAssets.animal("horse")),
    // I
    WordEntry("ICE", KidAssets.weather("snowflake")),
    // J
    WordEntry("JET", KidAssets.transport("airplane")), WordEntry("JEANS", KidAssets.clothes("jeans")),
    // K
    WordEntry("KEY", KidAssets.home("key")), WordEntry("KITE", KidAssets.toy("kite")),
    WordEntry("KOALA", KidAssets.animal("koala")), WordEntry("KIWI", KidAssets.fruit("kiwi")),
    // L
    WordEntry("LION", KidAssets.animal("lion")), WordEntry("LEG", KidAssets.body("leg")),
    WordEntry("LEAF", KidAssets.nature("maple-leaf")), WordEntry("LEMON", KidAssets.fruit("lemon")),
    // M
    WordEntry("MOON", KidAssets.weather("moon")), WordEntry("MILK", KidAssets.food("milk")),
    WordEntry("MOUSE", KidAssets.animal("mouse")), WordEntry("MANGO", KidAssets.fruit("mango")),
    // N
    WordEntry("NOSE", KidAssets.body("nose")), WordEntry("NEST", KidAssets.bird("hatching-chick")),
    // O
    WordEntry("OWL", KidAssets.bird("owl")), WordEntry("OX", KidAssets.animal("ox")),
    WordEntry("ONION", KidAssets.vegetable("onion")),
    // P
    WordEntry("PIG", KidAssets.animal("pig")), WordEntry("PEAR", KidAssets.fruit("pear")),
    WordEntry("PLANE", KidAssets.transport("small-plane")), WordEntry("PANDA", KidAssets.animal("panda")),
    WordEntry("PIZZA", KidAssets.food("pizza")),
    // Q
    WordEntry("QUEEN", KidAssets.clothes("crown")),
    // R
    WordEntry("RAT", KidAssets.animal("rat")), WordEntry("ROSE", KidAssets.nature("rose")),
    WordEntry("RAIN", KidAssets.weather("rain")), WordEntry("ROCK", KidAssets.nature("rock")),
    // S
    WordEntry("SUN", KidAssets.weather("sun")), WordEntry("STAR", KidAssets.weather("star")),
    WordEntry("SOCK", KidAssets.clothes("socks")), WordEntry("SHIP", KidAssets.transport("ship")),
    WordEntry("SNAKE", KidAssets.animal("snake")), WordEntry("SHOE", KidAssets.clothes("shoe")),
    // T
    WordEntry("TREE", KidAssets.nature("tree")), WordEntry("TENT", KidAssets.place("tent")),
    WordEntry("TAXI", KidAssets.transport("taxi")), WordEntry("TRAIN", KidAssets.transport("train")),
    WordEntry("TIGER", KidAssets.animal("tiger")),
    // U
    WordEntry("UFO", KidAssets.transport("flying-saucer")),
    // V
    WordEntry("VAN", KidAssets.transport("minibus")), WordEntry("VIOLIN", KidAssets.instrument("violin")),
    // W
    WordEntry("WOLF", KidAssets.animal("wolf")), WordEntry("WAVE", KidAssets.weather("wave")),
    WordEntry("WHALE", KidAssets.seaAnimal("whale")), WordEntry("WORM", KidAssets.insect("worm")),
    // X / extra
    WordEntry("BOX", KidAssets.toy("gift")), WordEntry("SIX", KidAssets.number(6)),
    // Y
    WordEntry("YAK", KidAssets.animal("buffalo")), WordEntry("YOYO", KidAssets.toy("yo-yo")),
    // Z
    WordEntry("ZEBRA", KidAssets.animal("zebra")),
)

// ── Animals ───────────────────────────────────────────────────────────────────

data class Animal(val name: String, val sound: String, val svg: AssetPath)

val ANIMALS = listOf(
    Animal("Lion",  "Roar!",   KidAssets.animal("lion")),
    Animal("Dog",   "Woof!",   KidAssets.animal("dog")),
    Animal("Cat",   "Meow!",   KidAssets.animal("cat")),
    Animal("Cow",   "Moo!",    KidAssets.animal("cow")),
    Animal("Duck",  "Quack!",  KidAssets.bird("duck")),
    Animal("Frog",  "Ribbit!", KidAssets.animal("frog")),
    Animal("Sheep", "Baa!",    KidAssets.animal("sheep")),
    Animal("Horse", "Neigh!",  KidAssets.animal("horse")),
    Animal("Pig",   "Oink!",   KidAssets.animal("pig")),
    Animal("Bee",   "Buzz!",   KidAssets.insect("bee")),
    Animal("Bird",  "Tweet!",  KidAssets.bird("bird")),
    Animal("Elephant", "Trumpet!", KidAssets.animal("elephant")),
)

// ── Body Parts ────────────────────────────────────────────────────────────────

data class BodyPart(val name: String, val svg: AssetPath)

val BODY_PARTS = listOf(
    BodyPart("Hand",  KidAssets.body("hand")),
    BodyPart("Eye",   KidAssets.body("eye")),
    BodyPart("Ear",   KidAssets.body("ear")),
    BodyPart("Nose",  KidAssets.body("nose")),
    BodyPart("Foot",  KidAssets.body("foot")),
    BodyPart("Mouth", KidAssets.body("mouth")),
    // "Hair" was here but has no illustration; "Heart" does, and is just as familiar.
    BodyPart("Heart", KidAssets.body("heart")),
    BodyPart("Tooth", KidAssets.body("tooth")),
    BodyPart("Leg",   KidAssets.body("leg")),
    BodyPart("Tongue", KidAssets.body("tongue")),
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

val GAME_PICTURES: List<AssetPath> = listOf(
    // animals
    KidAssets.animal("dog"), KidAssets.animal("cat"), KidAssets.animal("lion"),
    KidAssets.animal("cow"), KidAssets.animal("frog"), KidAssets.bird("duck"),
    KidAssets.animal("pig"), KidAssets.insect("bee"), KidAssets.insect("butterfly"),
    KidAssets.seaAnimal("tropical-fish"), KidAssets.animal("turtle"), KidAssets.animal("unicorn"),
    KidAssets.seaAnimal("octopus"), KidAssets.seaAnimal("crab"), KidAssets.bird("owl"),
    KidAssets.bird("penguin"), KidAssets.seaAnimal("dolphin"), KidAssets.animal("fox"),
    KidAssets.animal("panda"), KidAssets.animal("koala"),
    // food
    KidAssets.fruit("apple"), KidAssets.fruit("strawberry"), KidAssets.fruit("banana"),
    KidAssets.fruit("grapes"), KidAssets.fruit("watermelon"), KidAssets.fruit("orange"),
    KidAssets.fruit("cherries"), KidAssets.vegetable("carrot"), KidAssets.vegetable("corn"),
    KidAssets.food("donut"),
    // things & nature
    KidAssets.weather("star"), KidAssets.nature("blossom"), KidAssets.weather("rainbow"),
    KidAssets.transport("car"), KidAssets.toy("balloon"), KidAssets.weather("moon"),
    KidAssets.nature("sunflower"), KidAssets.transport("rocket"), KidAssets.sport("soccer"),
    KidAssets.instrument("guitar"), KidAssets.transport("steam-train"), KidAssets.transport("airplane"),
    KidAssets.sport("basketball"), KidAssets.toy("gift"), KidAssets.toy("kite"),
    KidAssets.nature("tulip"), KidAssets.nature("cactus"), KidAssets.toy("teddy-bear"),
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

data class Month(val name: String, val color: Color, val svg: AssetPath)

val MONTHS = listOf(
    Month("January",   Color(0xFF46A6F0), KidAssets.month("january")),
    Month("February",  Color(0xFFFF8FB6), KidAssets.month("february")),
    Month("March",     Color(0xFF4FCB94), KidAssets.month("march")),
    Month("April",     Color(0xFFFF8FB6), KidAssets.month("april")),
    Month("May",       Color(0xFFFFC24A), KidAssets.month("may")),
    Month("June",      Color(0xFFFF8B6B), KidAssets.month("june")),
    Month("July",      Color(0xFFFF5A52), KidAssets.month("july")),
    Month("August",    Color(0xFFFFC24A), KidAssets.month("august")),
    Month("September", Color(0xFFFF9F1C), KidAssets.month("september")),
    Month("October",   Color(0xFFFF8B6B), KidAssets.month("october")),
    Month("November",  Color(0xFF8B5E3C), KidAssets.month("november")),
    Month("December",  Color(0xFF46A6F0), KidAssets.month("december")),
)

// ── Seasons & Weather ─────────────────────────────────────────────────────────

data class SeasonItem(
    val name: String,
    val svg: AssetPath,
    val weather: String,
    /** Picture clues the quiz shows instead of naming the season outright. */
    val weatherSvgs: List<AssetPath>,
    val color: Color,
)

val SEASONS_LIST = listOf(
    SeasonItem("Spring", KidAssets.nature("blossom"), "warm and rainy",
        listOf(KidAssets.weather("rain"), KidAssets.weather("rainbow"), KidAssets.nature("seedling")),
        Color(0xFF4FCB94)),
    SeasonItem("Summer", KidAssets.weather("sun"), "hot and sunny",
        listOf(KidAssets.weather("sun"), KidAssets.weather("thermometer"), KidAssets.food("ice-cream")),
        Color(0xFFFF8B6B)),
    SeasonItem("Autumn", KidAssets.nature("fallen-leaves"), "cool and windy",
        listOf(KidAssets.nature("fallen-leaves"), KidAssets.nature("maple-leaf"), KidAssets.weather("windy")),
        Color(0xFFFF9F1C)),
    SeasonItem("Winter", KidAssets.weather("snowflake"), "cold and snowy",
        listOf(KidAssets.weather("snowflake"), KidAssets.weather("snowman"), KidAssets.clothes("scarf")),
        Color(0xFF46A6F0)),
)

// ── Emotions / Feelings ───────────────────────────────────────────────────────

data class Emotion(val name: String, val description: String, val svg: AssetPath)

val EMOTIONS = listOf(
    Emotion("Happy",     "I feel great!", KidAssets.feeling("happy")),
    Emotion("Sad",       "I feel upset",  KidAssets.feeling("sad")),
    Emotion("Angry",     "I feel mad",    KidAssets.feeling("angry")),
    Emotion("Surprised", "Wow!",          KidAssets.feeling("surprised")),
    Emotion("Scared",    "I feel afraid", KidAssets.feeling("scared")),
    Emotion("Sleepy",    "I need sleep",  KidAssets.feeling("sleepy")),
    Emotion("Silly",     "Hehe!",         KidAssets.feeling("silly")),
    Emotion("Excited",   "Yay!",          KidAssets.feeling("excited")),
)

// ── Vehicles / Transport ──────────────────────────────────────────────────────

data class Vehicle(val name: String, val sound: String, val svg: AssetPath)

val VEHICLES = listOf(
    Vehicle("Car",        "Vroom!",     KidAssets.transport("car")),
    Vehicle("Bus",        "Honk!",      KidAssets.transport("bus")),
    Vehicle("Train",      "Choo choo!", KidAssets.transport("steam-train")),
    Vehicle("Airplane",   "Whoosh!",    KidAssets.transport("airplane")),
    Vehicle("Rocket",     "Blast off!", KidAssets.transport("rocket")),
    Vehicle("Ship",       "Splash!",    KidAssets.transport("ship")),
    Vehicle("Bicycle",    "Ring ring!", KidAssets.transport("bicycle")),
    Vehicle("Helicopter", "Whirr!",     KidAssets.transport("helicopter")),
    Vehicle("Boat",       "Sail away!", KidAssets.transport("sailboat")),
    Vehicle("Ambulance",  "Wee woo!",   KidAssets.transport("ambulance")),
)

// ── Fruits & Vegetables ───────────────────────────────────────────────────────

data class FruitItem(val name: String, val isFruit: Boolean, val svg: AssetPath)

val FRUITS_VEGGIES = listOf(
    FruitItem("Apple",      true,  KidAssets.fruit("apple")),
    FruitItem("Banana",     true,  KidAssets.fruit("banana")),
    FruitItem("Grapes",     true,  KidAssets.fruit("grapes")),
    FruitItem("Strawberry", true,  KidAssets.fruit("strawberry")),
    FruitItem("Watermelon", true,  KidAssets.fruit("watermelon")),
    FruitItem("Pineapple",  true,  KidAssets.fruit("pineapple")),
    FruitItem("Mango",      true,  KidAssets.fruit("mango")),
    FruitItem("Orange",     true,  KidAssets.fruit("orange")),
    FruitItem("Carrot",     false, KidAssets.vegetable("carrot")),
    FruitItem("Broccoli",   false, KidAssets.vegetable("broccoli")),
    FruitItem("Corn",       false, KidAssets.vegetable("corn")),
    FruitItem("Potato",     false, KidAssets.vegetable("potato")),
    FruitItem("Tomato",     false, KidAssets.fruit("tomato")),
    FruitItem("Cucumber",   false, KidAssets.vegetable("cucumber")),
)

// ── Opposites ─────────────────────────────────────────────────────────────────

data class OppositePair(val word1: String, val svg1: AssetPath, val word2: String, val svg2: AssetPath)

/**
 * Opposite pairs, each side illustrated.
 *
 * Pairs whose concept has no artwork (up/down, full/empty, open/closed, loud/quiet,
 * clean/dirty, light/heavy) were replaced with equally concrete pairs that do — an abstract
 * opposite a four-year-old cannot see is not teaching them the idea anyway.
 */
val OPPOSITES = listOf(
    OppositePair("Big",   KidAssets.animal("elephant"),  "Small",  KidAssets.insect("ant")),
    OppositePair("Hot",   KidAssets.weather("fire"),     "Cold",   KidAssets.weather("snowflake")),
    OppositePair("Fast",  KidAssets.transport("rocket"), "Slow",   KidAssets.animal("turtle")),
    OppositePair("Happy", KidAssets.feeling("happy"),    "Sad",    KidAssets.feeling("sad")),
    OppositePair("Day",   KidAssets.weather("sun"),      "Night",  KidAssets.weather("moon")),
    OppositePair("Tall",  KidAssets.nature("palm-tree"), "Short",  KidAssets.nature("seedling")),
    OppositePair("Sweet", KidAssets.food("candy"),       "Sour",   KidAssets.fruit("lemon")),
    OppositePair("Soft",  KidAssets.toy("teddy-bear"),   "Hard",   KidAssets.nature("rock")),
    OppositePair("Land",  KidAssets.animal("lion"),      "Sea",    KidAssets.seaAnimal("dolphin")),
    OppositePair("Awake", KidAssets.feeling("excited"),  "Asleep", KidAssets.feeling("sleepy")),
)

// ── Phonics — Letter Sounds ───────────────────────────────────────────────────

/** Each letter paired with its primary phonetic sound, a starter word, and its picture. */
data class LetterPhonic(val letter: String, val sound: String, val example: String, val svg: AssetPath)

/**
 * Starter words are chosen so every one has artwork; a few differ from the usual primer choices
 * (Igloo, Jam, Nest, Yarn) because those have no illustration in the library.
 */
val PHONICS_DATA = listOf(
    LetterPhonic("A", "ahh",  "Apple",    KidAssets.fruit("apple")),
    LetterPhonic("B", "buh",  "Ball",     KidAssets.sport("soccer")),
    LetterPhonic("C", "kuh",  "Cat",      KidAssets.animal("cat")),
    LetterPhonic("D", "duh",  "Dog",      KidAssets.animal("dog")),
    LetterPhonic("E", "ehh",  "Egg",      KidAssets.food("egg")),
    LetterPhonic("F", "fuh",  "Fish",     KidAssets.seaAnimal("fish")),
    LetterPhonic("G", "guh",  "Goat",     KidAssets.animal("goat")),
    LetterPhonic("H", "huh",  "Hat",      KidAssets.clothes("top-hat")),
    LetterPhonic("I", "ih",   "Insect",   KidAssets.insect("ladybug")),
    LetterPhonic("J", "juh",  "Jet",      KidAssets.transport("airplane")),
    LetterPhonic("K", "kuh",  "Kite",     KidAssets.toy("kite")),
    LetterPhonic("L", "luh",  "Lion",     KidAssets.animal("lion")),
    LetterPhonic("M", "muh",  "Moon",     KidAssets.weather("moon")),
    LetterPhonic("N", "nuh",  "Nose",     KidAssets.body("nose")),
    LetterPhonic("O", "ohh",  "Orange",   KidAssets.fruit("orange")),
    LetterPhonic("P", "puh",  "Pig",      KidAssets.animal("pig")),
    LetterPhonic("Q", "kwuh", "Queen",    KidAssets.clothes("crown")),
    LetterPhonic("R", "ruh",  "Rainbow",  KidAssets.weather("rainbow")),
    LetterPhonic("S", "suh",  "Sun",      KidAssets.weather("sun")),
    LetterPhonic("T", "tuh",  "Tree",     KidAssets.nature("tree")),
    LetterPhonic("U", "uhh",  "Umbrella", KidAssets.weather("umbrella")),
    LetterPhonic("V", "vuh",  "Violin",   KidAssets.instrument("violin")),
    LetterPhonic("W", "wuh",  "Wolf",     KidAssets.animal("wolf")),
    LetterPhonic("X", "ksss", "X-ray",    KidAssets.body("bone")),
    LetterPhonic("Y", "yuh",  "Yo-yo",    KidAssets.toy("yo-yo")),
    LetterPhonic("Z", "zuh",  "Zebra",    KidAssets.animal("zebra")),
)

// ── Rhyming Word Groups ───────────────────────────────────────────────────────

/** A target word with several rhyming alternatives, each with its picture. */
data class RhymeGroup(val word: String, val svg: AssetPath, val rhymes: List<Pair<String, AssetPath>>)

/**
 * Rhyme families. Every word on both sides is illustrated, so groups built around words with no
 * artwork (mat, log, lake, dish, wall, ring) were reworked into families that are fully drawable.
 */
val RHYME_GROUPS = listOf(
    RhymeGroup("CAT", KidAssets.animal("cat"), listOf(
        "BAT" to KidAssets.animal("bat"),
        "HAT" to KidAssets.clothes("top-hat"),
        "RAT" to KidAssets.animal("rat"),
    )),
    RhymeGroup("DOG", KidAssets.animal("dog"), listOf(
        "FROG" to KidAssets.animal("frog"),
        "HOG" to KidAssets.animal("pig"),
    )),
    RhymeGroup("SUN", KidAssets.weather("sun"), listOf(
        "BUN" to KidAssets.food("bread"),
        "ONE" to KidAssets.number(1),
    )),
    RhymeGroup("TREE", KidAssets.nature("tree"), listOf(
        "BEE" to KidAssets.insect("bee"),
        "KEY" to KidAssets.home("key"),
    )),
    RhymeGroup("STAR", KidAssets.weather("star"), listOf(
        "CAR" to KidAssets.transport("car"),
        "GUITAR" to KidAssets.instrument("guitar"),
    )),
    RhymeGroup("BEAR", KidAssets.animal("bear"), listOf(
        "PEAR" to KidAssets.fruit("pear"),
        "CHAIR" to KidAssets.home("chair"),
    )),
    RhymeGroup("GOAT", KidAssets.animal("goat"), listOf(
        "BOAT" to KidAssets.transport("sailboat"),
        "COAT" to KidAssets.clothes("coat"),
    )),
    RhymeGroup("MOUSE", KidAssets.animal("mouse"), listOf(
        "HOUSE" to KidAssets.place("house"),
        "BLOUSE" to KidAssets.clothes("blouse"),
    )),
    RhymeGroup("SNAKE", KidAssets.animal("snake"), listOf(
        "CAKE" to KidAssets.food("cake"),
        "BAKE" to KidAssets.food("cupcake"),
    )),
)

// ── Hindi (Devanagari) varnamala ────────────────────────────────────────────────

/**
 * One Hindi letter for the trace + learn track.
 *
 * @param char       The Devanagari character to draw/recognise (e.g. "अ", "क").
 * @param romanized  ASCII name used for audio fallback and accessibility ("a", "ka").
 * @param example    A starter word beginning with the letter, in Devanagari ("अनानास").
 * @param svg        Asset path of the illustrated badge.
 */
data class HindiLetter(
    val char: String,
    val romanized: String,
    val example: String,
    val svg: AssetPath,
)

/** 13 vowels (स्वर) in traditional order. */
val HINDI_VOWELS = listOf(
    HindiLetter("अ",  "a",  "अनानास", AssetPath("kids/hindi-vowels/hindi-swar-a.svg")),
    HindiLetter("आ",  "aa", "आम", AssetPath("kids/hindi-vowels/hindi-swar-aa.svg")),
    HindiLetter("इ",  "i",  "इंजन", AssetPath("kids/hindi-vowels/hindi-swar-i.svg")),
    HindiLetter("ई",  "ii", "ईंट", AssetPath("kids/hindi-vowels/hindi-swar-ii.svg")),
    HindiLetter("उ",  "u",  "उल्लू", AssetPath("kids/hindi-vowels/hindi-swar-u.svg")),
    HindiLetter("ऊ",  "uu", "ऊँट", AssetPath("kids/hindi-vowels/hindi-swar-uu.svg")),
    HindiLetter("ऋ",  "ri", "ऋषि", AssetPath("kids/hindi-vowels/hindi-swar-ri.svg")),
    HindiLetter("ए",  "e",  "एड़ी", AssetPath("kids/hindi-vowels/hindi-swar-e.svg")),
    HindiLetter("ऐ",  "ai", "ऐनक", AssetPath("kids/hindi-vowels/hindi-swar-ai.svg")),
    HindiLetter("ओ",  "o",  "ओम", AssetPath("kids/hindi-vowels/hindi-swar-o.svg")),
    HindiLetter("औ",  "au", "औज़ार", AssetPath("kids/hindi-vowels/hindi-swar-au.svg")),
    HindiLetter("अं", "am", "अंगूर", AssetPath("kids/hindi-vowels/hindi-swar-am.svg")),
    HindiLetter("अः", "ah", "",   AssetPath("kids/hindi-vowels/hindi-swar-ah.svg")),
)

/** 36 consonants (व्यंजन) in traditional order, including the common conjuncts क्ष त्र ज्ञ. */
val HINDI_CONSONANTS = listOf(
    HindiLetter("क",   "ka",   "कबूतर", AssetPath("kids/hindi-consonants/hindi-vyanjan-ka.svg")),
    HindiLetter("ख",   "kha",  "खरगोश", AssetPath("kids/hindi-consonants/hindi-vyanjan-kha.svg")),
    HindiLetter("ग",   "ga",   "गाय", AssetPath("kids/hindi-consonants/hindi-vyanjan-ga.svg")),
    HindiLetter("घ",   "gha",  "घड़ी", AssetPath("kids/hindi-consonants/hindi-vyanjan-gha.svg")),
    HindiLetter("ङ",   "nga",  "",   AssetPath("kids/hindi-consonants/hindi-vyanjan-nga.svg")),
    HindiLetter("च",   "cha",  "चम्मच", AssetPath("kids/hindi-consonants/hindi-vyanjan-cha.svg")),
    HindiLetter("छ",   "chha", "छाता", AssetPath("kids/hindi-consonants/hindi-vyanjan-chha.svg")),
    HindiLetter("ज",   "ja",   "जहाज", AssetPath("kids/hindi-consonants/hindi-vyanjan-ja.svg")),
    HindiLetter("झ",   "jha",  "झंडा", AssetPath("kids/hindi-consonants/hindi-vyanjan-jha.svg")),
    HindiLetter("ञ",   "nya",  "",   AssetPath("kids/hindi-consonants/hindi-vyanjan-nya.svg")),
    HindiLetter("ट",   "tta",  "टमाटर", AssetPath("kids/hindi-consonants/hindi-vyanjan-tta.svg")),
    HindiLetter("ठ",   "ttha", "ठंडा", AssetPath("kids/hindi-consonants/hindi-vyanjan-ttha.svg")),
    HindiLetter("ड",   "dda",  "डमरू", AssetPath("kids/hindi-consonants/hindi-vyanjan-dda.svg")),
    HindiLetter("ढ",   "ddha", "ढोल", AssetPath("kids/hindi-consonants/hindi-vyanjan-ddha.svg")),
    HindiLetter("ण",   "nna",  "बाण", AssetPath("kids/hindi-consonants/hindi-vyanjan-nna.svg")),
    HindiLetter("त",   "ta",   "तरबूज", AssetPath("kids/hindi-consonants/hindi-vyanjan-ta.svg")),
    HindiLetter("थ",   "tha",  "थाली", AssetPath("kids/hindi-consonants/hindi-vyanjan-tha.svg")),
    HindiLetter("द",   "da",   "दूध", AssetPath("kids/hindi-consonants/hindi-vyanjan-da.svg")),
    HindiLetter("ध",   "dha",  "धनुष", AssetPath("kids/hindi-consonants/hindi-vyanjan-dha.svg")),
    HindiLetter("न",   "na",   "नाव", AssetPath("kids/hindi-consonants/hindi-vyanjan-na.svg")),
    HindiLetter("प",   "pa",   "पतंग", AssetPath("kids/hindi-consonants/hindi-vyanjan-pa.svg")),
    HindiLetter("फ",   "pha",  "फूल", AssetPath("kids/hindi-consonants/hindi-vyanjan-pha.svg")),
    HindiLetter("ब",   "ba",   "बकरी", AssetPath("kids/hindi-consonants/hindi-vyanjan-ba.svg")),
    HindiLetter("भ",   "bha",  "भालू", AssetPath("kids/hindi-consonants/hindi-vyanjan-bha.svg")),
    HindiLetter("म",   "ma",   "मछली", AssetPath("kids/hindi-consonants/hindi-vyanjan-ma.svg")),
    HindiLetter("य",   "ya",   "योग", AssetPath("kids/hindi-consonants/hindi-vyanjan-ya.svg")),
    HindiLetter("र",   "ra",   "रेल", AssetPath("kids/hindi-consonants/hindi-vyanjan-ra.svg")),
    HindiLetter("ल",   "la",   "लोमड़ी", AssetPath("kids/hindi-consonants/hindi-vyanjan-la.svg")),
    HindiLetter("व",   "va",   "वर्षा", AssetPath("kids/hindi-consonants/hindi-vyanjan-va.svg")),
    HindiLetter("श",   "sha",  "शेर", AssetPath("kids/hindi-consonants/hindi-vyanjan-sha.svg")),
    HindiLetter("ष",   "ssa",  "",   AssetPath("kids/hindi-consonants/hindi-vyanjan-ssa.svg")),
    HindiLetter("स",   "sa",   "सेब", AssetPath("kids/hindi-consonants/hindi-vyanjan-sa.svg")),
    HindiLetter("ह",   "ha",   "हाथी", AssetPath("kids/hindi-consonants/hindi-vyanjan-ha.svg")),
    HindiLetter("क्ष", "ksha", "क्षितिज", AssetPath("kids/hindi-consonants/hindi-vyanjan-ksha.svg")),
    HindiLetter("त्र", "tra",  "त्रिशूल", AssetPath("kids/hindi-consonants/hindi-vyanjan-tra.svg")),
    HindiLetter("ज्ञ", "gya",  "ज्ञान", AssetPath("kids/hindi-consonants/hindi-vyanjan-gya.svg")),
)

/** Fast lookup of a Hindi letter by its Devanagari character (used by the trace prompt/audio). */
val HINDI_BY_CHAR: Map<String, HindiLetter> =
    (HINDI_VOWELS + HINDI_CONSONANTS).associateBy { it.char }

// ── Hindi Barakhadi (बारहखड़ी — consonant × the 12 vowel matras) ──────────────────

/**
 * One vowel matra used to form barakhadi: its filename [key] (matches `barakhadi-<cons><key>.svg`)
 * and the combining [sign] appended to a consonant to render the syllable (empty for the base form,
 * which carries the inherent 'a').
 */
data class Matra(val key: String, val sign: String)

/** The 12 matras in traditional barakhadi order (क का कि की कु कू के कै को कौ कं कः). */
val BARAKHADI_MATRAS = listOf(
    Matra("a", ""),  Matra("aa", "ा"), Matra("i", "ि"), Matra("ii", "ी"),
    Matra("u", "ु"), Matra("uu", "ू"), Matra("e", "े"), Matra("ai", "ै"),
    Matra("o", "ो"), Matra("au", "ौ"), Matra("am", "ं"), Matra("ah", "ः"),
)

/** Asset path of the barakhadi badge for a consonant (by its romanized name) and a [Matra]. */

/** Asset path of the illustrated badge for a consonant + matra combination. */
fun barakhadiSvg(consonantRomanized: String, matra: Matra): AssetPath =
    AssetPath("kids/hindi-barakhadi/barakhadi-${consonantRomanized.dropLast(1)}${matra.key}.svg")

/**
 * Accent and tint for a consonant's twelve barakhadi forms, as (color, tint).
 *
 * The badge assets colour each consonant's set differently, cycling through the palette every
 * eight consonants, so the grid reads as a sequence rather than a wall of one colour. This
 * mirrors that rotation so anything drawn alongside or instead of the assets matches them.
 */
fun barakhadiPalette(consonantIndex: Int): Pair<Color, Color> =
    BARAKHADI_PALETTE[consonantIndex.mod(BARAKHADI_PALETTE.size)]

private val BARAKHADI_PALETTE: List<Pair<Color, Color>> = listOf(
    Teal to TintTeal,
    Coral to TintCoral,
    Grape to TintGrape,
    Blue to TintBlue,
    Pink to TintPink,
    Green to TintGreen,
    Sun to TintSun,
    Aqua to TintAqua,
)

/** The Devanagari syllable for a consonant char combined with a [Matra] (e.g. क + ी → की). */
fun barakhadiSyllable(consonantChar: String, matra: Matra): String = consonantChar + matra.sign

// ── Telling Time (o'clock) ──────────────────────────────────────────────────────

/** A clock face showing a whole hour ([hour] o'clock). */
data class TimeItem(val hour: Int, val label: String, val svg: AssetPath)

val TIMES: List<TimeItem> = (1..12).map {
    TimeItem(it, "$it o'clock", AssetPath("kids/telling-time/time-$it.svg"))
}

// ── Fractions ───────────────────────────────────────────────────────────────────

/** A shaded pie illustrating a simple fraction (e.g. "1/2"). [spoken] is the read-aloud form. */
data class FractionItem(val label: String, val spoken: String, val svg: AssetPath)

val FRACTIONS = listOf(
    FractionItem("1/2", "one half",       AssetPath("kids/fractions/fraction-1-2.svg")),
    FractionItem("1/3", "one third",      AssetPath("kids/fractions/fraction-1-3.svg")),
    FractionItem("1/4", "one quarter",    AssetPath("kids/fractions/fraction-1-4.svg")),
    FractionItem("1/5", "one fifth",      AssetPath("kids/fractions/fraction-1-5.svg")),
    FractionItem("1/6", "one sixth",      AssetPath("kids/fractions/fraction-1-6.svg")),
    FractionItem("1/8", "one eighth",     AssetPath("kids/fractions/fraction-1-8.svg")),
    FractionItem("2/3", "two thirds",     AssetPath("kids/fractions/fraction-2-3.svg")),
    FractionItem("3/4", "three quarters", AssetPath("kids/fractions/fraction-3-4.svg")),
)
