"""Nature: plants, flowers and trees, each standing on its own."""
import math
from kit import OL, svg, P, L, C, E, R, G, sym, flip, shine, tube, scallop
from fruits import leaf, LEAF

CATEGORY = "nature"

TRUNK = "#A0663D"


def petals(cx, cy, n, length, width, fill, rot0=0, w=2.5):
    return "".join(G(E(cx, cy - length, width, length, fill, w), f"rotate({rot0 + i * 360 / n} {cx} {cy})") for i in range(n))


def flower(cx, cy, fill, centre="#FFC93C", n=6, size=1.0):
    return petals(cx, cy, n, 9 * size, 6 * size, fill) + C(cx, cy, 6 * size, centre, 2.5)


def tree():
    """A round leafy tree (also the letter T)."""
    return svg(P("M44 92 L46 58 L38 50 M46 62 L54 50", "none", 0)
               + P("M42 92 Q46 74 44 56 H56 Q54 74 58 92Z", TRUNK)
               + P(scallop(50, 40, 28, 10, 4), "#5DBB4A")
               + C(36, 34, 4, "#F0464B", 2) + C(60, 28, 4, "#F0464B", 2) + C(62, 50, 4, "#F0464B", 2)
               + shine("M30 30 Q34 20 44 16"))


def pine_tree():
    return svg(R(44, 76, 12, 16, 2, TRUNK)
               + P("M50 6 L70 34 H62 L78 56 H68 L86 78 H14 L32 56 H22 L38 34 H30Z", "#3E9C5A"))


def palm_tree():
    return svg(tube("M56 92 Q48 60 54 28", TRUNK, 8)
               + L("M50 82 H60 M50 70 H58 M50 58 H58 M51 46 H58", OL, 2)
               + "".join(P(d, LEAF) for d in [
                   "M54 28 Q34 10 12 26 Q32 22 54 30Z", "M54 28 Q74 10 94 26 Q72 22 54 30Z",
                   "M54 28 Q30 30 22 52 Q38 34 54 30Z", "M54 28 Q78 30 84 52 Q68 34 54 30Z",
                   "M54 28 Q50 8 60 4 Q58 18 56 30Z"])
               + C(50, 32, 4, "#8B5A3C", 2) + C(58, 32, 4, "#8B5A3C", 2))


def cactus():
    return svg(R(26, 80, 48, 14, 3, "#E0784A")
               + P("M40 80 V20 Q40 10 50 10 Q60 10 60 20 V80Z", "#5DBB4A")
               + P("M40 54 H30 Q22 54 22 46 V34 Q22 28 27 28 Q32 28 32 34 V44 H40Z", "#5DBB4A")
               + P("M60 46 H70 Q78 46 78 38 V28 Q78 22 73 22 Q68 22 68 28 V36 H60Z", "#5DBB4A")
               + L("M50 22 V74", "#3E9C3E", 2)
               + C(50, 10, 5, "#FF8FC7", 2))


def seedling():
    return svg(P("M22 76 Q22 66 50 66 Q78 66 78 76 L72 92 H28Z", "#B5794A")
               + L("M50 68 V40", "#5DBB4A", 4)
               + P("M50 44 Q30 24 18 34 Q28 50 50 46Z", LEAF) + P("M50 40 Q66 18 82 26 Q76 44 50 42Z", LEAF))


def herb():
    return svg(L("M50 92 V24 M50 74 L32 60 M50 58 L68 44 M50 42 L34 30", "#3E9C3E", 3)
               + "".join(G(P("M0 0 Q8 -10 18 -4 Q8 6 0 0Z", "#7ED957", 2.5), f"translate({x} {y}) rotate({r})")
                         for x, y, r in [(32, 60, 200), (68, 44, -20), (34, 30, 210), (50, 24, -80), (50, 74, 20), (50, 58, 160)]))


def clover():
    leafl = lambda r: G(P("M50 50 C40 40 36 26 46 22 Q50 22 50 28 Q50 22 54 22 C64 26 60 40 50 50Z", "#5DBB4A"), f"rotate({r} 50 50)")
    return svg(tube("M50 50 Q54 76 66 92", "#3E9C3E", 3) + leafl(0) + leafl(90) + leafl(180) + leafl(270))


def shamrock():
    leafl = lambda r: G(P("M50 50 C40 40 36 26 46 22 Q50 22 50 28 Q50 22 54 22 C64 26 60 40 50 50Z", "#3ECF5A"), f"rotate({r} 50 50)")
    return svg(tube("M50 50 Q50 76 42 92", "#2E9C3E", 3) + leafl(0) + leafl(120) + leafl(240))


def maple_leaf():
    return svg(L("M50 70 V94", "#B5401E", 4)
               + P("M50 70 L46 60 L30 66 L34 56 L14 46 L22 42 L16 30 L32 32 L34 24 L44 34 L42 14 L50 20 L58 14 L56 34 "
                   "L66 24 L68 32 L84 30 L78 42 L86 46 L66 56 L70 66 L54 60Z", "#F0642D")
               + L("M50 70 V24 M50 52 L28 40 M50 52 L72 40", "#B5401E", 2))


