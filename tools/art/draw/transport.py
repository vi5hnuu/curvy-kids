"""Transport: vehicles side-on, driving to the right, with round wheels and bright paint.
Land vehicles share wheel() and window(); boats sit on a strip of water."""
from kit import OL, svg, P, L, C, E, R, G, sym, flip, shine, tube

CATEGORY = "transport"

GLASS = "#BDEBFF"
TYRE = "#3B2F5B"


def wheel(x, y, r=9):
    return C(x, y, r, TYRE) + C(x, y, r * 0.45, "#D7DEE8", 0)


def window(x, y, w, h, rx=3):
    return R(x, y, w, h, rx, GLASS, 2.5)


def road():
    return L("M4 90 H96", "#A9B4C2", 3)


def water(y=74):
    """A rounded pool of water whose wavy top sits at y, kept inside the picture margins."""
    top = f"M4 {y} Q15 {y - 5} 27 {y} Q38 {y + 5} 50 {y} Q62 {y - 5} 73 {y} Q85 {y + 5} 96 {y}"
    return (P(top + " V86 Q96 92 90 92 H10 Q4 92 4 86Z", "#4FC3F7", 0)
            + L(top, "#2E9FD8", 2.5))


def car_shape(paint, roof=""):
    return (P("M8 70 V58 Q8 50 18 48 L28 46 L38 32 Q42 28 50 28 H66 Q72 28 76 34 L84 46 Q94 48 94 58 V70Z", paint)
            + window(40, 34, 14, 12) + P("M58 34 H66 Q70 34 72 38 L76 46 H58Z", GLASS, 2.5)
            + roof
            + wheel(28, 72) + wheel(74, 72)
            + R(86, 54, 6, 5, 2, "#FFE98A", 2) + R(8, 56, 5, 5, 2, "#F0464B", 2))


def car():
    return svg(car_shape("#F0464B"))


def taxi():
    return svg(car_shape("#FFC93C", R(46, 20, 16, 8, 2, "#FFFFFF", 2.5))
               + "".join(R(x, 60, 5, 5, 0, OL, 0) for x in (36, 46, 56, 66)))


def police_car():
    return svg(car_shape("#FFFFFF", R(48, 20, 7, 8, 2, "#F0464B", 2.5) + R(55, 20, 7, 8, 2, "#4FC3F7", 2.5))
               + R(10, 58, 82, 6, 0, "#2E5AAC", 0))


def suv():
    return svg(P("M6 72 V44 Q6 30 18 30 H70 Q78 30 82 40 L88 50 Q94 52 94 60 V72Z", "#7ED957")
               + window(14, 36, 18, 14) + window(36, 36, 18, 14) + P("M58 36 H70 Q74 36 76 40 L80 50 H58Z", GLASS, 2.5)
               + R(14, 24, 50, 5, 2, OL, 0)
               + wheel(26, 74, 10) + wheel(74, 74, 10))


def race_car():
    return svg(P("M4 70 V62 L14 56 L40 54 L48 42 H62 L66 54 L90 58 Q96 60 96 70Z", "#F0464B")
               + P("M4 50 H16 V62 H4Z", "#F0464B", 2.5)
               + C(55, 46, 6, "#FFFFFF") + R(48, 48, 14, 4, 0, OL, 0)
               + C(56, 62, 6, "#FFFFFF", 2.5) + L("M22 62 H40 M70 64 H86", "#FFFFFF", 3)
               + wheel(22, 72, 10) + wheel(78, 72, 10))


def pickup_truck():
    return svg(P("M6 70 V52 H48 V36 Q48 30 54 30 H68 Q74 30 78 38 L84 50 Q94 52 94 60 V70Z", "#4FC3F7")
               + P("M54 36 H68 Q71 36 73 40 L78 50 H54Z", GLASS, 2.5)
               + L("M6 58 H46", OL, 2.5)
               + wheel(24, 72) + wheel(74, 72))


