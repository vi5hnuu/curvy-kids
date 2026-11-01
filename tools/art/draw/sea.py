"""Sea animals: mostly side views swimming left, with a smiling face on the front."""
from kit import (OL, PALETTE as K, svg, P, L, C, E, R, G, sym, flip, eyes, eye, cheeks, cheek,
                 smile, shine, scallop, tube)

CATEGORY = "sea-animals"


def bubbles():
    return C(84, 20, 4, "#DFF5FF", 2) + C(76, 10, 2.6, "#DFF5FF", 2)


def fish_shape(body, fin, stripes=""):
    """A plump fish facing left; shared by fish and the letter F."""
    return (P("M70 50 L92 32 Q88 50 92 68Z", fin)
            + P("M44 30 Q52 18 64 30Z", fin)
            + E(46, 50, 30, 21, body)
            + stripes
            + P("M44 62 Q50 72 58 62Z", fin)
            + eye(28, 45) + cheek(32, 55, 4, 2.6)
            + smile(20, 56, 3))


def fish():
    return svg(fish_shape("#4FC3F7", "#2E9FD8") + bubbles())


def tropical_fish():
    stripes = (P("M38 30 Q32 50 38 70 L46 71 Q40 50 46 29Z", "#FFFFFF", 2)
               + P("M58 33 Q54 50 58 67 L64 64 Q60 50 64 36Z", "#FFFFFF", 2))
    return svg(fish_shape("#FF9F43", "#F0464B", stripes))


def pufferfish():
    spikes = "".join(G(P("M50 20 L46 12 L54 12Z", "#FFC93C", 2), f"rotate({a} 50 50)") for a in range(0, 360, 30))
    return svg(
        P("M80 50 L94 40 L94 60Z", "#FFC93C")
        + spikes + C(50, 50, 30, "#FFD84D")
        + E(50, 62, 20, 12, "#FFF3C4", 0)
        + eyes(44, 11) + cheeks(54, 18)
        + E(50, 60, 4, 3.5, "#FF7A90"))


def shark():
    return svg(
        P("M74 48 L94 26 Q90 48 94 72 L74 56Z", "#7FA6C9")
        + P("M44 32 L54 10 L62 34Z", "#7FA6C9")
        + P("M8 52 Q20 30 52 30 Q80 32 80 52 Q78 70 50 72 Q20 72 8 52Z", "#7FA6C9")
        + P("M12 56 Q30 72 54 70 Q74 66 78 56 Q44 62 12 56Z", "#FFFFFF", 0)
        + P("M44 62 L54 80 L60 64Z", "#7FA6C9")
        + eye(28, 44) + cheek(32, 52, 4, 2.6)
        + L("M14 56 Q24 62 32 58")
        + L("M50 40 V48 M56 40 V48", OL, 2))


def whale():
    return svg(
        L("M38 24 Q34 12 28 10 M38 24 Q40 12 46 10 M38 24 V14", "#8ED8FF", 4)
        + P("M78 56 Q86 40 96 34 Q92 46 90 52 Q96 60 92 70 Q84 62 78 62Z", "#4A7FD6")
        + P("M6 58 Q6 26 40 26 Q76 26 82 58 Q76 78 42 78 Q10 78 6 58Z", "#4A7FD6")
        + P("M8 62 Q14 76 42 76 Q70 76 78 64 Q42 70 8 62Z", "#BDE3FF", 0)
        + L("M20 68 H60 M24 72 H56", "#8FBFEA", 2)
        + eye(26, 50) + cheek(30, 58, 4.5, 3) + smile(14, 58, 4))


def dolphin():
    return svg(
        P("M74 40 L90 22 Q90 36 96 44 Q84 46 78 50Z", "#5DADE2")
        + P("M8 58 L20 54 Q26 32 54 32 Q74 32 80 46 Q78 66 54 70 Q30 72 20 62 L8 62Z", "#5DADE2")
        + P("M20 62 Q34 70 54 68 Q70 64 76 56 Q46 64 20 60Z", "#D6EEFA", 0)
        + P("M48 32 Q52 20 62 18 Q60 26 62 34Z", "#5DADE2")
        + P("M50 62 L46 76 L60 66Z", "#5DADE2")
        + eye(32, 46) + cheek(36, 54, 4, 2.6) + smile(16, 59, 3))


