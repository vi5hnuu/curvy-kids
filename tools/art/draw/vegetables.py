"""Vegetables: whole vegetables, front or side on, no faces."""
from kit import OL, PALETTE as K, svg, P, L, C, E, R, G, sym, flip, shine, scallop, tube
from fruits import leaf, stem, LEAF

CATEGORY = "vegetables"


def carrot():
    return svg(
        P("M52 22 Q40 6 34 12 Q40 18 48 24Z M54 22 Q56 4 64 6 Q62 16 56 24Z M56 24 Q72 12 76 20 Q66 24 58 28Z", LEAF, 2.5)
        + P("M44 24 Q64 18 66 34 Q60 60 34 90 Q28 92 28 86 Q34 56 44 24Z", "#FF8A2A")
        + L("M46 40 L54 42 M42 54 L50 56 M38 68 L44 70", "#D9661A", 2.5))


def corn():
    kernels = "".join(C(x, y, 3.4, "#FFE14D", 1.5) for y in range(24, 74, 8) for x in (42 + (y % 16) / 4, 50 + (y % 16) / 4, 58 + (y % 16) / 4))
    return svg(
        E(51, 48, 15, 32, "#FFC93C")
        + kernels
        + P("M50 90 Q20 70 26 30 Q38 52 48 64Z", "#7ED957")
        + P("M52 90 Q82 70 76 30 Q64 52 54 64Z", "#5DBB4A"))


def broccoli():
    return svg(
        P("M42 54 L36 90 L64 90 L58 54Z", "#A8E06A")
        + L("M50 60 V84 M44 60 L42 72 M56 60 L58 72", "#7EBF4A", 2)
        + P(scallop(50, 38, 22, 9, 5), "#3E9C3E")
        + C(40, 34, 3, "#5DBB4A", 0) + C(58, 30, 3, "#5DBB4A", 0) + C(50, 44, 3, "#5DBB4A", 0))


def eggplant():
    body = "M30 26 Q16 34 20 52 Q26 72 52 86 Q74 96 86 84 Q94 70 78 56 Q62 44 54 30 Q44 18 30 26Z"
    return svg(
        P(body, "#7B3FA8")
        + P("M20 30 Q24 14 40 20 Q48 26 44 34 Q34 40 26 38 Q18 36 20 30Z", LEAF, 2.5)
        + L("M26 24 Q18 14 10 14", "#3E9C3E", 4)
        + shine("M30 50 Q38 66 52 74"))


def bell_pepper():
    return svg(
        L("M50 24 Q48 14 56 10", "#3E9C3E", 5)
        + P("M50 28 Q30 18 22 34 Q14 54 22 72 Q30 88 42 82 Q50 90 58 82 Q70 88 78 72 Q86 54 78 34 Q70 18 50 28Z", "#F0464B")
        + L("M42 34 Q36 56 42 80 M58 34 Q64 56 58 80", "#C42E36", 2.5)
        + P("M40 28 Q50 20 60 28 Q50 34 40 28Z", LEAF, 2.5)
        + shine("M28 46 Q28 38 32 34"))


def chili():
    return svg(
        P("M28 20 Q36 14 42 22 L36 30Z", LEAF, 2.5) + L("M32 20 Q28 12 22 12", "#3E9C3E", 4)
        + P("M38 26 Q70 30 80 60 Q84 80 74 88 Q70 66 54 54 Q36 42 34 32Z", "#E62E2E")
        + shine("M48 34 Q60 38 68 48", 3))


def cucumber():
    return svg(
        P("M16 74 Q8 64 18 56 L70 22 Q84 14 90 24 Q94 34 82 42 L30 76 Q22 82 16 74Z", "#4FA34A")
        + "".join(C(x, y, 1.6, "#BDEB9A", 0) for x, y in [(30, 64), (44, 54), (58, 44), (72, 34), (40, 66), (64, 48)])
        + P("M84 22 Q90 16 94 20", "none", 3))


def lettuce():
    return svg(
        P(scallop(50, 54, 30, 11, 4), "#8BD44A")
        + P(scallop(50, 56, 20, 8, 3), "#B5E86E")
        + L("M50 36 V80 M50 56 L38 46 M50 56 L62 46 M50 68 L40 60 M50 68 L60 60", "#7EBF4A", 2.5))