def bus():
    return svg(R(6, 26, 88, 46, 8, "#FFC93C")
               + "".join(window(x, 32, 14, 14) for x in (12, 30, 48, 66)) + window(80, 32, 10, 22)
               + L("M6 56 H78", "#F0464B", 4)
               + wheel(24, 74) + wheel(74, 74))


def minibus():
    return svg(P("M8 72 V40 Q8 28 22 28 H66 Q76 28 82 40 L90 52 Q94 54 94 62 V72Z", "#FF9F43")
               + window(16, 34, 16, 14) + window(36, 34, 16, 14) + P("M56 34 H66 Q72 34 76 40 L82 50 H56Z", GLASS, 2.5)
               + L("M8 58 H92", "#FFFFFF", 4)
               + wheel(26, 74) + wheel(74, 74))


def trolleybus():
    return svg(L("M30 26 L50 6 M44 26 L64 6", OL, 2.5) + L("M40 6 H96 M54 6 H96", "#A9B4C2", 0)
               + R(6, 26, 88, 46, 8, "#9B6BFF")
               + "".join(window(x, 32, 14, 14) for x in (12, 30, 48, 66)) + window(80, 32, 10, 22)
               + wheel(24, 74) + wheel(74, 74))


def ambulance():
    return svg(P("M6 72 V28 Q6 24 10 24 H62 V40 H76 Q82 40 86 48 L92 56 Q94 58 94 62 V72Z", "#FFFFFF")
               + P("M66 44 H76 Q80 44 82 48 L86 56 H66Z", GLASS, 2.5)
               + R(24, 34, 16, 6, 1, "#F0464B", 0) + R(29, 29, 6, 16, 1, "#F0464B", 0)
               + L("M6 58 H94", "#F0464B", 4)
               + R(30, 16, 12, 8, 2, "#4FC3F7", 2.5)
               + wheel(24, 74) + wheel(74, 74))


def fire_truck():
    return svg(P("M6 72 V36 H62 V28 H76 Q82 28 86 38 L92 52 Q94 54 94 60 V72Z", "#F0464B")
               + P("M66 32 H76 Q79 32 81 38 L85 48 H66Z", GLASS, 2.5)
               + L("M8 30 L56 18 M8 36 L56 24", "#D7DEE8", 3) + L("M16 34 L14 26 M28 31 L26 23 M40 28 L38 20 M52 25 L50 17", "#D7DEE8", 2.5)
               + R(10, 46, 46, 10, 2, "#D7DEE8", 2.5)
               + wheel(22, 74) + wheel(48, 74) + wheel(78, 74))


def delivery_truck():
    return svg(R(6, 22, 58, 50, 4, "#FFFFFF")
               + P("M64 36 H78 Q84 36 88 46 L94 56 V72 H64Z", "#4FC3F7")
               + P("M68 40 H78 Q81 40 83 44 L88 54 H68Z", GLASS, 2.5)
               + P("M24 38 H46 V58 H24Z", "#C47A30", 2.5) + L("M24 46 H46", OL, 2)
               + wheel(22, 74) + wheel(78, 74))


def lorry():
    return svg(R(4, 24, 62, 46, 4, "#7ED957")
               + P("M66 34 H80 Q86 34 90 44 L96 54 V70 H66Z", "#FF9F43")
               + P("M70 38 H80 Q83 38 85 42 L90 52 H70Z", GLASS, 2.5)
               + L("M14 32 V62 M26 32 V62 M38 32 V62 M50 32 V62", "#5DBB4A", 2.5)
               + wheel(18, 74) + wheel(36, 74) + wheel(80, 74))


def tractor():
    return svg(P("M14 64 V46 H44 V22 H66 V46 H78 Q86 46 86 54 V64Z", "#7ED957")
               + window(48, 26, 14, 16)
               + R(70, 36, 5, 10, 1, OL, 0)
               + C(28, 66, 18, TYRE) + C(28, 66, 8, "#FFC93C", 0)
               + C(78, 72, 10, TYRE) + C(78, 72, 4.5, "#FFC93C", 0))


