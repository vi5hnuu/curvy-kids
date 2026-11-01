"""Weather and the sky: sun, moon, clouds, rain and snow, built from a few shared pieces so
combined weather (sun behind a cloud, sun and rain) is drawn from the same sun and cloud."""
import math
from kit import OL, svg, P, L, C, E, R, G, sym, flip, shine, tube, eyes, cheeks, smile

CATEGORY = "weather"

SUN = "#FFC93C"
CLOUD = "#FFFFFF"
GREY_CLOUD = "#C9D2DD"
RAIN = "#4FC3F7"


def sun_shape(cx=50, cy=50, r=20, face=True):
    rays = "".join(G(R(cx - 3, cy - r - 16, 6, 11, 3, "#FFB020", 2.5), f"rotate({a} {cx} {cy})") for a in range(0, 360, 45))
    f = (eyes(cy - 3, r * 0.3, cx, r * 0.13) + cheeks(cy + r * 0.25, r * 0.55, cx, r * 0.18, r * 0.12)
         + smile(cx, cy + r * 0.25, r * 0.22)) if face else ""
    return rays + C(cx, cy, r, SUN) + f


def cloud_path(cx, cy, s=1.0):
    """A puffy cloud centred near (cx, cy); s scales it."""
    k = lambda v: v * s
    return (f"M{cx - k(30)} {cy + k(14)} Q{cx - k(40)} {cy + k(14)} {cx - k(38)} {cy + k(2)} Q{cx - k(36)} {cy - k(8)} "
            f"{cx - k(24)} {cy - k(6)} Q{cx - k(22)} {cy - k(22)} {cx - k(4)} {cy - k(20)} Q{cx + k(8)} {cy - k(32)} "
            f"{cx + k(22)} {cy - k(18)} Q{cx + k(38)} {cy - k(18)} {cx + k(36)} {cy - k(2)} Q{cx + k(44)} {cy + k(4)} "
            f"{cx + k(38)} {cy + k(14)}Z")


def cloud_shape(cx=50, cy=50, s=1.0, fill=CLOUD):
    return P(cloud_path(cx, cy, s), fill)


def drops(xs, y, color=RAIN, s=1.5):
    """Raindrops hanging from y, one per x."""
    return "".join(P(f"M{x} {y} Q{x + 5 * s} {y + 8 * s} {x} {y + 11 * s} Q{x - 5 * s} {y + 8 * s} {x} {y}Z", color, 2.5)
                   for x in xs)


def flakes(pts, size=5):
    return "".join(L(f"M{x - size} {y} H{x + size} M{x - size / 2} {y - size * .87} L{x + size / 2} {y + size * .87} "
                     f"M{x + size / 2} {y - size * .87} L{x - size / 2} {y + size * .87}", "#8ED8FF", 2.5) for x, y in pts)


def bolt(x, y, s=1.0, fill=SUN):
    return G(P("M8 0 H24 L16 14 H26 L4 40 L10 20 H0Z", fill, 2.5), f"translate({x} {y}) scale({s})")


def star_path(cx, cy, ro, ri, n=5):
    pts = []
    for i in range(n * 2):
        r = ro if i % 2 == 0 else ri
        a = -math.pi / 2 + i * math.pi / n
        pts.append(f"{cx + r * math.cos(a):.1f} {cy + r * math.sin(a):.1f}")
    return "M" + " L".join(pts) + "Z"


def sun():
    """The smiling sun (also the letter S)."""
    return svg(sun_shape(50, 50, 22))


def moon():
    """A sleepy crescent moon (also the letter M)."""
    return svg(P("M60 12 A38 38 0 1 0 88 66 A30 30 0 1 1 60 12Z", "#FFE14D")
               + L("M36 50 Q40 54 44 50", OL, 2.5) + smile(42, 62, 4)
               + P(star_path(80, 22, 7, 3), "#FFE14D", 2))


def star():
    return svg(P(star_path(50, 52, 40, 17), SUN) + eyes(54, 7, 50, 3) + smile(50, 60, 3))


def glowing_star():
    return svg(L("M50 4 V12 M50 88 V96 M8 50 H16 M84 50 H92 M18 18 L24 24 M76 76 L82 82 M82 18 L76 24 M18 82 L24 76", "#FFC93C", 3)
               + P(star_path(50, 52, 32, 14), "#FFE14D") + shine("M42 36 L46 30", 3))


