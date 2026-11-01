"""Toys and party things."""
import math
from kit import OL, PALETTE as K, svg, P, L, C, E, R, G, sym, flip, eyes, cheeks, smile, shine, tube, clip

CATEGORY = "toys"


def kite():
    """A diamond kite with a bow tail (also used for K and पतंग)."""
    return svg(
        L("M54 66 Q44 78 54 86 Q66 92 60 98", OL, 2.5)
        + P("M50 70 L52 78 L58 72Z M56 84 L62 88 L60 80Z", "#FF5C8A", 2)
        + P("M56 6 L82 34 L54 70 L24 36Z", "#FF5C8A")
        + P("M56 6 L54 70 L24 36Z", "#FFC93C")
        + P("M56 6 L82 34 L54 70", "none", 0)
        + L("M24 36 L82 34 M56 6 L54 70", OL, 2.5))


def yo_yo():
    return svg(
        L("M50 6 V40", OL, 2.5) + R(46, 2, 8, 8, 4, "#FFC93C", 2)
        + C(50, 62, 30, "#F0464B")
        + C(50, 62, 18, "#FF8F8F", 2.5) + C(50, 62, 5, "#FFFFFF", 2.5)
        + shine("M30 52 Q32 42 40 38"))


def teddy_bear():
    return svg(
        sym(C(30, 22, 9, "#C68A54") + C(30, 22, 4, "#E8B88A", 0))
        + sym(C(24, 66, 10, "#C68A54") + C(34, 88, 10, "#C68A54") + C(34, 88, 5, "#E8B88A", 0))
        + E(50, 70, 20, 22, "#C68A54") + E(50, 72, 11, 13, "#E8B88A", 0)
        + C(50, 36, 20, "#C68A54") + E(50, 43, 9, 7, "#E8B88A", 0)
        + eyes(33, 8, r=3) + E(50, 40, 3.5, 2.5, OL, 0) + smile(50, 44, 3)
        + P("M42 54 L50 58 L58 54 L58 62 L50 58 L42 62Z", "#FF5C8A", 2))


def balloon():
    return svg(
        L("M50 72 Q44 84 52 96", OL, 2.5)
        + P("M50 6 C24 6 16 30 22 46 C28 62 42 70 50 72 C58 70 72 62 78 46 C84 30 76 6 50 6Z", "#F0464B")
        + P("M46 72 L54 72 L50 78Z", "#F0464B", 2.5)
        + shine("M32 26 Q36 16 46 14"))


def gift():
    return svg(
        R(14, 42, 72, 48, 4, "#9B6BFF")
        + R(10, 30, 80, 16, 4, "#B58CFF")
        + R(44, 30, 12, 60, 0, "#FFC93C")
        + P("M50 30 Q30 4 22 18 Q20 28 50 30Z M50 30 Q70 4 78 18 Q80 28 50 30Z", "#FFC93C"))


def dice():
    dot = lambda x, y: C(x, y, 4.5, OL, 0)
    return svg(R(14, 14, 72, 72, 16, "#FFFFFF")
               + dot(32, 32) + dot(68, 32) + dot(50, 50) + dot(32, 68) + dot(68, 68))


def puzzle():
    piece = ("M14 26 H38 Q34 14 46 14 Q58 14 54 26 H78 V48 Q90 44 90 56 Q90 68 78 64 V86 H54 Q58 74 46 74 "
             "Q34 74 38 86 H14 V64 Q26 68 26 56 Q26 44 14 48Z")
    return svg(P(piece, "#7ED957") + shine("M22 36 V44", 3))


def dartboard():
    return svg(C(50, 52, 38, "#FFFFFF") + C(50, 52, 30, "#F0464B") + C(50, 52, 21, "#FFFFFF")
               + C(50, 52, 12, "#F0464B") + C(50, 52, 4, "#FFFFFF", 2)
               + L("M50 52 L80 22", OL, 3) + P("M80 22 L92 18 L86 10 L82 14Z", "#4FC3F7", 2.5))


