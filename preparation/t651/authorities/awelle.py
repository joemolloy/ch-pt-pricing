import re

def read(path):
    zones = []
    names = []

    with open(path) as f:
        reading = False

        for line in f:
            line = line.strip()

            if line.startswith("Entwerter"):
                reading = True
                continue

            if reading:
                if line.startswith("09.12.2018"):
                    continue

                if line.startswith("Seite"):
                    continue

                if line.startswith("T651"):
                    continue

                if line.startswith("Zone"):
                    continue

                if line.startswith("Entwerter"):
                    continue

                if line.startswith("Haltestelle"):
                    continue

                if line == "RVBW" or line == "BULI" or line == "HZH" or line == "NIIN" or line == "OBBI" or line == "NBD" or line == "BBA" or line == "SNAG" or line == "Schönenwerd":
                    continue

                if len(line) <= 1:
                    continue

                if re.match(r'^[0-9]{3}$', line) or re.match(r'^[0-9]{3}/[0-9]{3}$', line):
                    zones.append(list(map(int, line.split("/"))))
                elif not re.match(r'[0-9]{1,}', line):
                    names.append(line)

    stations = []

    for station_name, station_zones in zip(names, zones):
        stations.append((station_name, station_zones, "AWelle"))

    return stations
