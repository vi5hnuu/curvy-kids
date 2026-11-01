"""Body parts: each part drawn on its own, big and simple, in the child character's skin tones."""
from kit import OL, svg, P, L, C, E, R, G, sym, flip, eye, eyes, cheek, cheeks, shine, tube
from people import SKIN, HAIR, head, dot_eyes, cheeks2, mouth_smile, closed_eyes

CATEGORY = "body"

LIP = "#E86A7E"
TONGUE = "#FF7A90"


def hand(skin=SKIN[1]):
    """An open hand, palm towards us (also used for the jobs and sports pictures)."""
    return (P("M30 92 L30 58 Q22 48 16 40 Q12 34 18 31 Q24 30 30 40 L32 44 L32 18 Q32 12 37 12 Q42 12 42 18 "
              "L42 40 L44 12 Q44 6 50 6 Q56 6 56 12 L56 40 L58 16 Q58 10 63 10 Q68 10 68 16 L68 44 L70 26 "
              "Q70 20 75 20 Q80 20 80 26 L78 64 Q76 80 70 92Z", skin)
            + L("M42 52 Q50 56 58 52", OL, 2))


def foot(skin=SKIN[2]):
    toes = "".join(C(x, y, r, skin) for x, y, r in [(30, 24, 8), (44, 18, 6.5), (56, 17, 6), (67, 19, 5.5), (76, 24, 5)])
    return svg(toes + P("M22 34 Q50 20 82 32 Q86 52 74 70 Q66 92 46 92 Q26 92 26 70 Q26 54 22 34Z", skin)
               + L("M40 72 Q48 76 56 72", OL, 2))


def arm():
    """A flexed arm showing its muscle."""
    s = SKIN[3]
    return svg(
        P("M8 64 Q24 44 44 54 Q52 46 56 38 L54 30 Q50 20 60 14 Q72 8 80 16 Q86 24 80 34 "
          "Q84 60 80 76 Q74 90 56 90 H8Z", s)
        + L("M56 38 Q62 36 66 30", OL, 2.5)
        + L("M8 64 V90", OL, 3) + R(4, 60, 10, 32, 3, "#4FC3F7"))


def leg():
    s = SKIN[1]
    return svg(
        P("M36 6 H60 L58 60 Q58 72 62 78 L78 80 Q88 82 86 90 Q84 94 76 94 H44 Q38 94 38 86 L38 60Z", s)
        + R(32, 4, 32, 26, 6, "#4FC3F7")
        + L("M44 58 Q48 60 52 58", OL, 2))


def ear():
    s = SKIN[0]
    return svg(
        P("M38 10 Q76 6 76 42 Q76 58 64 66 Q58 70 58 80 Q56 94 42 92 Q28 90 30 76", s)
        + P("M30 76 Q26 60 34 50 Q24 34 30 18 Q34 10 38 10", s)
        + P("M44 24 Q64 20 64 42 Q62 52 54 56 Q48 60 48 66", "none", 3)
        + P("M48 36 Q56 36 54 46", "none", 3))


def big_eye(cx=50, cy=50, iris="#6B3E26"):
    return (P(f"M{cx - 36} {cy} Q{cx} {cy - 34} {cx + 36} {cy} Q{cx} {cy + 34} {cx - 36} {cy}Z", "#FFFFFF")
            + C(cx, cy, 14, iris) + C(cx, cy, 7, OL, 0) + C(cx + 4, cy - 4, 3, "#fff", 0)
            + L(f"M{cx - 30} {cy - 12} L{cx - 36} {cy - 20} M{cx - 14} {cy - 20} L{cx - 16} {cy - 30} "
                f"M{cx + 4} {cy - 22} L{cx + 4} {cy - 32} M{cx + 22} {cy - 18} L{cx + 26} {cy - 28}"))


def eye_one():
    return svg(big_eye())


def eyes_pair():
    return svg(G(big_eye(50, 50, "#3E7DD6"), "translate(26 52) scale(.6) translate(-50 -50)")
               + G(big_eye(50, 50, "#3E7DD6"), "translate(74 52) scale(.6) translate(-50 -50)"))


def nose():
    s = SKIN[2]
    return svg(
        P("M50 10 Q56 10 58 30 L66 62 Q80 64 80 76 Q80 88 66 88 Q58 92 50 88 Q42 92 34 88 Q20 88 20 76 Q20 64 34 62 L42 30 Q44 10 50 10Z", s)
        + E(40, 78, 5, 3.5, "#7C4E33", 0) + E(60, 78, 5, 3.5, "#7C4E33", 0)
        + shine("M50 30 L50 52", 3))


