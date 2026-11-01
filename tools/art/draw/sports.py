"""Sports: the ball or kit that names each sport, and a child doing the sport where there is no
single object (swimming, gymnastics, cycling, skiing)."""
import math
from kit import OL, svg, P, L, C, E, R, G, sym, flip, shine, tube, clip, eye, cheek
from people import SKIN, HAIR, head, dot_eyes, cheeks2, mouth_smile

CATEGORY = "sports"


def ball(fill, seams="", r=36):
    return C(50, 50, r, fill) + seams + shine("M26 40 Q28 30 36 24")


def soccer():
    """A football with a pentagon pattern (also the letter B, for Ball)."""
    pent = lambda cx, cy, s: P(" ".join(
        f"{'M' if i == 0 else 'L'}{cx + s * math.cos(-math.pi / 2 + i * 2 * math.pi / 5):.1f} "
        f"{cy + s * math.sin(-math.pi / 2 + i * 2 * math.pi / 5):.1f}" for i in range(5)) + "Z", OL, 0)
    seams = pent(50, 50, 11) + "".join(
        L(f"M{50 + 11 * math.cos(a):.1f} {50 + 11 * math.sin(a):.1f} L{50 + 26 * math.cos(a):.1f} {50 + 26 * math.sin(a):.1f}", OL, 2.5)
        for a in [-math.pi / 2 + i * 2 * math.pi / 5 for i in range(5)])
    outer = clip("socc", C(50, 50, 34.5, "#000", 0), "".join(
        pent(50 + 34 * math.cos(a), 50 + 34 * math.sin(a), 10) for a in [math.pi / 2 + i * 2 * math.pi / 5 for i in range(5)]))
    return svg(ball("#FFFFFF", seams + outer))


def basketball():
    return svg(ball("#FF8A2A", L("M14 50 H86 M50 14 V86 M24 24 Q40 50 24 76 M76 24 Q60 50 76 76", OL, 2.5)))


def baseball():
    return svg(ball("#FFFFFF", L("M28 22 Q44 50 28 78 M72 22 Q56 50 72 78", "#F0464B", 2.5)
                    + L("M33 32 L39 30 M37 42 L43 41 M37 58 L43 59 M33 68 L39 70 M67 32 L61 30 M63 42 L57 41 M63 58 L57 59 M67 68 L61 70", "#F0464B", 2)))


def volleyball():
    seams = "M50 50 Q40 30 50 14 M50 50 Q30 62 20 82 M50 50 Q72 56 86 68"
    panels = (P("M50 50 Q40 30 50 14 L100 0 V82 L86 68 Q72 56 50 50Z", "#FFC93C", 0)
              + P("M50 50 Q30 62 20 82 L0 100 V0 L50 14 Q40 30 50 50Z", "#4FC3F7", 0)
              + L(seams, OL, 2.5))
    return svg(C(50, 50, 36, "#FFFFFF") + clip("vb", C(50, 50, 36, "#000", 0), panels) + C(50, 50, 36, "none"))


def football():
    return svg(G(P("M10 50 Q50 10 90 50 Q50 90 10 50Z", "#A0502A")
                 + L("M34 50 H66", "#FFFFFF", 3) + L("M40 45 V55 M47 45 V55 M54 45 V55 M61 45 V55", "#FFFFFF", 2.5),
                 "rotate(-30 50 50)"))


def rugby():
    return svg(G(P("M8 50 Q50 18 92 50 Q50 82 8 50Z", "#FFFFFF")
                 + L("M8 50 Q50 40 92 50", "#F0464B", 3) + L("M40 40 V60 M60 40 V60", "#F0464B", 3), "rotate(30 50 50)"))


def tennis():
    return svg(
        G(E(50, 34, 22, 28, "#FFFFFF", 4) + L("M34 16 V52 M42 10 V60 M50 8 V62 M58 10 V60 M66 16 V52 M30 26 H70 M28 36 H72 M30 46 H70", "#A9B4C2", 1.5)
          + E(50, 34, 22, 28, "none", 4) + R(46, 62, 8, 30, 3, "#F0464B"), "rotate(-30 50 50)")
        + C(78, 74, 12, "#D8F04A") + L("M68 68 Q78 74 74 86 M88 66 Q80 74 86 84", "#FFFFFF", 2))


def table_tennis():
    return svg(G(C(42, 42, 26, "#F0464B") + R(36, 64, 12, 28, 4, "#C47A30"), "rotate(30 42 42)")
               + C(76, 26, 9, "#FFFFFF"))


def badminton():
    return svg(G(E(40, 30, 16, 22, "#FFFFFF", 4) + L("M30 18 V44 M40 10 V50 M50 18 V44 M26 30 H54 M28 22 H52 M28 40 H52", "#A9B4C2", 1.5)
                 + R(37, 52, 6, 40, 3, "#4FC3F7"), "rotate(-25 40 50)")
               + P("M70 60 L62 80 L86 84 L88 64Z", "#FFFFFF", 2.5) + C(76, 84, 6, "#F0464B"))


def cricket():
    """A cricket bat and red ball."""
    return svg(G(R(42, 4, 16, 18, 4, "#4A2E22") + P("M38 22 H62 V84 Q62 92 50 92 Q38 92 38 84Z", "#F2C98A")
                 + L("M44 30 V84", "#D9A863", 2), "rotate(30 50 50)")
               + C(22, 76, 10, "#F0464B") + L("M15 70 Q22 76 18 85", "#FFFFFF", 2))


def hockey():
    return svg(tube("M56 6 L44 74 Q42 86 30 84 L12 82", "#C47A30", 6)
               + E(74, 84, 12, 5, OL, 0) + R(44, 22, 10, 14, 2, "#FFFFFF", 2.5, rot=10))


