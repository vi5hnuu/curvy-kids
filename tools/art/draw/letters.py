"""
Example pictures for the letter cards: the picture always shows the word the child hears.

English letters follow PHONICS_DATA in data/content/ActivityContent.kt (A apple, B ball ...);
uppercase-A and lowercase-a-low show the same picture. Hindi letters follow HINDI_VOWELS and
HINDI_CONSONANTS there (अ अनानास, क कबूतर ...). Pictures that already exist in another category
are reused from that module, so an apple is the same apple everywhere; the words with no picture
elsewhere (ईंट, ऋषि, ओम, त्रिशूल ...) are drawn here.
"""
import math
from kit import clip, OL, PALETTE as K, svg, P, L, C, E, R, G, sym, flip, eye, eyes, cheek, cheeks, smile, shine, tube
from people import SKIN, HAIR, head, body, dot_eyes, closed_eyes, cheeks2, mouth_smile
import animals, birds, sea, insects, fruits, food, clothes, toys, instruments, sports, transport, home, nature, weather, body as body_parts

# ── English: letter -> the PHONICS_DATA picture ──────────────────────────────────────────────

PHONICS = {
    "A": fruits.DRAWINGS["fruit-apple"], "B": sports.DRAWINGS["sport-soccer"], "C": animals.DRAWINGS["animal-cat"],
    "D": animals.DRAWINGS["animal-dog"], "E": food.DRAWINGS["food-egg"], "F": sea.DRAWINGS["sea-fish"],
    "G": animals.DRAWINGS["animal-goat"], "H": clothes.DRAWINGS["clothes-top-hat"], "I": insects.DRAWINGS["insect-ladybug"],
    "J": sea.DRAWINGS["sea-jellyfish"], "K": toys.DRAWINGS["toy-kite"], "L": animals.DRAWINGS["animal-lion"],
    "M": weather.DRAWINGS["weather-moon"], "N": body_parts.DRAWINGS["body-nose"], "O": sea.DRAWINGS["sea-octopus"],
    "P": animals.DRAWINGS["animal-pig"], "Q": clothes.DRAWINGS["clothes-crown"], "R": weather.DRAWINGS["weather-rainbow"],
    "S": weather.DRAWINGS["weather-sun"], "T": nature.DRAWINGS["nature-tree"], "U": weather.DRAWINGS["weather-umbrella"],
    "V": instruments.DRAWINGS["instrument-violin"], "W": animals.DRAWINGS["animal-wolf"], "X": animals.DRAWINGS["animal-fox"],
    "Y": toys.DRAWINGS["toy-yo-yo"], "Z": animals.DRAWINGS["animal-zebra"],
}


# ── Hindi words with no picture in another category ─────────────────────────────────────────

def brick():
    """ईंट: one red brick, drawn as a block so it reads as solid."""
    return svg(P("M10 46 L30 30 H90 L70 46Z", "#E07A5A") + P("M70 46 L90 30 V62 L70 78Z", "#A84A34")
               + R(10, 46, 60, 32, 2, "#C85A40")
               + E(28, 38, 6, 3, "#A84A34", 2) + E(52, 38, 6, 3, "#A84A34", 2))


def sage():
    """ऋषि: an old wise man with a white beard and top knot, sitting calmly."""
    return svg(
        P("M14 92 Q14 66 50 64 Q86 66 86 92Z", "#FF9F43")
        + C(50, 14, 7, "#F4F1F8")
        + head(SKIN[2], "#F4F1F8", "bald", 50, 38, 20)
        + P("M32 42 Q30 72 50 80 Q70 72 68 42 Q60 54 50 52 Q40 54 32 42Z", "#F4F1F8")
        + closed_eyes(50, 38, 7, up=False) + L("M42 30 H46 M54 30 H58", "#F4F1F8", 3)
        + smile(50, 48, 3) + L("M50 26 V32", "#F0464B", 2.5)
        + C(50, 80, 3, "#8B3A2A", 2) + C(42, 78, 3, "#8B3A2A", 2) + C(58, 78, 3, "#8B3A2A", 2))