def bicycle():
    return svg(C(24, 64, 18, "none", 4) + C(76, 64, 18, "none", 4)
               + L("M24 64 L40 40 H68 L76 64 M40 40 L50 64 L68 40 M50 64 H24 M66 30 L68 40 M34 34 H46 M62 30 H72", "#4FC3F7", 4)
               + C(50, 64, 4, OL, 0))


def motorcycle():
    return svg(C(20, 66, 14, TYRE) + C(20, 66, 6, "#D7DEE8", 0) + C(80, 66, 14, TYRE) + C(80, 66, 6, "#D7DEE8", 0)
               + P("M30 60 Q34 44 50 44 H64 L72 54 Q66 66 50 66 H32Z", "#F0464B")
               + P("M30 44 Q40 36 52 40 L50 46 H30Z", OL, 0)
               + L("M64 46 L72 30 L80 28 M72 54 L80 66", OL, 3.5)
               + R(80, 30, 6, 5, 2, "#FFE98A", 2))


def scooter():
    return svg(C(22, 70, 11, TYRE) + C(22, 70, 5, "#D7DEE8", 0) + C(78, 70, 11, TYRE) + C(78, 70, 5, "#D7DEE8", 0)
               + P("M12 64 Q12 48 30 48 H44 Q50 48 52 58 H70 L66 26 H74 L80 62 Q80 68 72 68 H14Z", "#4FC3F7")
               + R(20, 40, 24, 8, 4, OL, 0) + L("M62 26 H80", OL, 4))


def kick_scooter():
    return svg(L("M74 16 L80 70", "#F0464B", 5) + L("M66 16 H84", OL, 5)
               + R(18, 66, 62, 8, 4, "#F0464B")
               + C(22, 80, 8, TYRE) + C(80, 80, 8, TYRE))


def train_engine(paint="#F0464B", accent="#FFC93C"):
    """A front-and-side steam engine; also used for रेल and इंजन."""
    return (R(52, 22, 34, 46, 4, paint)
            + window(58, 28, 22, 14)
            + R(48, 16, 42, 8, 3, OL, 0)
            + R(10, 40, 44, 28, 6, paint)
            + R(18, 22, 10, 18, 2, OL, 0) + E(23, 20, 9, 4, OL, 0)
            + P("M6 68 L12 56 H14 V68Z", accent, 2.5)
            + L("M10 50 H52", accent, 3)
            + wheel(22, 72, 9) + wheel(44, 72, 9) + wheel(70, 70, 12)
            + C(30, 10, 5, "#E4ECF5", 2) + C(40, 5, 3.5, "#E4ECF5", 2))


def steam_train():
    return svg(train_engine())


def train():
    """A passenger train: engine and one carriage."""
    return svg(P("M4 70 V40 Q4 28 18 28 H40 V70Z", "#4FC3F7") + window(10, 34, 14, 12)
               + R(44, 28, 52, 42, 4, "#F0464B") + window(50, 34, 12, 12) + window(66, 34, 12, 12) + window(82, 34, 10, 12)
               + L("M40 56 H44", OL, 3)
               + wheel(16, 74, 7) + wheel(32, 74, 7) + wheel(56, 74, 7) + wheel(84, 74, 7)
               + L("M2 84 H98", "#A9B4C2", 3))


def bullet_train():
    return svg(P("M96 70 H22 Q4 70 4 58 Q4 40 34 34 H96Z", "#FFFFFF")
               + P("M10 50 Q16 40 34 38 L38 50Z", GLASS, 2.5)
               + "".join(window(x, 40, 10, 10) for x in (48, 64, 80))
               + L("M8 60 H96", "#2E5AAC", 4)
               + L("M2 80 H98", "#A9B4C2", 3) + "".join(C(x, 74, 4, TYRE, 0) for x in (30, 50, 70, 88)))


def monorail():
    return svg(R(4, 76, 92, 10, 3, "#A9B4C2")
               + L("M30 86 V96 M70 86 V96", "#A9B4C2", 6)
               + P("M94 72 H18 Q4 72 4 58 Q4 36 28 34 H94Z", "#7ED957")
               + "".join(window(x, 42, 12, 12) for x in (34, 52, 70)) + P("M10 52 Q14 42 26 40 L28 52Z", GLASS, 2.5))


