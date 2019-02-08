import docx

def read(path):
    stations = []
    document = docx.Document(path)

    for table in document.tables:
        for row in table.rows:
            row = [cell.text.strip() for cell in row.cells]

            if not (row[3] == "TU-C" or len(row[1].strip()) == 0):
                station_name = row[0]
                station_zones = [int(row[1])]

                if len(row[2]) > 0:
                    station_zones.append(int(row[2]))

                stations.append((station_name, station_zones, "TVSZ"))

    return stations
