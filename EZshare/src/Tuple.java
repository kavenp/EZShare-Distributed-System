
public class Tuple<X,Y,Z> {
    public final X x;
    public final Y y;
    public final Z z;
    	
    public Tuple(X x, Y y, Z z) {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		if (z == null) {
			if (other.z != null)
				return false;
		} else if (!z.equals(other.z))
			return false;
		return true;
	}

}