def tram():
    return svg(L("M50 6 L42 22 M50 6 L58 22", OL, 2.5) + L("M4 6 H96", OL, 2)
               + R(8, 22, 84, 52, 10, "#FF9F43")
               + "".join(window(x, 30, 14, 16) for x in (16, 34, 52, 70))
               + L("M8 58 H92", "#FFFFFF", 4)
               + wheel(26, 76, 7) + wheel(74, 76, 7) + L("M2 86 H98", "#A9B4C2", 3))


def cable_car():
    return svg(L("M2 18 L98 8", OL, 3)
               + L("M50 13 V30", OL, 3) + R(40, 10, 20, 8, 3, "#A9B4C2", 2.5)
               + R(18, 30, 64, 52, 10, "#F0464B")
               + window(26, 38, 20, 18) + window(54, 38, 20, 18)
               + L("M18 66 H82", "#FFFFFF", 3))


def airplane():
    """A jet plane flying right, also used for the letter and world pictures."""
    return svg(P("M14 58 L4 34 H14 L28 48Z", "#4FC3F7")
               + P("M8 50 Q8 42 20 42 H80 Q94 42 96 50 Q94 58 80 58 H20 Q8 58 8 50Z", "#FFFFFF")
               + P("M40 50 L58 50 L40 82 L30 82Z", "#4FC3F7")
               + P("M44 42 L58 42 L44 20 L36 20Z", "#4FC3F7")
               + "".join(C(x, 49, 2.6, GLASS, 1.5) for x in (30, 40, 50, 60, 70))
               + P("M84 44 Q92 46 92 50 H82Z", GLASS, 2))


def small_plane():
    return svg(P("M12 52 L4 32 H12 L24 46Z", "#F0464B")
               + P("M8 50 Q8 42 22 42 H70 Q84 42 84 52 Q84 60 70 60 H22 Q8 60 8 50Z", "#FFC93C")
               + R(34, 24, 30, 8, 3, "#F0464B") + R(34, 62, 30, 8, 3, "#F0464B")
               + L("M48 32 V42 M48 60 V62", OL, 2.5)
               + window(60, 44, 10, 8) + L("M88 40 V64", OL, 3) + C(86, 52, 3, OL, 0))


def helicopter():
    return svg(L("M14 14 H86 M50 14 V26", OL, 3.5)
               + P("M14 46 H44 L40 52 H14Z", "#7ED957", 2.5) + L("M8 40 L14 52", OL, 3)
               + P("M36 56 Q36 26 64 26 Q86 26 86 50 Q86 70 64 70 H46 Q36 70 36 56Z", "#7ED957")
               + P("M64 32 Q80 32 80 50 H64Z", GLASS, 2.5)
               + L("M42 70 V80 M74 70 V80 M34 80 H84", OL, 3))


def rocket():
    return svg(P("M40 76 Q50 98 60 76Z", "#FF9F43", 2.5) + P("M44 76 Q50 90 56 76Z", "#FFE14D", 0)
               + P("M36 56 L20 74 L36 72Z M64 56 L80 74 L64 72Z", "#F0464B")
               + P("M50 6 Q70 22 66 50 L64 76 H36 L34 50 Q30 22 50 6Z", "#FFFFFF")
               + C(50, 40, 9, GLASS, 3)
               + P("M50 6 Q60 12 64 22 H36 Q40 12 50 6Z", "#F0464B", 3))


def flying_saucer():
    return svg(E(50, 40, 18, 16, GLASS)
               + E(50, 54, 42, 12, "#A9B4C2")
               + "".join(C(x, 56, 3, "#FFE14D", 2) for x in (26, 42, 58, 74))
               + P("M38 66 L28 90 H72 L62 66Z", "#FFF59A", 0, op=".55"))


