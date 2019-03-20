import re

def read(path):
    stations = []

    with open(path, encoding="utf-8") as f:
        reading = False

        for line in f:
            line = line.strip()

            match = re.match(r'^(\w{2}.*?)\s+([0-9/]{3,7})', line, re.UNICODE)

            if match:
                station_name = match.group(1).strip()
                zones = [int(z) for z in match.group(2).split("/")]
                stations.append((station_name, zones, "Libero"))

    return stations
