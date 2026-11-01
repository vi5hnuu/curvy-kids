#!/usr/bin/env python3
"""
Builds app/src/main/assets/art/ from the old badge library in app/src/main/assets/kids/.

The old badges bake a coloured frame and the item's name into every picture. The app now draws
frames and names itself (so a picture inside a quiz tile no longer shows a frame inside a frame,
and the name can be spoken, styled and translated). This strips them:

  * emoji-based pictures (incl. letter cards like "A + apple"): keep only the picture group,
    re-based to its own 36x36 viewBox
  * counting / fractions / telling-time: drop the frame rect, keep the drawing
  * everything else (shapes, patterns, and text-only badges that screens will draw in Compose):
    copied unchanged

Pictures drawn by tools/art/draw (marked data-bare="1") have replaced every emoji-based one and
are never overwritten here, so re-running only refreshes the non-picture files.
Usage: python3 tools/art/unframe.py
"""
import pathlib
import re

ROOT = pathlib.Path(__file__).resolve().parents[2] / "app/src/main/assets"
SRC, DST = ROOT / "kids", ROOT / "art"

FRAME = re.compile(r'<rect x="7" y="7" width="86" height="86"[^>]*/>')
TEXT = re.compile(r"<text\b.*?</text>", re.S)
# The picture group: <g transform="translate(X Y) scale(S)"> ... </g> wrapping 36x36 emoji art.
PICTURE = re.compile(r'<g transform="translate\([\d. ]+\) scale\([\d.]+\)">(.*)</g>', re.S)
DROP_FRAME_ONLY = {"counting", "fractions", "telling-time"}


def convert(category: str, svg: str) -> str:
    picture = PICTURE.search(svg)
    if picture:
        return ('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 36 36" '
                f'preserveAspectRatio="xMidYMid meet">{picture.group(1)}</svg>')
    if category in DROP_FRAME_ONLY:
        return FRAME.sub("", svg, count=1)
    return svg


def main() -> None:
    counts = {"picture": 0, "unframed": 0, "copied": 0, "kept drawn": 0}
    for src in sorted(SRC.rglob("*.svg")):
        category = src.parent.name
        svg = src.read_text(encoding="utf-8")
        out = convert(category, svg)
        kind = "picture" if out.startswith('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 36 36"') \
            else "unframed" if out != svg else "copied"
        dst = DST / category / src.name
        if dst.exists() and 'data-bare="1"' in dst.read_text(encoding="utf-8")[:200]:
            counts["kept drawn"] += 1
            continue
        counts[kind] += 1
        dst.parent.mkdir(parents=True, exist_ok=True)
        dst.write_text(out, encoding="utf-8")
    print(counts)


if __name__ == "__main__":
    main()
