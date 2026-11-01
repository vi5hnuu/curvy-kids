"""Feelings: one child's face per feeling, with the expression doing all the work."""
from kit import OL, svg, P, L, C, E, R, G, sym, eye, cheek, shine
from people import (SKIN, HAIR, head, dot_eyes, closed_eyes, brows, cheeks2, mouth_smile, mouth_frown,
                    mouth_open, mouth_o, tear)

CATEGORY = "feelings"


def face(i, style, hair, *parts):
    return svg(head(SKIN[i % len(SKIN)], HAIR[hair], style) + "".join(parts))


def happy():
    return face(0, "pigtails", "brown", dot_eyes(), cheeks2(), mouth_smile())


def excited():
    star = lambda x: P(f"M{x} 50 L{x + 2} 54 L{x + 6} 55 L{x + 3} 58 L{x + 4} 62 L{x} 60 L{x - 4} 62 L{x - 3} 58 "
                       f"L{x - 6} 55 L{x - 2} 54Z", "#FFC93C", 2)
    return face(1, "curly", "black", star(39) + star(61), cheeks2(), mouth_open(y=66, w=11, h=12))


def laughing():
    return face(2, "short", "black", closed_eyes(y=54), cheeks2(), mouth_open(y=64, w=12, h=13),
                tear(26, 56, .7), tear(74, 56, .7))


def scared():
    return face(0, "short", "blond",
                C(39, 56, 7, "#fff") + C(39, 57, 3.4, OL, 0) + C(61, 56, 7, "#fff") + C(61, 57, 3.4, OL, 0),
                brows(y=42, tilt=-3),
                L("M40 72 L44 69 L48 72 L52 69 L56 72 L60 69"),
                P("M78 36 Q82 44 78 46 Q74 44 78 36Z", "#7CCBF5", 2))


def surprised():
    return face(3, "bun", "black", dot_eyes(r=4.6), brows(y=40, tilt=-1), cheeks2(), mouth_o(y=71, rx=6, ry=8))


def calm():
    return face(4, "short", "black", closed_eyes(y=56, up=False), cheeks2(), mouth_smile(y=68, w=6))


def cool():
    return face(2, "short", "brown",
                P("M26 50 H74 L72 56 Q70 64 60 63 Q53 62 52 56 H48 Q47 62 40 63 Q30 64 28 56Z", OL)
                + shine("M32 54 L38 54", 2) + shine("M58 54 L64 54", 2),
                L("M42 70 Q52 74 60 68"))


def crying():
    return face(1, "pigtails", "black", closed_eyes(y=54, up=False), brows(y=44, tilt=-3),
                tear(36, 60, 1.1), tear(64, 60, 1.1),
                P("M40 76 Q50 64 60 76 Q50 72 40 76Z", "#8E2F4A"))


def frustrated():
    return face(3, "short", "black", dot_eyes(), brows(y=46, tilt=3),
                R(38, 66, 24, 9, 3, "#FFFFFF", 2.5) + L("M44 66 V75 M50 66 V75 M56 66 V75", OL, 2),
                L("M76 30 L82 24 M80 36 L88 34 M70 26 L72 18", "#F0464B", 2.5))


def angry():
    return face(0, "curly", "auburn", dot_eyes(), brows(y=46, tilt=4),
                cheek(32, 64, 6, 4) + cheek(68, 64, 6, 4), mouth_frown(y=74, w=8))


def sad():
    return face(4, "long", "black", dot_eyes(), brows(y=44, tilt=-3), tear(64, 62, .9), mouth_frown())


def worried():
    return face(2, "bun", "brown", dot_eyes(), brows(y=44, tilt=-4),
                L("M40 72 Q45 68 50 72 Q55 76 60 72"))


def nervous():
    return face(0, "short", "brown", dot_eyes(dx=11, y=56), brows(y=44, tilt=-2),
                L("M40 72 L44 69 L48 72 L52 69 L56 72 L60 69"),
                P("M80 34 Q86 44 80 48 Q74 44 80 34Z", "#7CCBF5", 2.5))


