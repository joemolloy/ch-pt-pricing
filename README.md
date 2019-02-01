This repository contains a collection of tools for PT routing and pricing in
Switzerland. It consists of two major parts: One part that is mainly used for
preparing raw data (such as the T603 tarif document) and one part that integrates
this data into MATSim / SwissRailRaptor. Finally, there is a public transit routing
server that offers an API and a simple web interface.

# Preparation

All data preparation is found in `/preparation`. In principle, the script
`/preparation/prepare.sh` should do all the work. It requires four input
parameters, which are:

1. Path to the HAFAS data set (i.e. a directory containing `BFKOORD_GEO`)
2. Path to the tarif data. It should contain the following files:
   - `t603.pdf`, the T603 SBB tarif information
   - `t603_bold.raw.txt`, a manually created list of station ids that are written in **bold** in T603 (this will probably not be needed anymore in future versions)
   - `t651.{authority}.pdf`, all T651 tarif documents for the transport authorities in Switzerland. Currently, these are `awelle`, `engadin_mobil`, `frimobil`, `libero`, `ostwind`, `passepartout`, `transreno`, `tvzg`, `unireso`, and `zvv`.
3. Path to a temporary directory. It will be used to perform some temporary file conversion.
4. Path to the output directory. Several files will be created here:
   - `t603.csv`, all the relevant information from T603 in digital form
   - `t603_bold.txt`, the **bold** station ids, properly formatted
   - `t651.csv`, a mapping of HAFAS station id to transport authorities and respective zones
   - `t651.hafas_mapped.shp`, a shape file containing all HAFAS stations with authority and zone information
   - `t651.convex_hulls.shp`, a shape file containing the convex hulls used to match HAFAS stations that could not be matched by name

To run the preparation scripts the following is needed:
- `python3` with `pandas` installed
- `pdf-stapler` command line utility to process PDF files [(see here)][1]
- `pdftotext` command line utility to process PDF files

# Java framework

TODO

# Web service

All the code for the web service is located in `ch.ethz.matsim.ch_pt_utils.server`.
The main script is `ch.ethz.matsim.ch_pt_utils.server.RunRoutingServer`. It requires
a number of input arguments:

1. The port on which to listen for the web service.
2. Path to `switzerland_transit_schedule.xml.gz`
3. Path to `switzerland_network.xml.gz`
4. Path to `t651.csv` (from the preparation output)
5. Path to `t603.csv` (from the preparation output)
6. Path to `t603_bold.txt` (from the preparation output)

Furthermore, the server requires a working installation of [GLPK for Java][2]. Note
that it is usually necessary to build it manually and put it for instance in `~/glpk`.

An example how to run:

```
java -Xmx10G -Djava.library.path=~/glpk/lib/jni ch.ethz.matsim.ch_pt_utils.server.RunRoutingServer 7050 switzerland_transit_schedule.xml.gz switzerland_network.xml.gz t651.csv t603.csv t603_bold.txt
```

[1]: https://pypi.org/project/stapler/
[2]: http://glpk-java.sourceforge.net/gettingStarted.html