def cloud():
    return svg(cloud_shape(50, 54, 1.15))


def partly_cloudy():
    return svg(sun_shape(36, 36, 16, face=False) + cloud_shape(58, 62, 0.95))


def sun_behind_cloud():
    return svg(sun_shape(60, 34, 18, face=False) + cloud_shape(44, 62, 1.0))


def rain():
    return svg(cloud_shape(50, 38, 1.0, GREY_CLOUD) + drops((28, 50, 72), 60) + drops((39, 61), 78))


def sun_and_rain():
    return svg(sun_shape(64, 30, 15, face=False) + cloud_shape(44, 50, 0.9, CLOUD) + drops((28, 46, 64), 72))


def snow_cloud():
    return svg(cloud_shape(50, 38, 1.0, GREY_CLOUD) + flakes([(30, 70), (50, 80), (70, 70), (40, 90), (62, 92)]))


def thunderstorm():
    return svg(cloud_shape(50, 36, 1.0, "#8A97AA") + bolt(40, 50, 1.1) + drops((24, 76), 62))


def lightning():
    return svg(cloud_shape(50, 34, 0.9, "#8A97AA") + bolt(36, 48, 1.3))


def lightning_bolt():
    return svg(bolt(22, 6, 2.2))


def snowflake():
    arm = lambda a: G(L("M50 50 V12 M50 24 L42 16 M50 24 L58 16 M50 36 L44 30 M50 36 L56 30", "#4FC3F7", 4), f"rotate({a} 50 50)")
    return svg("".join(arm(a) for a in range(0, 360, 60)) + C(50, 50, 5, "#BDEBFF", 2.5))


def snowman():
    return svg(C(50, 70, 22, "#FFFFFF") + C(50, 36, 16, "#FFFFFF")
               + R(36, 10, 28, 14, 2, OL, 0) + R(30, 22, 40, 4, 2, OL, 0)
               + eyes(34, 6, 50, 2.8) + P("M50 39 L64 42 L50 44Z", "#FF9F43", 2)
               + C(50, 62, 2.6, OL, 0) + C(50, 72, 2.6, OL, 0) + C(50, 82, 2.6, OL, 0)
               + L("M30 62 L14 50 M70 62 L86 50", "#8B5A3C", 3)
               + P("M36 48 Q50 54 64 48 L64 54 Q50 60 36 54Z", "#F0464B", 2.5))


def snowman_with_snow():
    return svg(G(snowman().split('data-bare="1">')[1].replace("</svg>", ""), "translate(8 6) scale(.84)")
               + flakes([(12, 14), (86, 20), (14, 60), (88, 66), (30, 6)], 4))


def umbrella():
    """An open umbrella with rain on top (also used for U and छाता)."""
    return svg(drops((22, 50, 78), 2, s=1.0)
               + tube("M50 50 V82 Q50 92 42 92 Q36 92 36 86", "#8B5A3C", 3)
               + P("M8 52 Q10 18 50 16 Q90 18 92 52 Q84 46 78 52 Q71 46 64 52 Q57 46 50 52 Q43 46 36 52 Q29 46 22 52 Q15 46 8 52Z", "#9B6BFF")
               + L("M50 16 Q36 30 36 52 M50 16 Q64 30 64 52", OL, 2.5)
               + P("M8 52 Q10 18 50 16 Q36 30 36 52 Q29 46 22 52 Q15 46 8 52Z", "#FF5C8A", 0)
               + P("M50 16 Q64 30 64 52 Q71 46 78 52 Q84 46 92 52 Q90 18 50 16Z", "#FF5C8A", 0)
               + P("M8 52 Q10 18 50 16 Q90 18 92 52 Q84 46 78 52 Q71 46 64 52 Q57 46 50 52 Q43 46 36 52 Q29 46 22 52 Q15 46 8 52Z", "none")
               + L("M50 16 Q36 30 36 52 M50 16 Q64 30 64 52", OL, 2.5))


