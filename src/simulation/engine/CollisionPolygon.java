package simulation.engine;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

/**
 * A polygon that can collide with other shapes.
 * 
 * @author Jacob Taylor
 * 
 */
public final class CollisionPolygon extends CollisionShape {
	// original angles of the vertices
	private double[] origAngles;
	// original distances of the vertices from the center
	private double[] origMagnitudes;
	// the current positions of the vertices
	private Vector[] vertices;
	private Vector center;
	private double momentOfInertia;
	private double area;
	private double rotation;

	/**
	 * Create a polygon from the given vertices
	 * 
	 * @param vertices
	 *            the vertices
	 */
	public CollisionPolygon(Vector[] vertices) {
		if (vertices.length < 3) {
			throw new IllegalArgumentException(
					"CollisionPolygon must have at least 3 vertices.");
		}
		this.vertices = vertices.clone();

		setValues();
	}

	/**
	 * Creates a CollisionPolygon with the same points as the specified polygon
	 * 
	 * @param poly
	 *            the polygon to get the points from
	 */
	public CollisionPolygon(Polygon poly) {
		ArrayList<Vector> points = new ArrayList<Vector>();
		PathIterator iter = poly.getPathIterator(null);
		double[] coords = new double[6];
		while (!iter.isDone()) {
			iter.currentSegment(coords);
			points.add(new Vector(coords[0], coords[1]));
			iter.next();
		}
		vertices = points.toArray(new Vector[0]);
		setValues();
	}
	
	/**
	 * Creates a new polygon with the given set of points.
	 * 
	 * @see Polygon
	 * @param x - an array containing the x-coordinates of the vertices.
	 * @param y - an array containing the y-coordinates of the vertices.
	 * @param npoints - the number of vertices.
	 */
	public CollisionPolygon(int[] x, int y[], int npoints) {
		this(new Polygon(x, y, npoints));
	}

	private CollisionPolygon() {

	}

	/**
	 * Get the number of vertices.
	 * 
	 * @return how many vertices there are
	 */
	public int numVertices() {
		return vertices.length;
	}

	/**
	 * Get the vertex at a certain index
	 * 
	 * @param i
	 *            the vertex's index
	 * @return the vertex
	 */
	public Vector getVertex(int i) {
		return vertices[i];
	}

	@Override
	public double area() {
		return area;
	}

	@Override
	public Vector center() {
		return center;
	}

	@Override
	public double rotation() {
		return rotation;
	}

	/**
	 * Calculate some properties of the polygon.
	 */
	private void setValues() {
		// first, calculate area and center by adding stuff
		double atotal = 0, xtotal = 0, ytotal = 0;
		Vector v1, v2;
		v1 = vertices[vertices.length - 1];
		for (int i = 0; i < vertices.length; ++i, v1 = v2) {
			v2 = vertices[i];
			double v1x = v1.getX(), v1y = v1.getY();
			double v2x = v2.getX(), v2y = v2.getY();
			double temp = v1x * v2y - v2x * v1y;
			atotal += temp;
			xtotal += (v1x + v2x) * temp;
			ytotal += (v1y + v2y) * temp;

		}
		area = .5 * Math.abs(atotal);
		double coeff = 1 / (3 * atotal);
		center = new Vector(coeff * xtotal, coeff * ytotal);
		// next, calculate moment of inertia
		double mtotal = 0;
		v1 = vertices[vertices.length - 1];
		for (int i = 0; i < vertices.length; ++i, v1 = v2) {
			v2 = vertices[i];
			Vector cv1 = v1.subtract(center);
			Vector cv2 = v2.subtract(center);
			// formula from http://www.gvu.gatech.edu/~jarek/demos/inertia/
			mtotal += (cv2.dotProduct(cv1.quarterCounter()))
					* (cv1.dotProduct(cv1) + cv1.dotProduct(cv2) + cv2
							.dotProduct(cv2));
		}
		momentOfInertia = Math.abs(mtotal) / 12;
		// finally, get angles and magnitudes
		origAngles = new double[vertices.length];
		origMagnitudes = new double[vertices.length];
		for (int i = 0; i < vertices.length; ++i) {
			// vertex relative to center
			Vector relV = vertices[i].subtract(center);
			origAngles[i] = relV.angle();
			origMagnitudes[i] = relV.magnitude();
		}
	}

