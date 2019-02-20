import re

def read(path):
    stations = []

    with open(path, encoding="utf8") as f:
        reading = False

        for line in f:
            line = line.strip()

            match = re.match(r'^[0-9]+\s+([0-9]{1,2})\s+([\w ,/]+)$', line)

            if match:
                station_name = match.group(2).strip()
                zones = [int(match.group(1))]
                stations.append((station_name, zones, "TransReno"))

    return stations
