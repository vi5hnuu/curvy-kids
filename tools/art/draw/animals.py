"""Animals: friendly front faces for most, side views where the body is what names the animal."""
from kit import (OL, PALETTE as K, svg, P, L, C, E, R, G, sym, flip, eyes, eye, cheeks, cheek,
                 smile, wmouth, shine, scallop, zigzag)

CATEGORY = "animals"

PINK_IN = "#FFB8C2"


def nose(cx, y, rx=5, ry=3.5, fill=OL):
    return E(cx, y, rx, ry, fill, 0 if fill == OL else 3)


# ── Front faces ──────────────────────────────────────────────────────────────────────────────

def lion():
    return svg(
        P(scallop(50, 52, 32, 10, 7), "#F0883A")
        + sym(C(31, 33, 8, "#FFC857") + C(31, 33, 3.5, "#F2A07B", 0))
        + C(50, 54, 24, "#FFC857") + E(50, 65, 11, 8, K["cream"], 0)
        + eyes(49) + cheeks(60, 17)
        + P("M44.5 58 Q50 55 55.5 58 Q50 64 44.5 58Z", "#7A4A32")
        + wmouth(50, 61, 5))


def cat():
    return svg(
        sym(P("M24 46 L22 18 L42 32 Z", "#F6A04D") + P("M27 38 L26 25 L36 32Z", PINK_IN, 0))
        + E(50, 55, 31, 27, "#F6A04D")
        + L("M44 30 Q50 36 56 30", "#D9772A") + L("M40 34 Q42 38 40 42", "#D9772A") + L("M60 34 Q58 38 60 42", "#D9772A")
        + E(50, 66, 13, 9, K["cream"], 0)
        + eyes(52, 12) + cheeks(63, 20)
        + P("M47 61 L53 61 L50 64.5Z", "#FF7A90") + wmouth(50, 64, 4.5)
        + sym(L("M20 60 L33 62 M20 68 L33 66")))


def dog():
    return svg(
        E(50, 52, 27, 25, "#E0A86A")
        + sym(P("M27 30 C13 30 8 54 15 66 C21 73 30 64 31 50Z", "#8B5A3C"))
        + E(50, 65, 14, 10, K["cream"], 0)
        + E(40, 44, 7, 6, "#C98B4F", 0)
        + eyes(48, 11) + cheeks(61, 19)
        + P("M47 69 Q50 79 53 69Z", "#FF7A90")
        + nose(50, 59, 5.5, 4) + wmouth(50, 62, 5))


def pig():
    return svg(
        sym(P("M28 36 L23 16 L43 28Z", "#FF9DB8"))
        + C(50, 54, 29, "#FFB3C7")
        + E(50, 63, 13, 9.5, "#FF8FAE")
        + E(45.5, 63, 2.2, 3.4, OL, 0) + E(54.5, 63, 2.2, 3.4, OL, 0)
        + eyes(48, 12) + cheeks(60, 22)
        + smile(50, 75, 4))


def cow():
    return svg(
        sym(E(22, 38, 11, 5.5, "#FFFFFF", rot=-20) + P("M33 26 Q30 16 36 12 Q38 20 40 26Z", "#F5D46B"))
        + E(50, 50, 25, 28, "#FFFFFF")
        + P("M30 34 Q36 28 44 32 Q46 42 36 44 Q29 42 30 34Z", OL, 0)
        + P("M62 52 Q70 48 72 56 Q70 64 64 62 Q60 58 62 52Z", OL, 0)
        + E(50, 68, 19, 13, "#FFB3C7")
        + E(44, 68, 2.4, 3.6, OL, 0) + E(56, 68, 2.4, 3.6, OL, 0)
        + eyes(48, 11, ring=True) + cheek(31, 57) + cheek(69, 57))


def ox():
    return svg(
        sym(P("M30 34 Q14 34 10 22 Q8 16 12 14 Q16 24 32 26Z", "#F4E6C8")
            + E(23, 42, 10, 5, "#A0663D", rot=-15))
        + E(50, 50, 24, 27, "#A0663D")
        + E(50, 69, 18, 12, "#E8B49A")
        + E(44, 69, 2.4, 3.4, OL, 0) + E(56, 69, 2.4, 3.4, OL, 0)
        + L("M44 74 Q50 84 56 74", "#FFC93C", 3.5)
        + L("M40 32 Q50 26 60 32", "#7E4D2C")
        + eyes(48, 10, ring=True) + cheek(32, 57) + cheek(68, 57))


