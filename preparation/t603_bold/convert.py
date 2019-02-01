if __name__ == "__main__":
    import sys

    input_path = sys.argv[1]
    output_path = sys.argv[2]

    hafas_ids = set()

    with open(input_path) as f:
        for line in f:
            hafas_ids.add(int(line))

    with open(output_path, "w+") as f:
        for hafas_id in hafas_ids:
            f.write(str(int("85%05d" % int(hafas_id))) + "\n")
