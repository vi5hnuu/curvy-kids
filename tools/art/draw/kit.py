"""
Drawing kit for the CurvyKids picture set.

Every picture in app/src/main/assets/art/ (except the custom number/shape/time sets) is drawn
from these helpers so the whole app shares one style:

  * a 100x100 canvas, nothing touching the edge (keep ~6 units of margin);
  * one outline colour (OL), 3 units wide, round joins and caps;
  * flat bright fills from PALETTE, at most one white highlight stroke;
  * creatures get dot eyes with a white glint and pink cheeks;
  * no text: the app draws the name under the picture (SvgBadge).

The root <svg> carries data-bare="1", which tells SvgBadge the picture has no frame of its own.
Category modules (animals.py, food.py, ...) build their drawings with these functions and are
written to the assets folder by build.py.
"""

OL = "#3B2F5B"

PALETTE = {
    "sky": "#4FC3F7", "sun": "#FFC93C", "grass": "#7ED957", "berry": "#FF5C8A",
    "grape": "#9B6BFF", "orange": "#FF9F43", "red": "#F0464B", "pink": "#FFB3C7",
    "cheek": "#FF8FA3", "cream": "#FFF0D4", "white": "#FFFFFF", "brown": "#B5794A",
    "darkbrown": "#7A4A32", "grey": "#A9B4C2", "glass": "#8ED8FF",
}


def svg(body: str) -> str:
    """Wraps drawing markup in the standard canvas."""
    return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" data-bare="1">{body}</svg>'


def st(w: float = 3, color: str = OL) -> str:
    """Outline attributes; w=0 means no outline."""
    if not w:
        return ""
    return f'stroke="{color}" stroke-width="{w}" stroke-linejoin="round" stroke-linecap="round"'


def _op(op):
    return f' opacity="{op}"' if op is not None else ""


def P(d: str, fill: str = "none", w: float = 3, op=None) -> str:
    """A filled, outlined path."""
    return f'<path d="{d}" fill="{fill}" {st(w)}{_op(op)}/>'


def L(d: str, color: str = OL, w: float = 3, op=None) -> str:
    """A stroke-only line in any colour (details, whiskers, stripes)."""
    return (f'<path d="{d}" fill="none" stroke="{color}" stroke-width="{w}" '
            f'stroke-linecap="round" stroke-linejoin="round"{_op(op)}/>')


def C(cx, cy, r, fill, w: float = 3, op=None) -> str:
    return f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="{fill}" {st(w)}{_op(op)}/>'


def E(cx, cy, rx, ry, fill, w: float = 3, rot: float = 0, op=None) -> str:
    t = f' transform="rotate({rot} {cx} {cy})"' if rot else ""
    return f'<ellipse cx="{cx}" cy="{cy}" rx="{rx}" ry="{ry}" fill="{fill}" {st(w)}{t}{_op(op)}/>'


def R(x, y, w, h, rx, fill, sw: float = 3, rot: float = 0, op=None) -> str:
    t = f' transform="rotate({rot} {x + w / 2} {y + h / 2})"' if rot else ""
    return f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{rx}" fill="{fill}" {st(sw)}{t}{_op(op)}/>'


def G(markup: str, transform: str) -> str:
    return f'<g transform="{transform}">{markup}</g>'


def sym(markup: str) -> str:
    """The markup plus its mirror image across the vertical centre line (ears, horns, wings)."""
    return markup + G(markup, "matrix(-1 0 0 1 100 0)")


def flip(markup: str) -> str:
    """Mirrors a whole drawing left to right."""
    return G(markup, "matrix(-1 0 0 1 100 0)")


def eyes(y, dx=10, cx=50, r=4.2, ring=False) -> str:
    """Two dot eyes with a glint. ring=True adds a white surround, for dark faces."""
    s = ""
    for x in (cx - dx, cx + dx):
        if ring:
            s += C(x, y, r * 1.55, "#fff", 0)
        s += C(x, y, r, OL, 0) + C(round(x + r * 0.33, 1), round(y - r * 0.36, 1), round(r * 0.36, 1), "#fff", 0)
    return s


def eye(x, y, r=4.2, ring=False) -> str:
    """One eye, for side views."""
    s = C(x, y, r * 1.55, "#fff", 0) if ring else ""
    return s + C(x, y, r, OL, 0) + C(round(x + r * 0.33, 1), round(y - r * 0.36, 1), round(r * 0.36, 1), "#fff", 0)


def cheeks(y, dx=16, cx=50, rx=5, ry=3.2) -> str:
    return "".join(E(x, y, rx, ry, PALETTE["cheek"], 0, op=".75") for x in (cx - dx, cx + dx))


def cheek(x, y, rx=5, ry=3.2) -> str:
    return E(x, y, rx, ry, PALETTE["cheek"], 0, op=".75")


def smile(cx, y, w=5) -> str:
    return L(f"M{cx - w} {y} Q{cx} {y + w} {cx + w} {y}")


def wmouth(cx, y, w=5) -> str:
    """The little 'w' mouth of cats, dogs and bunnies, hanging from the nose at (cx, y)."""
    return L(f"M{cx} {y} V{y + 3} M{cx} {y + 3} Q{cx - w * 0.7} {y + 7} {cx - w * 1.3} {y + 4.5} "
             f"M{cx} {y + 3} Q{cx + w * 0.7} {y + 7} {cx + w * 1.3} {y + 4.5}")


def shine(d, w=4) -> str:
    """The one white highlight stroke a picture may have."""
    return L(d, "#fff", w, op=".75")


def scallop(cx, cy, r, bumps, depth, a0=0.0, a1=None) -> str:
    """A closed bumpy outline (manes, wool, clouds, bushes)."""
    import math
    full = a1 is None
    a1 = 2 * math.pi if full else a1
    pts = []
    for i in range(bumps):
        s = a0 + (a1 - a0) * i / bumps
        e = a0 + (a1 - a0) * (i + 1) / bumps
        m = (s + e) / 2
        pts.append((cx + r * math.cos(s), cy + r * math.sin(s),
                    cx + (r + depth * 2) * math.cos(m), cy + (r + depth * 2) * math.sin(m),
                    cx + r * math.cos(e), cy + r * math.sin(e)))
    d = f"M{pts[0][0]:.1f} {pts[0][1]:.1f}" + "".join(
        f" Q{p[2]:.1f} {p[3]:.1f} {p[4]:.1f} {p[5]:.1f}" for p in pts)
    return d + ("Z" if full else "")


def zigzag(cx, cy, r, n, depth, a0, a1) -> str:
    """Spikes along an arc (hedgehog, dinosaur plates); returns path data, not closed."""
    import math
    out = []
    for i in range(n * 2 + 1):
        a = a0 + (a1 - a0) * i / (n * 2)
        rr = r + (depth if i % 2 else 0)
        out.append(f"{cx + rr * math.cos(a):.1f} {cy + rr * math.sin(a):.1f}")
    return "M" + " L".join(out)


def tube(d: str, fill: str, w: float = 8) -> str:
    """A thick outlined stroke (necks, tails, handles, stems): the outline drawn under the fill."""
    return L(d, OL, w + 6) + L(d, fill, w)