def buffalo():
    return svg(
        sym(P("M32 30 C16 32 6 26 6 12 C14 20 24 22 36 22Z", "#E8DCC8")
            + E(24, 40, 10, 5, "#5E5A6E", rot=-15))
        + E(50, 50, 23, 27, "#5E5A6E")
        + E(50, 69, 17, 12, "#8A859A")
        + E(44, 69, 2.4, 3.4, OL, 0) + E(56, 69, 2.4, 3.4, OL, 0)
        + eyes(46, 10, ring=True) + cheek(32, 56) + cheek(68, 56))


def bison():
    return svg(
        sym(P("M30 34 Q20 30 20 18 Q26 24 34 26Z", "#F4E6C8"))
        + P(scallop(50, 64, 20, 7, 4, 0.3, 2.84), "#4E3220")
        + E(50, 50, 23, 25, "#8A5A34")
        + P(scallop(50, 34, 17, 7, 4, 3.3, 6.1), "#4E3220")
        + E(50, 64, 14, 10, "#B98A62")
        + E(45, 64, 2.2, 3.2, OL, 0) + E(55, 64, 2.2, 3.2, OL, 0)
        + eyes(48, 10, ring=True))


def panda():
    return svg(
        sym(C(29, 30, 9.5, OL, 0))
        + C(50, 54, 29, "#FFFFFF")
        + sym(E(38, 50, 8, 10.5, OL, 0, rot=25))
        + sym(C(39, 50, 3.6, "#FFFFFF", 0) + C(39.8, 49, 1.6, OL, 0))
        + E(50, 62, 4.5, 3.2, OL, 0) + wmouth(50, 64, 4)
        + cheeks(64, 20))


def bear():
    return svg(
        sym(C(28, 31, 10, "#B5794A") + C(28, 31, 5, "#E6B48A", 0))
        + C(50, 54, 28, "#B5794A")
        + E(50, 64, 14, 11, "#E6B48A", 0)
        + eyes(48, 11) + cheeks(60, 20)
        + nose(50, 59, 5.5, 4) + wmouth(50, 62, 5))


def koala():
    return svg(
        sym(C(24, 38, 15, "#A9B4C2") + C(24, 38, 8, "#F2D4DC", 0))
        + E(50, 56, 28, 26, "#A9B4C2")
        + eyes(50, 14) + cheeks(64, 20)
        + E(50, 60, 7, 10, "#4A4060", 0)
        + shine("M48 54 L48 57", 2)
        + smile(50, 74, 4))


def tiger():
    return svg(
        sym(C(29, 31, 8.5, "#FF9F43") + C(29, 31, 4, PINK_IN, 0))
        + C(50, 55, 29, "#FF9F43")
        + sym(P("M22 50 L32 52 L22 56Z", OL, 0) + P("M24 62 L33 62 L25 67Z", OL, 0))
        + P("M50 27 L46 36 L54 36Z", OL, 0) + P("M41 29 L40 37 L44 34Z", OL, 0) + P("M59 29 L60 37 L56 34Z", OL, 0)
        + E(50, 66, 14, 10, "#FFF6EA", 0)
        + eyes(50, 11) + cheek(30, 61) + cheek(70, 61)
        + P("M46.5 61 L53.5 61 L50 65Z", "#FF7A90") + wmouth(50, 64.5, 4.5))


def monkey():
    return svg(
        sym(C(24, 52, 10, "#9B6B4A") + C(24, 52, 5.5, "#F2C9A0", 0))
        + C(50, 50, 26, "#9B6B4A")
        + C(42, 46, 10, "#F2C9A0", 0) + C(58, 46, 10, "#F2C9A0", 0) + E(50, 62, 17, 12, "#F2C9A0", 0)
        + eyes(46, 8) + cheeks(60, 14, rx=4)
        + E(47, 56, 1.4, 2, OL, 0) + E(53, 56, 1.4, 2, OL, 0)
        + smile(50, 63, 7))


def mouse():
    return svg(
        sym(C(26, 34, 15, "#B8B8C8") + C(26, 34, 9, PINK_IN, 0))
        + E(50, 58, 24, 22, "#B8B8C8")
        + eyes(54, 9) + cheeks(64, 15, rx=4)
        + C(50, 64, 3.6, "#FF7A90", 2.5) + wmouth(50, 67, 3.5)
        + sym(L("M20 60 L36 63 M22 68 L36 66")))