def sleepy():
    return face(1, "short", "black", closed_eyes(y=56, up=False), cheeks2(), mouth_o(y=72, rx=4, ry=4),
                L("M70 16 H80 L70 26 H80 M84 6 H90 L84 12 H90", OL, 2.5))


def sick():
    return face(0, "pigtails", "auburn",
                P("M20 54 A30 30 0 0 0 80 54 Z", "#B8E39A", 0, op=".55"),
                closed_eyes(y=54, up=False), brows(y=44, tilt=-2),
                R(56, 64, 30, 7, 3.5, "#FFFFFF", 2.5, rot=-20) + C(84, 58, 4, "#F0464B", 2.5),
                L("M38 72 Q44 68 50 72"))


def silly():
    return face(3, "pigtails", "black",
                eye(39, 56) + L("M56 56 Q61 52 66 56"),
                cheeks2(),
                mouth_smile(y=66, w=9) + P("M48 70 Q46 82 54 82 Q60 80 58 70Z", "#FF7A90"))


def loved():
    heart = lambda x: P(f"M{x} 62 C{x - 10} 54 {x - 8} 46 {x - 3} 47 Q{x} 48 {x} 52 Q{x} 48 {x + 3} 47 "
                        f"C{x + 8} 46 {x + 10} 54 {x} 62Z", "#FF5C8A", 2.5)
    return face(4, "curly", "black", heart(39) + heart(61), cheeks2(y=68), mouth_smile(y=70, w=7))


def thinking():
    return face(1, "short", "auburn",
                eye(41, 54, 4) + eye(63, 54, 4) + L("M34 44 L44 42 M58 40 L68 44"),
                L("M44 72 H56"),
                P("M38 80 Q30 88 36 94 H58 Q62 86 54 82 Q50 76 38 80Z", "#F2C291"),
                C(80, 22, 4, "#fff", 2) + C(88, 12, 2.6, "#fff", 2))


def yummy():
    return face(2, "bun", "black", closed_eyes(y=54), cheeks2(),
                mouth_smile(y=66, w=9) + P("M54 70 Q62 76 66 70 Q62 66 58 68Z", "#FF7A90"))


def hug():
    """Two children hugging."""
    left = head(SKIN[1], HAIR["black"], "short", cx=34, cy=40, r=19)
    right = head(SKIN[3], HAIR["brown"], "pigtails", cx=68, cy=42, r=19)
    return svg(
        P("M10 96 Q10 64 34 62 Q50 62 54 74 V96Z", "#4FC3F7")
        + P("M46 96 V76 Q50 64 68 64 Q92 66 92 96Z", "#FF8FC7")
        + left + right
        + closed_eyes(cx=34, y=42, dx=7) + closed_eyes(cx=68, y=44, dx=7)
        + mouth_smile(cx=34, y=49, w=4) + mouth_smile(cx=68, y=51, w=4)
        + cheek(24, 48, 3.5, 2.4) + cheek(78, 50, 3.5, 2.4)
        + tube_arm("M22 72 Q40 86 62 76", SKIN[1]) + tube_arm("M82 74 Q66 88 40 78", SKIN[3])
        + P("M50 18 C46 14 42 18 46 22 L50 26 L54 22 C58 18 54 14 50 18Z", "#FF5C8A", 2))


def tube_arm(d, skin):
    from kit import tube
    return tube(d, skin, 7)


DRAWINGS = {
    "feeling-happy": happy(), "feeling-excited": excited(), "feeling-laughing": laughing(),
    "feeling-scared": scared(), "feeling-surprised": surprised(), "feeling-calm": calm(),
    "feeling-cool": cool(), "feeling-crying": crying(), "feeling-frustrated": frustrated(),
    "feeling-angry": angry(), "feeling-sad": sad(), "feeling-worried": worried(),
    "feeling-nervous": nervous(), "feeling-sleepy": sleepy(), "feeling-sick": sick(),
    "feeling-silly": silly(), "feeling-loved": loved(), "feeling-thinking": thinking(),
    "feeling-yummy": yummy(), "feeling-hug": hug(),
}
