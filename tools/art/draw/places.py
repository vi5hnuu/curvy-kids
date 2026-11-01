"""Places: buildings front-on on a strip of grass, each with the one feature that names it."""
from kit import OL, svg, P, L, C, E, R, G, sym, flip, shine, tube, scallop

CATEGORY = "places"

GLASS = "#BDEBFF"
WALL = "#FFF0D4"
GRASS = "#7ED957"


def ground():
    return R(4, 86, 92, 8, 4, GRASS, 2.5)


def win(x, y, w=10, h=10):
    return R(x, y, w, h, 2, GLASS, 2.5)


def door(x, y=66, w=14, h=22, fill="#C47A30"):
    return P(f"M{x} {y + h} V{y + w / 2} Q{x} {y} {x + w / 2} {y} Q{x + w} {y} {x + w} {y + w / 2} V{y + h}Z", fill, 2.5)


def house():
    return svg(ground() + R(20, 46, 60, 42, 2, WALL) + P("M12 48 L50 16 L88 48Z", "#F0464B")
               + R(66, 20, 9, 18, 1, "#C47A30", 2.5) + door(43, 64) + win(26, 56, 12, 12) + win(62, 56, 12, 12))


def home_garden():
    bush = lambda x: P(scallop(x, 78, 9, 7, 2), "#5DBB4A")
    return svg(ground() + R(26, 46, 48, 42, 2, "#FFE2A8") + P("M18 48 L50 20 L82 48Z", "#9B6BFF")
               + door(43, 66) + win(30, 56) + win(60, 56)
               + bush(14) + bush(86) + C(14, 70, 3, "#FF5C8A", 2) + C(86, 70, 3, "#FFC93C", 2))


def school():
    return svg(ground() + R(10, 44, 80, 44, 2, "#FFC93C")
               + P("M30 44 V24 L50 12 L70 24 V44Z", "#FFE98A") + C(50, 28, 7, "#FFFFFF") + L("M50 28 V24 M50 28 H54", OL, 2)
               + L("M50 12 V2", OL, 2.5) + P("M50 2 H62 L58 6 L62 10 H50Z", "#F0464B", 2)
               + door(43, 66) + win(16, 54) + win(28, 54) + win(62, 54) + win(74, 54) + win(16, 70) + win(74, 70))


def hospital():
    return svg(ground() + R(14, 28, 72, 60, 2, "#FFFFFF")
               + R(40, 8, 20, 20, 2, "#FFFFFF") + R(46, 12, 8, 12, 0, "#F0464B", 0) + R(44, 14, 12, 8, 0, "#F0464B", 0)
               + door(42, 68, 16, 20, GLASS) + "".join(win(x, y) for x in (20, 34, 56, 70) for y in (36, 52)))


def bank():
    cols = "".join(R(x, 46, 7, 34, 1, "#FFFFFF", 2.5) for x in (20, 36, 57, 73))
    return svg(ground() + R(12, 78, 76, 8, 1, "#D7DEE8") + R(14, 38, 72, 8, 1, "#D7DEE8")
               + P("M10 38 L50 12 L90 38Z", "#D7DEE8") + cols
               + C(50, 28, 6, "#FFC93C", 2.5) + door(44, 60, 12, 18, "#7A4A32"))


def museum():
    cols = "".join(R(x, 44, 6, 36, 1, WALL, 2.5) for x in (18, 32, 46, 60, 74))
    return svg(ground() + R(12, 78, 76, 8, 1, WALL) + R(12, 36, 76, 8, 1, WALL)
               + P("M8 36 L50 10 L92 36Z", "#F2C14E") + C(50, 26, 4, OL, 0) + cols)


