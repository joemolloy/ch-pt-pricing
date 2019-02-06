package ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Zone implements Comparable<Zone> {
	private final long zoneId;
	private final Authority authority;
	private final Collection<Long> hafasIds;

	public Zone(Authority authority, long zoneId, Collection<Long> hafasIds) {
		this.zoneId = zoneId;
		this.authority = authority;
		this.hafasIds = hafasIds;
	}

	public Authority getAuthority() {
		return authority;
	}

	public long getZoneId() {
		return zoneId;
	}

	public Collection<Long> getHafasIds() {
		return hafasIds;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Zone) {
			Zone otherZone = (Zone) other;
			return otherZone.zoneId == zoneId && otherZone.authority.equals(authority);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return (int) zoneId + authority.hashCode() * 33;
	}

	@Override
	public String toString() {
		return String.format("Zone(%s, %d)", authority.toString(), zoneId);
	}

	@Override
	public int compareTo(Zone o) {
		return Long.compare(zoneId, o.zoneId);
	}

	static public class Builder {
		private final Authority authority;
		private final long zoneId;
		private final Set<Long> hafasIds = new HashSet<>();

		public Builder(Authority authority, long zoneId) {
			this.authority = authority;
			this.zoneId = zoneId;
		}

		public void addHafasId(long hafasId) {
			hafasIds.add(hafasId);
		}

		public Zone build() {
			return new Zone(authority, zoneId, hafasIds);
		}
	}
}