def game_controller():
    return svg(
        P("M24 30 H76 Q92 30 94 54 Q96 80 82 80 Q72 80 66 66 H34 Q28 80 18 80 Q4 80 6 54 Q8 30 24 30Z", "#5E5A6E")
        + R(22, 44, 16, 6, 2, "#FFFFFF", 0) + R(27, 39, 6, 16, 2, "#FFFFFF", 0)
        + C(68, 42, 4, "#F0464B", 2) + C(78, 50, 4, "#4FC3F7", 2) + C(68, 58, 4, "#7ED957", 2) + C(58, 50, 4, "#FFC93C", 2))


def nesting_dolls():
    def doll(x, s, body, scarf):
        return G(P("M0 -30 Q-14 -30 -14 -14 Q-20 0 -18 18 Q-16 30 0 30 Q16 30 18 18 Q20 0 14 -14 Q14 -30 0 -30Z", body)
                 + P("M-12 -14 Q-12 -28 0 -28 Q12 -28 12 -14 Q12 -2 0 -2 Q-12 -2 -12 -14Z", scarf, 2.5)
                 + C(0, -14, 8, "#FFDDBF", 2) + C(-3, -15, 1.4, OL, 0) + C(3, -15, 1.4, OL, 0)
                 + C(0, 12, 6, "#FFC93C", 2),
                 f"translate({x} {72 - 30 * s}) scale({s})")
    return svg(doll(72, .6, "#9B6BFF", "#FF8FC7") + doll(50, .8, "#4FC3F7", "#FFC93C") + doll(26, 1.0, "#F0464B", "#FFC93C"))


def pinata():
    """A donkey piñata hanging on a string."""
    stripes = "".join(R(10, y, 80, 5, 0, c, 0) for y, c in [(46, "#FFC93C"), (56, "#4FC3F7"), (66, "#7ED957")])
    return svg(
        L("M46 4 V44", OL, 2.5)
        + "".join(R(x, 66, 8, 22, 2, "#FF8FC7") for x in (22, 34, 50, 62))
        + P("M14 52 Q8 50 8 58", "none", 3)
        + R(16, 42, 56, 30, 10, "#FF8FC7")
        + clip("pin", R(17.5, 43.5, 53, 27, 9, "#000", 0), stripes)
        + P("M62 46 L68 24 L80 24 L76 48Z", "#FF8FC7")
        + E(76, 26, 14, 10, "#FF8FC7")
        + E(68, 10, 3.5, 10, "#FF8FC7", rot=-15) + E(78, 10, 3.5, 10, "#FF8FC7", rot=15)
        + C(74, 24, 2.4, OL, 0) + E(86, 28, 2.2, 1.6, OL, 0))


def party_popper():
    return svg(
        P("M14 88 L36 44 L58 66Z", "#FFC93C")
        + L("M24 68 L44 76 M28 58 L50 70", "#F0464B", 3)
        + "".join(R(x, y, 6, 3, 1, c, 0, rot=r) for x, y, c, r in
                  [(56, 30, "#F0464B", 30), (70, 22, "#4FC3F7", -20), (78, 40, "#7ED957", 50), (62, 46, "#9B6BFF", 10),
                   (48, 18, "#FF8FC7", -40), (84, 58, "#FFC93C", 20)])
        + L("M44 40 Q50 24 62 20 M60 56 Q76 48 86 30", "#FF5C8A", 2.5))


def confetti():
    bits = [(20, 20, "#F0464B", 20), (40, 34, "#4FC3F7", -30), (64, 18, "#7ED957", 40), (80, 36, "#FFC93C", -10),
            (26, 56, "#9B6BFF", 60), (52, 60, "#FF8FC7", -50), (74, 70, "#4FC3F7", 30), (34, 82, "#FFC93C", 10),
            (60, 86, "#F0464B", -20), (84, 54, "#7ED957", 70), (14, 40, "#FFC93C", -60)]
    return svg("".join(R(x, y, 10, 5, 2, c, 2, rot=r) for x, y, c, r in bits)
               + "".join(C(x, y, 3, c, 0) for x, y, c in [(50, 44, "#FFC93C"), (30, 70, "#4FC3F7"), (70, 50, "#F0464B")]))