def bunny():
    return svg(
        sym(P("M38 40 C30 30 28 10 34 6 C42 4 44 24 44 38Z", "#F4F1F8") + P("M37 32 C34 24 34 14 36 11 C39 12 40 24 40 32Z", PINK_IN, 0))
        + E(50, 60, 26, 24, "#F4F1F8")
        + eyes(56, 10) + cheeks(66, 17)
        + P("M47 63 L53 63 L50 66Z", "#FF7A90") + wmouth(50, 66, 4))


def hamster():
    return svg(
        sym(C(27, 32, 8, "#F2B66D") + C(27, 32, 4, PINK_IN, 0))
        + E(50, 57, 32, 28, "#F2B66D")
        + E(50, 68, 21, 14, "#FFF4E0", 0)
        + eyes(50, 13) + cheeks(62, 22, rx=6, ry=4)
        + C(50, 60, 2.8, "#FF7A90", 2) + wmouth(50, 62.5, 3.5))


def chipmunk():
    return svg(
        sym(C(30, 30, 8, "#B9773F") + C(30, 30, 4, PINK_IN, 0))
        + E(50, 56, 29, 27, "#B9773F")
        + L("M50 30 V44", OL, 4) + L("M42 32 Q40 38 41 44", "#FFF4E0", 4) + L("M58 32 Q60 38 59 44", "#FFF4E0", 4)
        + sym(L("M30 40 Q34 50 30 56", OL, 3))
        + E(50, 67, 18, 12, "#FFF4E0", 0)
        + eyes(52, 12) + cheeks(64, 20)
        + C(50, 63, 2.8, "#7A4A32", 2) + wmouth(50, 65.5, 3.5))


def raccoon():
    return svg(
        sym(P("M26 38 L24 18 L42 28Z", "#9AA0AE") + P("M29 32 L28 23 L36 28Z", "#4A4060", 0))
        + E(50, 54, 30, 26, "#9AA0AE")
        + L("M30 38 Q40 34 46 40 M54 40 Q60 34 70 38", "#FFFFFF", 4)
        + P("M20 52 Q34 38 50 47 Q66 38 80 52 Q66 60 50 54 Q34 60 20 52Z", "#3B2F5B", 0)
        + eyes(49, 13, ring=True)
        + E(50, 66, 13, 9, "#FFFFFF", 0)
        + nose(50, 61, 4.5, 3.2) + wmouth(50, 63.5, 4))


def fox_face(main, light, ear_in):
    return (
        sym(P("M22 44 L24 12 L44 30Z", main) + P("M26 36 L27 19 L38 29Z", ear_in, 0))
        + P("M14 40 Q50 22 86 40 Q80 64 50 84 Q20 64 14 40Z", main)
        + sym(P("M16 43 Q34 50 50 84 Q26 70 16 43Z", light, 0))
        + P("M14 40 Q50 22 86 40 Q80 64 50 84 Q20 64 14 40Z", "none")
        + eyes(52, 13) + cheek(30, 62) + cheek(70, 62)
        + nose(50, 78, 5, 3.6))


def fox():
    return svg(fox_face("#FF8A3D", "#FFF6EA", "#3B2F5B"))


def wolf():
    return svg(fox_face("#8A94A6", "#E3E7EE", "#F2D4DC") + L("M38 44 L44 47 M62 44 L56 47", OL, 3))


def badger():
    return svg(
        sym(C(26, 36, 7, "#9AA0AE"))
        + P("M18 36 Q50 20 82 36 Q80 62 50 84 Q20 62 18 36Z", "#FFFFFF")
        + sym(P("M26 32 Q38 48 46 80 Q40 82 34 72 Q24 52 20 38Z", OL, 0))
        + sym(C(35, 52, 3.4, "#FFFFFF", 0) + C(35.6, 51.4, 1.4, OL, 0))
        + P("M18 36 Q50 20 82 36 Q80 62 50 84 Q20 62 18 36Z", "none")
        + nose(50, 78, 5, 3.4) + cheek(32, 64) + cheek(68, 64))


def skunk():
    tail = P("M70 80 C96 74 96 36 76 22 C66 16 58 24 64 32 C80 44 76 62 62 70Z", "#3B3550")
    stripe = L("M76 24 C90 36 90 62 72 74", "#FFFFFF", 5)
    return svg(
        tail + stripe
        + sym(C(30, 34, 8, "#3B3550") + C(30, 34, 3.6, PINK_IN, 0))
        + E(46, 56, 26, 24, "#3B3550")
        + P("M46 32 Q40 44 43 60 L49 60 Q52 44 46 32Z", "#FFFFFF", 0)
        + E(46, 66, 12, 8, "#FFFFFF", 0)
        + eyes(52, 11, cx=46, ring=True)
        + C(46, 63, 3, "#FF7A90", 2) + wmouth(46, 65.5, 3.5))


