"""Jobs: the child character dressed for the job, with the one hat or tool that names it."""
from kit import OL, svg, P, L, C, E, R, G, sym, flip, eye, cheek, shine, tube
from people import SKIN, HAIR, head, body, dot_eyes, cheeks2, mouth_smile

CATEGORY = "jobs"

CX, CY, RR = 50, 48, 22  # head placement shared by every job


def person(i, hair, style, shirt, extra_face=""):
    """The child from the shoulders up, smiling."""
    return (body(shirt, top=74, w=32)
            + head(SKIN[i % len(SKIN)], HAIR[hair], style, CX, CY, RR)
            + dot_eyes(CX, 50, 8, 3.2) + cheeks2(CX, 57, 13) + mouth_smile(CX, 57, 5)
            + extra_face)


def doctor():
    return svg(
        person(0, "brown", "short", "#FFFFFF")
        + L("M38 74 Q34 88 46 90 M62 74 Q66 88 54 90", OL, 2.5) + C(50, 90, 3.5, "#A9B4C2", 2)
        + R(36, 22, 28, 6, 3, "#FFFFFF", 2.5) + C(50, 26, 5, "#A9B4C2", 2)
        + P("M44 80 H56 M50 74 V86", "none", 0) + R(64, 80, 12, 12, 2, "#F0464B", 0)
        + L("M70 82 V90 M66 86 H74", "#fff", 2.5))


def chef():
    return svg(
        person(1, "black", "short", "#FFFFFF")
        + P("M32 34 Q22 30 24 20 Q26 10 38 14 Q42 4 50 4 Q58 4 62 14 Q74 10 76 20 Q78 30 68 34Z", "#FFFFFF")
        + R(32, 30, 36, 8, 2, "#FFFFFF", 2.5)
        + C(50, 80, 2, OL, 0) + C(50, 88, 2, OL, 0)
        + P("M38 74 L50 80 L62 74", "none", 2.5))


def firefighter():
    return svg(
        person(2, "brown", "short", "#F0464B")
        + P("M24 38 Q26 14 50 14 Q74 14 76 38 Q86 40 88 44 H12 Q14 40 24 38Z", "#F0464B")
        + P("M44 20 H56 V32 H44Z", "#FFC93C", 2.5)
        + L("M20 84 H80", "#FFD84D", 4))


def police():
    return svg(
        person(3, "black", "short", "#2E5AAC")
        + P("M26 34 Q28 18 50 18 Q72 18 74 34Z", "#2E5AAC")
        + R(24, 32, 52, 8, 3, OL, 0)
        + P("M50 22 L54 26 L50 32 L46 26Z", "#FFC93C", 2)
        + P("M62 80 L66 78 L70 80 L70 86 L66 89 L62 86Z", "#FFC93C", 2))


def builder():
    return svg(
        person(4, "black", "short", "#FF9F43")
        + P("M26 36 Q26 16 50 16 Q74 16 74 36Z", "#FFC93C")
        + R(22, 34, 56, 6, 3, "#FFC93C", 2.5)
        + L("M50 16 V34", OL, 2.5)
        + L("M30 82 H70 M42 74 V96 M58 74 V96", "#FFF06B", 3))


def mechanic():
    return svg(
        person(0, "auburn", "short", "#4A7FD6")
        + P("M28 34 Q28 18 50 18 Q72 18 72 34Z", "#F0464B") + P("M70 32 Q84 32 84 38 H64Z", "#F0464B")
        + G(R(-4, -22, 8, 34, 3, "#A9B4C2") + P("M-10 -26 Q-10 -36 0 -36 Q10 -36 10 -26 L4 -26 L4 -32 L-4 -32 L-4 -26Z", "#A9B4C2"),
            "translate(80 80) rotate(35)"))


def farmer():
    return svg(
        person(1, "brown", "short", "#7ED957")
        + E(50, 32, 36, 8, "#F2C14E")
        + P("M32 32 Q32 14 50 14 Q68 14 68 32Z", "#F2C14E")
        + R(32, 26, 36, 5, 2, "#F0464B", 2)
        + L("M38 74 V96 M62 74 V96", "#2E5AAC", 4)
        + L("M84 96 L84 62", "#C9A04A", 3) + E(84, 58, 4, 8, "#F2C14E", 2) + E(78, 66, 3, 6, "#F2C14E", 2, rot=-30) + E(90, 66, 3, 6, "#F2C14E", 2, rot=30))


