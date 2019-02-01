import re
import sys
import json
import pandas as pd

FIRST_IS_NUMBER_PATTERN = re.compile(r"[0-9]+")
STATION_NAME_PATTERN = re.compile(r"[\S ]+? [0-9X]{4,5}")
IS_NUMBER_PATTERN = re.compile("^[0-9]+$")
DIRECTION_PATTERN = re.compile(r"\(Fahrtrichtung (.*?)\)")

MISSING_STATION_IDS = [
    {"page" : 29, "name" : "Le Crêt-du-Locle", "new_id" : "00001"},
    {"page" : 26, "name" : "La Neuveville", "new_id" : "00002"},
    {"page" : 33, "name" : "Fribourg/Freiburg Poya", "new_id" : "00003"},
    {"page" : 60, "name" : "Niederurnen, Ziegelbrückstr.", "new_id" : "00004"}
]

def fix_line(line, page_number):
    if "Oberdorf SO 264" in line:
        print("Fixing Oberdorf on page %d" % page_number)
        line = line.replace("Oberdorf SO 264", "Oberdorf SO 0264")

    for fix in MISSING_STATION_IDS:
        if fix["page"] == page_number and line.endswith(fix["name"]):
            print("Fixing %s on page %d to id '%s'" % (fix["name"], fix["page"], fix["new_id"]))
            line += " " + fix["new_id"]

    return line

def fix_station_line(line, page_number):
    for i in range(len(line)):
        if page_number == 29 and (line[i] == "29a)" or line[i] == "44a)"):
            line[i] = line[i][:2]
        elif page_number == 20 and line[i] == "*":
            line[i] = "13"
        elif page_number == 34 and line[i] == "40a)":
            line[i] = "40"

    return line

def read_station(line, direction):
    # Some stations have spaces in the name. Here we count until we find the
    # first number. Everything before belongs to the name.
    number_count = 0

    while re.match(IS_NUMBER_PATTERN, line[number_count]):
        number_count += 1

    station_name = " ".join(line[number_count:][:-1])
    station_id = line[-1]
    distances = list(map(int, line[:number_count]))

    if "Bözingerfeld" in station_name:
        print("Fixing Bözingerfeld -> Bözingenfeld")
        station_name = station_name.replace("Bözingerfeld", "Bözingenfeld")

    if station_name == "Linthal *":
        print("Fixing Linthal * -> Linthal")
        station_name = "Linthal"

    if station_id == "18452":
        print("Fixing ID 18452 -> 18542")
        station_id = "18542"

    if len(station_id) < 5:
        initial_station_id = station_id
        station_id = ("0" * (5 - len(station_id))) + station_id
        print("Fixing ID %s -> %s" % (initial_station_id, station_id))

    return {
        "name" : station_name, "id" : station_id, "distances" : distances,
        "direction" : direction
    }

def read_triangle(page_number, path):
    with open(path, "r") as f:
        lines = [re.sub(r"\s+", " ", line).strip() for line in f]

        # Find triangle id
        triangle_id = lines[0].split(" ")[-1]

        # Find first station in the lines of the page
        first_station_index = 0
        for index, line in enumerate(lines):
            if re.match(STATION_NAME_PATTERN, line):
                first_station_index = index
                break

        # This will be filled with the station information
        reading_stations = False
        stations = []

        # Read through the file
        for line in lines[first_station_index:]:
            # Some special lines
            if len(line) == 0:
                #print("Ending page %d with empty line" % page_number)
                break

            if "Streckenabonnemente" in line:
                print("Skipping 'Streckenabonnemente' on page %d" % page_number)
                continue

            # Some fixing
            line = fix_line(line, page_number)

            # Read through the stations
            station_match = re.search(STATION_NAME_PATTERN, line)
            if station_match:
                reading_stations = True # From now on we always expect a station in the next line

                # Find the direction if the station has one
                current_direction = None
                direction_match = re.search(DIRECTION_PATTERN, line)

                if direction_match:
                    current_direction = direction_match.group(1).split(" ")[-1]

                # Cut the line
                line = line[:station_match.end(0)].split(" ")

                # Apply some fixes
                line = fix_station_line(line, page_number)

                # Read station information
                stations.append(read_station(line, current_direction))
            elif reading_stations:
                raise RuntimeError("Page %d contains line without a station" % page_number)

        # Some distances are direction-dependent
        forward_direction = None
        backward_direction = None

        if page_number == 24:
            forward_direction = "Visp"
            backward_direction = "Leuk"

        # Construct two lists of indices to traverse the route
        forward_indices = [
            index
            for index, station in enumerate(stations)
            if station["direction"] is None or station["direction"] == forward_direction
        ]

        backward_indices = [
            index
            for index, station in enumerate(stations)
            if station["direction"] is None or station["direction"] == backward_direction
        ]

        # Here we have read all stations
        matrix = []

        # First, go forward
        for destination_index in forward_indices:
            destination_station = stations[destination_index]

            for j in range(len(destination_station["distances"])):
                origin_index = forward_indices[j]
                origin_station = stations[origin_index]
                distance = destination_station["distances"][j]
                matrix.append((origin_station["id"], destination_station["id"], distance))

        # Second, go backward
        for origin_index in backward_indices:
            origin_station = stations[origin_index]

            for j in range(len(origin_station["distances"])):
                destination_index = backward_indices[j]
                destination_station = stations[destination_index]
                distance = origin_station["distances"][j]
                matrix.append((origin_station["id"], destination_station["id"], distance))

        for station in stations:
            matrix.append((station["id"], station["id"], 0.0))

        return { "matrix" : matrix, "id" : triangle_id, "stations" : stations }

if __name__ == "__main__":
    conversion_path = sys.argv[1]
    output_path = sys.argv[2]

    pages = list(range(20, 63))
    triangles = []

    for page in pages:
        triangle = read_triangle(page, "%s/%d.txt" % (conversion_path, page))
        triangles.append(triangle)

    df_triangles = []

    for triangle in triangles:
        triangle_id = triangle["id"]

        for origin_hafas_id, destination_hafas_id, distance in triangle["matrix"]:
            origin_hafas_id = int("85%05d" % int(origin_hafas_id))
            destination_hafas_id = int("85%05d" % int(destination_hafas_id))
            df_triangles.append([triangle_id, origin_hafas_id, destination_hafas_id, distance])

    df_triangles = pd.DataFrame.from_records(
        df_triangles,
        columns = ["triangle_id", "origin_hafas_id", "destination_hafas_id", "distance"]
    )

    df_triangles.to_csv(output_path, sep = ";", index = None)

    #with open(output_path, "w+") as f:
    #    output = json.dump(triangles, f)
