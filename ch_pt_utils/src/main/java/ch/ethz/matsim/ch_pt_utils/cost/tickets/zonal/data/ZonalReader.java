package ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.geojson.Feature;
import org.geojson.FeatureCollection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ZonalReader {
	public Collection<Authority> readAuthorities(File path) throws IOException {
		FeatureCollection featureCollection = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(path, FeatureCollection.class);
		Map<String, Authority> authorities = new HashMap<>();

		for (Feature feature : featureCollection.getFeatures()) {
			String authorityId = feature.getProperty("station_authority");

			if (authorityId != null) {
				authorities.put(authorityId, new Authority(authorityId));
			}
		}

		return authorities.values();
	}

	public Collection<Zone> readZones(File path, Collection<Authority> authorities) throws IOException {
		Map<String, Authority> authoritiesMap = new HashMap<>();

		for (Authority authority : authorities) {
			authoritiesMap.put(authority.getId(), authority);
		}

		Map<Authority, Map<Long, Zone.Builder>> builders = new HashMap<>();

		FeatureCollection featureCollection = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.readValue(path, FeatureCollection.class);

		for (Feature feature : featureCollection.getFeatures()) {
			String authorityId = feature.getProperty("station_authority");
			Double rawZoneId = feature.getProperty("station_zone");
			Integer hafasId = feature.getProperty("hafas_id");

			if (authorityId != null && rawZoneId != null && hafasId != null) {
				long zoneId = (long) (double) rawZoneId;
				Authority authority = authoritiesMap.get(authorityId);

				if (!builders.containsKey(authority)) {
					builders.put(authority, new HashMap<>());
				}

				Map<Long, Zone.Builder> authorityBuilders = builders.get(authority);

				if (!authorityBuilders.containsKey(zoneId)) {
					authorityBuilders.put(zoneId, new Zone.Builder(authority, zoneId));
				}

				Zone.Builder builder = authorityBuilders.get(zoneId);
				builder.addHafasId(hafasId);
			}
		}

		List<Zone> zones = new LinkedList<>();

		for (Map<Long, Zone.Builder> authorityBuilders : builders.values()) {
			for (Zone.Builder builder : authorityBuilders.values()) {
				zones.add(builder.build());
			}
		}

		return zones;
	}
}