def fallen_leaves():
    lf = lambda x, y, r, c: G(P("M0 0 Q14 -16 32 -6 Q16 10 0 0Z", c) + L("M0 0 L28 -6", OL, 1.6), f"translate({x} {y}) rotate({r})")
    return svg(lf(14, 40, -10, "#F0642D") + lf(40, 70, 20, "#FFC93C") + lf(52, 34, 30, "#C2662A") + lf(24, 84, -30, "#F0464B"))


def rose():
    return svg(L("M50 50 Q46 72 52 94", "#3E9C3E", 4) + leaf(48, 74, 200, 1.1) + leaf(51, 66, -30, 1.1)
               + P("M30 34 Q28 10 50 12 Q72 10 70 34 Q70 54 50 54 Q30 54 30 34Z", "#F0464B")
               + L("M42 22 Q50 16 58 24 Q56 36 46 34 Q40 30 46 26 M36 32 Q40 46 54 44", "#B82E3A", 2.5))


def tulip():
    return svg(L("M50 50 V94", "#3E9C3E", 4) + P("M50 92 Q30 80 28 58 Q46 66 50 84Z", LEAF)
               + P("M30 20 L40 30 L50 14 L60 30 L70 20 Q74 50 50 54 Q26 50 30 20Z", "#FF5C8A"))


def daisy():
    return svg(L("M50 56 Q46 76 50 94", "#3E9C3E", 4) + leaf(48, 78, 200, 1.1)
               + petals(50, 38, 12, 14, 6, "#FFFFFF") + C(50, 38, 9, "#FFC93C", 2.5))


def sunflower():
    return svg(L("M50 60 V94", "#3E9C3E", 5) + leaf(50, 80, 200, 1.3) + leaf(51, 72, -20, 1.3)
               + petals(50, 36, 14, 16, 6, "#FFC93C") + C(50, 36, 14, "#8B5A3C", 2.5)
               + "".join(C(50 + 6 * math.cos(a), 36 + 6 * math.sin(a), 1.4, "#5A321E", 0) for a in [i * math.pi / 3 for i in range(6)]))


def blossom():
    return svg(flower(50, 50, "#FFB3C7", "#FF5C8A", 5, 2.6) + "".join(L(f"M50 50 L{50 + 12 * math.cos(a):.1f} {50 + 12 * math.sin(a):.1f}", "#FF5C8A", 2)
                                                                    for a in [i * math.pi / 2.5 - math.pi / 2 for i in range(5)]))


def hibiscus():
    return svg(flower(50, 50, "#F0464B", "#B82E3A", 5, 2.8)
               + tube("M50 50 L70 26", "#FFC93C", 2) + C(71, 25, 4, "#FFC93C", 2))


def lotus():
    return svg(E(50, 82, 36, 7, "#5DBB4A")
               + P("M50 76 Q24 70 14 46 Q34 50 50 76Z M50 76 Q76 70 86 46 Q66 50 50 76Z", "#FFB3C7")
               + P("M50 76 Q30 60 32 36 Q46 50 50 76Z M50 76 Q70 60 68 36 Q54 50 50 76Z", "#FF8FC7")
               + P("M50 76 Q38 50 50 20 Q62 50 50 76Z", "#FFB3C7"))


def bouquet():
    return svg(flower(34, 32, "#FF5C8A") + flower(64, 30, "#FFC93C", "#FF9F43") + flower(50, 18, "#9B6BFF")
               + leaf(30, 50, 200, 1.0) + leaf(68, 48, -20, 1.0)
               + P("M30 46 H70 L56 92 H44Z", "#4FC3F7")
               + P("M44 62 L36 72 L50 66 L64 72 L56 62Z", "#FF5C8A", 2.5))


def wheat():
    grain = lambda x, y, r: E(x, y, 4, 7, "#F2C14E", 2.5, rot=r)
    stalk = lambda x, rot: G(L(f"M{x} 92 V20", "#C9A04A", 3)
                             + "".join(grain(x - 5, y, -30) + grain(x + 5, y, 30) for y in (24, 36, 48)) + grain(x, 14, 0),
                             f"rotate({rot} {x} 92)")
    return svg(stalk(50, 0) + stalk(50, -18) + stalk(50, 18))


def rock():
    return svg(P("M10 84 Q8 60 26 50 Q34 30 54 32 Q76 32 86 56 Q94 74 88 84Z", "#A9B4C2")
               + L("M40 50 Q48 56 46 66 M66 48 L62 58", "#7D8A9A", 2.5) + shine("M28 60 Q32 50 40 46")
               + L("M6 86 H94", OL, 3))


DRAWINGS = {
    "nature-tree": tree(), "nature-pine-tree": pine_tree(), "nature-palm-tree": palm_tree(), "nature-cactus": cactus(),
    "nature-seedling": seedling(), "nature-herb": herb(), "nature-clover": clover(), "nature-shamrock": shamrock(),
    "nature-maple-leaf": maple_leaf(), "nature-fallen-leaves": fallen_leaves(), "nature-rose": rose(),
    "nature-tulip": tulip(), "nature-daisy": daisy(), "nature-sunflower": sunflower(), "nature-blossom": blossom(),
    "nature-hibiscus": hibiscus(), "nature-lotus": lotus(), "nature-bouquet": bouquet(), "nature-wheat": wheat(),
    "nature-rock": rock(),
}