def pilot():
    return svg(
        person(3, "black", "short", "#FFFFFF")
        + P("M26 34 Q28 18 50 18 Q72 18 74 34Z", "#2E3A6E")
        + R(24, 32, 52, 7, 3, OL, 0)
        + P("M38 25 Q44 22 50 26 Q56 22 62 25 L50 30Z", "#FFC93C", 2)
        + L("M44 76 L50 92 L56 76", "#2E3A6E", 3)
        + L("M28 84 H36 M64 84 H72", "#FFC93C", 3))


def astronaut():
    return svg(
        P("M14 96 Q14 72 50 70 Q86 72 86 96Z", "#FFFFFF")
        + R(40, 80, 20, 12, 3, "#4FC3F7", 2.5)
        + C(50, 44, 32, "#FFFFFF")
        + C(50, 46, 24, "#2E3A6E")
        + head(SKIN[2], HAIR["black"], "short", 50, 48, 18, ears=False)
        + dot_eyes(50, 50, 7, 3) + cheeks2(50, 56, 11) + mouth_smile(50, 56, 4)
        + shine("M32 32 Q38 24 46 24", 3))


def scientist():
    return svg(
        person(4, "black", "curly", "#FFFFFF")
        + R(32, 40, 14, 10, 4, "#BDEBFF", 2.5) + R(54, 40, 14, 10, 4, "#BDEBFF", 2.5) + L("M46 45 H54", OL, 2.5)
        + P("M72 60 V70 L64 88 Q62 94 68 94 H88 Q94 94 92 88 L84 70 V60Z", "#EAF7FF")
        + P("M67 82 L65 88 Q64 92 68 92 H88 Q92 92 91 88 L89 82Z", "#7ED957", 0)
        + R(70, 56, 16, 4, 2, "#EAF7FF", 2.5))


def teacher():
    return svg(
        R(6, 6, 88, 46, 4, "#3E7D5A", 3)
        + L("M14 18 H34 M14 28 H28 M60 16 L68 28 M68 16 L60 28", "#FFFFFF", 2.5)
        + person(2, "black", "bun", "#9B6BFF")
        + C(42, 50, 6, "none", 2) + C(58, 50, 6, "none", 2) + L("M48 50 H52", OL, 2)
        + R(56, 76, 26, 18, 2, "#F0464B") + L("M69 76 V94", OL, 2))


def artist():
    return svg(
        person(0, "blond", "long", "#FF8FC7")
        + P("M28 32 Q30 18 50 20 Q70 20 72 30 Q62 36 28 32Z", "#F0464B") + C(50, 18, 3, "#F0464B", 2)
        + P("M58 76 Q60 66 74 66 Q92 66 92 80 Q92 92 76 92 Q70 92 72 86 Q74 80 66 82 Q56 84 58 76Z", "#F2C79A")
        + C(68, 72, 3, "#F0464B", 0) + C(78, 70, 3, "#4FC3F7", 0) + C(86, 76, 3, "#FFC93C", 0) + C(82, 86, 3, "#7ED957", 0))


def detective():
    return svg(
        person(1, "brown", "short", "#A0663D")
        + P("M24 36 Q24 14 50 14 Q76 14 76 36Z", "#A0663D") + R(20, 32, 60, 6, 3, "#7A4A32", 2.5)
        + tube("M70 92 L78 80", "#7A4A32", 5)
        + C(84, 72, 10, "#BDEBFF", 4) + shine("M80 68 Q82 66 86 66", 2))


def guard():
    return svg(
        person(3, "black", "short", "#F0464B")
        + P("M30 40 Q26 4 50 4 Q74 4 70 40Z", "#2E2433")
        + L("M30 60 Q50 64 70 60", OL, 2.5)
        + L("M42 74 V96 M58 74 V96", "#FFFFFF", 3)
        + C(50, 82, 2.5, "#FFC93C", 0) + C(50, 90, 2.5, "#FFC93C", 0))


def programmer():
    return svg(
        person(2, "brown", "pigtails", "#4FC3F7")
        + P("M18 96 L24 74 H76 L82 96Z", "#A9B4C2")
        + R(26, 64, 48, 32, 3, "#5E5A6E")
        + L("M36 74 L32 80 L36 86 M64 74 L68 80 L64 86 M54 72 L46 88", "#7ED957", 2.5))


DRAWINGS = {
    "job-doctor": doctor(), "job-chef": chef(), "job-firefighter": firefighter(),
    "job-police-officer": police(), "job-builder": builder(), "job-mechanic": mechanic(),
    "job-farmer": farmer(), "job-pilot": pilot(), "job-astronaut": astronaut(),
    "job-scientist": scientist(), "job-teacher": teacher(), "job-artist": artist(),
    "job-detective": detective(), "job-guard": guard(), "job-programmer": programmer(),
}
