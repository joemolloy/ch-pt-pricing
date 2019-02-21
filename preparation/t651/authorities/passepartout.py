import re

def read(path):
    zones = []
    names = []

    with open(path, 'r', encoding='utf8') as f:
        reading = False

        content = ""

        for line in f:
            content += line

        for k in range(60, 80):
            content = content.replace("\n\n%d\n\n" % k, "\n")

        for line in content.split("\n"):
            line = line.strip()

            if line.startswith("Haltestelle"):
                reading = True
                continue

            if reading:
                if line.startswith("09.12.2018"):
                    continue

                if line.startswith("Zone"):
                    continue

                if line.startswith("DidokNr."):
                    continue

                if line.startswith("Haltestelle"):
                    continue

                if line.startswith("T651.5"):
                    continue

                if line.startswith("Haltestelle"):
                    continue

                if line.startswith("Anhang"):
                    continue

                if len(line) == 0:
                    continue

                if re.match(r'^[0-9]{2}$', line) or re.match(r'^[0-9]{2}/[0-9]{2}$', line):
                    zones.append(list(map(int, line.split("/"))))
                elif not re.match(r'[0-9]{1,}', line):
                    names.append(line)

    stations = []

    for station_name, station_zones in zip(names, zones):
        stations.append((station_name, station_zones, "Passepartout"))

    return stations