def sheep_face():
    """A white wool cloud around a dark face, the way children draw sheep."""
    return (P(scallop(50, 48, 29, 12, 5), "#F7F2E6")
            + sym(E(25, 52, 10, 5, "#5E5A6E", rot=20))
            + E(50, 58, 16, 19, "#5E5A6E")
            + P(scallop(50, 38, 13, 6, 3, 3.0, 6.4), "#F7F2E6")
            + eyes(56, 7, r=3.4, ring=True) + cheeks(66, 11, rx=3.5, ry=2.4)
            + E(50, 68, 4, 2.6, "#2A2438", 0) + L("M50 70 V72 M46 73 Q50 76 54 73", "#2A2438", 2.5))


def sheep():
    return svg(sheep_face())


def ram():
    curl = "M30 38 C12 36 10 64 24 66 C36 66 36 50 26 50 C20 51 21 59 27 58"
    return svg(sheep_face() + sym(L(curl, OL, 10) + L(curl, "#D9B37A", 6.5)))


def goat():
    return svg(
        sym(P("M40 30 Q34 12 24 8 Q30 18 34 32Z", "#C9B79C")
            + E(25, 40, 12, 5.5, "#F4F1EA", rot=18))
        + P("M32 34 Q50 22 68 34 Q68 62 58 74 Q50 80 42 74 Q32 62 32 34Z", "#F4F1EA")
        + P("M44 76 Q50 94 56 76 Q50 80 44 76Z", "#D8CFC0")
        + eyes(48, 9, r=3.8) + cheeks(60, 13, rx=4)
        + E(50, 66, 5, 3, "#C79A8A", 0) + smile(50, 70, 3.5))


def deer():
    antler = "M38 28 Q34 16 26 10 M34 20 Q28 20 22 22 M31 15 Q30 9 34 5"
    return svg(
        sym(L(antler, OL, 7) + L(antler, "#C98B4F", 4)
            + E(25, 42, 11, 6, "#C98B4F", rot=25) + E(26, 42, 6, 2.8, PINK_IN, 0, rot=25))
        + P("M30 36 Q50 26 70 36 Q72 60 58 76 Q50 82 42 76 Q28 60 30 36Z", "#C98B4F")
        + E(50, 70, 10, 8, "#FFF0D4", 0)
        + sym(C(36, 42, 1.8, "#FFF0D4", 0)) + C(50, 38, 1.8, "#FFF0D4", 0)
        + eyes(52, 10) + cheeks(62, 15, rx=4)
        + nose(50, 68, 4.5, 3.2) + smile(50, 73, 3))


def llama():
    return svg(
        R(36, 58, 28, 40, 12, "#F7EBD7")
        + sym(P("M36 26 Q28 6 35 4 Q42 6 42 24Z", "#F7EBD7") + P("M36 19 Q33 9 36 8 Q39 9 39 19Z", PINK_IN, 0))
        + E(50, 44, 20, 22, "#F7EBD7")
        + P(scallop(50, 28, 12, 6, 3, 3.2, 6.2), "#FFFFFF")
        + E(50, 57, 11, 8, "#EAD7BA", 0)
        + eyes(42, 9, r=3.8) + cheeks(51, 14, rx=4)
        + E(46, 55, 1.4, 2.2, OL, 0) + E(54, 55, 1.4, 2.2, OL, 0) + smile(50, 60, 3.5)
        + P(scallop(50, 74, 10, 5, 3, 0.2, 2.94), "#FFFFFF", 0))


def koala_like_sloth():
    return svg(
        L("M4 18 Q50 10 96 18", "#8B5A3C", 8)
        + sym(P("M30 30 Q20 18 26 14 Q34 18 36 28Z", "#B4977A"))
        + C(50, 52, 28, "#B4977A")
        + E(50, 56, 21, 17, "#F2E2C8", 0)
        + sym(E(40, 52, 8, 5, "#6B4F3A", 0, rot=-20))
        + eyes(52, 10, r=3.6, ring=False)
        + cheeks(62, 15)
        + E(50, 60, 4.5, 3, OL, 0) + smile(50, 65, 4))


