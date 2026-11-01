"""Insects and small crawlies: seen from above (beetles, ladybug) or the side (ant, snail)."""
from kit import (OL, PALETTE as K, svg, P, L, C, E, R, G, sym, flip, eyes, eye, cheeks, cheek,
                 smile, shine, scallop, tube)

CATEGORY = "insects"


def feelers(x1, y1, x2, y2, dx=8, dy=-14):
    return (L(f"M{x1} {y1} Q{x1 - dx / 2} {y1 + dy} {x1 - dx} {y1 + dy}")
            + L(f"M{x2} {y2} Q{x2 + dx / 2} {y2 + dy} {x2 + dx} {y2 + dy}")
            + C(x1 - dx, y1 + dy, 3, OL, 0) + C(x2 + dx, y2 + dy, 3, OL, 0))


def bee():
    return svg(
        sym(E(36, 30, 13, 17, "#DFF5FF", rot=-30, op=".9"))
        + feelers(44, 24, 56, 24, 6, -12)
        + P("M50 88 L46 80 L54 80Z", OL, 0)
        + E(50, 56, 22, 26, "#FFD84D")
        + P("M30 50 Q50 56 70 50 L72 58 Q50 64 28 58Z", OL, 0)
        + P("M32 68 Q50 74 68 68 L64 76 Q50 80 36 76Z", OL, 0)
        + C(50, 30, 14, "#FFD84D")
        + eyes(30, 6, r=3.2) + cheeks(36, 10, rx=3.2, ry=2.2) + smile(50, 36, 3))


def ladybug():
    return svg(
        sym(L("M30 46 L16 40 M28 60 L14 62 M32 74 L20 82", OL, 3))
        + C(50, 28, 13, OL, 0)
        + feelers(44, 18, 56, 18, 6, -8)
        + C(50, 58, 28, "#F0464B")
        + L("M50 32 V86", OL, 3)
        + sym(C(38, 46, 5, OL, 0) + C(34, 64, 6, OL, 0) + C(42, 78, 4, OL, 0))
        + eyes(26, 5, r=2.8, ring=True)
        + shine("M64 40 Q70 44 72 52", 3))


def beetle():
    return svg(
        sym(L("M30 44 L14 36 M30 58 L12 60 M32 72 L16 84", OL, 3))
        + feelers(44, 18, 56, 18, 8, -10)
        + C(50, 26, 11, "#2E7D5A")
        + E(50, 60, 24, 30, "#3ECF8E")
        + L("M50 32 V90", OL, 3)
        + shine("M38 42 Q34 52 36 64", 3)
        + eyes(25, 5, r=2.8, ring=True))


def cockroach():
    return svg(
        sym(L("M32 44 L14 30 M30 58 L10 60 M32 72 L14 88", OL, 3))
        + L("M44 22 Q30 2 14 6 M56 22 Q70 2 86 6")
        + C(50, 26, 10, "#7A4A32")
        + E(50, 60, 20, 30, "#A0663D")
        + L("M50 34 V88", OL, 3)
        + eyes(24, 4.5, r=2.6, ring=True))


def ant():
    return svg(
        L("M38 58 L28 76 M50 58 V80 M60 58 L70 78", OL, 3)
        + L("M26 34 Q20 18 12 16 M30 32 Q30 16 24 10", OL, 2.5)
        + E(72, 56, 16, 13, "#A0503A") + C(50, 56, 9, "#A0503A") + C(30, 42, 13, "#A0503A")
        + eye(26, 40, 3.4) + cheek(32, 47, 3.5, 2.4) + smile(22, 47, 2.5))


def spider():
    legs = sym("".join(L(f"M42 {y} Q{24} {y - 14} {12} {y + 6}", OL, 3) for y in (46, 54, 62, 70)))
    return svg(
        L("M50 4 V30", "#A9B4C2", 2)
        + legs + C(50, 58, 20, "#5E5A6E")
        + C(50, 34, 10, "#5E5A6E")
        + eyes(56, 7, r=4) + cheeks(66, 12, rx=3.5, ry=2.4) + smile(50, 66, 4))


def butterfly():
    return svg(
        L("M47 30 Q40 14 34 12 M53 30 Q60 14 66 12")
        + sym(P("M48 46 Q30 14 12 24 Q6 44 46 52Z", "#FF8FC7") + C(26, 34, 5, "#FFD84D", 2)
              + P("M48 54 Q16 54 20 76 Q34 88 48 62Z", "#9B6BFF") + C(32, 70, 4, "#FFD84D", 2))
        + E(50, 54, 5, 24, "#3B2F5B")
        + C(50, 30, 6, "#3B2F5B"))