def mushroom():
    return svg(
        P("M38 52 Q36 80 42 86 H58 Q64 80 62 52Z", "#FFF0D4")
        + P("M12 54 Q12 18 50 16 Q88 18 88 54 Q50 62 12 54Z", "#F0464B")
        + C(34, 34, 6, "#fff", 2) + C(58, 28, 5, "#fff", 2) + C(70, 44, 4, "#fff", 2) + C(46, 46, 4, "#fff", 2))


def potato():
    return svg(
        P("M18 52 Q14 30 40 26 Q66 20 80 36 Q92 52 80 70 Q64 86 38 80 Q20 74 18 52Z", "#D9A863")
        + "".join(E(x, y, 2.4, 1.6, "#A97A3A", 0) for x, y in [(36, 42), (60, 38), (48, 60), (70, 62), (30, 64)])
        + shine("M28 44 Q30 36 38 34"))


def sweet_potato():
    return svg(
        L("M14 60 L6 66 M88 40 L96 34", "#A0503A", 3)
        + P("M14 60 Q16 40 40 34 Q66 28 88 40 Q86 60 62 70 Q36 80 14 60Z", "#C2566E")
        + L("M34 46 Q36 50 34 54 M56 42 Q58 48 56 52 M70 50 Q72 54 70 58", "#8E3A50", 2.5))


def onion():
    return svg(
        L("M50 20 Q46 10 40 8 M50 20 Q54 10 60 6", "#7EBF4A", 3)
        + P("M50 20 Q56 32 72 40 Q90 56 76 76 Q64 90 50 90 Q36 90 24 76 Q10 56 28 40 Q44 32 50 20Z", "#C0679E")
        + L("M50 26 Q34 52 42 88 M50 26 Q66 52 58 88", "#9C4E80", 2.5)
        + L("M44 90 L42 96 M50 90 V96 M56 90 L58 96", "#D9C4A0", 2))


def garlic():
    return svg(
        P("M50 12 Q46 22 50 28 Q54 22 50 12Z", "#F4EFE6", 2.5)
        + P("M50 26 Q30 34 22 56 Q18 80 40 86 Q50 90 60 86 Q82 80 78 56 Q70 34 50 26Z", "#F7F2EA")
        + L("M50 30 Q38 50 42 86 M50 30 Q62 50 58 86", "#D9CCE0", 2.5)
        + L("M42 88 L40 94 M50 88 V95 M58 88 L60 94", "#C9B89A", 2))


def ginger():
    return svg(
        P("M14 62 Q12 48 28 48 Q32 30 46 34 Q50 20 62 24 Q72 28 68 40 Q84 40 86 54 Q86 70 66 72 Q50 80 34 74 Q16 76 14 62Z", "#E0B060")
        + L("M30 56 Q38 58 42 54 M54 46 Q60 50 66 48 M50 64 Q58 66 64 62", "#B08030", 2.5))


def beans():
    pods = "".join(G(P("M0 0 Q24 -10 48 0 Q50 8 44 10 Q24 4 2 10 Q-4 6 0 0Z", "#5DBB4A")
                     + "".join(C(x, 4, 3, "#8BD44A", 1.5) for x in (12, 24, 36)), f"translate({x} {y}) rotate({r})")
                   for x, y, r in [(16, 36, 10), (24, 54, -8), (30, 70, 6)])
    return svg(pods)


def peanuts():
    def nut(x, y, r):
        return G(P("M0 -20 Q12 -20 12 -8 Q10 0 12 8 Q12 20 0 20 Q-12 20 -12 8 Q-10 0 -12 -8 Q-12 -20 0 -20Z", "#D9A863")
                 + L("M-6 -12 L6 -12 M-6 12 L6 12", "#B08040", 2) + L("M-4 -2 L4 2", "#B08040", 2),
                 f"translate({x} {y}) rotate({r})")
    return svg(G(nut(36, 50, -25) + nut(64, 54, 20), "translate(-25 -27) scale(1.5)"))


DRAWINGS = {
    "veg-carrot": carrot(), "veg-corn": corn(), "veg-broccoli": broccoli(), "veg-eggplant": eggplant(),
    "veg-bell-pepper": bell_pepper(), "veg-chili-pepper": chili(), "veg-cucumber": cucumber(),
    "veg-lettuce": lettuce(), "veg-mushroom": mushroom(), "veg-potato": potato(),
    "veg-sweet-potato": sweet_potato(), "veg-onion": onion(), "veg-garlic": garlic(),
    "veg-ginger": ginger(), "veg-beans": beans(), "veg-peanuts": peanuts(),
}
