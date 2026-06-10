package com.vi5hnu.curvykids.data.content

/** The learning tracks the child can switch between. */
enum class Level(val label: String) {
    UPPERCASE("ABC"),
    LOWERCASE("abc"),
    NUMBERS("123");

    /** Ordered characters that make up this level. */
    val characters: List<String>
        get() = when (this) {
            UPPERCASE -> ('A'..'Z').map(Char::toString)
            LOWERCASE -> ('a'..'z').map(Char::toString)
            NUMBERS -> ('0'..'9').map(Char::toString)
        }
}
