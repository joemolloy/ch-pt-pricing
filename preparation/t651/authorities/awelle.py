import re
import pandas as pd

def read(path):
    stations = []
    df = pd.read_excel(path)

    for station_name, station_zone in zip(df["station_name"], df["station_zone"]):
        stations.append((station_name, [int(z) for z in str(station_zone).split("/")], "AWelle"))

    return stations

if __name__ == "__main__":
    import sys
    print(read(sys.argv[1]))