def otter():
    return svg(
        sym(C(30, 32, 6, "#9B6B4A"))
        + E(50, 74, 22, 22, "#9B6B4A")
        + E(50, 46, 24, 20, "#9B6B4A")
        + E(50, 54, 15, 10, "#F2D9BC", 0)
        + E(50, 80, 14, 14, "#F2D9BC", 0)
        + eyes(44, 10) + cheeks(53, 17)
        + nose(50, 51, 4.5, 3) + wmouth(50, 53, 3.5)
        + sym(L("M24 52 L38 54 M25 58 L38 57"))
        + P("M42 72 Q50 64 58 72 Q50 78 42 72Z", "#F48FB1")
        + sym(E(38, 70, 6, 4.5, "#9B6B4A", rot=30)))


def frog():
    return svg(
        sym(C(33, 30, 13, "#7ED957") + C(33, 30, 7, "#FFFFFF", 0) + C(34, 31, 4, OL, 0) + C(35.3, 29.6, 1.4, "#FFFFFF", 0))
        + E(50, 58, 36, 26, "#7ED957")
        + E(50, 70, 22, 10, "#C6EFA6", 0)
        + E(45, 52, 1.5, 1.2, "#3E8E2E", 0) + E(55, 52, 1.5, 1.2, "#3E8E2E", 0)
        + cheeks(60, 26, rx=6, ry=4)
        + L("M34 62 Q50 74 66 62"))


# ── Side views ───────────────────────────────────────────────────────────────────────────────

def horse_face(body, muzzle, mane, stripes=False, horn=False, rainbow=False):
    """A long front face with pointed ears, a forelock and a soft muzzle (horse, zebra, unicorn)."""
    s = sym(P("M36 30 L30 8 L46 22Z", body) + P("M36 25 L33 13 L42 22Z", PINK_IN, 0))
    s += P("M30 40 Q30 16 50 16 Q70 16 70 40 L68 64 Q66 70 50 70 Q34 70 32 64Z", body)
    if stripes:
        s += sym(L("M31 36 Q38 38 42 34", OL, 3.5) + L("M31 46 Q38 48 41 44", OL, 3.5) + L("M32 56 Q38 58 42 54", OL, 3.5))
        s += L("M44 24 Q50 30 56 24 M46 30 Q50 34 54 30", OL, 3.5)
    s += E(50, 72, 17, 13, muzzle)
    s += E(44, 73, 2.2, 3.2, OL, 0) + E(56, 73, 2.2, 3.2, OL, 0) + smile(50, 79, 3.5)
    if rainbow:
        s += P("M40 18 Q36 30 44 34 Q46 26 50 22Z", "#FF8FC8") + P("M50 22 Q50 30 56 34 Q62 28 60 18Z", "#7FD3FF")
        s += P("M44 16 Q48 26 52 26 Q56 22 56 16Z", "#FFE066")
    else:
        s += P(scallop(50, 20, 8, 4, 3, 3.3, 6.1), mane)
    if horn:  # on top of the forelock so it is never hidden
        s += P("M43 24 L50 2 L57 24Z", "#FFC93C") + L("M45.5 17 L54 14.5 M47.5 10 L52.5 8.5", "#E0A020", 2)
    s += eyes(46, 11) + cheeks(58, 15, rx=4)
    return s


def horse():
    return svg(horse_face("#B87445", "#E3B58C", "#5A3826"))


def zebra():
    return svg(horse_face("#FFFFFF", "#5E5A6E", OL, stripes=True))


def unicorn():
    return svg(horse_face("#FFFFFF", "#FFE3EE", "#FF8FC8", horn=True, rainbow=True))


def rhino():
    return svg(
        P("M24 32 L18 14 L34 26Z", "#A4ADBD")
        + P("M12 58 Q14 30 42 30 Q62 30 78 48 Q90 60 86 72 Q72 82 42 80 Q14 78 12 58Z", "#A4ADBD")
        + P("M70 44 Q76 16 86 18 Q86 34 82 54Z", "#F5EBDC")
        + P("M58 36 Q62 26 68 26 Q68 34 66 40Z", "#F5EBDC")
        + eye(52, 50) + cheek(54, 60)
        + E(80, 66, 1.8, 2.6, OL, 0)
        + L("M60 74 Q70 76 78 72")
        + L("M26 50 Q30 60 26 68", "#8A93A4", 3))


def hippo():
    return svg(
        sym(C(31, 24, 6, "#B49ACB") + C(31, 24, 2.6, PINK_IN, 0))
        + E(50, 38, 22, 17, "#B49ACB")
        + E(50, 64, 32, 22, "#B49ACB")
        + E(41, 56, 3.4, 4.6, "#7E6698", 0) + E(59, 56, 3.4, 4.6, "#7E6698", 0)
        + eyes(34, 9) + cheeks(68, 22, rx=6)
        + L("M32 72 Q50 82 68 72")
        + R(40, 74, 6, 7, 2, "#FFFFFF", 2.5) + R(54, 74, 6, 7, 2, "#FFFFFF", 2.5))


