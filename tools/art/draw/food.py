"""Food and drink: each dish on its own, front or three-quarter on, no faces."""
from kit import OL, PALETTE as K, svg, P, L, C, E, R, G, sym, flip, shine, scallop, tube

CATEGORY = "food"

BREAD = "#E8A85A"
CRUST = "#C47A30"


def plate(y=80, rx=40):
    return E(50, y, rx, 9, "#FFFFFF") + E(50, y - 1, rx - 12, 5, "#EEF3FA", 0)


def egg():
    """A whole white egg (also the letter E)."""
    return svg(P("M50 12 C30 12 20 44 20 60 C20 80 34 90 50 90 C66 90 80 80 80 60 C80 44 70 12 50 12Z", "#FFF8EC")
               + shine("M32 44 Q34 32 42 26"))


def fried_egg():
    return svg(P("M20 40 Q24 16 50 20 Q76 14 84 38 Q96 58 78 76 Q60 92 38 82 Q10 76 20 40Z", "#FFFFFF")
               + C(52, 50, 14, "#FFC12E") + shine("M46 44 Q48 40 52 40", 3))


def bread():
    return svg(
        P("M14 46 Q10 22 34 22 Q50 14 66 22 Q90 22 86 46 Q84 52 78 52 L78 84 H22 L22 52 Q16 52 14 46Z", CRUST)
        + P("M28 50 Q22 30 40 30 Q50 24 60 30 Q78 30 72 50 V78 H28Z", "#FCE2B0", 0))


def baguette():
    return svg(
        P("M16 80 Q6 70 18 58 L68 16 Q84 6 90 18 Q94 28 82 38 L32 80 Q24 88 16 80Z", BREAD)
        + L("M30 62 L40 66 M44 50 L54 54 M58 38 L68 42 M72 26 L80 30", CRUST, 3))


def croissant():
    return svg(
        P("M8 66 Q10 52 22 50 L30 44 Q50 22 70 44 L78 50 Q90 52 92 66 Q80 70 72 62 Q50 74 28 62 Q20 70 8 66Z", BREAD)
        + L("M30 46 Q36 58 32 64 M50 30 V68 M70 46 Q64 58 68 64", CRUST, 2.5))


def cheese():
    return svg(
        P("M10 64 L80 30 L90 44 V78 H10Z", "#FFD84D")
        + P("M10 64 H90", "none", 3)
        + P("M10 64 L80 30 L90 44 L90 64Z", "#FFE98A", 3)
        + C(30, 72, 4, "#F2B92A", 0) + C(56, 70, 5, "#F2B92A", 0) + C(76, 76, 3, "#F2B92A", 0) + C(66, 52, 3.5, "#F2B92A", 0))


def burger():
    return svg(
        P("M16 46 Q16 18 50 18 Q84 18 84 46Z", BREAD)
        + "".join(E(x, y, 2, 1.2, "#FFF0D4", 0) for x, y in [(36, 30), (50, 26), (64, 30), (44, 38), (58, 38)])
        + P("M12 50 Q20 44 28 50 Q36 44 44 50 Q52 44 60 50 Q68 44 76 50 Q84 44 90 50 L88 54 H12Z", "#7ED957")
        + R(12, 54, 76, 10, 5, "#8B4A2B")
        + P("M14 64 L86 64 L80 72 L20 72Z", "#FFC93C")
        + P("M16 72 H84 Q84 86 50 86 Q16 86 16 72Z", BREAD))


def hot_dog():
    return svg(
        P("M10 52 Q10 38 24 40 H76 Q90 38 90 52 Q90 70 76 70 H24 Q10 70 10 52Z", BREAD)
        + R(6, 44, 88, 14, 7, "#D9543A")
        + L("M18 50 Q24 46 30 50 Q36 54 42 50 Q48 46 54 50 Q60 54 66 50 Q72 46 80 50", "#FFC93C", 3))


def fries():
    sticks = "".join(R(x, y, 9, 40, 2, "#FFD84D", 2.5, rot=r) for x, y, r in
                     [(30, 18, -10), (40, 12, -3), (50, 14, 4), (60, 18, 10), (45, 22, 0)])
    return svg(sticks + P("M24 42 L76 42 L70 90 L30 90Z", "#F0464B")
               + P("M24 42 Q50 56 76 42", "none", 3))


def pizza():
    return svg(
        P("M50 92 L14 22 Q50 6 86 22Z", "#FFD15C")
        + P("M14 22 Q50 6 86 22 L82 30 Q50 16 18 30Z", CRUST, 2.5)
        + C(48, 36, 6, "#E8463A", 2) + C(60, 54, 6, "#E8463A", 2) + C(40, 58, 5, "#E8463A", 2) + C(50, 76, 4, "#E8463A", 2)
        + E(34, 40, 3, 2, "#5DBB4A", 0) + E(62, 36, 3, 2, "#5DBB4A", 0))


