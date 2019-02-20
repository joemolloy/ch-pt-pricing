import re

def read(path):
    stations = []

    with open(path, encoding="utf8") as f:
        reading = False

        for line in f:
            line = line.strip()

            if re.search(r'[0-9]{4,}$', line):
                line = line.replace(" / ", "/")
                parts = re.split(r'\s+', line)

                zones = list(map(int, parts[-2].split("/")))
                station_name = " ".join(parts[:-3])

                stations.append((station_name, zones, "TVZG"))

    return stations
