import json

with open("./seeds.txt", "r") as f:
    content = f.read().splitlines()
    seeds = []
    for i in content:
        seeds.append(int(i))
    with open("./seeds.json", "w") as w:
        json.dump(seeds, w, indent=4)
