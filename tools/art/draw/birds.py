"""Birds: round side-on bodies with one big eye, so each bird reads by its colour, beak and tail."""
from kit import tube, OL, PALETTE as K, svg, P, L, C, E, R, G, sym, flip, eyes, eye, cheeks, cheek, smile, shine, scallop

CATEGORY = "birds"

BEAK = "#FFA726"


def legs(x1, x2, y, length=10, color=BEAK):
    """Two stick legs with three-toed feet."""
    s = ""
    for x in (x1, x2):
        s += L(f"M{x} {y} V{y + length} M{x - 4} {y + length + 2} L{x} {y + length} L{x + 4} {y + length + 2}", color, 3)
    return s


def chick(fill="#FFD84D"):
    """A round baby chick facing the viewer."""
    return (legs(43, 57, 80, 6)
            + C(50, 56, 26, fill)
            + P("M48 30 Q46 22 50 20 Q52 26 54 22 Q56 28 52 31Z", fill)
            + sym(P("M25 58 Q18 64 24 70 Q30 70 32 64Z", "#FFC31F"))
            + eyes(50, 9) + cheeks(60, 16)
            + P("M45 57 L55 57 L50 64Z", BEAK))


def baby_chick():
    return svg(chick())


def hatching_chick():
    return svg(
        C(50, 46, 20, "#FFD84D")
        + P("M48 26 Q46 18 50 16 Q52 22 54 18 Q56 24 52 27Z", "#FFD84D")
        + eyes(43, 7, r=3.6) + cheeks(51, 13, rx=3.5, ry=2.4)
        + P("M46 49 L54 49 L50 55Z", BEAK)
        + P("M22 56 L30 50 L37 57 L45 50 L52 57 L60 50 L68 57 L78 52 Q82 84 50 88 Q18 84 22 56Z", "#FFF8EC")
        + shine("M30 66 Q31 76 38 81", 3))


def chick_front():
    """'chick': the chick standing side-on, so it differs from baby-chick."""
    return svg(
        legs(46, 56, 76, 8)
        + E(52, 60, 24, 19, "#FFD84D")
        + C(36, 40, 15, "#FFD84D")
        + P("M64 58 Q78 52 76 66 Q70 72 62 66Z", "#FFC31F")
        + P("M22 38 L13 41 L22 44Z", BEAK)
        + eye(33, 37) + cheek(37, 46, 4, 2.6)
        + P("M34 25 Q34 18 38 18 Q38 23 40 26", "#FFD84D"))


def songbird(body, belly, wing, beak=BEAK, tail=None, crest=False):
    """A small perching bird facing left (bird, blackbird, parrot share it)."""
    tail = tail or wing
    return (legs(48, 58, 74, 9, "#8A7E70")
            + P("M70 60 L92 72 L88 80 L66 70Z", tail)
            + E(54, 56, 26, 21, body)
            + E(50, 64, 15, 11, belly, 0)
            + (P("M34 22 Q36 12 44 14 Q40 18 42 24", body) if crest else "")
            + C(38, 40, 17, body)
            + P("M58 50 Q74 46 78 60 Q66 68 56 62Z", wing)
            + P("M22 38 L10 43 L22 47Z", beak)
            + eye(34, 37, ring=body == "#3E3A4F") + cheek(38, 46, 4, 2.6))


def bird():
    return svg(songbird("#4FC3F7", "#BDEBFF", "#2E9FD8"))


def blackbird():
    return svg(songbird("#3E3A4F", "#55506A", "#2A2738", beak="#FFB300"))


def parrot():
    return svg(
        P("M54 70 L60 94 L68 94 L64 70Z", "#2E7DF0") + P("M46 70 L48 92 L56 92 L56 70Z", "#F0464B")
        + E(52, 56, 20, 24, "#3ECF5A")
        + C(48, 32, 16, "#F0464B")
        + P("M60 46 Q74 54 68 76 Q58 70 56 54Z", "#2E7DF0")
        + P("M34 28 Q24 30 26 42 Q30 46 34 38Z", "#FFF3C4")
        + C(44, 30, 6, "#fff", 0) + eye(44, 30, 3.6)
        + cheek(48, 40, 4, 2.6)
        + L("M42 82 H64", "#8A6A4A", 5))