def heel():
    """एड़ी: a foot from the side with the heel ringed."""
    s = SKIN[1]
    return svg(
        P("M30 8 H52 V50 Q52 58 60 62 L84 68 Q94 72 92 80 Q90 88 80 88 H30 Q16 88 18 74 Q20 62 30 56Z", s)
        + "".join(C(x, 80, 3.5, s, 2) for x in (86, 80))
        + R(28, 6, 26, 14, 4, "#4FC3F7")
        + C(28, 76, 13, "none", 0) + L("M20 64 A14 14 0 1 0 38 88", "#F0464B", 4)
        + P("M10 60 L18 66 L8 70Z", "#F0464B", 2))


def om():
    """ओम: the ॐ symbol drawn as a thick saffron stroke on a round sun-coloured badge."""
    return svg(
        C(50, 54, 40, "#FFF0D4")
        + L("M26 36 Q34 28 42 34 Q46 42 36 48 Q50 46 50 60 Q50 76 34 74 Q26 72 24 66", "#FF7A1A", 7)
        + L("M42 48 Q58 40 66 52 Q72 64 62 72 Q70 80 80 72", "#FF7A1A", 7)
        + L("M58 22 Q70 32 82 22", "#FF7A1A", 5) + C(70, 14, 4, "#FF7A1A", 0))


def tools():
    """औज़ार: a hammer and a spanner crossed."""
    return svg(
        G(R(46, 30, 8, 62, 3, "#C47A30") + R(30, 14, 40, 18, 4, "#A9B4C2"), "rotate(-35 50 50)")
        + G(R(46, 22, 8, 64, 3, "#7D8A9A")
            + P("M40 14 Q40 4 50 4 Q60 4 60 14 L56 22 H44Z M46 4 V12 H54 V4", "#A9B4C2")
            + P("M44 86 Q44 96 50 96 Q56 96 56 86Z", "#A9B4C2"), "rotate(35 50 50)"))


def pigeon():
    """कबूतर: a grey pigeon with a green-purple neck."""
    return svg(
        birds.legs(48, 58, 74, 9, "#FF7A90")
        + P("M70 58 L92 66 L88 76 L66 68Z", "#7D8A9A")
        + E(54, 56, 26, 20, "#A9B4C2")
        + C(36, 40, 15, "#A9B4C2")
        + P("M28 50 Q36 58 48 52 Q46 44 40 44Z", "#6FB59A", 0, op=".8")
        + P("M56 50 Q72 46 78 58 Q66 68 54 62Z", "#8E9AAB")
        + L("M62 54 L70 52 M60 60 L70 60", OL, 2)
        + P("M22 38 L12 42 L22 45Z", "#5E5A6E")
        + eye(32, 37, 3.6, ring=False) + cheek(37, 45, 3.6, 2.4))


def spoon():
    """चम्मच: a spoon."""
    return svg(G(E(50, 26, 16, 22, "#C9D6E4") + E(50, 26, 10, 15, "#E4ECF5", 0) + R(46, 46, 8, 46, 4, "#C9D6E4"),
                 "rotate(25 50 50)"))


def flag():
    """झंडा: the Indian tricolour on a pole."""
    navy = "#2E3A9C"
    spokes = "".join(L(f"M56 39 L{56 + 6 * math.cos(a):.1f} {39 + 6 * math.sin(a):.1f}", navy, 1)
                     for a in [i * math.pi / 6 for i in range(12)])
    return svg(R(18, 8, 6, 86, 3, "#A9B4C2") + C(21, 8, 4, "#FFC93C", 2)
               + R(24, 16, 64, 15, 0, "#FF9933", 0) + R(24, 31, 64, 16, 0, "#FFFFFF", 0) + R(24, 47, 64, 15, 0, "#138808", 0)
               + R(24, 16, 64, 46, 3, "none")
               + L("M50 39 a6 6 0 1 0 12 0 a6 6 0 1 0 -12 0", navy, 1.6) + spokes)


def ice():
    """ठंडा: cold, shown as an ice cube with frost and a shiver."""
    return svg(P("M22 34 L44 22 L80 32 L58 46Z", "#E4F6FF") + P("M58 46 L80 32 V68 L58 84Z", "#8ED8FF")
               + P("M22 34 L58 46 V84 L22 70Z", "#BDEBFF")
               + shine("M30 44 L30 60", 3)
               + weather.flakes([(14, 18), (88, 14), (90, 86)], 4))