def elephant():
    return svg(
        sym(P("M34 32 C14 18 2 40 8 60 C12 72 26 72 34 62Z", "#9DB3D8") + P("M30 38 C18 30 12 44 14 56 C18 64 26 62 30 56Z", "#F2C4D4", 0))
        + C(50, 46, 22, "#B6C6E4")
        + P("M42 56 Q40 76 48 86 Q56 92 62 84 Q56 82 54 76 Q54 64 58 56Z", "#B6C6E4")
        + L("M44 72 L52 72 M46 80 L54 80", "#8EA2C8", 2.5)
        + P("M38 60 Q34 70 40 72 Q40 66 42 62Z", "#FFFFFF", 2.5) + P("M62 60 Q66 70 60 72 Q60 66 58 62Z", "#FFFFFF", 2.5)
        + eyes(44, 11) + cheeks(54, 15, rx=4))


def giraffe():
    return svg(
        R(42, 46, 16, 50, 6, "#FFC93C")
        + sym(L("M43 22 L41 10", OL, 3) + C(41, 9, 3.5, "#B5794A") + E(31, 28, 8, 4, "#FFC93C", rot=-20))
        + E(50, 32, 16, 15, "#FFC93C")
        + E(50, 42, 12, 8, "#F7DFA0")
        + C(46, 60, 4, "#C98B4F", 0) + C(54, 72, 4.5, "#C98B4F", 0) + C(46, 84, 3.5, "#C98B4F", 0)
        + C(41, 24, 3, "#C98B4F", 0) + C(60, 26, 2.5, "#C98B4F", 0)
        + L("M58 50 Q64 54 60 60 Q56 64 58 70", "#B5794A", 3)
        + eyes(30, 8, r=3.6) + cheeks(37, 13, rx=3.5)
        + E(46.5, 42, 1.4, 2, OL, 0) + E(53.5, 42, 1.4, 2, OL, 0) + smile(50, 46, 3))


def camel():
    body = ("M16 60 Q16 44 28 42 Q34 26 44 28 Q52 30 56 42 Q62 44 66 50 L70 34 Q70 22 78 20 "
            "Q88 20 90 26 Q90 30 84 30 L80 32 Q78 46 76 60 Q74 68 64 68 L24 68 Q16 68 16 60Z")
    return svg(
        R(24, 64, 7, 28, 3, "#E0B66E") + R(36, 64, 7, 28, 3, "#C99D55")
        + R(56, 64, 7, 28, 3, "#E0B66E") + R(66, 64, 7, 28, 3, "#C99D55")
        + P(body, "#E0B66E")
        + P("M74 22 L72 16 L78 20Z", "#E0B66E", 2.5)
        + eye(80, 25, 2.8) + E(89, 27, 1, 1.4, OL, 0)
        + L("M14 52 Q8 56 10 64", OL, 3))


def kangaroo():
    return svg(
        P("M30 80 Q8 86 6 96", "none", 7) + L("M30 80 Q8 86 6 96", "#D9935A", 4)
        + P("M36 92 L66 92 Q70 88 62 84 L44 84Z", "#C98048")
        + P("M30 84 Q24 60 36 44 Q44 34 54 36 Q66 40 64 62 Q62 80 54 88 Q40 92 30 84Z", "#D9935A")
        + P("M42 64 Q50 60 58 66 Q58 80 50 82 Q42 78 42 64Z", "#F0C08A")
        + C(50, 66, 5, "#D9935A", 2.5) + eye(48, 65, 1.6)
        + P("M48 40 Q48 24 60 18 Q72 14 80 22 Q84 28 76 32 Q68 36 60 42Z", "#D9935A")
        + P("M58 20 L54 4 L64 16Z", "#D9935A") + P("M64 18 L64 4 L70 16Z", "#D9935A")
        + eye(66, 24, 3.2) + E(81, 25, 1.4, 1.8, OL, 0) + cheek(68, 31, 4, 2.6)
        + L("M60 50 Q68 52 70 46", OL, 3))


