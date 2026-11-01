"""Fruits: whole fruit, front-on, each with a leaf or stem and one highlight. No faces, so the
fruit itself is what the child learns."""
from kit import clip, OL, PALETTE as K, svg, P, L, C, E, R, G, sym, flip, shine, scallop, tube

CATEGORY = "fruits"

LEAF = "#5DBB4A"


def leaf(x, y, rot=-30, size=1.0, fill=LEAF):
    """A pointed leaf whose base sits at (x, y)."""
    s = size
    return G(P(f"M0 0 Q{9 * s} {-9 * s} {20 * s} {-4 * s} Q{9 * s} {5 * s} 0 0Z", fill, 2.5),
             f"translate({x} {y}) rotate({rot})")


def stem(x, y, h=10):
    return L(f"M{x} {y} Q{x + 1} {y - h / 2} {x + 4} {y - h}", "#7A4A32", 4)


def apple(fill="#F0464B"):
    return (stem(50, 28) + leaf(53, 22, -20)
            + P("M50 30 C40 22 16 24 16 52 C16 76 34 90 50 84 C66 90 84 76 84 52 C84 24 60 22 50 30Z", fill)
            + shine("M28 46 Q28 38 34 34"))


def red_apple():
    return svg(apple())


def green_apple():
    return svg(apple("#8BD44A"))


def pear():
    return svg(
        stem(50, 22) + leaf(53, 16, -25)
        + P("M50 22 C40 22 38 34 38 42 C24 50 20 68 26 78 C34 92 66 92 74 78 C80 68 76 50 62 42 C62 34 60 22 50 22Z", "#C5DE4A")
        + shine("M34 62 Q34 54 40 50"))


def orange():
    return svg(
        leaf(52, 22, -30, 1.1) + C(50, 56, 32, "#FF9F43")
        + C(50, 26, 2.5, "#7A9A2A", 0)
        + "".join(C(x, y, 1.3, "#E8822A", 0) for x, y in [(36, 50), (62, 46), (56, 70), (40, 68), (68, 62)])
        + shine("M28 48 Q30 38 38 34"))


def lemon():
    return svg(
        leaf(66, 26, -50, 1.0)
        + P("M14 54 Q14 52 20 46 Q32 26 56 28 Q74 28 82 40 Q88 44 88 50 Q86 52 84 56 Q76 78 48 78 Q26 78 18 62 Q14 58 14 54Z", "#FFE14D")
        + shine("M30 46 Q36 38 46 36"))


def banana():
    return svg(
        P("M24 22 L30 18 L34 26Z", "#7A4A32", 2.5)
        + P("M30 24 Q24 70 70 82 Q86 84 88 76 Q50 74 42 26Z", "#FFE14D")
        + P("M30 24 Q22 58 56 76 Q40 56 38 26Z", "#F6CB2A", 0)
        + P("M86 76 L92 74 L90 80Z", "#7A4A32", 2))


def grapes():
    pts = [(38, 38), (52, 36), (66, 38), (32, 52), (46, 50), (60, 50), (74, 52),
           (40, 64), (54, 64), (68, 64), (47, 77), (61, 77), (54, 88)]
    return svg(
        stem(52, 30, 12) + leaf(56, 24, -15, 1.1)
        + "".join(C(x, y, 8.5, "#9B6BFF", 2.5) for x, y in pts)
        + "".join(C(x - 3, y - 3, 1.8, "#fff", 0, op=".7") for x, y in pts[:7]))


def cherries():
    return svg(
        L("M30 66 Q34 36 54 16 M68 70 Q62 40 54 16", "#5DBB4A", 3.5)
        + leaf(54, 16, -10, 1.2)
        + C(30, 72, 15, "#E62E4D") + C(68, 74, 15, "#E62E4D")
        + shine("M22 68 Q24 62 28 60", 3) + shine("M60 70 Q62 64 66 62", 3))