def golf():
    return svg(L("M44 10 V82", OL, 3) + P("M44 10 L80 22 L44 34Z", "#F0464B")
               + E(50, 86, 34, 7, "#7ED957") + E(44, 84, 6, 2.5, OL, 0)
               + C(74, 76, 7, "#FFFFFF"))


def billiards():
    return svg(C(50, 50, 36, OL) + C(50, 50, 16, "#FFFFFF", 0) + C(40, 30, 6, "#fff", 0, op=".35")
               + C(50, 44, 4.5, "none", 3) + C(50, 56, 5.5, "none", 3))


def frisbee():
    return svg(E(50, 54, 40, 18, "#4FC3F7") + E(50, 50, 26, 10, "#8ED8FF", 2.5) + shine("M24 46 Q34 38 50 38"))


def boxing():
    glove = ("M24 38 Q22 14 48 12 Q76 12 78 38 Q80 54 70 62 V80 H32 V64 Q22 58 22 46 Q14 44 16 36 Q20 30 26 34Z")
    return svg(P(glove, "#F0464B") + R(30, 76, 42, 14, 3, "#FFFFFF") + L("M26 40 Q36 46 40 40", OL, 2.5)
               + shine("M40 22 Q50 18 60 20"))


def child(cx, cy, r, i=1):
    return (head(SKIN[i], HAIR["black"], "short", cx, cy, r) + dot_eyes(cx, cy + 1, r * 0.35, r * 0.14)
            + cheeks2(cx, cy + r * 0.32, r * 0.58) + mouth_smile(cx, cy + r * 0.3, r * 0.2))


def swimming():
    return svg(
        P("M4 58 Q16 50 28 58 Q40 66 52 58 Q64 50 76 58 Q88 66 96 58 V96 H4Z", "#4FC3F7")
        + tube("M58 52 Q70 30 86 34", SKIN[2], 6)
        + child(40, 46, 16, 2)
        + R(26, 30, 28, 8, 4, "#F0464B", 2.5)
        + L("M4 70 Q16 62 28 70 Q40 78 52 70 Q64 62 76 70 Q88 78 96 70", "#BDEBFF", 3))


def gymnastics():
    return svg(
        tube("M50 50 L22 22 M50 50 L78 22", SKIN[0], 6)
        + P("M40 46 H60 L64 76 H36Z", "#FF5C8A")
        + tube("M42 76 L30 94 M58 76 L70 94", SKIN[0], 6)
        + G(child(50, 0, 13, 0), "translate(0 30)")
        + R(10, 92, 80, 4, 2, "#9B6BFF", 2))


def cycling():
    return svg(
        C(24, 72, 16, "none", 4) + C(78, 72, 16, "none", 4)
        + L("M24 72 L42 50 H68 L78 72 M42 50 L52 72 L68 50 M52 72 H24 M66 44 L68 50 M40 44 H48", "#F0464B", 3.5)
        + tube("M50 54 L54 40 L66 44", "#4FC3F7", 7)
        + child(54, 26, 12, 3) + P("M42 22 Q44 10 56 10 Q68 12 66 22Z", "#FFC93C"))


def skiing():
    return svg(
        L("M8 70 L84 92", "#F0464B", 4) + L("M18 62 L94 84", "#F0464B", 4)
        + L("M30 74 L60 26 M70 82 L84 36", "#A9B4C2", 2.5)
        + P("M40 48 H60 L62 72 H40Z", "#4FC3F7")
        + child(50, 32, 13, 1) + P("M37 28 Q38 16 50 16 Q62 16 63 28Z", "#F0464B") + C(50, 14, 4, "#FFFFFF", 2))


def snowboard():
    return svg(
        G(R(10, 80, 80, 10, 5, "#9B6BFF"), "rotate(-15 50 85)")
        + P("M38 46 H62 L66 76 H36Z", "#FF9F43")
        + tube("M38 50 L18 40 M62 50 L82 58", SKIN[4], 5)
        + child(50, 30, 13, 4) + P("M37 26 Q38 14 50 14 Q62 14 63 26Z", "#7ED957"))


def ice_skating():
    return svg(P("M24 10 H60 V50 Q80 52 84 64 V72 H20 V50Z", "#FFFFFF")
               + L("M32 20 H52 M32 30 H52 M32 40 H52", "#4FC3F7", 2.5)
               + L("M24 80 H88 Q94 80 92 74", "#A9B4C2", 4) + L("M34 72 V80 M74 72 V80", "#A9B4C2", 3))


def skateboard():
    return svg(R(8, 44, 84, 12, 6, "#F0464B") + L("M20 50 H80", "#FFC93C", 3)
               + R(20, 56, 14, 6, 2, "#A9B4C2", 2) + R(66, 56, 14, 6, 2, "#A9B4C2", 2)
               + C(27, 68, 7, "#4FC3F7") + C(73, 68, 7, "#4FC3F7"))


DRAWINGS = {
    "sport-soccer": soccer(), "sport-basketball": basketball(), "sport-baseball": baseball(),
    "sport-volleyball": volleyball(), "sport-football": football(), "sport-rugby": rugby(),
    "sport-tennis": tennis(), "sport-table-tennis": table_tennis(), "sport-badminton": badminton(),
    "sport-cricket": cricket(), "sport-hockey": hockey(), "sport-golf": golf(), "sport-billiards": billiards(),
    "sport-frisbee": frisbee(), "sport-boxing": boxing(), "sport-swimming": swimming(),
    "sport-gymnastics": gymnastics(), "sport-cycling": cycling(), "sport-skiing": skiing(),
    "sport-snowboard": snowboard(), "sport-ice-skating": ice_skating(), "sport-skateboard": skateboard(),
}