def turtle():
    return svg(
        E(24, 76, 8, 5, "#7ED957") + E(72, 76, 8, 5, "#7ED957")
        + P("M74 60 Q86 44 92 52 Q96 60 88 66 Q80 70 72 66Z", "#7ED957")
        + eye(86, 55, 3) + smile(88, 61, 2.5)
        + P("M12 70 Q14 30 48 28 Q80 30 82 70Z", "#3FA35A")
        + P("M36 46 L48 38 L60 46 L58 60 L38 60Z", "#64C979", 2.5)
        + L("M20 66 L38 60 M58 60 L76 66 M36 46 L26 40 M60 46 L70 40 M48 38 V30", OL, 2.5)
        + R(8, 66, 80, 8, 4, "#C6EFA6"))


def crocodile():
    teeth = "".join(f'<path d="M{x} 54 l3 5 l3 -5Z" fill="#fff" stroke="{OL}" stroke-width="1.5"/>' for x in range(52, 88, 7))
    return svg(
        R(20, 62, 8, 14, 4, "#5DBB63") + R(40, 62, 8, 14, 4, "#5DBB63")
        + P("M4 58 Q20 46 40 46 L94 50 Q96 56 90 58 L40 66 Q20 68 4 58Z", "#5DBB63")
        + P("M40 46 Q44 38 52 40 L90 50Z", "#5DBB63") + teeth
        + C(48, 40, 6, "#5DBB63") + eye(49, 40, 3)
        + "".join(C(x, 48, 2, "#3E8E44", 0) for x in (16, 24, 32))
        + E(90, 50, 1.4, 1, OL, 0))


def snake():
    body = "M18 88 Q8 76 22 70 L60 66 Q80 64 78 52 Q76 40 56 42 L32 44 Q14 44 16 30 Q18 18 36 18 L60 18"
    return svg(
        L(body, OL, 17) + L(body, "#7ED957", 11)
        + L(body, "#FFC93C", 3, op=".9")
        + E(68, 18, 13, 10, "#7ED957")
        + eye(71, 15, 3.2) + cheek(74, 22, 3.6, 2.4)
        + L("M81 20 L88 20 L91 17 M88 20 L91 23", "#FF5C8A", 2.2))


def lizard():
    return svg(
        L("M30 70 Q12 78 14 90 Q18 96 24 90", OL, 9) + L("M30 70 Q12 78 14 90 Q18 96 24 90", "#8BD45A", 5)
        + sym(L("M40 46 L28 38 M40 64 L28 72", OL, 7) + L("M40 46 L28 38 M40 64 L28 72", "#8BD45A", 3.5))
        + E(50, 56, 14, 22, "#8BD45A")
        + E(50, 26, 12, 13, "#8BD45A")
        + C(50, 52, 3, "#5FAE34", 0) + C(48, 62, 2.5, "#5FAE34", 0) + C(53, 70, 2.2, "#5FAE34", 0)
        + eyes(24, 6, r=3.2) + cheeks(30, 9, rx=3, ry=2)
        + smile(50, 31, 3))


def dinosaur():
    return svg(
        R(30, 64, 9, 24, 4, "#4FC3A1") + R(44, 64, 9, 24, 4, "#3FAE8C")
        + R(58, 64, 9, 24, 4, "#4FC3A1") + R(70, 62, 9, 24, 4, "#3FAE8C")
        + P("M8 74 Q20 70 30 56 Q40 44 58 46 Q72 48 76 40 L80 18 Q82 10 90 12 Q96 16 92 22 L88 24 "
            "Q86 50 80 62 Q74 72 58 72 L36 72 Q20 78 8 74Z", "#4FC3A1")
        + "".join(C(x, y, 3, "#9BE6C8", 0) for x, y in ((44, 56), (54, 54), (64, 58)))
        + eye(88, 16, 2.4) + cheek(90, 21, 2.5, 1.8))


def t_rex():
    return svg(
        P("M30 64 Q10 70 4 86 Q18 80 34 76Z", "#7ED957")
        + R(34, 70, 10, 22, 4, "#5FBF3E") + R(50, 70, 10, 22, 4, "#7ED957")
        + P("M26 70 Q24 44 44 36 Q52 32 54 24 Q56 12 70 10 Q90 10 92 22 Q94 34 82 36 L66 38 "
            "Q66 50 62 60 Q56 76 40 78 Q28 78 26 70Z", "#7ED957")
        + P("M66 32 L90 30 Q90 36 84 36 L66 38Z", "#FFFFFF", 2)
        + L("M58 50 Q66 50 66 56", OL, 6) + L("M58 50 Q66 50 66 56", "#7ED957", 2.5)
        + eye(72, 20, 3.2) + cheek(78, 27, 3.5, 2.2)
        + L("M34 44 L40 40 M38 50 L44 46", "#5FBF3E", 3))


