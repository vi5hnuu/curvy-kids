"""Clothes: each item laid flat, front-on, in one bright colour with a simple detail."""
from kit import OL, svg, P, L, C, E, R, G, sym, flip, shine, tube

CATEGORY = "clothes"


def t_shirt():
    return svg(P("M34 14 Q50 24 66 14 L90 28 L80 46 L70 40 V88 H30 V40 L20 46 L10 28Z", "#4FC3F7")
               + P("M34 14 Q50 28 66 14", "none", 3) + C(50, 56, 8, "#FFD84D", 2.5))


def blouse():
    return svg(P("M36 12 L50 24 L64 12 L86 24 L80 52 L72 48 L74 88 H26 L28 48 L20 52 L14 24Z", "#FF8FC7")
               + P("M36 12 L50 30 L64 12", "none", 3)
               + C(50, 44, 2.5, OL, 0) + C(50, 56, 2.5, OL, 0) + C(50, 68, 2.5, OL, 0))


def dress():
    return svg(P("M38 10 V24 Q36 34 40 40 L18 88 Q50 96 82 88 L60 40 Q64 34 62 24 V10 L54 14 Q50 18 46 14Z", "#9B6BFF")
               + L("M40 40 H60", OL, 3) + C(36, 70, 3, "#FFD84D", 0) + C(56, 62, 3, "#FFD84D", 0) + C(64, 80, 3, "#FFD84D", 0)
               + C(46, 82, 3, "#FFD84D", 0))


def coat():
    return svg(P("M34 10 L50 20 L66 10 L86 22 L90 86 H72 V90 H28 V86 H10 L14 22Z", "#C47A30")
               + P("M34 10 L50 34 L66 10", "#A0602A", 3)
               + L("M50 34 V90", OL, 3)
               + C(44, 46, 2.6, OL, 0) + C(44, 60, 2.6, OL, 0) + C(44, 74, 2.6, OL, 0)
               + R(56, 62, 14, 10, 2, "#A0602A", 2.5) + R(30, 62, 14, 10, 2, "#A0602A", 2.5, op="0"))


def jeans():
    return svg(P("M24 10 H76 L82 90 H58 L50 36 L42 90 H18Z", "#3E7DD6")
               + L("M24 20 H76", OL, 3) + L("M34 20 Q34 30 26 32 M66 20 Q66 30 74 32", "#BDEBFF", 2)
               + C(50, 15, 2.2, "#FFC93C", 0))


def socks():
    sock = lambda x, c: P(f"M{x} 10 H{x + 20} V54 L{x + 32} 70 Q{x + 36} 82 {x + 24} 84 L{x + 6} 76 Q{x} 70 {x} 62Z", c) \
        + R(x, 10, 20, 10, 2, "#FFFFFF", 2.5) + L(f"M{x} 34 H{x + 20} M{x} 44 H{x + 20}", "#FFFFFF", 3)
    return svg(sock(14, "#F0464B") + sock(48, "#FF9F43"))


def gloves():
    glove = lambda: P("M24 90 V64 L14 48 Q10 42 16 40 Q22 40 26 48 V22 Q26 16 31 16 Q36 16 36 22 V40 V16 Q36 10 41 10 "
                      "Q46 10 46 16 V40 V18 Q46 12 51 12 Q56 12 56 18 V40 V26 Q56 20 61 20 Q66 20 66 26 V64 L62 90Z", "#7ED957")
    return svg(G(glove(), "translate(4 0) scale(.82)") + G(flip(glove()), "translate(18 8) scale(.82)")
               + L("M23 70 H55 M42 78 H76", "#FFFFFF", 0))


def scarf():
    return svg(P("M20 30 Q50 44 80 30 Q86 40 80 48 Q50 60 20 48 Q14 40 20 30Z", "#F0464B")
               + P("M58 50 L70 88 L54 92 L46 54Z", "#F0464B")
               + L("M56 88 V96 M62 86 V94 M68 84 V92", OL, 2.5)
               + L("M28 36 L32 48 M40 40 L42 52 M66 40 L64 52", "#FFFFFF", 3))


def necktie():
    return svg(P("M40 8 H60 L56 20 H44Z", "#2E5AAC")
               + P("M44 20 H56 L66 74 L50 92 L34 74Z", "#2E5AAC")
               + L("M46 34 L58 42 M42 50 L60 60 M38 66 L62 78", "#FFC93C", 3))


def shoe():
    return svg(P("M8 74 Q8 52 20 50 L36 48 Q44 30 60 32 Q70 40 74 52 Q92 56 92 74Z", "#7A4A32")
               + R(6, 74, 88, 10, 4, "#4A2E22") + shine("M54 40 Q60 38 66 42", 3))


def sneaker():
    return svg(P("M8 70 Q8 50 22 50 Q36 52 42 36 Q54 30 64 36 Q74 50 92 58 Q96 66 92 72Z", "#F0464B")
               + P("M6 72 H94 Q94 84 84 84 H14 Q6 84 6 72Z", "#FFFFFF")
               + L("M46 40 L54 46 M42 46 L50 52 M38 52 L46 58", "#FFFFFF", 3)
               + P("M60 54 Q72 56 82 64", "#FFFFFF", 3))