def caterpillar():
    xs = (84, 70, 56, 42)
    body = "".join(C(x, 58 - (6 if i % 2 else 0), 12, "#7ED957" if i % 2 else "#5DBB4A") for i, x in enumerate(xs))
    return svg(
        "".join(L(f"M{x} 66 V76", OL, 3) for x in xs)
        + body
        + C(26, 50, 16, "#7ED957")
        + feelers(20, 36, 32, 36, 4, -10)
        + eyes(48, 6, cx=26, r=3.2) + cheeks(56, 10, cx=26, rx=3.2, ry=2.2) + smile(26, 56, 3))


def worm():
    return svg(
        P("M6 84 H94", "none", 0) + R(6, 76, 88, 14, 7, "#B5794A", 0)
        + tube("M84 76 Q76 56 62 64 Q48 72 40 56 Q34 42 30 40", "#FF9EB0", 12)
        + C(28, 38, 12, "#FF9EB0")
        + eye(25, 35, 3.4) + cheek(31, 42, 3.5, 2.4) + smile(22, 43, 2.5))


def snail():
    return svg(
        P("M8 78 Q12 66 24 66 L84 66 Q92 70 88 78Z", "#C9B8F0")
        + L("M18 66 Q14 42 10 34 M24 66 Q24 44 22 36")
        + C(10, 33, 3, OL, 0) + C(22, 35, 3, OL, 0)
        + C(60, 48, 24, "#FF9F43")
        + L("M60 48 m0 -4 a4 4 0 1 1 -4 6 a9 9 0 1 1 14 -10 a14 14 0 1 1 -22 12", OL, 3)
        + eye(20, 56, 3.4) + cheek(26, 62, 3.5, 2.4))


def fly():
    return svg(
        sym(E(30, 36, 12, 20, "#DFF5FF", rot=-40, op=".9"))
        + sym(L("M40 60 L28 70 M42 68 L32 82", OL, 3))
        + E(50, 62, 14, 20, "#4A5C6A")
        + L("M38 60 H62 M40 70 H60", "#7D93A4", 2)
        + C(50, 36, 12, "#4A5C6A")
        + sym(C(42, 34, 6, "#F0464B", 2))
        + smile(50, 42, 3))


def mosquito():
    return svg(
        sym(E(34, 40, 10, 18, "#DFF5FF", rot=-35, op=".9"))
        + sym(L("M44 56 L28 72 L22 90 M46 62 L36 80 L34 92", OL, 2.5))
        + L("M40 30 L18 44", OL, 2.5)
        + E(56, 58, 9, 18, "#7D93A4", rot=-25)
        + C(44, 34, 9, "#7D93A4")
        + eye(41, 31, 3.2))


def cricket():
    return svg(
        L("M30 38 Q18 16 8 12 M34 36 Q28 16 22 8", OL, 2.5)
        + L("M40 62 L34 80 M50 64 L50 82", OL, 3)
        + P("M60 52 L86 30 L92 34 L76 74Z", "#5DBB4A")
        + P("M30 52 Q36 40 60 42 Q86 46 88 58 Q70 66 40 64 Q30 62 30 52Z", "#7ED957")
        + C(32, 46, 12, "#7ED957")
        + eye(28, 43, 3.4) + cheek(34, 51, 3.5, 2.4))


def scorpion():
    return svg(
        tube("M62 64 Q90 60 86 34 Q84 20 70 22", "#E0A040", 7)
        + P("M70 16 L60 22 L72 28Z", OL, 0)
        + sym(L("M40 66 L28 80 M46 70 L38 84", OL, 2.5))
        + L("M36 58 Q20 52 18 40", OL, 3)
        + P("M10 30 Q10 22 18 26 Q24 32 18 40 Q8 40 10 30Z", "#E0A040")
        + E(50, 62, 18, 12, "#E0A040")
        + eyes(56, 5, cx=46, r=2.6))


DRAWINGS = {
    "insect-bee": bee(), "insect-ladybug": ladybug(), "insect-beetle": beetle(),
    "insect-cockroach": cockroach(), "insect-ant": ant(), "insect-spider": spider(),
    "insect-butterfly": butterfly(), "insect-caterpillar": caterpillar(), "insect-worm": worm(),
    "insect-snail": snail(), "insect-fly": fly(), "insect-mosquito": mosquito(),
    "insect-cricket": cricket(), "insect-scorpion": scorpion(),
}
