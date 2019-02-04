package ch.ethz.matsim.ch_pt_utils.cost.zonal.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ZonalReader {
	public Collection<Authority> readAuthorities(File path) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		String line = null;
		List<String> header = null;

		Map<String, Authority> authorities = new HashMap<>();

		while ((line = reader.readLine()) != null) {
			List<String> row = Arrays.asList(line.split(";"));

			if (header == null) {
				header = row;
			} else {
				String authorityId = row.get(header.indexOf("authority_id"));

				if (!authorities.containsKey(authorityId)) {
					Authority authority = new Authority(authorityId);
					authorities.put(authorityId, authority);
				}
			}
		}

		reader.close();

		return authorities.values();
	}

	public Collection<Zone> readZones(File path, Collection<Authority> authorities) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		String line = null;
		List<String> header = null;

		Map<String, Authority> authoritiesMap = new HashMap<>();

		for (Authority authority : authorities) {
			authoritiesMap.put(authority.getId(), authority);
		}

		Map<Authority, Map<Long, Zone.Builder>> builders = new HashMap<>();

		while ((line = reader.readLine()) != null) {
			List<String> row = Arrays.asList(line.split(";"));

			if (header == null) {
				header = row;
			} else {
				String authorityId = row.get(header.indexOf("authority_id"));
				long zoneId = Long.parseLong(row.get(header.indexOf("zone_id")));
				long hafasId = Long.parseLong(row.get(header.indexOf("hafas_id")));

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

		reader.close();

		List<Zone> zones = new LinkedList<>();

		for (Map<Long, Zone.Builder> authorityBuilders : builders.values()) {
			for (Zone.Builder builder : authorityBuilders.values()) {
				zones.add(builder.build());
			}
		}

		return zones;
	}
}
