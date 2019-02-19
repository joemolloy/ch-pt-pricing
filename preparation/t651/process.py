import pickle, re
import geopandas as gpd
import shapely.geometry as geo
import pandas as pd
import numpy as np
import sys

def read_stations(authorities_path):
    print("Loading stations ...")
    stations = []

    import authorities.awelle
    stations += authorities.awelle.read("%s/t651.awelle.txt" % authorities_path)

    import authorities.engadin_mobil
    stations += authorities.engadin_mobil.read("%s/t651.engadin_mobil.txt" % authorities_path)

    import authorities.frimobil
    stations += authorities.frimobil.read("%s/t651.frimobil.txt" % authorities_path)

    import authorities.libero
    stations += authorities.libero.read("%s/t651.libero.txt" % authorities_path)

    import authorities.mobilis
    stations += authorities.mobilis.read("%s/t651.mobilis.xls" % authorities_path)

    import authorities.ostwind
    stations += authorities.ostwind.read("%s/t651.ostwind.txt" % authorities_path)

    import authorities.passepartout
    stations += authorities.passepartout.read("%s/t651.passepartout.txt" % authorities_path)

    import authorities.transreno
    stations += authorities.transreno.read("%s/t651.transreno.txt" % authorities_path)

    import authorities.tvsz
    stations += authorities.tvsz.read("%s/t651.tvsz.docx" % authorities_path)

    import authorities.tvzg
    stations += authorities.tvzg.read("%s/t651.tvzg.txt" % authorities_path)

    import authorities.unireso
    stations += authorities.unireso.read("%s/t651.unireso.txt" % authorities_path)

    import authorities.zvv
    stations += authorities.zvv.read("%s/t651.zvv.txt" % authorities_path)

    df_stations = []

    for station_name, station_zones, station_authority in stations:
        for station_zone in station_zones:
            df_stations.append([station_name, station_zone, station_authority])

    df_stations = pd.DataFrame.from_records(
        df_stations,
        columns = ["station_name", "station_zone", "station_authority"]
    )
    df_stations["station_id"] = np.arange(len(df_stations))
    return df_stations

def read_hafas(hafas_path):
    print("Reading HAFAS ...")
    df_hafas = []

    with open("%s/BFKOORD_GEO" % hafas_path) as f:
        for line in f:
            parts = line.split("%")
            data = re.split(r'[ ]+', parts[0])

            hafas_name = parts[1].strip()
            hafas_id = int(data[0])
            hafas_geometry = geo.Point(float(data[1]), float(data[2]))

            df_hafas.append([hafas_id, hafas_name, hafas_geometry])

    df_hafas = pd.DataFrame.from_records(
        df_hafas,
        columns =  ["hafas_id", "hafas_name", "geometry"]
    )

    df_hafas = gpd.GeoDataFrame(df_hafas, crs = {"init" : "EPSG:4326"})
    df_hafas = df_hafas.to_crs({ "init" : "EPSG:2056" })
    return df_hafas

if __name__ == "__main__":
    hafas_path = sys.argv[1]
    authorities_path = sys.argv[2]
    output_path = sys.argv[3]

    # Read input data
    df_hafas = read_hafas(hafas_path)
    df_stations = read_stations(authorities_path)

    # Merge by name
    print("Merging ...")
    df_merge = pd.merge(
        df_hafas, df_stations,
        left_on = "hafas_name", right_on = "station_name",
        how = "left"
    )

    # Some of the merging doesn't work. Here are those that cannot be matched
    # and would need to be fixed.

    all_ids = set(df_stations["station_id"])
    matched_ids = set(df_merge["station_id"].dropna())
    missing_ids = all_ids - matched_ids

    df_errors = df_stations[df_stations["station_id"].isin(missing_ids)]

    print("Stations with zones that could not be matched: %d (%.2f%%)" % (
        len(missing_ids), 100.0 * len(missing_ids) / len(all_ids)
    ))

    # Impute zones

    # Here we create convex hulls for all (zone, authority) pairs and find a zone
    # for all unmatched HAFAS ids via spatial join.

    df_convex_hulls = gpd.GeoDataFrame(df_merge.dropna().groupby([
        "station_authority", "station_zone"
    ])["geometry"].agg(
        lambda group: geo.MultiPoint(list(group.values)).convex_hull if len(group.values) > 3 else None
    ).reset_index(name = "geometry")).dropna()
    df_convex_hulls.crs = {"init" : "EPSG:2056"}

    df_missing = df_merge[df_merge["station_name"].isna()][["hafas_id", "hafas_name", "geometry"]]
    df_spatial_merge = gpd.sjoin(df_missing, df_convex_hulls, op = "within")

    print("HAFAS stations matched by spatial merge: %d" % len(df_spatial_merge))

    # Cleanup

    df_spatial_merge["imputed"] = 1
    df_merge["imputed"] = 0

    df_merge = df_merge[~df_merge["hafas_id"].isin(df_spatial_merge["hafas_id"])]
    df_merge = df_merge[["hafas_id", "hafas_name", "station_zone", "station_authority", "imputed", "geometry"]]
    df_spatial_merge = df_spatial_merge[["hafas_id", "hafas_name", "station_zone", "station_authority", "imputed", "geometry"]]
    df_merge = pd.concat([df_merge, df_spatial_merge])

    assert(len(np.unique(df_merge["hafas_id"])) == len(np.unique(df_hafas["hafas_id"])))
    df_merge.columns = ["hafas_id", "hafas_name", "zone_id", "authority_id", "imputed", "geometry"]

    # Output shape file
    print("Writing HAFAS shape file ...")
    df_merge.to_file("%s/t651.hafas_mapped.shp" % output_path)

    # Output convex hulls
    print("Writing convex hulls shape file ...")
    df_convex_hulls.to_file("%s/t651.convex_hulls.shp" % output_path)

    # Output routing information
    print("Writing csv ...")
    df_output = df_merge[["hafas_id", "zone_id", "authority_id", "imputed"]].dropna()
    df_output["zone_id"] = df_output["zone_id"].astype(np.int)
    df_output["imputed"] = df_output["imputed"].astype(np.bool)
    df_output.to_csv("%s/t651.csv" % output_path, sep = ";", index = None)
