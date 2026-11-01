"""Musical instruments, plus the two Indian drums used by the Hindi letters (डमरू, ढोल)."""
from kit import OL, svg, P, L, C, E, R, G, sym, flip, shine, tube

CATEGORY = "instruments"

WOOD = "#C47A30"


def guitar():
    return svg(G(
        R(46, 2, 8, 40, 2, "#7A4A32") + R(43, 0, 14, 10, 3, "#7A4A32", 2.5)
        + P("M50 38 C34 38 34 52 40 58 C26 62 26 92 50 92 C74 92 74 62 60 58 C66 52 66 38 50 38Z", "#FF9F43")
        + C(50, 64, 7, OL, 0) + R(42, 80, 16, 4, 1, "#7A4A32", 2)
        + L("M48 10 V80 M52 10 V80", "#FFF0D4", 1), "rotate(30 50 50)"))


def violin():
    """A violin with its bow (also the letter V)."""
    body = ("M50 34 C34 34 32 48 40 54 C30 60 28 74 36 82 C42 92 58 92 64 82 C72 74 70 60 60 54 "
            "C68 48 66 34 50 34Z")
    return svg(
        G(R(46, 4, 8, 34, 3, "#4A2E22") + C(50, 6, 5, "#4A2E22", 2.5)
          + P(body, "#C2662A")
          + L("M42 62 Q40 66 42 70 M58 62 Q60 66 58 70", OL, 2.5) + R(44, 76, 12, 6, 2, "#4A2E22", 2)
          + L("M48 10 V76 M52 10 V76", "#FFF0D4", 1), "rotate(-30 50 50)")
        + L("M18 88 L88 30", OL, 5) + L("M18 88 L88 30", "#E8C99A", 2))


def piano():
    whites = "".join(R(x, 40, 12, 46, 2, "#FFFFFF", 2.5) for x in range(14, 86, 12))
    blacks = "".join(R(x, 40, 7, 26, 2, OL, 0) for x in (23, 35, 59, 71))
    return svg(R(8, 14, 84, 76, 6, "#3B2F5B") + R(14, 22, 72, 14, 3, "#5E5A6E", 0) + whites + blacks)


def drum():
    return svg(
        L("M24 6 L46 28 M76 6 L54 28", "#C47A30", 4) + C(24, 6, 4, "#FFF0D4", 2) + C(76, 6, 4, "#FFF0D4", 2)
        + P("M16 40 V76 Q50 92 84 76 V40Z", "#F0464B")
        + L("M16 44 L32 82 L50 46 L68 82 L84 44", "#FFC93C", 3)
        + E(50, 40, 34, 12, "#FFF0D4")
        + L("M16 76 Q50 92 84 76", OL, 3))


def trumpet():
    return svg(
        P("M8 52 V40 L16 44 H66 Q80 30 92 26 V70 Q80 66 66 52 H16 L8 56Z", "#FFC93C")
        + R(30, 30, 6, 14, 2, "#F2A81E", 2.5) + R(40, 30, 6, 14, 2, "#F2A81E", 2.5) + R(50, 30, 6, 14, 2, "#F2A81E", 2.5)
        + P("M30 52 Q40 66 52 52", "none", 3) + E(92, 48, 4, 22, "#F2A81E"))


def saxophone():
    return svg(
        P("M30 8 L42 6 L44 12 L50 12 Q56 20 56 40 V68 Q56 90 38 90 Q20 90 22 72 L24 64 L34 66 L32 72 Q32 80 38 80 Q46 80 46 68 V40 Q46 22 42 16Z", "#FFC93C")
        + P("M24 64 Q10 58 12 46 Q26 46 34 66Z", "#FFC93C")
        + "".join(C(51, y, 3, "#F2A81E", 2) for y in (34, 44, 54, 64)))


def flute():
    return svg(G(R(4, 44, 92, 12, 6, "#C9D6E4") + "".join(C(x, 50, 2.6, OL, 0) for x in (30, 42, 54, 66, 78))
                 + E(14, 50, 4, 3, OL, 0) + shine("M8 47 H90", 2), "rotate(-30 50 50)"))