def rabbit():
    return svg(
        P("M56 30 Q50 8 58 4 Q66 6 64 28Z", "#E8E2EE") + P("M58 26 Q55 12 59 9 Q62 12 61 26Z", PINK_IN, 0)
        + P("M64 30 Q66 10 76 8 Q82 12 72 32Z", "#E8E2EE")
        + C(22, 72, 9, "#FFFFFF")
        + E(42, 72, 24, 18, "#E8E2EE")
        + E(62, 88, 12, 5, "#E8E2EE")
        + E(64, 42, 16, 14, "#E8E2EE")
        + eye(68, 40, 3.4) + cheek(70, 48, 4, 2.6)
        + C(79, 45, 2.2, "#FF7A90", 0)
        + L("M70 52 Q74 54 77 51", OL, 2.5))


def rat():
    return svg(
        L("M24 70 C6 74 6 92 22 88 C30 86 30 80 26 80", "#FF9DB8", 4)
        + E(46, 66, 25, 16, "#A3A3B5")
        + P("M60 52 Q84 56 90 66 Q84 72 64 76Z", "#A3A3B5")
        + C(62, 50, 8, "#A3A3B5") + C(62, 50, 4, PINK_IN, 0)
        + eye(72, 60, 3) + C(90, 66, 3, "#FF7A90", 2) + cheek(74, 68, 3.5, 2.4)
        + L("M84 70 L94 74 M84 68 L96 68", OL, 1.6)
        + E(38, 82, 6, 3, "#FFB3C7", 2.5) + E(58, 82, 6, 3, "#FFB3C7", 2.5))


def bat():
    wing = "M38 52 Q22 30 6 38 Q12 46 10 56 Q18 52 22 62 Q28 54 36 64Z"
    return svg(
        sym(P(wing, "#8A78B0"))
        + sym(P("M40 40 L36 24 L46 34Z", "#6B5B8E"))
        + E(50, 54, 15, 18, "#6B5B8E")
        + E(50, 62, 9, 8, "#9C8CC2", 0)
        + eyes(48, 6, r=3.4, ring=True) + cheeks(56, 10, rx=3, ry=2)
        + P("M45 60 L47 64 L49 60Z M51 60 L53 64 L55 60Z", "#FFFFFF", 1.5)
        + smile(50, 59, 5))


def hedgehog():
    return svg(
        P(zigzag(46, 66, 30, 9, 9, 3.14, 6.0) + " L76 66 Q60 84 30 80 Q16 76 16 66Z", "#8B5A3C")
        + P("M62 50 Q90 58 92 68 Q84 80 58 78Z", "#F2C9A0")
        + C(91, 66, 3.2, OL, 0)
        + eye(74, 60, 3.2) + cheek(76, 70, 4, 2.6)
        + E(36, 84, 6, 3, "#F2C9A0", 2.5) + E(56, 84, 6, 3, "#F2C9A0", 2.5))


DRAWINGS = {
    "animal-lion": lion(), "animal-cat": cat(), "animal-dog": dog(), "animal-pig": pig(),
    "animal-cow": cow(), "animal-ox": ox(), "animal-buffalo": buffalo(), "animal-bison": bison(),
    "animal-panda": panda(), "animal-bear": bear(), "animal-koala": koala(), "animal-tiger": tiger(),
    "animal-monkey": monkey(), "animal-mouse": mouse(), "animal-bunny": bunny(), "animal-rabbit": rabbit(),
    "animal-hamster": hamster(), "animal-chipmunk": chipmunk(), "animal-raccoon": raccoon(),
    "animal-fox": fox(), "animal-wolf": wolf(), "animal-badger": badger(), "animal-skunk": skunk(),
    "animal-sheep": sheep(), "animal-ram": ram(), "animal-goat": goat(), "animal-deer": deer(),
    "animal-llama": llama(), "animal-sloth": koala_like_sloth(), "animal-otter": otter(),
    "animal-frog": frog(), "animal-horse": horse(), "animal-zebra": zebra(), "animal-unicorn": unicorn(),
    "animal-rhino": rhino(), "animal-hippo": hippo(), "animal-elephant": elephant(),
    "animal-giraffe": giraffe(), "animal-camel": camel(), "animal-kangaroo": kangaroo(),
    "animal-turtle": turtle(), "animal-crocodile": crocodile(), "animal-snake": snake(),
    "animal-lizard": lizard(), "animal-dinosaur": dinosaur(), "animal-t-rex": t_rex(),
    "animal-rat": rat(), "animal-bat": bat(), "animal-hedgehog": hedgehog(),
}