def arrow():
    """बाण: an arrow."""
    return svg(G(R(14, 47, 66, 6, 3, "#C47A30") + P("M78 40 L94 50 L78 60Z", "#A9B4C2")
                 + P("M8 40 L24 40 L30 50 L24 60 L8 60 L14 50Z", "#F0464B"), "rotate(-30 50 50)"))


def bow():
    """धनुष: a bow with its string and an arrow ready."""
    return svg(L("M26 10 Q80 50 26 90", "#C47A30", 0) + tube("M26 10 Q80 50 26 90", "#C47A30", 5)
               + L("M26 10 V90", OL, 2)
               + R(20, 47, 64, 5, 2, "#E0A85A", 2) + P("M82 42 L94 49.5 L82 57Z", "#A9B4C2", 2.5)
               + P("M14 42 L24 42 L28 49.5 L24 57 L14 57 L18 49.5Z", "#F0464B", 2))


def thali():
    """थाली: a round steel plate with little bowls of food."""
    return svg(C(50, 52, 42, "#D7DEE8") + C(50, 52, 34, "#EEF3FA", 2.5)
               + C(34, 40, 10, "#C9D2DD") + C(34, 40, 7, "#FFC93C", 0)
               + C(60, 34, 10, "#C9D2DD") + C(60, 34, 7, "#F0642D", 0)
               + C(66, 60, 10, "#C9D2DD") + C(66, 60, 7, "#7ED957", 0)
               + E(40, 66, 14, 8, "#FFFFFF") + C(36, 66, 1.5, "#E4ECF5", 0)
               + P("M46 46 Q50 42 56 46 Q52 52 46 46Z", "#F2C98A", 2))


def yoga():
    """योग: a child sitting cross-legged with hands on knees, eyes closed."""
    return svg(
        E(50, 88, 40, 6, "#9B6BFF")
        + P("M14 84 Q20 70 50 70 Q80 70 86 84Z", "#4FC3F7")
        + P("M34 48 Q30 72 36 76 H64 Q70 72 66 48Z", "#FF8FC7")
        + tube("M36 52 Q24 64 22 78 M64 52 Q76 64 78 78", SKIN[2], 6)
        + C(22, 78, 4, SKIN[2], 2) + C(78, 78, 4, SKIN[2], 2)
        + head(SKIN[2], HAIR["black"], "bun", 50, 32, 16)
        + closed_eyes(50, 33, 6, up=False) + mouth_smile(50, 38, 3) + cheeks2(50, 37, 10))


def horizon():
    """क्षितिज: the sun half-risen where the sky meets the sea."""
    return svg(R(6, 10, 88, 50, 6, "#FFE2B8", 0)
               + clip("sky", R(0, 0, 100, 60, 0, "#000", 0), weather.sun_shape(50, 60, 20, face=False))
               + P("M6 60 H94 V86 Q94 92 88 92 H12 Q6 92 6 86Z", "#4FC3F7", 0)
               + L("M6 60 H94", OL, 3)
               + L("M20 70 H40 M56 76 H80 M30 82 H46", "#FFFFFF", 3))


def trident():
    """त्रिशूल: a trident with a small damru tied below its prongs."""
    return svg(R(47, 30, 6, 64, 3, "#C47A30")
               + P("M50 4 L58 22 L54 22 V34 H46 V22 L42 22Z", "#A9B4C2")
               + P("M30 10 Q26 30 46 36 L46 30 Q36 28 36 14Z", "#A9B4C2")
               + P("M70 10 Q74 30 54 36 L54 30 Q64 28 64 14Z", "#A9B4C2")
               + G(P("M-8 -8 L8 -8 L2 0 L8 8 L-8 8 L-2 0Z", "#E89A3A", 2.5), "translate(50 48) scale(1.2)")
               + R(44, 34, 12, 4, 2, "#F0464B", 2))


