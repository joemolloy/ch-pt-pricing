This document describes how various data sources are prepared so they can used
to calculate prices for public transport connections in Switzerland.

**01_digitize_t301.sh**

T603 is the official document of VÖV which describes how distances for public
transport connections (long distance) are calculated. This covers basically the
whole train network of Switzerland. For that, the document provides a number
of "triangles", which contain a number of stations. Between each of the stations
on one triangle a distance is given. This is not necessarily the actual distance
between two stations, but represents the distance on which the price calcluation
of SBB is based.

In order to calculate the distance between Geneva and Luzern for instance, one
then needs to "travel" through the triangles through their connecting stations
until one reaches the destination. It should be noted that just T603 does not
given automatically the correct distance, given a origin and destination station.
In theory, one would first find a route through the official schedule, because
obviously the distance depends on the chosen route. If one takes a large detour
the distance is longer and hence the price will be higher.

The script converts the pages of the T603 document (which is a PDF) to text
files which can be processed later on by the cleaning scripts.

It can be called as follows:

```
sh 01_prepare_t603.sh PATH_TO_T603.pdf CONVERSION_OUTPUT_DIRECTORY
```

The script will read the PDF and create a `*.txt` file for each page with
distance information.

**02_clean_t301.py**

This script cleans the `*.txt` and brings them in JSON format, so it can be read
by Java/MATSim later on. It can be called as follows:

```
python3 02_clean_t603.py CONVERSION_OUTPUT_DIRECTORY PATH/TO/t603.json
```
