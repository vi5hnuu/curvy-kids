"""Mirror of analyseDevanagari, rendered as a contact sheet so every letter can be checked."""
from PIL import Image, ImageDraw, ImageFont
import sys

FONT = "/System/Library/Fonts/Supplemental/Devanagari Sangam MN.ttc"
BOX = 128

def raster(ch):
    f = ImageFont.truetype(FONT, int(BOX*0.62))
    img = Image.new("L", (BOX, BOX), 0)
    d = ImageDraw.Draw(img)
    b = d.textbbox((0,0), ch, font=f)
    d.text(((BOX-(b[2]-b[0]))//2 - b[0], (BOX-(b[3]-b[1]))//2 - b[1]), ch, font=f, fill=255)
    return img

def analyse(ch):
    img = raster(ch); px = img.load()
    on = lambda x,y: px[x,y] > 100
    left, right, top, bot = BOX, -1, BOX, -1
    for y in range(BOX):
        for x in range(BOX):
            if on(x,y):
                left=min(left,x); right=max(right,x); top=min(top,y); bot=max(bot,y)
    if right < 0: return img, None
    inkW = right-left+1; inkH = bot-top+1

    # The shirorekha is the widest horizontal run in the letter. Anchor on that rather than on
    # "near the top": matras and the anusvara sit ABOVE the headline and were shifting the top.
    runs = []
    for y in range(top, top+int(inkH*0.70)+1):
        runs.append((sum(1 for x in range(left,right+1) if on(x,y)), y))
    best_run, best_y = max(runs) if runs else (0, top)
    if best_run < inkW*0.5:
        headline_top = headline_bottom = float(top)
    else:
        y = best_y
        while y > top and sum(1 for x in range(left,right+1) if on(x,y-1)) >= inkW*0.5: y -= 1
        headline_top = float(y)
        y = best_y
        while y < bot and sum(1 for x in range(left,right+1) if on(x,y+1)) >= inkW*0.5: y += 1
        headline_bottom = float(y+1)

    body_top = int(headline_bottom); body_h = max(1, bot-body_top+1)
    tall = [False]*BOX
    for x in range(left,right+1):
        run = sum(1 for y in range(body_top,bot+1) if on(x,y))
        tall[x] = run >= body_h*0.80
    stem = None
    x = right
    while x >= left:
        if tall[x]:
            end = x
            while x >= left and tall[x]: x -= 1
            start = x+1
            if (end-start+1) < inkW*0.45: stem = (start, end+1)
            break
        x -= 1
    return img, dict(left=left, right=right+1, top=top, bottom=bot+1,
                     headline=headline_bottom, headline_top=headline_top, stem=stem)

def sheet(chars, path, cols=7):
    cell = BOX+8
    rows = (len(chars)+cols-1)//cols
    out = Image.new("RGB", (cols*cell, rows*cell), (255,255,255))
    d = ImageDraw.Draw(out)
    problems = []
    for i,ch in enumerate(chars):
        img, a = analyse(ch)
        ox, oy = (i%cols)*cell+4, (i//cols)*cell+4
        out.paste(Image.merge("RGB",(img,img,img)).point(lambda v:255-v), (ox,oy))
        if not a:
            problems.append((ch,'no ink')); continue
        # headline line (red) and stem band (blue)
        d.rectangle([ox+a['left'],oy+a['headline_top'],ox+a['right'],oy+a['headline']],
                    outline=(230,40,40), width=2)
        if a['stem']:
            d.rectangle([ox+a['stem'][0],oy+a['top'],ox+a['stem'][1],oy+a['bottom']],
                        outline=(30,90,230), width=2)
        inkH = a['bottom']-a['top']
        band = a['headline']-a['headline_top']
        if band <= 1: problems.append((ch,'no headline found'))
        elif band > inkH*0.22: problems.append((ch,f'headline band too thick ({band/inkH:.0%})'))
    out.save(path)
    return problems

VOWELS = list("अआइईउऊऋएऐओऔ") + ["अं","अः"]
CONSONANTS = list("कखगघङचछजझञटठडढणतथदधनपफबभमयरलवशषसह") + ["क्ष","त्र","ज्ञ"]
p1 = sheet(VOWELS, sys.argv[1]+"/parts-vowels.png")
p2 = sheet(CONSONANTS, sys.argv[1]+"/parts-consonants.png")
print("VOWEL issues:");    [print("  ",c,r) for c,r in p1] or print("   none")
print("CONSONANT issues:");[print("  ",c,r) for c,r in p2] or print("   none")