def church():
    return svg(ground() + R(24, 50, 52, 38, 2, WALL) + P("M18 52 L50 30 L82 52Z", "#4FC3F7")
               + R(40, 22, 20, 30, 2, WALL) + P("M36 24 L50 6 L64 24Z", "#4FC3F7")
               + L("M50 0 V10 M46 4 H54", OL, 2.5)
               + door(43, 68) + C(50, 38, 4, "#FFC93C", 2) + win(30, 60, 8, 12) + win(62, 60, 8, 12))


def mosque():
    return svg(ground() + R(18, 54, 64, 34, 2, "#FFFFFF")
               + P("M28 56 Q28 30 50 22 Q72 30 72 56Z", "#7ED957") + L("M50 22 V12", OL, 2.5) + C(50, 10, 3, "#FFC93C", 2)
               + R(8, 34, 8, 54, 2, "#FFFFFF") + P("M6 36 L12 22 L18 36Z", "#7ED957")
               + R(84, 34, 8, 54, 2, "#FFFFFF") + P("M82 36 L88 22 L94 36Z", "#7ED957")
               + door(43, 68) + win(26, 64, 8, 12) + win(66, 64, 8, 12))


def palace():
    dome = lambda x, w, top: P(f"M{x - w} {top + w} Q{x - w} {top} {x} {top - w * 0.6} Q{x + w} {top} {x + w} {top + w}Z", "#FFC93C")
    return svg(ground() + R(10, 50, 80, 38, 2, "#FFB3C7")
               + dome(50, 16, 34) + dome(20, 8, 42) + dome(80, 8, 42)
               + L("M50 24 V14 M20 37 V30 M80 37 V30", OL, 2.5)
               + door(42, 64, 16, 24) + win(18, 60, 10, 14) + win(72, 60, 10, 14))


def castle():
    top = lambda x, y, w: P(f"M{x} {y} V{y - 8} H{x + w / 5} V{y - 3} H{x + 2 * w / 5} V{y - 8} H{x + 3 * w / 5} V{y - 3} H{x + 4 * w / 5} V{y - 8} H{x + w} V{y}Z", "#A9B4C2")
    return svg(ground() + R(10, 32, 22, 56, 1, "#C9D2DD") + R(68, 32, 22, 56, 1, "#C9D2DD")
               + top(10, 32, 22) + top(68, 32, 22)
               + R(30, 46, 40, 42, 1, "#A9B4C2") + top(30, 46, 40)
               + door(40, 62, 20, 26, "#7A4A32") + win(17, 44, 8, 12) + win(75, 44, 8, 12)
               + L("M21 24 V8 M79 24 V8", OL, 2.5) + P("M21 8 H31 L27 12 L31 16 H21Z M79 8 H89 L85 12 L89 16 H79Z", "#F0464B", 2))


def tower():
    return svg(ground() + P("M50 6 L66 86 H58 L50 50 L42 86 H34Z", "#C47A30")
               + L("M40 66 H60 M44 46 H56 M47 28 H53", OL, 3)
               + L("M50 6 V0", OL, 2.5))


def stadium():
    return svg(ground() + E(50, 62, 44, 22, "#D7DEE8") + E(50, 64, 32, 14, GRASS)
               + L("M50 50 V78", "#FFFFFF", 2.5) + C(50, 64, 5, "none", 2)
               + "".join(L(f"M{x} 40 V22", OL, 2.5) + R(x - 4, 18, 8, 6, 2, "#FFE98A", 2) for x in (14, 86)))


def store():
    stripes = "".join(P(f"M{x} 34 H{x + 10} V44 Q{x + 5} 50 {x} 44Z", c, 2.5)
                      for x, c in zip(range(10, 90, 10), ["#F0464B", "#FFFFFF"] * 4))
    return svg(ground() + R(12, 34, 76, 54, 2, WALL) + R(10, 18, 80, 16, 3, "#4FC3F7") + stripes
               + R(18, 54, 30, 22, 2, GLASS, 2.5) + door(58, 54, 22, 34, "#C47A30")
               + C(26, 64, 3, "#FF9F43", 2) + C(36, 66, 3, "#7ED957", 2))


