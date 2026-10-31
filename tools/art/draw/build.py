"""
Writes the drawn pictures into app/src/main/assets/art/ and optionally a contact sheet.

    python3 tools/art/draw/build.py animals              # write one category
    python3 tools/art/draw/build.py animals --sheet DIR  # ...and a labelled contact sheet in DIR
    python3 tools/art/draw/build.py all
    python3 tools/art/draw/build.py animals --dry --sheet DIR  # preview only, assets untouched

Each category module (animals.py, ...) defines CATEGORY (the assets folder) and DRAWINGS
({file name without .svg: svg markup}). Only files that already exist are replaced, so a typo in a
name fails loudly instead of adding a picture nothing points at. Kotlin paths never change.
"""
import importlib
import pathlib
import re
import subprocess
import sys

HERE = pathlib.Path(__file__).resolve().parent
ART = HERE.parents[2] / "app/src/main/assets/art"
MODULES = ["animals"]

sys.path.insert(0, str(HERE))


def write(module_name: str, dry: bool = False) -> dict:
    """Replaces the category's files (or, with dry, only checks the names) and returns the drawings."""
    mod = importlib.import_module(module_name)
    folder = ART / mod.CATEGORY
    existing = {p.stem for p in folder.glob("*.svg")}
    unknown = set(mod.DRAWINGS) - existing
    if unknown:
        sys.exit(f"{module_name}: no such files in {folder}: {sorted(unknown)}")
    if dry:
        return mod.DRAWINGS
    for name, markup in mod.DRAWINGS.items():
        (folder / f"{name}.svg").write_text(markup + "\n")
    missing = sorted(existing - set(mod.DRAWINGS))
    print(f"{mod.CATEGORY}: wrote {len(mod.DRAWINGS)}" + (f", not drawn yet: {missing}" if missing else ""))
    return mod.DRAWINGS


def sheet(name: str, drawings: dict, out: pathlib.Path) -> pathlib.Path:
    """Tiles the drawings with their names into one SVG and renders it to PNG with qlmanage."""
    cols, cell = 7, 170
    rows = (len(drawings) + cols - 1) // cols
    w, h = cols * cell + 20, rows * (cell + 30) + 20
    w = h = max(w, h)  # qlmanage thumbnails are square; keep every row in view
    cells = []
    for i, (n, s) in enumerate(sorted(drawings.items())):
        x, y = 10 + (i % cols) * cell, 10 + (i // cols) * (cell + 30)
        inner = re.search(r"<svg[^>]*>(.*)</svg>", s, re.S).group(1)
        cells.append(
            f'<rect x="{x + 5}" y="{y + 5}" width="{cell - 10}" height="{cell - 10}" rx="22" fill="#EAF7FF"/>'
            f'<svg x="{x + 15}" y="{y + 15}" width="{cell - 30}" height="{cell - 30}" viewBox="0 0 100 100">{inner}</svg>'
            f'<text x="{x + cell / 2}" y="{y + cell + 14}" text-anchor="middle" font-family="Helvetica" '
            f'font-size="15" fill="#3B2F5B">{n.split("-", 1)[-1]}</text>')
    doc = (f'<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}">'
           f'<rect width="{w}" height="{h}" fill="#fff"/>{"".join(cells)}</svg>')
    out.mkdir(parents=True, exist_ok=True)
    path = out / f"{name}.svg"
    path.write_text(doc)
    subprocess.run(["qlmanage", "-t", "-s", str(max(w, h)), "-o", str(out), str(path)],
                   stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    return out / f"{name}.svg.png"


if __name__ == "__main__":
    args = sys.argv[1:]
    out = pathlib.Path(args[args.index("--sheet") + 1]) if "--sheet" in args else None
    names = [a for a in args if not a.startswith("--") and (out is None or a != str(out))]
    for n in (MODULES if names == ["all"] else names):
        d = write(n, dry="--dry" in args)
        if out:
            print(sheet(n, d, out))
