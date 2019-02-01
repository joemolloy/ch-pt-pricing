import re

def read(path):
    stations = []

    with open(path) as f:
        reading = False
        expect = None

        last_line = None

        for line in f:
            line = line.strip()

            if line.startswith("L’impression"):
                reading = True
                continue

            if reading:
                if line.startswith("09.12.2018"):
                    continue

                if line.startswith("T651"):
                    continue

                if line.startswith("Seite"):
                    continue

                if line.startswith("_____"):
                    continue

                if len(line) == 0:
                    continue

                if re.match(r'^[0-9]{3}$', line) or re.match(r'^[0-9]{3}/[0-9]{3}$', line):
                    station_name = last_line
                    zones = list(map(int, line.split("/")))
                    stations.append((station_name, zones, "Libero"))
                elif not re.match(r'[0-9]{3}', line):
                    last_line = line

    return stations
