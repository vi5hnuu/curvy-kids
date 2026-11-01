"""
The one friendly child character used by feelings, body, jobs and the letter pictures.

head() draws the face and hair; the expression helpers draw eyes, brows and mouths in the same
coordinates (a head centred on cx, cy with radius r), so any face can be combined with any feeling.
Skin tones and hair styles are varied across pictures so every child sees someone like them.
"""
from kit import OL, PALETTE as K, P, L, C, E, R, G, sym, eye, cheek

SKIN = ["#FFDDBF", "#F2C291", "#D9A06E", "#A9714A", "#7C4E33"]
HAIR = {"black": "#2E2433", "brown": "#6B3E26", "auburn": "#A6532A", "blond": "#F2C14E"}


def head(skin: str, hair: str, style: str = "short", cx=50, cy=54, r=30, ears=True) -> str:
    """A round face with hair. style: short, long, bun, pigtails, curly, bald."""
    back = ""
    if style == "long":
        back = P(f"M{cx - r - 2} {cy} Q{cx - r - 4} {cy - r - 6} {cx} {cy - r - 4} Q{cx + r + 4} {cy - r - 6} "
                 f"{cx + r + 2} {cy} L{cx + r + 2} {cy + r * 0.8} Q{cx} {cy + r * 0.95} {cx - r - 2} {cy + r * 0.8}Z", hair)
    elif style == "pigtails":
        back = C(cx - r - 4, cy - 2, r * 0.36, hair) + C(cx + r + 4, cy - 2, r * 0.36, hair)
    elif style == "bun":
        back = C(cx, cy - r - 2, r * 0.38, hair)
    elif style == "curly":
        from kit import scallop
        back = P(scallop(cx, cy - r * 0.25, r * 0.98, 12, r * 0.08, 3.0, 6.42) + "Z", hair)
    ear = ""
    if ears and style not in ("long",):
        ear = C(cx - r, cy + 2, r * 0.2, skin) + C(cx + r, cy + 2, r * 0.2, skin)
    face = C(cx, cy, r, skin)
    fringe = ""
    if style != "bald":
        fringe = P(f"M{cx - r} {cy - 2} Q{cx - r} {cy - r - 3} {cx} {cy - r - 2} Q{cx + r} {cy - r - 3} {cx + r} {cy - 2} "
                   f"Q{cx + r * 0.75} {cy - r * 0.55} {cx + r * 0.2} {cy - r * 0.62} Q{cx - r * 0.2} {cy - r * 0.52} "
                   f"{cx - r * 0.55} {cy - r * 0.68} Q{cx - r * 0.9} {cy - r * 0.55} {cx - r} {cy - 2}Z", hair)
    return back + ear + face + fringe


def body(shirt: str, cx=50, top=72, w=34) -> str:
    """Shoulders and chest under a head, running off the bottom of the canvas."""
    return P(f"M{cx - w} 96 Q{cx - w} {top} {cx} {top - 2} Q{cx + w} {top} {cx + w} 96Z", shirt)


# ── Expressions (cx, cy, r of the head) ─────────────────────────────────────────────────────

def dot_eyes(cx=50, y=56, dx=11, r=4) -> str:
    return eye(cx - dx, y, r) + eye(cx + dx, y, r)


def closed_eyes(cx=50, y=56, dx=11, up=True) -> str:
    """Curved lines: up=True is a happy ^ ^ arc, False a sleepy ‿ ‿ one."""
    k = -5 if up else 4
    return "".join(L(f"M{x - 5} {y} Q{x} {y + k} {x + 5} {y}") for x in (cx - dx, cx + dx))


def brows(cx=50, y=44, dx=11, tilt=0) -> str:
    """tilt > 0 slopes down to the middle (angry), < 0 slopes up (worried)."""
    return (L(f"M{cx - dx - 5} {y - tilt} L{cx - dx + 5} {y + tilt}")
            + L(f"M{cx + dx - 5} {y + tilt} L{cx + dx + 5} {y - tilt}"))


def cheeks2(cx=50, y=66, dx=18) -> str:
    return cheek(cx - dx, y) + cheek(cx + dx, y)


def mouth_smile(cx=50, y=68, w=8) -> str:
    return L(f"M{cx - w} {y} Q{cx} {y + w * 0.9} {cx + w} {y}")


def mouth_frown(cx=50, y=72, w=7) -> str:
    return L(f"M{cx - w} {y} Q{cx} {y - w * 0.8} {cx + w} {y}")


def mouth_open(cx=50, y=66, w=10, h=10, tongue=True) -> str:
    """A big laughing D-shaped mouth."""
    s = P(f"M{cx - w} {y} Q{cx} {y + 2} {cx + w} {y} Q{cx + w} {y + h} {cx} {y + h} Q{cx - w} {y + h} {cx - w} {y}Z", "#8E2F4A")
    if tongue:
        s += E(cx, y + h - 2.5, w * 0.5, 3, "#FF7A90", 0)
    return s


def mouth_o(cx=50, y=70, rx=5, ry=6) -> str:
    return E(cx, y, rx, ry, "#8E2F4A")


def tear(x, y, s=1.0) -> str:
    return P(f"M{x} {y} Q{x + 5 * s} {y + 8 * s} {x} {y + 11 * s} Q{x - 5 * s} {y + 8 * s} {x} {y}Z", "#7CCBF5", 2)
