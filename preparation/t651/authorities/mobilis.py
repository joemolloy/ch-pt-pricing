import pandas as pd

def read(path):
    stations = []
    df = pd.read_excel(path)[["ZONE_1", "ARRET_LIB"]]

    for station_zone, station_name in zip(df["ZONE_1"], df["ARRET_LIB"]):
        stations.append((station_name, [int(station_zone)], "Mobilis"))

    return stations

if __name__ == "__main__":
    import sys
    print(read(sys.argv[1]))