def sandwich():
    return svg(
        P("M12 76 L50 22 L88 76Z", "#FCE2B0")
        + P("M16 70 L84 70 L88 76 L12 76Z", "#7ED957")
        + P("M12 76 H88 V84 H12Z", "#FFC93C")
        + P("M12 84 H88 L88 88 Q50 92 12 88Z", "#FCE2B0")
        + L("M12 76 L50 22 L88 76", CRUST, 4))


def taco():
    return svg(
        P("M12 70 Q12 30 50 28 Q88 30 88 70Z", "#7ED957", 2.5)
        + P("M18 66 Q22 40 44 38 L52 40 Q64 36 80 56 L82 66Z", "#E8463A", 0)
        + "".join(C(x, y, 3, "#FFD84D", 0) for x, y in [(30, 54), (46, 46), (62, 48), (72, 58)])
        + P("M10 72 Q12 30 50 30 Q88 30 90 72 Q76 62 50 62 Q24 62 10 72Z", "#FFC93C", 3, op="0")
        + P("M8 74 Q8 34 50 34 Q92 34 92 74 Q50 52 8 74Z", "none")
        + P("M8 74 Q50 52 92 74 Q92 82 50 82 Q8 82 8 74Z", "#FFC93C"))


def noodles():
    return svg(
        L("M40 50 Q42 20 52 10 M50 50 Q54 24 66 14", "#C47A30", 4)
        + L("M28 52 Q34 44 40 52 Q46 60 52 52 Q58 44 64 52 Q70 60 76 52", "#FFE14D", 4)
        + P("M12 50 H88 Q86 84 50 86 Q14 84 12 50Z", "#F0464B")
        + L("M24 64 H76", "#FFFFFF", 3))


def pasta():
    return svg(
        plate(78, 42)
        + P("M22 66 Q20 40 50 38 Q80 40 78 66Z", "#FFE08A")
        + L("M28 60 Q36 46 44 60 Q52 46 60 60 Q68 46 74 60 M30 50 Q40 40 50 50 Q60 40 70 50", "#E8B850", 2.5)
        + C(40, 46, 6, "#E8463A", 2) + C(58, 48, 6, "#E8463A", 2)
        + L("M76 20 L60 54", "#A9B4C2", 4))


def pancakes():
    return svg(
        plate(84, 42)
        + "".join(P(f"M18 {y} Q18 {y - 8} 50 {y - 8} Q82 {y - 8} 82 {y} Q82 {y + 8} 50 {y + 8} Q18 {y + 8} 18 {y}Z", BREAD)
                  for y in (74, 62, 50))
        + P("M26 46 Q50 36 74 46 Q70 56 60 52 Q56 62 50 54 Q40 60 36 52 Q26 54 26 46Z", "#B5652A", 2.5)
        + R(42, 34, 16, 10, 2, "#FFE98A", 2.5))


def waffle():
    grid = "".join(L(f"M{x} 22 V78", "#C47A30", 2.5) for x in (36, 50, 64)) + \
           "".join(L(f"M22 {y} H78", "#C47A30", 2.5) for y in (36, 50, 64))
    return svg(R(16, 16, 68, 68, 14, "#F2B24A") + grid)


def cake():
    return svg(
        L("M50 18 V30", OL, 3) + P("M50 8 Q56 14 50 20 Q44 14 50 8Z", "#FF9F43", 2.5)
        + R(46, 22, 8, 14, 2, "#4FC3F7", 2.5)
        + R(18, 36, 64, 46, 8, "#FFB3C7")
        + P("M18 44 Q18 36 26 36 H74 Q82 36 82 44 V50 Q76 58 70 50 Q64 58 58 50 Q52 58 46 50 Q40 58 34 50 Q28 58 22 50 Q18 52 18 50Z", "#FFFFFF", 2.5)
        + L("M18 68 H82", "#FF5C8A", 3)
        + E(50, 84, 40, 5, "#FFFFFF"))


def cupcake():
    return svg(
        P(scallop(50, 40, 22, 7, 4, 3.14, 6.28) + " Q72 52 50 52 Q28 52 28 40Z", "#FFB3C7")
        + C(50, 18, 6, "#F0464B")
        + P("M26 50 H74 L66 88 H34Z", "#4FC3F7")
        + L("M38 52 L42 86 M50 52 V86 M62 52 L58 86", "#2E9FD8", 2.5)
        + "".join(R(x, y, 4, 2, 1, c, 0) for x, y, c in [(38, 34, "#FFD84D"), (56, 30, "#7ED957"), (48, 40, "#9B6BFF"), (62, 42, "#FFD84D")]))