def mall():
    return svg(ground() + R(8, 30, 84, 58, 3, "#9B6BFF")
               + R(14, 38, 72, 14, 2, GLASS, 2.5) + R(14, 56, 26, 14, 2, GLASS, 2.5) + R(60, 56, 26, 14, 2, GLASS, 2.5)
               + R(42, 62, 16, 26, 2, GLASS, 2.5) + L("M50 62 V88", OL, 2.5)
               + R(30, 18, 40, 12, 3, "#FFC93C"))


def factory():
    return svg(ground() + P("M8 88 V50 L28 38 V50 L48 38 V50 L68 38 V50 H92 V88Z", "#A9B4C2")
               + R(72, 14, 12, 36, 1, "#F0464B") + C(78, 8, 5, "#E4ECF5", 2) + C(88, 4, 3.5, "#E4ECF5", 2)
               + win(16, 62) + win(32, 62) + win(48, 62) + door(66, 64, 16, 24, "#FFC93C"))


def station():
    return svg(ground() + R(10, 40, 80, 48, 2, "#FFE2A8") + P("M4 42 L14 26 H86 L96 42Z", "#F0464B")
               + C(50, 52, 8, "#FFFFFF") + L("M50 52 V47 M50 52 H54", OL, 2)
               + door(18, 62, 14, 26) + door(68, 62, 14, 26) + win(40, 66, 20, 12))


def bridge():
    return svg(P("M4 74 Q20 70 34 74 Q50 78 66 74 Q80 70 96 74 V88 Q96 92 90 92 H10 Q4 92 4 88Z", "#4FC3F7", 0)
               + P("M4 50 H96 V58 H4Z", "#F0464B")
               + P("M14 58 V86 H24 V70 Q50 50 76 70 V86 H86 V58Z", "#C9D2DD")
               + L("M4 50 Q30 20 50 30 Q70 20 96 50", "#F0464B", 3)
               + L("M24 50 V36 M36 50 V30 M64 50 V30 M76 50 V36", OL, 2))


def fountain():
    return svg(E(50, 80, 40, 10, "#C9D2DD") + E(50, 77, 32, 6, "#4FC3F7", 2)
               + R(44, 46, 12, 30, 2, "#C9D2DD") + E(50, 46, 20, 5, "#C9D2DD")
               + L("M50 40 Q50 10 30 30 M50 40 Q50 10 70 30 M50 40 V10", "#4FC3F7", 4)
               + C(30, 34, 3, "#8ED8FF", 0) + C(70, 34, 3, "#8ED8FF", 0))


def beach():
    return svg(C(78, 22, 10, "#FFC93C")
               + P("M4 56 Q30 50 50 56 Q70 62 96 56 V66 H4Z", "#4FC3F7", 2.5)
               + P("M4 66 H96 V86 Q96 92 90 92 H10 Q4 92 4 86Z", "#FFE2A8")
               + L("M34 86 L46 34", OL, 3) + P("M16 46 Q40 18 70 38Z", "#F0464B")
               + C(70, 80, 5, "#FF8FC7", 2))


def tent():
    return svg(ground() + P("M50 14 L88 86 H12Z", "#FF9F43") + P("M50 14 L60 86 H40Z", "#C2662A", 2.5)
               + L("M50 14 V6 M50 6 L58 9 L50 12", OL, 2.5))


DRAWINGS = {
    "place-house": house(), "place-home-garden": home_garden(), "place-school": school(),
    "place-hospital": hospital(), "place-bank": bank(), "place-museum": museum(), "place-church": church(),
    "place-mosque": mosque(), "place-palace": palace(), "place-castle": castle(), "place-tower": tower(),
    "place-stadium": stadium(), "place-store": store(), "place-mall": mall(), "place-factory": factory(),
    "place-station": station(), "place-bridge": bridge(), "place-fountain": fountain(),
    "place-beach": beach(), "place-tent": tent(),
}