	@Override
	public double momentOfInertia() {
		return momentOfInertia;
	}

	@Override
	public boolean contains(Vector point) {
		/*
		 * To calculate this, I draw a ray beginning at point and going to the
		 * right. I count the number of edges that intersects this ray; if it is
		 * odd, point in polygon; if it is even, point not in polygon.
		 */
		Vector v1, v2; // the points that make up the current edge
		v1 = vertices[vertices.length - 1];
		int intersections = 0;
		for (int i = 0; i < vertices.length; ++i, v1 = v2) {
			v2 = vertices[i];
			/*
			 * boolean v1LowerPoint = v1.y() <= point.y(); boolean v2LowerPoint
			 * = v2.y() <= point.y(); //one vertex must be higher and one must
			 * be lower than point if(v1LowerPoint ^ v2LowerPoint) {
			 */
			if (v1.getY() <= point.getY() && v2.getY() >= point.getY()
					|| v2.getY() <= point.getY() && v1.getY() >= point.getY()) {
				double dx = v2.getX() - v1.getX();
				if (dx == 0) {
					// line is vertical, so it must be to the right
					if (v1.getX() > point.getX())
						++intersections;
				} else {
					double dy = v2.getY() - v1.getY();
					// calculate slope and y-intercept of line that overlaps
					// edge
					double slope = dy / dx;
					// y = m*x + b; b = y - m*x
					double yInt = v1.getY() - slope * v1.getX();
					// y = m*x + b; m*x = y - b; x = (y - b)/m
					double xInt = (point.getY() - yInt) / slope;
					// x intercept is to the right of the point
					if (xInt > point.getX())
						++intersections;
				}
			}
		}
		return intersections % 2 == 1;
	}

	/**
	 * Add vectors going from the point to an edge or vertex to a
	 * TranslationMinimizer.
	 * 
	 * @param point
	 *            what to find translations from
	 * @param mini
	 *            what to add translations to
	 */
	private void addTranslations(Vector point, TranslationMinimizer mini) {
		// add translations from point to vertices
		for (Vector selfPoint : vertices) {
			mini.add(new CollisionResult(selfPoint, point.subtract(selfPoint)));
		}
		// add translations from point to edges
		Vector v1, v2;
		v1 = vertices[vertices.length - 1];
		for (int i = 0; i < vertices.length; ++i, v1 = v2) {
			v2 = vertices[i];
			Vector trans = perpendicularTranslation(point, v1, v2, true);
			if (trans != null)
				mini.add(new CollisionResult(point, trans));
		}
	}

	/**
	 * Add translations to separate two polygons to a TranslationMaximizer.
	 * 
	 * @param pointPoly
	 *            the polygon to remove from edgePoly
	 * @param edgePoly
	 *            the polygon to remove pointPoly from
	 * @param maxi
	 *            what to add translations to
	 * @param invert
	 *            whether or not to invert translations before adding
	 */
	private static void getTranslation(CollisionPolygon pointPoly,
			CollisionPolygon edgePoly, TranslationMaximizer maxi, boolean invert) {
		for (Vector point : pointPoly.vertices) {
			if (edgePoly.contains(point)) {
				// find the shortest way to get the point out of edgePoly
				TranslationMinimizer mini = new TranslationMinimizer();
				edgePoly.addTranslations(point, mini);
				CollisionResult best = mini.currentBest();
				if (best != null) {
					if (invert)
						best = best.invert();
					maxi.add(best);
				}
			}
		}
	}

	/**
	 * Gets a translation that will put a point between two points and that is
	 * perpendicular to a line between the two points.
	 * 
	 * @param point
	 *            the point to translate
	 * @param v1
	 *            first vertex
	 * @param v2
	 *            second vertex
	 * @param filter
	 *            whether or not to reject translations that won't be between
	 *            the points
	 * @return
	 */
	private static Vector perpendicularTranslation(Vector point, Vector v1,
			Vector v2, boolean filter) {
		// make things relative to v1
		Vector relV2 = v2.subtract(v1);
		Vector relPoint = point.subtract(v1);
		Vector relV2unit = relV2.unit();
		// project to get the intersection's positive or negative distance from
		// v1
		// v2*
		// |
		// i*-*p
		// |/
		// v1*
		// i = intersection, p = point
		// projected = how far i is along the v1 -> v2 line
		double projected = relV2unit.dotProduct(relPoint);
		if (filter && (projected < 0 || projected > relV2.magnitude())) {
			// falls outside the line segment, but on the infinite line
			return null;
		}
		// the intersection point relative to v1
		Vector relIntersection = relV2unit.multiply(projected);
		// (i - v1) - (p - v1) = i - p
		return relIntersection.subtract(relPoint);

	}