def seal():
    return svg(
        P("M74 74 L94 64 L92 82 Z", "#8A8FA8")
        + P("M28 50 Q24 82 50 84 Q80 84 80 72 Q60 66 52 48Z", "#A9B0C6")
        + C(36, 40, 18, "#A9B0C6")
        + P("M36 66 Q30 80 44 80 Q46 72 42 64Z", "#8A8FA8")
        + eyes(36, 7, cx=34, r=3.6)
        + E(30, 46, 6, 4, "#C9CEDD", 0) + E(38, 46, 6, 4, "#C9CEDD", 0)
        + E(34, 43, 2.8, 2, OL, 0)
        + L("M22 46 L12 44 M22 49 L12 50 M46 46 L56 44 M46 49 L56 50", OL, 1.6)
        + C(36, 20, 7, "#F0464B") + shine("M33 17 L35 15", 2))


def sea_turtle():
    return svg(
        sym(P("M30 40 Q14 24 8 30 Q14 40 30 48Z", "#7ED957") + P("M34 66 Q20 76 22 82 Q32 80 38 70Z", "#7ED957"))
        + E(50, 26, 10, 12, "#7ED957")
        + E(50, 56, 24, 28, "#3E9C5A")
        + P("M50 36 L60 46 L60 62 L50 72 L40 62 L40 46Z", "#5DBB6E", 2.5)
        + L("M50 36 V28 M60 46 L72 42 M60 62 L72 68 M40 46 L28 42 M40 62 L28 68 M50 72 V84", OL, 2.5)
        + eyes(24, 5, r=2.8) + smile(50, 30, 3))


def crab():
    return svg(
        sym(L("M32 66 L18 74 L14 84 M34 70 L24 82 L22 90", OL, 3)
            + tube("M30 52 Q18 46 18 34", "#F0464B", 5)
            + P("M10 24 Q12 14 22 16 Q20 22 24 26 Q20 34 12 32Z", "#F0464B")
            + tube("M42 44 V34", "#F0464B", 3) + C(42, 31, 6, "#fff") + C(43, 31, 3, OL, 0))
        + E(50, 60, 26, 18, "#F0464B")
        + cheeks(64, 14) + smile(50, 62, 5))


def lobster():
    return svg(
        L("M44 20 Q30 6 18 10 M56 20 Q70 6 82 10", "#D63A3F", 2.5)
        + sym(tube("M40 34 Q24 34 20 22", "#D63A3F", 5)
              + P("M12 18 Q12 6 24 8 Q20 14 26 18 Q20 26 12 24Z", "#D63A3F")
              + L("M38 46 L26 52 M38 54 L26 60 M38 62 L28 68", OL, 2.5))
        + P("M40 74 L36 90 L50 84 L64 90 L60 74Z", "#D63A3F")
        + E(50, 52, 13, 26, "#F0464B")
        + L("M38 60 H62 M39 68 H61", OL, 2)
        + eyes(34, 6, r=3.2) + smile(50, 40, 3))


def shrimp():
    return svg(
        L("M24 30 Q12 12 30 6 M26 32 Q22 16 40 12", "#FF8A65", 2)
        + P("M30 34 Q28 24 40 24 Q70 24 80 46 Q86 66 70 78 L78 88 L62 86 L64 76 Q74 64 66 50 Q58 40 38 44 Q30 42 30 34Z", "#FF9E80")
        + L("M50 32 Q56 38 54 44 M62 38 Q64 44 60 48 M70 50 Q68 56 64 56", OL, 2)
        + L("M44 44 L40 54 M52 46 L50 56 M60 52 L60 60", OL, 2)
        + eye(36, 32, 3.4) + cheek(40, 39, 3.5, 2.4))


