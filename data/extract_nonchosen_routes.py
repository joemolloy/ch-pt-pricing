import savReaderWriter as spss
import sys, tqdm
import pandas as pd

if __name__ == "__main__":
    input_path = sys.argv[1]
    output_path = sys.argv[2]

    total_count = 0
    invalid_count = 0

    data = []

    with open(output_path, "w+") as writer:
        with spss.SavReader(input_path) as reader:
            header = reader.header

            columns = ["HHNR", "WEGNR"]

            for alternative in [1, 2, 3, 4]:
                columns.append("transfers_%d" % alternative)
                columns.append("x_%d" % alternative)
                columns.append("y_%d" % alternative)

            for line in tqdm.tqdm(reader):
                HHNR = int(line[header.index(b"HHNR")])
                WEGNR = int(line[header.index(b"WEGNR")])

                row = [HHNR, WEGNR]

                for alternative in [1, 2, 3, 4]:
                    departure_x = line[header.index(b"departureStopsX_pt.%d" % alternative)]
                    departure_y = line[header.index(b"departureStopsY_pt.%d" % alternative)]
                    arrival_x = line[header.index(b"arrivalStopsX_pt.%d" % alternative)]
                    arrival_y = line[header.index(b"arrivalStopsY_pt.%d" % alternative)]
                    transfers = int(line[header.index(b"transfers_pt.%d" % alternative)])
                    total_count += 1

                    if transfers != -99:
                        departure_x = [item.strip() for item in departure_x.split(b",")]
                        departure_y = [item.strip() for item in departure_y.split(b",")]
                        arrival_x = [item.strip() for item in arrival_x.split(b",")]
                        arrival_y = [item.strip() for item in arrival_y.split(b",")]

                        departure_x = [item for item in departure_x if len(item) > 0]
                        departure_y = [item for item in departure_y if len(item) > 0]
                        arrival_x = [item for item in arrival_x if len(item) > 0]
                        arrival_y = [item for item in arrival_y if len(item) > 0]

                        valid = True

                        if len(departure_x) != transfers + 1:
                            valid = False

                        if len(departure_y) != transfers + 1:
                            valid = False

                        if len(arrival_x) != transfers + 1:
                            valid = False

                        if len(arrival_y) != transfers + 1:
                            valid = False

                        if valid:
                            departure_x = [str(float(item)) for item in departure_x]
                            departure_y = [str(float(item)) for item in departure_y]
                            arrival_x = [str(float(item)) for item in arrival_x]
                            arrival_y = [str(float(item)) for item in arrival_y]

                            x = departure_x + [arrival_x[-1]]
                            y = departure_y + [arrival_y[-1]]

                            row.append(transfers)
                            row.append(",".join(x))
                            row.append(",".join(y))
                        else:
                            invalid_count += 1
                            row.append("-99")
                            row.append("NaN")
                            row.append("NaN")
                    else:
                        row.append("-99")
                        row.append("NaN")
                        row.append("NaN")

                assert(len(row) == len(columns))
                data.append(row)

        print("%d / %d (%.2f%%)" % (invalid_count, total_count, 100.0 * invalid_count / total_count))

        df = pd.DataFrame.from_records(data, columns = columns)
        df.to_csv(output_path, index = False, sep = ";")
