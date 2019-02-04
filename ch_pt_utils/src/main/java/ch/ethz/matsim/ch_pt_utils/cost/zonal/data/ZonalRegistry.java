package ch.ethz.matsim.ch_pt_utils.cost.zonal.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ZonalRegistry {
	private final Map<Long, Collection<Zone>> zonesByHafasId = new HashMap<>();
	private final Map<String, Authority> authoritiesById = new HashMap<>();
	private final Map<Authority, Collection<Zone>> zonesByAuthority = new HashMap<>();

	public ZonalRegistry(Collection<Authority> authorities, Collection<Zone> zones) {
		for (Authority authority : authorities) {
			authoritiesById.put(authority.getId(), authority);
			zonesByAuthority.put(authority, new HashSet<>());
		}

		for (Zone zone : zones) {
			zonesByAuthority.get(zone.getAuthority()).add(zone);

			for (long hafasId : zone.getHafasIds()) {
				if (!zonesByHafasId.containsKey(hafasId)) {
					zonesByHafasId.put(hafasId, new HashSet<>());
				}

				zonesByHafasId.get(hafasId).add(zone);
			}
		}
	}

	public Authority getAuthority(String authorityId) {
		return authoritiesById.get(authorityId);
	}

	public Zone getZone(Authority authority, long zoneId) {
		for (Zone zone : zonesByAuthority.get(authority)) {
			if (zone.getZoneId() == zoneId) {
				return zone;
			}
		}

		throw new IllegalStateException();
	}

	public Collection<Zone> getZones(long hafasId) {
		if (zonesByHafasId.containsKey(hafasId)) {
			return zonesByHafasId.get(hafasId);
		} else {
			return Collections.emptyList();
		}
	}

	public Collection<Zone> getZones(Authority authority) {
		return zonesByAuthority.get(authority);
	}
}