def knowledge():
    """ज्ञान: an open book with a glowing light bulb above it."""
    return svg(
        L("M50 4 V10 M30 12 L34 17 M70 12 L66 17 M22 30 H28 M72 30 H78", "#FFC93C", 3)
        + P("M50 14 C38 14 34 24 36 32 C38 38 44 40 44 46 H56 C56 40 62 38 64 32 C66 24 62 14 50 14Z", "#FFE14D")
        + R(44, 46, 12, 6, 2, "#A9B4C2", 2.5)
        + P("M50 64 Q32 56 10 60 V88 Q32 84 50 92Z", "#FFFFFF") + P("M50 64 Q68 56 90 60 V88 Q68 84 50 92Z", "#FFFFFF")
        + L("M50 64 V92", OL, 3)
        + L("M18 68 Q30 66 42 70 M18 76 Q30 74 42 78 M58 70 Q70 66 82 68 M58 78 Q70 74 82 76", "#A9B4C2", 2))


# ── Hindi: file key -> the picture of the spoken word ────────────────────────────────────────

HINDI_VOWEL_PICTURES = {
    "a": fruits.DRAWINGS["fruit-pineapple"],      # अनानास
    "aa": fruits.DRAWINGS["fruit-mango"],         # आम
    "i": transport.DRAWINGS["transport-steam-train"],  # इंजन
    "ii": brick(),                                # ईंट
    "u": birds.DRAWINGS["bird-owl"],              # उल्लू
    "uu": animals.DRAWINGS["animal-camel"],       # ऊँट
    "ri": sage(),                                 # ऋषि
    "e": heel(),                                  # एड़ी
    "ai": clothes.DRAWINGS["clothes-glasses"],    # ऐनक
    "o": om(),                                    # ओम
    "au": tools(),                                # औज़ार
    "am": fruits.DRAWINGS["fruit-grapes"],        # अंगूर
}

HINDI_CONSONANT_PICTURES = {
    "ka": pigeon(),                               # कबूतर
    "kha": animals.DRAWINGS["animal-rabbit"],     # खरगोश
    "ga": animals.DRAWINGS["animal-cow"],         # गाय
    "gha": home.DRAWINGS["home-clock"],           # घड़ी
    "cha": spoon(),                               # चम्मच
    "chha": weather.DRAWINGS["weather-umbrella"], # छाता
    "ja": transport.DRAWINGS["transport-ship"],   # जहाज
    "jha": flag(),                                # झंडा
    "tta": fruits.DRAWINGS["fruit-tomato"],       # टमाटर
    "ttha": ice(),                                # ठंडा
    "dda": svg(instruments.damru().split('data-bare="1">')[1].replace("</svg>", "")),  # डमरू
    "ddha": instruments.dhol(),                   # ढोल
    "nna": arrow(),                               # बाण
    "ta": fruits.DRAWINGS["fruit-watermelon"],    # तरबूज
    "tha": thali(),                               # थाली
    "da": food.DRAWINGS["food-milk"],             # दूध
    "dha": bow(),                                 # धनुष
    "na": transport.DRAWINGS["transport-canoe"],  # नाव
    "pa": toys.DRAWINGS["toy-kite"],              # पतंग
    "pha": nature.DRAWINGS["nature-hibiscus"],    # फूल
    "ba": animals.DRAWINGS["animal-goat"],        # बकरी
    "bha": animals.DRAWINGS["animal-bear"],       # भालू
    "ma": sea.DRAWINGS["sea-fish"],               # मछली
    "ya": yoga(),                                 # योग
    "ra": transport.DRAWINGS["transport-train"],  # रेल
    "la": animals.DRAWINGS["animal-fox"],         # लोमड़ी
    "va": weather.DRAWINGS["weather-rain"],       # वर्षा
    "sha": animals.DRAWINGS["animal-lion"],       # शेर
    "sa": fruits.DRAWINGS["fruit-apple"],         # सेब
    "ha": animals.DRAWINGS["animal-elephant"],    # हाथी
    "ksha": horizon(),                            # क्षितिज
    "tra": trident(),                             # त्रिशूल
    "gya": knowledge(),                           # ज्ञान
}

CATEGORIES = {
    "uppercase": {f"uppercase-{k}": v for k, v in PHONICS.items()},
    "lowercase": {f"lowercase-{k.lower()}-low": v for k, v in PHONICS.items()},
    "hindi-vowels": {f"hindi-swar-{k}": v for k, v in HINDI_VOWEL_PICTURES.items()},
    "hindi-consonants": {f"hindi-vyanjan-{k}": v for k, v in HINDI_CONSONANT_PICTURES.items()},
}
