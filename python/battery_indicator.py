#!/usr/bin/env python
# coding=UTF-8

import math
import subprocess

p = subprocess.Popen(["ioreg", "-rc", "AppleSmartBattery"],
                     stdout=subprocess.PIPE)
output = p.communicate()[0]

o_max = [l for l in output.splitlines() if 'MaxCapacity' in l][0]
o_cur = [l for l in output.splitlines() if 'CurrentCapacity' in l][0]

b_max = float(o_max.rpartition('=')[-1].strip())
b_cur = float(o_cur.rpartition('=')[-1].strip())

charge = b_cur / b_max
charge_threshold = int(math.ceil(10 * charge))

# Output

total_slots, slots = 10, []
filled = int(math.ceil(charge_threshold * (total_slots / 10.0))) * u'▸'
empty = (total_slots - len(filled)) * u'▹'

out = (filled + empty).encode('utf-8')
import sys

green = '\033[01;32m'
red = '\033[01;31m'
yello = '\033[01;33m'
reset = '\033[00m'

color_out = (
    green if len(filled) > 6
    else yello if len(filled) > 4
    else red
)

out = green + out + reset
sys.stdout.write(out)
