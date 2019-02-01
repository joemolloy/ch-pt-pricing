import re

def read(path):
    stations = []

    with open(path) as f:
        reading = False

        for line in f:
            line = line.strip()

            match = re.match(r'^([\w+ ,/]+)\s+([0-9]{2})$', line)

            if match:
                station_name = match.group(1).strip()
                zones = [int(match.group(2))]
                stations.append((station_name, zones, "Unireso"))

    return stations