def donut():
    return svg(
        C(50, 50, 36, BREAD)
        + P(scallop(50, 50, 28, 12, 2.5), "#FF8FC7", 2.5)
        + C(50, 50, 11, "#EAF7FF")
        + "".join(R(x, y, 6, 2.5, 1, c, 0, rot=r) for x, y, c, r in
                  [(30, 34, "#FFD84D", 30), (60, 28, "#4FC3F7", -20), (68, 56, "#7ED957", 40), (34, 62, "#FFFFFF", -30), (48, 70, "#FFD84D", 10)]))


def cookie():
    return svg(
        C(50, 50, 36, "#E0A85A")
        + "".join(C(x, y, r, "#6B3E22", 0) for x, y, r in [(36, 34, 5), (60, 30, 4), (66, 54, 5), (40, 60, 4), (54, 72, 4), (28, 50, 3)]))


def chocolate():
    grid = "".join(L(f"M{x} 22 V82", "#5A321E", 2.5) for x in (40, 56)) + \
           "".join(L(f"M24 {y} H72", "#5A321E", 2.5) for y in (42, 62))
    return svg(R(24, 22, 48, 60, 6, "#7A4A32") + grid
               + P("M18 50 H78 V90 H18Z", "#F0464B") + L("M18 64 H78", "#FFD84D", 3))


def candy():
    return svg(
        P("M30 50 L10 34 L14 50 L10 66Z", "#FF8FC7") + P("M70 50 L90 34 L86 50 L90 66Z", "#FF8FC7")
        + C(50, 50, 22, "#FF5C8A")
        + L("M36 38 Q50 50 40 66 M50 28 Q62 46 50 72 M64 36 Q56 50 64 66", "#FFFFFF", 3))


def lollipop():
    return svg(
        R(47, 54, 6, 40, 3, "#FFFFFF")
        + C(50, 36, 28, "#FF5C8A")
        + L("M50 36 m0 -4 a4 4 0 1 1 -4 6 a9 9 0 1 1 14 -10 a14 14 0 1 1 -22 12 a19 19 0 1 1 30 -14", "#FFD84D", 4))


def ice_cream():
    return svg(
        P("M30 48 L50 94 L70 48Z", "#E8A85A")
        + L("M38 56 L58 76 M46 52 L64 66 M62 56 L44 76 M54 52 L38 66", "#C47A30", 2)
        + C(50, 30, 16, "#FFB3C7") + C(36, 44, 11, "#FFE98A") + C(64, 44, 11, "#A8E06A")
        + C(52, 13, 5, "#F0464B"))


def popcorn():
    pops = "".join(C(x, y, 9, "#FFF6D8", 2.5) for x, y in [(30, 30), (44, 22), (58, 22), (72, 30), (38, 38), (62, 38), (50, 32)])
    return svg(
        pops
        + P("M22 38 H78 L70 92 H30Z", "#FFFFFF")
        + P("M30 38 H40 L38 92 H32Z M50 38 H60 L58 92 H52Z", "#F0464B", 0)
        + P("M22 38 H78 L70 92 H30Z", "none"))


def milk():
    """A glass of milk (also used for दूध)."""
    return svg(
        P("M28 14 H72 L66 90 H34Z", "#EAF7FF")
        + P("M30 32 H70 L66 90 H34Z", "#FFFFFF", 0)
        + P("M28 14 H72 L66 90 H34Z", "none")
        + L("M30 32 Q40 36 50 32 Q60 28 70 32", OL, 2.5)
        + shine("M38 42 L36 78", 4))


def juice():
    return svg(
        L("M56 6 L52 30", "#F0464B", 5)
        + P("M28 22 H72 L66 90 H34Z", "#EAF7FF")
        + P("M30 36 H70 L66 90 H34Z", "#FF9F43", 0)
        + P("M28 22 H72 L66 90 H34Z", "none")
        + L("M30 36 H70", OL, 2.5)
        + C(72, 24, 12, "#FF9F43") + C(72, 24, 7, "#FFD08A", 0)
        + shine("M38 46 L37 78", 4))


DRAWINGS = {
    "food-egg": egg(), "food-fried-egg": fried_egg(), "food-bread": bread(), "food-baguette": baguette(),
    "food-croissant": croissant(), "food-cheese": cheese(), "food-burger": burger(), "food-hot-dog": hot_dog(),
    "food-fries": fries(), "food-pizza": pizza(), "food-sandwich": sandwich(), "food-taco": taco(),
    "food-noodles": noodles(), "food-pasta": pasta(), "food-pancakes": pancakes(), "food-waffle": waffle(),
    "food-cake": cake(), "food-cupcake": cupcake(), "food-donut": donut(), "food-cookie": cookie(),
    "food-chocolate": chocolate(), "food-candy": candy(), "food-lollipop": lollipop(),
    "food-ice-cream": ice_cream(), "food-popcorn": popcorn(), "food-milk": milk(), "food-juice": juice(),
}
