import re

def read(path):
    stations = []

    with open(path, encoding="utf8") as f:
        reading = False

        for line in f:
            line = line.strip()

            match = re.match(r'^([\w+ ,/]+)\s+([0-9]{3})\s+[0-9]{2,3}\s+[\w/-]+$', line)

            if match:
                station_name = match.group(1).strip()
                zones = [int(match.group(2))]
                stations.append((station_name, zones, "Ostwind"))

    return stations
