import re

def read(path):
    stations = []

    with open(path, encoding="utf-8") as f:
        reading = False

        for line in f:
            line = line.strip()

            match = re.match(r"^\W*([\w+ ,/.'-]+?)\s+([0-9/]{2,5})(?=\s+[A-Za-z])", line)

            if match:
                station_name = match.group(1).strip()
                zones = [int(z) for z in match.group(2).split('/')]
                stations.append((station_name, zones, "OndeVerte"))

    return stations

if __name__ == "__main__":
    import sys
    print(read(sys.argv[1]))