def strawberry():
    seeds = "".join(E(x, y, 1.4, 2.4, "#FFE14D", 0) for x, y in
                    [(36, 44), (50, 42), (64, 44), (30, 56), (44, 56), (58, 56), (70, 56), (40, 68), (54, 68), (48, 80)])
    return svg(
        P("M50 32 Q86 28 82 52 Q78 76 50 90 Q22 76 18 52 Q14 28 50 32Z", "#F0464B")
        + seeds
        + P("M32 32 L40 22 L46 30 L50 18 L54 30 L60 22 L68 32 Q50 40 32 32Z", LEAF))


def watermelon():
    """A smiling slice: red flesh, black seeds, green rind (also used for तरबूज)."""
    seeds = "".join(E(x, y, 2, 3.5, OL, 0) for x, y in [(34, 44), (50, 50), (66, 44), (42, 58), (58, 58)])
    return svg(
        P("M8 34 Q50 34 92 34 Q88 78 50 80 Q12 78 8 34Z", "#5DBB4A")
        + P("M16 34 Q50 34 84 34 Q80 70 50 72 Q20 70 16 34Z", "#FFFFFF", 0)
        + P("M20 34 Q50 34 80 34 Q76 66 50 68 Q24 66 20 34Z", "#FF5C6E", 0)
        + L("M8 34 H92")
        + seeds)


def melon():
    return svg(
        stem(50, 18, 8)
        + E(50, 54, 34, 32, "#C5E27A")
        + L("M50 22 Q30 40 30 54 Q30 70 50 86 M50 22 Q70 40 70 54 Q70 70 50 86 M50 22 V86", "#9CC24A", 2)
        + L("M24 36 L30 42 M76 70 L70 64 M30 72 L36 66 M72 38 L66 44", "#F6FAE6", 2)
        + shine("M26 48 Q28 38 36 32"))


def pineapple():
    """A whole pineapple with its crown (also used for अनानास)."""
    grid = "".join(L(d, "#C98A1E", 2) for d in [
        "M30 46 L66 86", "M36 40 L76 80", "M30 62 L52 88", "M50 38 L78 66",
        "M70 46 L34 86", "M64 40 L24 80", "M70 62 L48 88", "M50 38 L22 66"])
    return svg(
        P("M50 36 L38 10 L48 20 L50 4 L52 20 L62 10Z", LEAF)
        + P("M50 36 L28 20 L42 26Z M50 36 L72 20 L58 26Z", "#3E9C3E")
        + E(50, 64, 24, 28, "#FFC12E")
        + clip("pine", E(50, 64, 22.5, 26.5, "#000", 0), grid)
        + shine("M34 54 Q34 46 40 42"))


def mango():
    """A ripe mango (also used for आम)."""
    return svg(
        stem(56, 22) + leaf(58, 18, -40, 1.2)
        + P("M56 22 C30 18 14 40 18 62 C22 84 44 90 62 82 C84 72 88 44 78 32 C72 24 64 22 56 22Z", "#FFB627")
        + P("M62 82 C84 72 88 44 78 32 C80 52 72 70 54 80Z", "#FF8A3D", 0)
        + shine("M28 50 Q30 40 38 34"))


def peach():
    return svg(
        stem(50, 24) + leaf(53, 18, -25)
        + C(50, 56, 30, "#FFB08A")
        + L("M50 28 Q38 50 46 84", "#F08A6A", 2.5)
        + E(66, 56, 12, 16, "#FF9070", 0, op=".55")
        + shine("M28 50 Q30 40 38 36"))


def kiwi():
    seeds = "".join(G(E(50, 36, 1.5, 3, OL, 0), f"rotate({a} 50 50)") for a in range(0, 360, 30))
    return svg(
        C(50, 50, 36, "#8A6A3A")
        + C(50, 50, 30, "#8BD44A", 0)
        + seeds
        + C(50, 50, 9, "#F4F8D6", 0))