def boot():
    return svg(P("M28 8 H62 V60 Q86 62 90 76 V88 H24 V60Z", "#FFC93C")
               + R(22, 84, 70, 8, 3, "#C9821E") + L("M28 18 H62", OL, 3))


def hiking_boot():
    return svg(P("M24 14 H62 L64 56 Q86 58 90 72 V84 H20 V54Z", "#A0663D")
               + R(18, 82, 76, 10, 3, "#4A2E22")
               + L("M38 22 L52 26 M38 32 L52 36 M38 42 L52 46", "#FFC93C", 3)
               + R(22, 10, 42, 8, 3, "#F0464B"))


def heels():
    return svg(P("M10 70 Q12 52 30 56 Q50 62 62 52 L84 32 Q92 32 92 42 L86 54 V88 H80 V60 Q66 76 48 78 H14Z", "#F0464B")
               + shine("M24 62 Q34 64 42 64", 3))


def sandal():
    return svg(P("M24 92 Q14 70 20 36 Q26 10 48 12 Q66 16 70 42 Q74 74 64 92Z", "#C47A30")
               + tube("M24 50 Q46 30 68 50", "#FF8FC7", 6) + tube("M42 22 V44", "#FF8FC7", 6)
               + C(42, 44, 5, "#FFD84D"))


def cap():
    return svg(P("M16 60 Q16 22 54 22 Q86 22 86 60Z", "#F0464B")
               + P("M16 60 Q50 56 86 60 Q96 74 76 76 Q50 70 16 60Z", "#C42E36")
               + C(52, 22, 4, "#C42E36", 2.5) + L("M52 26 Q40 40 38 58 M52 26 Q64 40 66 58", OL, 2))


def sun_hat():
    return svg(E(50, 62, 44, 14, "#F2D27A")
               + P("M26 62 Q26 26 50 26 Q74 26 74 62Z", "#F2D27A")
               + P("M26 54 Q50 60 74 54 V62 Q50 66 26 62Z", "#FF8FC7", 2.5)
               + C(68, 54, 6, "#FFFFFF", 2.5) + C(68, 54, 2.5, "#FFD84D", 0))


def top_hat():
    return svg(E(50, 80, 40, 9, "#3B2F5B")
               + P("M26 80 L28 18 Q50 12 72 18 L74 80Z", "#3B2F5B", 3)
               + R(27, 62, 46, 10, 0, "#F0464B", 3)
               + E(50, 18, 22, 5, "#5E5A6E", 3))


def crown():
    """A gold crown with jewels (also the letter Q, for Queen)."""
    return svg(P("M14 34 L30 54 L50 22 L70 54 L86 34 L80 80 H20Z", "#FFC93C")
               + R(18, 72, 64, 12, 3, "#F2A81E")
               + C(14, 32, 5, "#FFC93C") + C(50, 20, 5, "#FFC93C") + C(86, 32, 5, "#FFC93C")
               + C(36, 78, 3.5, "#F0464B", 2) + C(50, 78, 3.5, "#4FC3F7", 2) + C(64, 78, 3.5, "#7ED957", 2)
               + shine("M30 66 L36 60", 3))


def glasses():
    return svg(L("M10 40 L20 48 M90 40 L80 48", OL, 3)
               + C(32, 54, 16, "#BDEBFF", 4) + C(68, 54, 16, "#BDEBFF", 4)
               + P("M46 50 Q50 46 54 50", "none", 4)
               + shine("M24 50 Q26 44 32 44", 3) + shine("M60 50 Q62 44 68 44", 3))


def handbag():
    return svg(P("M34 40 Q34 14 50 14 Q66 14 66 40", "none", 6) + L("M34 40 Q34 14 50 14 Q66 14 66 40", "#C2566E", 2)
               + P("M18 40 H82 L76 88 H24Z", "#FF5C8A")
               + P("M18 40 H82 L74 56 H26Z", "#E8406E", 3)
               + R(44, 50, 12, 10, 2, "#FFC93C", 2.5))


def backpack():
    return svg(P("M36 22 Q36 8 50 8 Q64 8 64 22", "none", 5)
               + R(20, 20, 60, 72, 18, "#4FC3F7")
               + R(30, 54, 40, 28, 8, "#2E9FD8")
               + L("M30 64 H70", OL, 2.5) + R(46, 60, 8, 8, 2, "#FFC93C", 2)
               + L("M36 30 Q50 26 64 30", "#FFFFFF", 3))


DRAWINGS = {
    "clothes-t-shirt": t_shirt(), "clothes-blouse": blouse(), "clothes-dress": dress(), "clothes-coat": coat(),
    "clothes-jeans": jeans(), "clothes-socks": socks(), "clothes-gloves": gloves(), "clothes-scarf": scarf(),
    "clothes-necktie": necktie(), "clothes-shoe": shoe(), "clothes-sneaker": sneaker(), "clothes-boot": boot(),
    "clothes-hiking-boot": hiking_boot(), "clothes-heels": heels(), "clothes-sandal": sandal(),
    "clothes-cap": cap(), "clothes-sun-hat": sun_hat(), "clothes-top-hat": top_hat(), "clothes-crown": crown(),
    "clothes-glasses": glasses(), "clothes-handbag": handbag(), "clothes-backpack": backpack(),
}