def dove():
    return svg(
        P("M66 54 L90 44 L88 58 L70 64Z", "#FFFFFF")
        + E(52, 58, 24, 16, "#FFFFFF")
        + C(32, 46, 13, "#FFFFFF")
        + P("M50 52 Q58 22 80 18 Q74 40 62 56Z", "#F2F6FF")
        + P("M20 46 L10 49 L20 52Z", BEAK)
        + eye(29, 43, 3.6) + cheek(32, 52, 4, 2.6)
        + L("M14 52 Q8 60 12 66 M14 60 Q20 62 22 58", "#7ED957", 3)
        + E(10, 62, 3, 5, "#7ED957", 2))


def duck():
    return svg(
        P("M10 70 Q50 90 90 70", "none", 0) + E(50, 80, 40, 6, "#8ED8FF", 0, op=".7")
        + E(56, 62, 28, 17, "#FFD84D")
        + P("M80 56 L92 48 L88 64Z", "#FFD84D")
        + C(34, 38, 15, "#FFD84D")
        + P("M54 56 Q68 50 76 62 Q64 70 54 64Z", "#FFC31F")
        + P("M22 40 Q8 40 8 45 Q10 49 22 47Z", BEAK)
        + eye(31, 35) + cheek(36, 44, 4, 2.6))


def goose():
    return svg(
        legs(52, 62, 76, 10, BEAK)
        + E(58, 64, 26, 15, "#FFFFFF")
        + P("M80 58 L92 52 L88 66Z", "#FFFFFF")
        + P("M36 62 Q30 40 32 26 L44 26 Q44 44 48 58Z", "#FFFFFF")
        + C(38, 24, 10, "#FFFFFF")
        + P("M28 22 L14 26 L28 30Z", BEAK)
        + P("M56 60 Q70 54 80 64 Q68 72 56 66Z", "#E4ECF5")
        + eye(36, 21, 3.4))


def swan():
    return svg(
        E(50, 82, 40, 6, "#8ED8FF", 0, op=".7")
        + P("M24 70 Q50 86 82 72 Q94 52 78 42 Q64 56 48 60 Q30 60 24 70Z", "#FFFFFF")
        + tube("M44 64 Q32 44 36 28 Q38 16 48 16 Q56 18 55 25", "#FFFFFF", 7)
        + P("M54 24 L64 30 L54 30Z", "#FF8A3D")
        + eye(47, 21, 3)
        + P("M60 66 Q70 52 82 54 Q78 66 64 70Z", "#EEF3FA"))


def eagle():
    return svg(
        P("M50 44 Q24 30 6 40 Q20 48 30 58 Z", "#7A4A32")
        + P("M50 44 Q76 30 94 40 Q80 48 70 58 Z", "#7A4A32")
        + P("M42 80 L50 92 L58 80Z", "#F4F1F8")
        + E(50, 62, 15, 21, "#8B5A3C")
        + C(50, 34, 14, "#FFFFFF")
        + P("M46 38 Q50 50 58 46 Q60 40 54 38Z", "#FFC93C")
        + eyes(31, 6, r=3.2)
        + L("M38 28 L46 31 M62 28 L54 31"))


def flamingo():
    return svg(
        L("M54 64 V92 M54 78 L62 82", "#F06292", 3)
        + E(58, 56, 22, 14, "#FF8FB8")
        + P("M70 50 Q84 46 86 58 Q78 62 70 60Z", "#FF6FA0")
        + tube("M42 54 Q30 40 38 28 Q44 18 34 14", "#FF8FB8", 6)
        + C(32, 16, 8, "#FF8FB8")
        + P("M24 14 Q16 18 20 26 L26 20Z", "#3B2F5B")
        + eye(32, 14, 2.8))


def peacock():
    fan = "".join(
        G(E(50, 34, 6, 22, "#3ECF5A", 2) + C(50, 20, 5, "#2E7DF0", 2) + C(50, 20, 2, "#FFC93C", 0),
          f"rotate({a} 50 62)") for a in range(-75, 80, 25))
    return svg(
        fan
        + E(50, 66, 12, 18, "#2E7DF0")
        + C(50, 44, 10, "#2E7DF0")
        + L("M50 34 V26 M46 34 L42 27 M54 34 L58 27", OL, 2)
        + C(50, 25, 2, "#2E7DF0", 0) + C(42, 26, 2, "#2E7DF0", 0) + C(58, 26, 2, "#2E7DF0", 0)
        + eyes(43, 4, r=2.6)
        + P("M47 48 L53 48 L50 53Z", BEAK))