def sailboat():
    return svg(water(76)
               + L("M50 10 V66", OL, 3)
               + P("M52 12 L82 60 H52Z", "#FFFFFF") + P("M48 22 L24 60 H48Z", "#FF9F43")
               + P("M14 66 H86 L76 82 H24Z", "#F0464B"))


def canoe():
    return svg(water(70)
               + P("M6 56 Q50 80 94 56 Q90 70 50 74 Q10 70 6 56Z", "#C47A30")
               + L("M6 56 Q50 66 94 56", OL, 3)
               + L("M30 30 L70 82", "#7A4A32", 4) + E(72, 84, 4, 8, "#7A4A32", 2.5, rot=-38))


def speedboat():
    return svg(water(72)
               + P("M6 52 H86 Q96 52 94 58 L82 72 H14Z", "#FFFFFF")
               + L("M10 62 H90", "#F0464B", 4)
               + P("M40 52 L50 38 H64 L58 52Z", GLASS, 2.5)
               + L("M4 70 Q-2 64 6 60", "#FFFFFF", 3))


def ferry():
    return svg(water(76)
               + P("M6 58 H94 L84 78 H16Z", "#2E5AAC")
               + R(18, 38, 64, 20, 3, "#FFFFFF") + "".join(window(x, 43, 8, 8, 2) for x in (24, 38, 52, 66))
               + R(32, 24, 34, 14, 3, "#FFFFFF") + R(56, 14, 8, 10, 1, "#F0464B", 2.5))


def ship():
    """A big ship with two funnels (also used for जहाज)."""
    return svg(water(78)
               + P("M4 56 H96 L84 80 H14Z", "#F0464B")
               + L("M8 64 H92", "#FFFFFF", 3)
               + R(18, 38, 64, 18, 3, "#FFFFFF") + "".join(C(x, 47, 3.2, GLASS, 2) for x in (28, 40, 52, 64, 76))
               + R(30, 18, 12, 20, 2, "#FFC93C") + R(54, 18, 12, 20, 2, "#FFC93C")
               + L("M30 24 H42 M54 24 H66", OL, 2.5))


def cruise_ship():
    return svg(water(80)
               + P("M2 58 H98 L88 82 H12Z", "#FFFFFF")
               + L("M6 70 H94", "#2E5AAC", 4)
               + R(12, 42, 76, 16, 3, "#FFFFFF") + "".join(C(x, 50, 2.6, GLASS, 1.5) for x in range(20, 84, 9))
               + R(22, 28, 56, 14, 3, "#FFFFFF") + "".join(C(x, 35, 2.6, GLASS, 1.5) for x in range(30, 74, 9))
               + R(44, 12, 14, 16, 2, "#F0464B") + L("M44 18 H58", OL, 2.5))


DRAWINGS = {
    "transport-car": car(), "transport-taxi": taxi(), "transport-police-car": police_car(), "transport-suv": suv(),
    "transport-race-car": race_car(), "transport-pickup-truck": pickup_truck(), "transport-bus": bus(),
    "transport-minibus": minibus(), "transport-trolleybus": trolleybus(), "transport-ambulance": ambulance(),
    "transport-fire-truck": fire_truck(), "transport-delivery-truck": delivery_truck(), "transport-lorry": lorry(),
    "transport-tractor": tractor(), "transport-bicycle": bicycle(), "transport-motorcycle": motorcycle(),
    "transport-scooter": scooter(), "transport-kick-scooter": kick_scooter(), "transport-steam-train": steam_train(),
    "transport-train": train(), "transport-bullet-train": bullet_train(), "transport-monorail": monorail(),
    "transport-tram": tram(), "transport-cable-car": cable_car(), "transport-airplane": airplane(),
    "transport-small-plane": small_plane(), "transport-helicopter": helicopter(), "transport-rocket": rocket(),
    "transport-flying-saucer": flying_saucer(), "transport-sailboat": sailboat(), "transport-canoe": canoe(),
    "transport-speedboat": speedboat(), "transport-ferry": ferry(), "transport-ship": ship(),
    "transport-cruise-ship": cruise_ship(),
}