def mouth():
    return svg(
        P("M10 50 Q30 26 50 36 Q70 26 90 50 Q70 82 50 82 Q30 82 10 50Z", LIP)
        + P("M18 50 Q50 58 82 50 Q70 70 50 70 Q30 70 18 50Z", "#8E2F4A", 2.5)
        + R(32, 50, 36, 8, 3, "#FFFFFF", 2)
        + shine("M30 40 Q38 36 44 40", 3))


def tongue():
    return svg(
        P("M14 30 Q50 14 86 30 Q70 50 50 50 Q30 50 14 30Z", LIP)
        + P("M30 40 Q30 92 50 92 Q70 92 70 40Z", TONGUE)
        + L("M50 44 V70", "#E0566E", 3)
        + P("M14 30 Q50 44 86 30", "none", 3))


def tooth():
    return svg(
        P("M24 22 Q24 8 38 10 Q50 14 62 10 Q76 8 76 22 Q78 44 70 56 Q66 76 62 90 Q56 94 54 84 L50 66 L46 84 Q44 94 38 90 Q34 76 30 56 Q22 44 24 22Z", "#FFFFFF")
        + eyes(36, 9, r=3.4) + cheeks(44, 15, rx=3.5, ry=2.4) + L("M45 46 Q50 50 55 46")
        + shine("M32 20 Q30 28 32 34", 3))


def bone():
    end = "M0 -14 A9 9 0 1 0 -12 -4 A9 9 0 1 0 -12 14 L0 4Z"
    return svg(G(P("M22 -8 H78 V8 H22Z", "#FFF8EC", 0)
                 + P("M22 -8 Q18 -22 8 -20 Q-4 -16 4 -4 Q-6 4 4 12 Q16 22 22 8 H78 Q84 22 96 12 Q104 4 96 -4 "
                     "Q104 -16 92 -20 Q82 -22 78 -8Z", "#FFF8EC"),
                 "translate(50 50) rotate(-35) translate(-50 0)"))


def heart():
    """A drawn heart, the way children picture it."""
    return svg(
        P("M50 88 C20 66 8 50 10 32 C12 16 28 10 40 16 Q46 20 50 28 Q54 20 60 16 C72 10 88 16 90 32 C92 50 80 66 50 88Z", "#F0464B")
        + shine("M22 30 Q24 22 32 20", 4))


def lungs():
    return svg(
        tube("M50 8 V34 M50 34 Q40 36 36 44 M50 34 Q60 36 64 44", "#F4C9D4", 4)
        + P("M40 28 Q20 26 14 52 Q10 78 22 88 Q34 92 42 82 Q46 60 44 40Z", "#FF8FAE")
        + P("M60 28 Q80 26 86 52 Q90 78 78 88 Q66 92 58 82 Q54 60 56 40Z", "#FF8FAE")
        + L("M36 44 L28 56 M36 52 L32 66 M64 44 L72 56 M64 52 L68 66", "#E06A8A", 2.5))


def brain():
    return svg(
        P("M50 18 Q36 8 26 18 Q12 18 12 34 Q4 44 12 56 Q10 70 24 74 Q32 86 46 80 L50 78 L54 80 Q68 86 76 74 "
          "Q90 70 88 56 Q96 44 88 34 Q88 18 74 18 Q64 8 50 18Z", "#FFB3C7")
        + L("M50 20 V78", "#E06A8A", 3)
        + L("M26 32 Q34 30 36 38 M20 50 Q30 46 36 54 M28 66 Q36 60 42 66 M74 32 Q66 30 64 38 M80 50 Q70 46 64 54 M72 66 Q64 60 58 66", "#E06A8A", 2.5))


def baby():
    return svg(
        body("#BDEBFF", top=78, w=30)
        + head(SKIN[0], HAIR["brown"], "bald", cy=50, r=32)
        + L("M50 18 Q46 10 52 8 Q56 12 50 16", HAIR["brown"], 3)
        + closed_eyes(y=52, dx=12, up=False) + cheeks2(y=62, dx=19) + mouth_smile(y=64, w=5)
        + C(50, 74, 6, "#FF8FC7"))


from people import body  # noqa: E402  (kept next to baby, its only user)

DRAWINGS = {
    "body-hand": svg(hand()), "body-foot": foot(), "body-arm": arm(), "body-leg": leg(),
    "body-ear": ear(), "body-eye": eye_one(), "body-eyes": eyes_pair(), "body-nose": nose(),
    "body-mouth": mouth(), "body-tongue": tongue(), "body-tooth": tooth(), "body-bone": bone(),
    "body-heart": heart(), "body-lungs": lungs(), "body-brain": brain(), "body-baby": baby(),
}