def hen_body(comb_big=False, tail=("#F0464B",)):
    return (legs(48, 58, 76, 10)
            + "".join(P(f"M64 54 Q{90 - i * 2} {22 + i * 10} {90 - i * 2} {56 + i * 6}Z", c)
                      for i, c in enumerate(tail))
            + E(54, 60, 26, 20, "#FFFFFF")
            + C(36, 40, 14, "#FFFFFF")
            + P("M28 28 Q28 18 34 22 Q36 14 40 22 Q46 18 44 30Z" if comb_big else "M30 28 Q32 22 36 26 Q40 20 42 28Z", "#F0464B")
            + P("M30 48 Q28 56 33 56 Q36 52 34 48Z", "#F0464B")
            + P("M22 38 L13 42 L22 45Z", BEAK)
            + P("M54 56 Q66 52 72 62 Q62 70 54 64Z", "#F2EDE4")
            + eye(33, 37) + cheek(38, 45, 4, 2.6))


def hen():
    return svg(hen_body())


def rooster():
    return svg(hen_body(True, ("#2E7DF0", "#3ECF5A", "#F0464B")))


def turkey():
    fan = "".join(G(E(50, 32, 9, 24, c, 2.5), f"rotate({a} 50 60)")
                  for a, c in zip(range(-70, 75, 28), ["#F0464B", "#FF9F43", "#FFC93C", "#FF9F43", "#F0464B", "#FFC93C"]))
    return svg(
        fan + legs(44, 56, 80, 8)
        + C(50, 62, 20, "#8B5A3C")
        + C(50, 42, 11, "#A0663D")
        + eyes(40, 4.5, r=2.8)
        + P("M47 44 L53 44 L50 49Z", BEAK)
        + P("M51 48 Q56 54 52 58 Q48 54 51 48Z", "#F0464B"))


def owl():
    return svg(
        sym(P("M28 30 L24 14 L40 24Z", "#9B6B4A"))
        + E(50, 54, 28, 32, "#9B6B4A")
        + E(50, 64, 16, 18, "#F2D9B8", 0)
        + L("M44 60 Q46 63 48 60 M52 60 Q54 63 56 60 M46 68 Q48 71 50 68 M50 68 Q52 71 54 68", "#C9A27A", 2)
        + sym(C(38, 40, 11, "#FFFFFF") + C(39, 40, 5, OL, 0) + C(40.5, 38.5, 1.8, "#fff", 0))
        + P("M46 48 L54 48 L50 56Z", BEAK)
        + sym(P("M22 50 Q16 66 26 76 Q30 64 26 50Z", "#7A4A32"))
        + legs(42, 58, 84, 2))


def penguin():
    return svg(
        sym(P("M26 50 Q14 62 20 72 Q26 66 28 58Z", "#3B2F5B"))
        + E(50, 54, 25, 34, "#3B2F5B")
        + E(50, 62, 17, 25, "#FFFFFF", 0)
        + P("M36 38 Q50 26 64 38 Q60 46 50 44 Q40 46 36 38Z", "#FFFFFF", 0)
        + eyes(38, 7, r=3.4) + cheeks(46, 12, rx=3.5, ry=2.4)
        + P("M46 42 L54 42 L50 48Z", BEAK)
        + E(42, 88, 7, 3.5, BEAK) + E(58, 88, 7, 3.5, BEAK))


DRAWINGS = {
    "bird-baby-chick": baby_chick(), "bird-chick": chick_front(), "bird-hatching-chick": hatching_chick(),
    "bird-bird": bird(), "bird-blackbird": blackbird(), "bird-parrot": parrot(), "bird-dove": dove(),
    "bird-duck": duck(), "bird-goose": goose(), "bird-swan": swan(), "bird-eagle": eagle(),
    "bird-flamingo": flamingo(), "bird-peacock": peacock(), "bird-hen": hen(), "bird-rooster": rooster(),
    "bird-turkey": turkey(), "bird-owl": owl(), "bird-penguin": penguin(),
}
