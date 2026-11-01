"""Around the home: everyday things, front-on."""
from kit import OL, svg, P, L, C, E, R, G, sym, flip, shine, tube

CATEGORY = "around-home"

GLASS = "#BDEBFF"
WOOD = "#C47A30"


def bed():
    return svg(R(8, 30, 14, 58, 3, WOOD) + R(78, 46, 14, 42, 3, WOOD)
               + R(20, 56, 60, 20, 3, "#FFFFFF") + P("M38 52 H80 V76 H38Z", "#4FC3F7", 3)
               + E(30, 50, 10, 6, "#FFFFFF"))


def chair():
    return svg(R(26, 10, 48, 44, 6, "#FF9F43") + R(22, 52, 56, 10, 3, "#E8822A")
               + L("M28 62 V90 M72 62 V90", OL, 4) + L("M28 62 V90 M72 62 V90", WOOD, 0))


def sofa():
    return svg(R(14, 26, 72, 34, 10, "#9B6BFF") + R(4, 42, 18, 36, 8, "#B58CFF") + R(78, 42, 18, 36, 8, "#B58CFF")
               + R(20, 54, 60, 22, 4, "#B58CFF") + L("M50 54 V76", OL, 2.5) + L("M12 78 V86 M88 78 V86", OL, 4))


def door():
    return svg(R(22, 6, 56, 86, 4, WOOD) + R(30, 14, 40, 30, 3, "#E09A55", 2.5) + R(30, 50, 40, 34, 3, "#E09A55", 2.5)
               + C(66, 50, 4, "#FFC93C", 2.5))


def window():
    return svg(R(14, 12, 72, 72, 4, GLASS) + L("M50 12 V84 M14 48 H86", "#FFFFFF", 6) + L("M50 12 V84 M14 48 H86", OL, 0)
               + R(14, 12, 72, 72, 4, "none") + L("M50 12 V84 M14 48 H86", OL, 2)
               + R(8, 84, 84, 8, 3, "#FFFFFF") + shine("M22 22 L32 32", 3))


def mirror():
    return svg(E(50, 44, 28, 36, "#FFC93C") + E(50, 44, 21, 29, GLASS, 2.5)
               + shine("M38 30 L50 18 M40 40 L58 22", 3) + R(36, 82, 28, 8, 3, "#FFC93C"))


def clock():
    """A round wall clock (also used for घड़ी)."""
    return svg(C(50, 52, 38, "#F0464B") + C(50, 52, 30, "#FFFFFF", 2.5)
               + L("M50 26 V30 M50 74 V78 M24 52 H28 M72 52 H76", OL, 3)
               + L("M50 52 V34 M50 52 L64 60", OL, 4) + C(50, 52, 3, OL, 0)
               + P("M30 18 L22 10 M70 18 L78 10", "none", 4))


def television():
    return svg(L("M40 18 L28 4 M60 18 L72 4", OL, 3)
               + R(8, 18, 84, 60, 8, "#5E5A6E") + R(16, 26, 68, 44, 4, "#4FC3F7", 2.5)
               + C(40, 46, 6, "#FFC93C", 0) + P("M16 62 L36 50 L52 60 L66 46 L84 58 V70 H16Z", "#7ED957", 0)
               + L("M30 78 L24 90 M70 78 L76 90", OL, 4))


def telephone():
    return svg(P("M14 34 Q14 16 50 16 Q86 16 86 34 L76 42 L66 34 H34 L24 42Z", "#F0464B")
               + P("M26 44 H74 L84 86 H16Z", "#F0464B")
               + C(50, 64, 12, "#FFFFFF", 2.5) + "".join(C(50 + 7 * x, 64 + 7 * y, 1.8, OL, 0) for x, y in [(0, -1), (1, 0), (0, 1), (-1, 0)]))


def light_bulb():
    return svg(P("M50 8 C30 8 20 24 22 40 C24 54 36 60 36 72 H64 C64 60 76 54 78 40 C80 24 70 8 50 8Z", "#FFE14D")
               + L("M42 72 V50 L50 58 L58 50 V72", "#F2A81E", 2.5)
               + R(36, 72, 28, 16, 4, "#A9B4C2") + L("M36 80 H64", OL, 2) + R(44, 88, 12, 6, 3, OL, 0)
               + shine("M32 32 Q34 20 44 16"))


def plug():
    return svg(L("M38 6 V24 M62 6 V24", OL, 5)
               + R(24, 22, 52, 36, 10, "#FFFFFF") + P("M40 58 H60 L56 70 H44Z", "#FFFFFF")
               + tube("M50 70 Q50 92 74 92", "#FFFFFF", 4))


def key():
    return svg(G(C(26, 50, 16, "#FFC93C") + C(26, 50, 6, "#EAF7FF", 2.5)
                 + P("M40 46 H88 V54 H82 V64 H74 V54 H68 V60 H60 V54 H40Z", "#FFC93C"), "rotate(-30 50 50)"))