def streamers():
    colours = ["#F0464B", "#FFC93C", "#4FC3F7", "#7ED957"]
    return svg(L("M6 12 H94", OL, 3)
               + "".join(tube(f"M{x} 12 Q{x + 10} 30 {x} 48 Q{x - 10} 66 {x} 86", c, 5)
                         for x, c in zip((20, 40, 60, 80), colours)))


def bow():
    return svg(P("M50 50 Q28 20 12 30 Q6 50 12 70 Q28 80 50 50Z", "#FF5C8A")
               + P("M50 50 Q72 20 88 30 Q94 50 88 70 Q72 80 50 50Z", "#FF5C8A")
               + P("M46 56 L36 92 L46 86 L50 94 L54 56Z M54 56 L64 92 L54 86Z", "#FF5C8A", 2.5)
               + C(50, 50, 9, "#E8406E"))


def slide():
    return svg(
        L("M64 90 V30 M84 90 V30 M64 46 H84 M64 62 H84 M64 78 H84", "#4FC3F7", 4)
        + P("M64 30 H84 V36 H64Z", "#4FC3F7", 2.5)
        + P("M64 30 Q46 36 34 70 Q26 88 10 88 V94 Q30 96 40 76 Q50 50 64 40Z", "#FFC93C"))


def carousel():
    return svg(
        P("M10 34 L50 8 L90 34Z", "#F0464B") + P("M10 34 H90 Q86 42 80 34 Q74 42 68 34 Q62 42 56 34 Q50 42 44 34 Q38 42 32 34 Q26 42 20 34 Q14 42 10 34Z", "#FFC93C", 2.5)
        + L("M20 38 V84 M50 38 V84 M80 38 V84", OL, 3)
        + P("M28 60 Q30 52 40 54 L46 50 V58 Q46 66 36 66 H30Z", "#FFFFFF", 2.5)
        + P("M58 66 Q60 58 70 60 L76 56 V64 Q76 72 66 72 H60Z", "#FF8FC7", 2.5)
        + R(8, 84, 84, 8, 3, "#9B6BFF"))


def ferris_wheel():
    spokes = "".join(L(f"M50 44 L{50 + 32 * math.cos(a):.1f} {44 + 32 * math.sin(a):.1f}", OL, 2)
                     for a in [i * math.pi / 4 for i in range(8)])
    cars = "".join(R(50 + 32 * math.cos(a) - 6, 44 + 32 * math.sin(a) - 2, 12, 10, 3, c, 2.5)
                   for a, c in zip([i * math.pi / 3 for i in range(6)], ["#F0464B", "#FFC93C", "#4FC3F7", "#7ED957", "#9B6BFF", "#FF8FC7"]))
    return svg(L("M50 44 L30 92 M50 44 L70 92", OL, 4) + C(50, 44, 32, "none", 4) + spokes + C(50, 44, 5, "#FFC93C") + cars
               + L("M20 92 H80", OL, 4))


def roller_coaster():
    return svg(
        L("M20 92 V64 M36 92 V40 M52 92 V30 M68 92 V48 M84 92 V70", "#A9B4C2", 3)
        + L("M6 70 Q20 60 30 40 Q40 20 52 26 Q62 32 70 56 Q78 76 94 70", OL, 5)
        + G(R(-10, -8, 20, 12, 4, "#F0464B", 2.5) + C(-5, 6, 3, OL, 0) + C(5, 6, 3, OL, 0), "translate(36 26) rotate(-45)"))


DRAWINGS = {
    "toy-kite": kite(), "toy-yo-yo": yo_yo(), "toy-teddy-bear": teddy_bear(), "toy-balloon": balloon(),
    "toy-gift": gift(), "toy-dice": dice(), "toy-puzzle": puzzle(), "toy-dartboard": dartboard(),
    "toy-game-controller": game_controller(), "toy-nesting-dolls": nesting_dolls(), "toy-pi-ata": pinata(),
    "toy-party-popper": party_popper(), "toy-confetti": confetti(), "toy-streamers": streamers(),
    "toy-bow": bow(), "toy-slide": slide(), "toy-carousel": carousel(), "toy-ferris-wheel": ferris_wheel(),
    "toy-roller-coaster": roller_coaster(),
}