	@Override
	public CollisionResult collidePolygon(CollisionPolygon other) {
		// try to maximize the movement
		TranslationMaximizer maxi = new TranslationMaximizer();
		getTranslation(this, other, maxi, false);
		// invert when moving the other out of this
		getTranslation(other, this, maxi, true);
		return maxi.currentBest();
	}

	@Override
	public CollisionResult collideCircle(CollisionCircle other) {
		// maximize the translation
		TranslationMaximizer maxi = new TranslationMaximizer();
		// if this contains the circle's center, more movement is required to
		// get the circle out.
		boolean containsCenter = contains(other.center());
		// first, try to move the circle out of a point
		for (Vector vert : vertices) {
			Vector centerToVert = vert.subtract(other.center());
			double dist = centerToVert.magnitude();
			double mag;
			if (containsCenter) {
				mag = other.radius() + dist;
			} else {
				mag = other.radius() - dist;
			}
			if (mag > 0) {
				// translation is from circle to vertex because the polygon is
				// moving
				Vector trans = centerToVert.withMagnitude(mag);
				// contact point is the vertex
				maxi.add(new CollisionResult(vert, trans));
			}
		}
		// next, try to move the circle out through an edge
		Vector v1, v2;
		v1 = vertices[vertices.length - 1];
		for (int i = 0; i < vertices.length; ++i, v1 = v2) {
			v2 = vertices[i];
			// vector from the circle's center to the edge
			Vector centerToEdge = perpendicularTranslation(other.center(), v1,
					v2, true);
			if (centerToEdge != null) {
				double mag;
				if (containsCenter) {
					mag = other.radius() + centerToEdge.magnitude();
				} else {
					mag = other.radius() - centerToEdge.magnitude();
				}
				if (mag > 0) {
					// translation is from center to edge because polygon is
					// moving
					Vector trans = centerToEdge.withMagnitude(mag);
					// contact point is the edge point
					Vector contact = other.center().add(centerToEdge);
					maxi.add(new CollisionResult(contact, trans));
				}
			}
		}
		return maxi.currentBest();
	}

	/**
	 * Set the vertices to values based on center and rotation.
	 */
	private void setVertices() {
		for (int i = 0; i < vertices.length; ++i) {
			vertices[i] = center.add(Vector.fromAngle(origAngles[i] + rotation,
					origMagnitudes[i]));
		}
	}

	@Override
	public void rotate(double angle) {
		rotation += angle;
		setVertices();
	}

	@Override
	public void move(Vector movement) {
		// add movement to each vertex and the center
		for (int i = 0; i < vertices.length; ++i) {
			vertices[i] = vertices[i].add(movement);
		}
		center = center.add(movement);
	}

	@Override
	public void moveRotating(double angle, Vector movement) {
		center = center.add(movement);
		rotation += angle;
		setVertices();
	}

	@Override
	public CollisionShape clone() {
		CollisionPolygon clone = new CollisionPolygon();
		clone.vertices = vertices.clone();
		clone.area = area;
		clone.center = center;
		clone.momentOfInertia = momentOfInertia;
		return clone;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Vector v : vertices) {
			sb.append(' ');
			sb.append(v);
		}
		sb.append(" ]");
		return sb.toString();
	}

	@Override
	public void fill(Graphics g) {
		// get xs and ys
		int[] xs = new int[vertices.length];
		int[] ys = new int[vertices.length];
		for (int i = 0; i < vertices.length; ++i) {
			xs[i] = (int) vertices[i].getX();
			ys[i] = (int) vertices[i].getY();
		}
		g.fillPolygon(xs, ys, vertices.length);
	}

	@Override
	public Shape toShape() {
		Polygon result = new Polygon();
		for (Vector v : vertices) {
			result.addPoint((int) v.getX(), (int) v.getY());
		}
		return result;
	}
}
