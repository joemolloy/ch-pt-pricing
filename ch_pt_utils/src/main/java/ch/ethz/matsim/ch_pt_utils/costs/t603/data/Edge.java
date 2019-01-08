package ch.ethz.matsim.ch_pt_utils.costs.t603.data;

public class Edge<T> {
	private final T left;
	private final T right;

	public Edge(T left, T right) {
		this.left = left;
		this.right = right;
	}

	public T getLeft() {
		return left;
	}

	public T getRight() {
		return right;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Edge) {
			Edge<?> otherConnection = (Edge<?>) other;
			return left.equals(otherConnection.left) && right.equals(otherConnection.right);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return left.hashCode() + right.hashCode();
	}

	@Override
	public String toString() {
		return "Edge(" + left + " -> " + right + ")";
	}

	static public <U> Edge<U> of(U left, U right) {
		return new Edge<U>(left, right);
	}
}
