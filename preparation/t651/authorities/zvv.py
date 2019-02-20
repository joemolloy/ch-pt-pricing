import re

def read(path):
    stations = []

    with open(path, encoding="utf8") as f:
        expect_empty = False
        reading_stations = False

        station_length = None
        network_length = None

        for line in f:
            line = line.strip()

            if re.search(r'^[\w]+', line) and re.search(r'[0-9]+$', line) and not re.search(r'^[0-9]+', line):
                parts = re.split(r'[ ]{2,}', line)

                station_name = parts[0].strip()
                zones = parts[2].strip()

                if "/" in zones:
                    zones = [int(z) for z in zones.split("/")]
                else:
                    zones = [int(zones)]

                stations.append((station_name, zones, "ZVV"))

    return stations
