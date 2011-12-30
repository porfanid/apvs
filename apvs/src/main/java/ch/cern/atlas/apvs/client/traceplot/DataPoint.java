package ch.cern.atlas.apvs.client.traceplot;

public class DataPoint<X, Y> {

	private X x;
	private Y y;
	
	public DataPoint(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	public X getX() {
		return x;
	}
	
	public Y getY() {
		return y;
	}
	
	@Override
	public int hashCode() {
		return x.hashCode() + y.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof DataPoint<?, ?>)) {
			DataPoint<?, ?> obj = (DataPoint<?, ?>)o;
			return x.equals(obj.getX()) && y.equals(obj.getY());
		}
		return super.equals(o);
	}
	
	@Override
	public String toString() {
		return "P("+x+", "+y+")";
	}
}
