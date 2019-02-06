package ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data;

public class Authority {
	private final String authorityId;

	public Authority(String authorityId) {
		this.authorityId = authorityId;
	}

	public String getId() {
		return authorityId;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Authority) {
			return authorityId.equals(((Authority) other).authorityId);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return authorityId.hashCode();
	}

	@Override
	public String toString() {
		return "Authority(" + authorityId + ")";
	}
}