def accordion():
    folds = "".join(L(f"M{x} 30 L{x + 4} 50 L{x} 70", OL, 2) for x in range(32, 68, 8))
    return svg(R(8, 22, 20, 56, 4, "#F0464B") + R(72, 22, 20, 56, 4, "#F0464B")
               + R(28, 28, 44, 44, 0, "#FFF0D4") + folds
               + "".join(C(18, y, 2.4, "#FFFFFF", 1.5) for y in (34, 44, 54, 64)))


def banjo():
    return svg(G(R(46, 2, 8, 46, 2, "#7A4A32") + R(43, 0, 14, 8, 3, "#7A4A32", 2.5)
                 + C(50, 66, 26, "#C9D6E4") + C(50, 66, 20, "#FFF8EC", 2.5)
                 + L("M48 8 V82 M52 8 V82", "#A9B4C2", 1), "rotate(30 50 50)"))


def maracas():
    m = lambda c: P("M0 -40 C-16 -40 -18 -18 -6 -8 L-4 30 Q0 34 4 30 L6 -8 C18 -18 16 -40 0 -40Z", c) \
        + L("M-10 -28 H10 M-12 -20 H12", "#FFFFFF", 3)
    return svg(G(m("#F0464B"), "translate(36 54) rotate(-20)") + G(m("#7ED957"), "translate(64 54) rotate(20)"))


def bell():
    return svg(P("M50 10 Q50 6 54 6", "none", 3) + C(50, 12, 5, "#FFC93C")
               + P("M50 16 C30 16 26 36 26 56 L16 74 H84 L74 56 C74 36 70 16 50 16Z", "#FFC93C")
               + C(50, 80, 7, "#F2A81E") + shine("M36 34 Q36 26 42 22"))


def microphone():
    return svg(R(40, 52, 20, 40, 8, "#5E5A6E")
               + C(50, 32, 22, "#A9B4C2")
               + L("M36 24 L64 40 M34 34 L60 48 M40 14 L66 28", "#7D8A9A", 2)
               + R(36, 50, 28, 8, 3, "#3B2F5B"))


def damru():
    """डमरू: an hourglass drum with two beads on strings."""
    return svg(
        P("M24 18 L76 18 L56 50 L76 82 L24 82 L44 50Z", "#E89A3A")
        + E(50, 18, 26, 7, "#FFF0D4") + E(50, 82, 26, 7, "#F2D7A8")
        + L("M30 22 L50 50 L70 22 M30 78 L50 50 L70 78", "#F0464B", 2)
        + R(40, 46, 20, 8, 3, "#F0464B", 2.5)
        + L("M40 50 Q24 56 16 66 M60 50 Q76 44 84 34", OL, 2) + C(16, 68, 5, "#FFC93C", 2.5) + C(86, 32, 5, "#FFC93C", 2.5))


def dhol():
    """ढोल: a long barrel drum lying on its side, with a strap and a stick."""
    return svg(
        L("M24 28 Q50 6 76 28", "#F0464B", 4)
        + P("M18 32 Q50 24 82 32 V72 Q50 80 18 72Z", "#C2566E")
        + L("M18 36 L34 70 L50 34 L66 70 L82 36", "#FFC93C", 2.5)
        + E(18, 52, 8, 21, "#FFF0D4") + E(82, 52, 8, 21, "#F2D7A8")
        + tube("M88 86 L66 66", "#C47A30", 4))


DRAWINGS = {
    "instrument-guitar": guitar(), "instrument-violin": violin(), "instrument-piano": piano(),
    "instrument-drum": drum(), "instrument-trumpet": trumpet(), "instrument-saxophone": saxophone(),
    "instrument-flute": flute(), "instrument-accordion": accordion(), "instrument-banjo": banjo(),
    "instrument-maracas": maracas(), "instrument-bell": bell(), "instrument-microphone": microphone(),
}