def octopus():
    legs = "".join(tube(f"M{x} 62 Q{x + dx} 76 {x + dx * 2} 88", "#9B6BFF", 7)
                   for x, dx in [(30, -6), (42, -3), (58, 3), (70, 6)])
    return svg(
        legs
        + P("M22 62 Q16 18 50 14 Q84 18 78 62 Q50 72 22 62Z", "#9B6BFF")
        + C(36, 28, 4, "#C3A6FF", 0) + C(64, 30, 3, "#C3A6FF", 0)
        + eyes(44, 11) + cheeks(53, 18) + smile(50, 53, 5))


def squid():
    legs = "".join(tube(f"M{x} 64 Q{x + dx} 78 {x + dx * 1.5} 90", "#FF8FA3", 5)
                   for x, dx in [(36, -4), (44, -2), (50, 0), (56, 2), (64, 4)])
    return svg(
        legs
        + P("M50 6 L74 30 L66 30 L66 64 L34 64 L34 30 L26 30Z", "#FF8FA3")
        + C(42, 22, 2.4, "#FFC2CE", 0) + C(56, 18, 2, "#FFC2CE", 0)
        + eyes(48, 8, r=3.6) + cheeks(56, 12, rx=3.5, ry=2.4) + smile(50, 56, 3))


def jellyfish():
    legs = "".join(L(f"M{x} 56 Q{x - 6} 66 {x} 74 Q{x + 6} 82 {x} 92", c, 4)
                   for x, c in [(32, "#C3A6FF"), (44, "#FF8FC7"), (56, "#C3A6FF"), (68, "#FF8FC7")])
    return svg(
        legs
        + P("M16 56 Q16 14 50 14 Q84 14 84 56 Q76 62 68 56 Q60 62 50 56 Q40 62 32 56 Q24 62 16 56Z", "#FFB0DA")
        + shine("M28 34 Q32 24 42 22")
        + eyes(40, 10) + cheeks(48, 17) + smile(50, 46, 4))


def seashell():
    ribs = "".join(L(f"M50 82 L{x} 24", OL, 2) for x in (26, 38, 50, 62, 74))
    return svg(
        P("M40 80 L36 90 L64 90 L60 80Z", "#FFB59E")
        + P("M50 82 Q12 72 14 42 Q20 14 50 12 Q80 14 86 42 Q88 72 50 82Z", "#FFCBB8")
        + ribs)


def crocodile():
    """The sea set's crocodile, swimming with only its eyes and back above the water."""
    return svg(
        P("M8 60 Q50 54 92 60 V88 H8Z", "#8ED8FF", 0, op=".7")
        + P("M8 52 Q10 44 30 44 L40 36 Q50 32 56 40 L84 44 Q96 50 90 58 Q60 62 8 58Z", "#58B368")
        + L("M14 52 L18 56 L22 52 L26 56 L30 52", "#fff", 2)
        + C(44, 36, 7, "#58B368") + C(52, 36, 7, "#58B368")
        + eye(44, 35, 3) + eye(52, 35, 3)
        + L("M64 46 L66 42 L68 46 L70 42 L72 46 L74 42 L76 46", OL, 2)
        + L("M8 66 Q20 62 32 66 Q44 70 56 66 Q68 62 80 66 Q86 68 92 66", "#4FC3F7", 3))


DRAWINGS = {
    "sea-fish": fish(), "sea-tropical-fish": tropical_fish(), "sea-pufferfish": pufferfish(),
    "sea-shark": shark(), "sea-whale": whale(), "sea-dolphin": dolphin(), "sea-seal": seal(),
    "sea-sea-turtle": sea_turtle(), "sea-crab": crab(), "sea-lobster": lobster(), "sea-shrimp": shrimp(),
    "sea-octopus": octopus(), "sea-squid": squid(), "sea-jellyfish": jellyfish(),
    "sea-seashell": seashell(), "sea-crocodile": crocodile(),
}
