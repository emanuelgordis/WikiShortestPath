package graph;

import java.util.Objects;

/** An instance is a point (x, y) */
public class DPoint {
	private double x;
	private double y;

	/** Constructor: a point (0, 0). */
	public DPoint() {
		x= 0;
		y= 0;
	}

	/** Constructor: a point (x, y). */
	public DPoint(double x, double y) {
		this.x= x;
		this.y= y;
	}

	/** Constructor: a point with same values as v. */
	public DPoint(DPoint v) {
		x= v.x;
		y= v.y;
	}

	/** Return this point's x component. */
	public double getX() {
		return x;
	}

	/** Return this point's y component. */
	public double getY() {
		return y;
	}

	/** Add v's components to this point and return this DPoint */
	public DPoint addVector(DPoint v) {
		x+= v.x;
		y+= v.y;
		return this;
	}

	/** Multiply both of this point's components by s and return this DPoint. */
	public DPoint mult(double s) {
		x*= s;
		y*= s;
		return this;
	}

	/** Invert both of this' components - change this point to <1/x, 1/y> <br>
	 * -- and returns this DPoint.<br>
	 * Precondition: the x and y components are not 0. */
	public DPoint invert() {
		x= 1 / x;
		y= 1 / y;
		return this;
	}

	/** Make this vector have length 1, with the same direction, <br>
	 * and Return this DPoint.<br>
	 * Precondition: this point is not (0, 0). */
	public DPoint unit() {
		double l= length();
		x= x / l;
		y= y / l;
		return this;
	}

	/** Return the dot product of a and b/ */
	public static double dot(DPoint a, DPoint b) {
		return a.x * b.x + a.y * b.y;
	}

	/** Return the dot product of this DPoint and b. */
	public double dot(DPoint b) {
		return dot(this, b);
	}

	/** Return the cross product of a and b.<br>
	 * Because vectors are 2-d, the return is a vector of the form <0, 0, z><br>
	 * So a 1-D vector. */
	public static double cross(DPoint a, DPoint b) {
		return a.x * b.y - b.x * a.y;
	}

	/** Return the cross product of this and b. */
	public double cross(DPoint b) {
		return cross(this, b);
	}

	/** Return the length of a. */
	public static double length(DPoint a) {
		return a.length();
	}

	/** Return the length of this vector. */
	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	/** Return the distance between a and b. */
	public static double distance(DPoint a, DPoint b) {
		double xd= a.x - b.x;
		double yd= a.y - b.y;
		return Math.sqrt(xd * xd + yd * yd);
	}

	/** Return the distance between this point and b. */
	public double distance(DPoint b) {
		return distance(this, b);
	}

	/** Return a new point from this to b. */
	public DPoint to(DPoint b) {
		return new DPoint(b.x - x, b.y - y);
	}

	/** Return the cosine of the angle between a and b */
	public static double cos(DPoint a, DPoint b) {
		return dot(a, b) / (a.length() * b.length());
	}

	/** Return the cosine of the angle between this and b. */
	public double cos(DPoint b) {
		return cos(this, b);
	}

	/** Return the angle between a and b, in radians. Return is in the range 0.. PI */
	public static double radAngle(DPoint a, DPoint b) {
		return Math.acos(cos(a, b));
	}

	/** Return the angle between this and b, in radians. (in the range 0 .. PI) */
	public double radAngle(DPoint b) {
		return radAngle(this, b);
	}

	/** Tolerance to consider two point components equal. */
	public static final double TOLERANCE= 0.000001;

	/** Hash this point based on its components, using function Objects.hash.<br>
	 * Round both components to the nearest TOLERANCE */
	@Override
	public int hashCode() {
		return Objects.hash(Math.round(x / TOLERANCE) * TOLERANCE,
			Math.round(y / TOLERANCE) * TOLERANCE);
	}

	/** Return true iff ob is a DPoint and is equal to this one.<br>
	 * Two points are equal if their components are equal<br>
	 * - thus checks if they have the same hashcodes, <br>
	 * which are built off of their components */
	@Override
	public boolean equals(Object ob) {
		if (!(ob instanceof DPoint))
			return false;
		return hashCode() == ob.hashCode();
	}

	/** Return a string representation of this vector: <x, y> . */
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