def rainbow():
    """A rainbow between two clouds (also the letter R)."""
    bands = "".join(L(f"M{14 + i * 6} 72 A{36 - i * 6} {36 - i * 6} 0 0 1 {86 - i * 6} 72", c, 6.5)
                    for i, c in enumerate(["#F0464B", "#FF9F43", "#FFE14D", "#7ED957", "#4FC3F7"]))
    return svg(L("M14 72 A36 36 0 0 1 86 72", OL, 0) + bands
               + cloud_shape(18, 74, 0.42) + cloud_shape(82, 74, 0.42))


def wind():
    return svg(L("M10 34 H60 Q74 34 74 22 Q74 12 64 12 Q56 12 56 20", "#8ED8FF", 6)
               + L("M10 54 H76 Q90 54 90 66 Q90 76 80 76 Q72 76 72 68", "#8ED8FF", 6)
               + L("M20 44 H50", "#8ED8FF", 6))


def windy():
    return svg(cloud_shape(56, 40, 0.8)
               + L("M6 70 H50 Q62 70 62 80 Q62 88 54 88", "#8ED8FF", 5) + L("M14 60 H40", "#8ED8FF", 5)
               + L("M4 40 Q6 34 12 34", "none", 0))


def fog():
    return svg(cloud_shape(50, 34, 0.9, GREY_CLOUD)
               + "".join(L(f"M{x1} {y} H{x2}", "#A9B4C2", 5) for x1, x2, y in [(14, 70, 62), (28, 86, 74), (10, 60, 86)]))


def tornado():
    return svg("".join(L(f"M{50 - w} {y} H{50 + w}", "#A9B4C2", 6) for w, y in
                       [(36, 14), (30, 26), (24, 38), (18, 50), (12, 62), (8, 74)])
               + L("M44 84 Q50 92 56 88", "#A9B4C2", 5))


def thermometer():
    return svg(R(40, 8, 20, 64, 10, "#FFFFFF") + C(50, 76, 16, "#F0464B")
               + R(46, 30, 8, 44, 4, "#F0464B", 0)
               + L("M60 22 H66 M60 34 H66 M60 46 H66 M60 58 H66", OL, 2.5))


def fire():
    return svg(P("M50 6 Q72 30 76 52 Q80 82 50 92 Q20 82 24 56 Q26 42 36 34 Q36 46 44 50 Q38 26 50 6Z", "#FF7A2A")
               + P("M50 44 Q64 58 62 72 Q60 86 50 86 Q38 86 38 72 Q38 62 46 58 Q46 50 50 44Z", "#FFE14D", 2.5))


def water_drop():
    """A water drop (also used for वर्षा, rain)."""
    return svg(P("M50 8 Q80 46 80 64 Q80 92 50 92 Q20 92 20 64 Q20 46 50 8Z", RAIN)
               + shine("M34 62 Q34 50 42 42"))


def wave():
    return svg(P("M6 88 V62 Q10 30 42 24 Q72 20 82 44 Q70 36 60 42 Q52 50 60 58 Q68 64 76 58 Q84 70 94 66 V88 Q94 92 90 92 H10 Q6 92 6 88Z", RAIN)
               + L("M12 74 Q24 66 36 74 Q48 82 60 74 Q72 66 84 74", "#FFFFFF", 3))


DRAWINGS = {
    "weather-sun": sun(), "weather-moon": moon(), "weather-star": star(), "weather-glowing-star": glowing_star(),
    "weather-cloud": cloud(), "weather-partly-cloudy": partly_cloudy(), "weather-sun-behind-cloud": sun_behind_cloud(),
    "weather-rain": rain(), "weather-sun-and-rain": sun_and_rain(), "weather-snow-cloud": snow_cloud(),
    "weather-thunderstorm": thunderstorm(), "weather-lightning": lightning(), "weather-lightning-bolt": lightning_bolt(),
    "weather-snowflake": snowflake(), "weather-snowman": snowman(), "weather-snowman-with-snow": snowman_with_snow(),
    "weather-umbrella": umbrella(), "weather-rainbow": rainbow(), "weather-wind": wind(), "weather-windy": windy(),
    "weather-fog": fog(), "weather-tornado": tornado(), "weather-thermometer": thermometer(), "weather-fire": fire(),
    "weather-water-drop": water_drop(), "weather-wave": wave(),
}
