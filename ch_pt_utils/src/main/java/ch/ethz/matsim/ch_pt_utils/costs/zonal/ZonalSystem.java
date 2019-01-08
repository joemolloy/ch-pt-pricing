package ch.ethz.matsim.ch_pt_utils.costs.zonal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.matsim.api.core.v01.Coord;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

import ch.ethz.matsim.ch_pt_utils.costs.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.costs.zonal.data.ZoneGroup;
import ch.ethz.matsim.ch_pt_utils.costs.zonal.data.ZoneType;

public class ZonalSystem {
	private final Collection<Zone> zones;

	private ZonalSystem(Collection<Zone> zones) {
		this.zones = zones;
	}

	public Optional<Zone> getZone(Coord coord) {
		for (Zone zone : zones) {
			if (zone.contains(coord)) {
				return Optional.ofNullable(zone);
			}
		}

		return Optional.empty();
	}

	public double getPrice(List<Zone> zones, boolean halfFare) {
		Set<Zone> zonesSet = new HashSet<>(zones);

		long numberOfSingleZones = zonesSet.stream().filter(z -> z.getType().equals(ZoneType.SINGLE_ZONE)).count();
		long numberOfDoubleZones = zonesSet.stream().filter(z -> z.getType().equals(ZoneType.DOUBLE_ZONE)).count();

		double singleZonePrice = 0.0;
		double doubleZonePrice = 0.0;

		if (!halfFare) {
			singleZonePrice = numberOfSingleZones <= 1 ? 3.7 : 4.6;
			doubleZonePrice = numberOfDoubleZones <= 1 ? 4.6 : 6.2;
		} else {
			singleZonePrice = numberOfSingleZones <= 1 ? 2.5 : 3.0;
			doubleZonePrice = numberOfDoubleZones <= 1 ? 3.0 : 3.4;
		}

		return numberOfSingleZones * singleZonePrice + numberOfDoubleZones * doubleZonePrice;
	}

	static public ZonalSystem read(File shapefile) throws IOException, IOException {
		List<Zone> zones = new LinkedList<>();

		zones.add(new Zone("ZVV Zone 101 Winterhur", ZoneType.DOUBLE_ZONE, ZoneGroup.ZVV));
		zones.add(new Zone("ZVV Zone 100 Stadt Zürich", ZoneType.DOUBLE_ZONE, ZoneGroup.ZVV));
		zones.add(new Zone("A-Welle 510 Aarau", ZoneType.SINGLE_ZONE, ZoneGroup.AWelle));
		zones.add(new Zone("Arcobaleno Zone 10 Lugano", ZoneType.SINGLE_ZONE, ZoneGroup.Arcobaleno));
		zones.add(new Zone("ZVV 121 Zürich Flughafen", ZoneType.SINGLE_ZONE, ZoneGroup.ZVV));
		zones.add(new Zone("ZVV Zone 101 Winterhur", ZoneType.SINGLE_ZONE, ZoneGroup.ZVV));
		zones.add(new Zone("VVL Zone 10 Luzern", ZoneType.SINGLE_ZONE, ZoneGroup.VVL));
		zones.add(new Zone("Frimobil Zone 10 Fribourg", ZoneType.SINGLE_ZONE, ZoneGroup.Frimobil));
		zones.add(new Zone("Libero 100 und 101 Bern", ZoneType.DOUBLE_ZONE, ZoneGroup.Libero));
		zones.add(new Zone("Ostwind Zone 810 Schaffhausen", ZoneType.SINGLE_ZONE, ZoneGroup.Ostwind));
		zones.add(new Zone("TVZG Zone 610 Zug", ZoneType.SINGLE_ZONE, ZoneGroup.TVZG));
		zones.add(new Zone("Citybus Chur", ZoneType.SINGLE_ZONE, ZoneGroup.Chur));
		zones.add(new Zone("Ostwind 211/210 St.Gallen", ZoneType.DOUBLE_ZONE, ZoneGroup.Ostwind));
		zones.add(new Zone("Libero 300 und 301 Biel", ZoneType.DOUBLE_ZONE, ZoneGroup.Libero));
		zones.add(new Zone("Mobilis Zone 11-12 Lausanne", ZoneType.DOUBLE_ZONE, ZoneGroup.Mobilis));
		zones.add(new Zone("A-Welle 520 Olten", ZoneType.SINGLE_ZONE, ZoneGroup.AWelle));
		zones.add(new Zone("Unireso Zone 10 Geneva", ZoneType.DOUBLE_ZONE, ZoneGroup.Unireso));
		zones.add(new Zone("A-Welle 570 Baden Wettingen", ZoneType.SINGLE_ZONE, ZoneGroup.AWelle));
		zones.add(new Zone("Onde Verte zone 20 La Chaux-de-fonds Le Locle", ZoneType.SINGLE_ZONE, ZoneGroup.OndeVerte));
		zones.add(new Zone("Onde Verte Neuchatel zone 10", ZoneType.SINGLE_ZONE, ZoneGroup.OndeVerte));
		zones.add(new Zone("TNW 11", ZoneType.SINGLE_ZONE, ZoneGroup.TNW));
		zones.add(new Zone("TNW 10 Stadt Basel", ZoneType.DOUBLE_ZONE, ZoneGroup.TNW));
		zones.add(new Zone("Libero 200 und 201 Solothurn", ZoneType.DOUBLE_ZONE, ZoneGroup.Libero));
		zones.add(new Zone("BeoAbo 10 20 Thun", ZoneType.DOUBLE_ZONE, ZoneGroup.BeoAbo));

		Map<String, Geometry> geometries = new HashMap<>();

		DataStore dataStore = DataStoreFinder.getDataStore(Collections.singletonMap("url", shapefile.toURI().toURL()));
		SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
		SimpleFeatureCollection featureCollection = featureSource.getFeatures();
		SimpleFeatureIterator featureIterator = featureCollection.features();

		while (featureIterator.hasNext()) {
			SimpleFeature feature = featureIterator.next();
			String name = (String) feature.getAttribute("NAME");
			name = name.replace("Verde", "Verte").trim();
			name = name.replace("Onte", "Onde").trim();
			Geometry geometry = (Geometry) feature.getDefaultGeometry();
			geometries.put(name, geometry);
		}

		featureIterator.close();
		dataStore.dispose();

		for (Zone zone : zones) {
			if (!geometries.containsKey(zone.getName())) {
				throw new IllegalStateException("No geometry for zone " + zone.getName());
			} else {
				zone.setGeometry(geometries.get(zone.getName()));
			}
		}

		return new ZonalSystem(zones);
	}
}