def broom():
    return svg(G(R(47, 2, 6, 54, 3, WOOD) + P("M36 56 H64 L76 94 H24Z", "#FFC93C")
                 + L("M34 70 L30 92 M42 70 L40 92 M50 70 V92 M58 70 L60 92 M66 70 L70 92", "#E09A20", 2)
                 + R(34, 56, 32, 8, 2, "#F0464B"), "rotate(20 50 50)"))


def bucket():
    return svg(P("M24 32 Q24 6 50 6 Q76 6 76 32", "none", 3.5)
               + P("M18 32 H82 L74 90 H26Z", "#4FC3F7") + E(50, 32, 32, 6, "#2E9FD8")
               + shine("M28 44 L32 80", 3))


def basket():
    return svg(P("M24 44 Q24 10 50 10 Q76 10 76 44", "none", 5) + L("M24 44 Q24 10 50 10 Q76 10 76 44", "#E8B060", 2)
               + P("M12 42 H88 L78 88 H22Z", "#E8B060")
               + L("M16 56 H84 M18 70 H82 M30 42 L32 88 M50 42 V88 M70 42 L68 88", "#B98030", 2.5))


def ladder():
    return svg(L("M30 6 L22 94 M70 6 L78 94", WOOD, 0)
               + tube("M30 6 L22 94", WOOD, 5) + tube("M70 6 L78 94", WOOD, 5)
               + "".join(tube(f"M{29 - i * 1.6:.1f} {18 + i * 18} H{71 + i * 1.6:.1f}", "#E09A55", 4) for i in range(5)))


def cart():
    return svg(L("M4 18 H18 L26 62 H80", OL, 4)
               + P("M20 26 H90 L82 54 H26Z", "#4FC3F7")
               + L("M40 26 V54 M56 26 V54 M72 26 V54", "#2E9FD8", 2.5)
               + C(32, 76, 7, "#5E5A6E") + C(74, 76, 7, "#5E5A6E"))


def toothbrush():
    return svg(G(R(10, 46, 70, 10, 5, "#4FC3F7") + R(56, 30, 26, 16, 3, "#FFFFFF")
                 + L("M62 32 V44 M68 32 V44 M74 32 V44", "#8ED8FF", 2)
                 + P("M56 34 Q70 22 84 30", "#7ED957", 2.5), "rotate(-25 50 50)"))


def soap_bottle():
    return svg(R(46, 6, 22, 6, 2, "#FFFFFF") + R(50, 12, 8, 14, 2, "#FFFFFF")
               + R(28, 26, 44, 64, 12, "#FF8FC7") + R(36, 46, 28, 24, 4, "#FFFFFF", 2.5)
               + C(42, 54, 3, "#8ED8FF", 2) + C(56, 60, 4, "#8ED8FF", 2) + C(80, 30, 4, "#EAF7FF", 2))


def toilet():
    return svg(R(56, 10, 30, 40, 4, "#FFFFFF")
               + E(44, 54, 34, 9, "#FFFFFF") + P("M12 54 Q14 80 36 82 L34 92 H56 L56 82 Q74 80 76 54Z", "#FFFFFF")
               + E(44, 54, 26, 5, "#BDEBFF", 2.5) + R(72, 18, 8, 4, 2, "#A9B4C2", 2))


def bathtub():
    return svg(L("M18 48 V20 Q18 12 26 12 H34 V18", OL, 3.5)
               + "".join(C(x, y, r, "#FFFFFF", 2.5) for x, y, r in [(40, 44, 8), (54, 40, 9), (68, 44, 8), (80, 48, 6)])
               + P("M8 48 H92 V60 Q92 80 72 80 H28 Q8 80 8 60Z", "#4FC3F7")
               + L("M24 80 L20 90 M76 80 L80 90", OL, 4))


def shower():
    drops = "".join(L(f"M{x} {y} L{x - 3} {y + 8}", "#4FC3F7", 3) for x, y in
                    [(30, 52), (42, 56), (54, 52), (66, 56), (36, 72), (50, 76), (62, 72)])
    return svg(L("M80 92 V14 Q80 6 72 6 H52 V22", OL, 4)
               + P("M30 22 H74 L66 38 H38Z", "#A9B4C2") + drops)


DRAWINGS = {
    "home-bed": bed(), "home-chair": chair(), "home-sofa": sofa(), "home-door": door(), "home-window": window(),
    "home-mirror": mirror(), "home-clock": clock(), "home-television": television(), "home-telephone": telephone(),
    "home-light-bulb": light_bulb(), "home-plug": plug(), "home-key": key(), "home-broom": broom(),
    "home-bucket": bucket(), "home-basket": basket(), "home-ladder": ladder(), "home-cart": cart(),
    "home-toothbrush": toothbrush(), "home-soap-bottle": soap_bottle(), "home-toilet": toilet(),
    "home-bathtub": bathtub(), "home-shower": shower(),
}