def coconut():
    return svg(
        C(50, 54, 34, "#8B5A3C")
        + P("M24 44 Q50 30 76 44 Q74 76 50 80 Q26 76 24 44Z", "#FFFFFF", 2.5)
        + P("M24 44 Q50 30 76 44", "none", 3)
        + L("M28 26 Q32 30 30 34 M64 22 Q60 28 64 32", "#6B4430", 2))


def avocado():
    return svg(
        P("M50 10 C30 10 24 40 18 58 C12 80 30 92 50 92 C70 92 88 80 82 58 C76 40 70 10 50 10Z", "#3E7D2E")
        + P("M50 18 C36 18 32 44 26 60 C22 78 34 86 50 86 C66 86 78 78 74 60 C68 44 64 18 50 18Z", "#CDE88A", 0)
        + C(50, 64, 14, "#9B5E36"))


def blueberries():
    pts = [(34, 40), (62, 36), (48, 60), (26, 66), (72, 64)]
    return svg(
        "".join(C(x, y, 15, "#4A6FD6") + P(f"M{x - 4} {y - 4} L{x} {y - 8} L{x + 4} {y - 4} L{x} {y}Z", "#2E4AA0", 0)
                for x, y in pts))


def olive():
    return svg(
        L("M20 24 Q50 30 82 22", "#7A6A3A", 3)
        + leaf(36, 26, 200, 1.2, "#8FA85A") + leaf(70, 24, -20, 1.2, "#8FA85A")
        + E(38, 60, 14, 18, "#6B8E23", rot=-15) + E(66, 62, 14, 18, "#4A5A2A", rot=15)
        + L("M44 28 Q40 36 38 42 M58 26 Q64 34 66 44", "#7A6A3A", 2.5)
        + shine("M32 54 Q32 50 34 48", 3))


def chestnut():
    return svg(
        P("M50 16 Q66 30 80 52 Q86 80 50 84 Q14 80 20 52 Q34 30 50 16Z", "#8B4A2B")
        + P("M24 64 Q50 70 76 64 Q76 82 50 84 Q24 82 24 64Z", "#E8C89A", 2.5)
        + shine("M36 42 Q40 32 46 28"))


def pea_pod():
    return svg(
        P("M12 50 Q30 22 60 26 Q86 30 92 40 Q80 64 50 70 Q24 74 12 50Z", "#7ED957")
        + "".join(C(x, y, 8, "#A8E06A", 2.5) for x, y in [(30, 50), (46, 46), (62, 42), (77, 40)])
        + P("M12 50 Q40 60 92 40", "none", 3)
        + L("M92 40 Q96 30 90 24", "#5DBB4A", 3))


def tomato():
    """A red tomato with a green star on top (also used for टमाटर)."""
    return svg(
        P("M50 26 C24 24 14 44 16 58 C18 78 34 86 50 86 C66 86 82 78 84 58 C86 44 76 24 50 26Z", "#F0464B")
        + P("M50 32 L40 22 L46 30 L32 30 L44 34 L38 42 L50 36 L62 42 L56 34 L68 30 L54 30 L60 22Z", LEAF, 2.5)
        + L("M50 26 V16", "#3E9C3E", 4)
        + shine("M26 54 Q26 44 34 38"))


DRAWINGS = {
    "fruit-apple": red_apple(), "fruit-green-apple": green_apple(), "fruit-pear": pear(),
    "fruit-orange": orange(), "fruit-lemon": lemon(), "fruit-banana": banana(), "fruit-grapes": grapes(),
    "fruit-cherries": cherries(), "fruit-strawberry": strawberry(), "fruit-watermelon": watermelon(),
    "fruit-melon": melon(), "fruit-pineapple": pineapple(), "fruit-mango": mango(), "fruit-peach": peach(),
    "fruit-kiwi": kiwi(), "fruit-coconut": coconut(), "fruit-avocado": avocado(),
    "fruit-blueberries": blueberries(), "fruit-olive": olive(), "fruit-chestnut": chestnut(),
    "fruit-pea-pod": pea_pod(), "fruit-tomato": tomato(),
}
