package graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import gui.Circle;
import gui.DraggableCircle;

/** A Node (vertex) of the graph. Each Node maintains<br>
 * (1) a set of edges that exit it,
 *
 * All methods that modify these collections are protected, but all getters<br>
 * are public for use by the user. <br>
 * Additionally, convenience methods such as isConnectedTo(Node n) are provided <br>
 * for user use.
 *
 * @author MPatashnik **/
public class Node implements GraphElement {
	/** The name of this node. Set during construction */
	public final String name;

	private final Graph graph; // The graph in which this Node is contained

	protected int x;	// x coordinate of this node in independent project space
	protected int y;	// y coordinate of this node in independent project space

	private List<Edge> exits;  // Edges leaving this Node
	// When viewed as a directed graph, the first node of an edge is the source
	// and the second is the sink.

	@SuppressWarnings("unused")
	private Object userData;

	private Circle circle;	// Circle that represents this graphically

	/** Constructor: a Node named n on Board m with no edges leaving it <br>
	 * and with drawing circle c. */
	protected Node(Graph m, String n, DraggableCircle c) {
		this(m, n, c, null);
	}

	/** Constructor: a Node named n on Board m with edges in exists <br>
	 * and with drawing circle c. If c is null, a new circle at (0, 0) is created. */
	protected Node(Graph m, String n, DraggableCircle c, List<Edge> exits) {
		graph= m;
		name= n;

		if (c == null)
			circle= new DraggableCircle(this, 0, 0, Circle.DEFAULT_DIAMETER);
		else circle= c;

		if (exits != null) {
			this.exits= Collections.synchronizedList(exits);
		} else {
			this.exits= Collections.synchronizedList(new ArrayList<Edge>());
		}
	}

	/** Return the graph in which this Node exists. */
	@Override
	public Graph getGraph() {
		return graph;
	}

	/** Return the exits of this Node. */
	protected List<Edge> getTrueExits() {
		return exits;
	}

	/** Used when viewing this graph as a directed graph. <br>
	 * Return the indegree of this node. */
	public int indegree() {
		int d= 0;
		for (Edge e : exits) {
			if (e.getSink() == this) d= d + 1;
		}
		return d;
	}

	/** Used when viewing this graph as a directed graph. <br>
	 * Return a list of neighbors of this node. <br>
	 * Suppose edge e is attached to this node. <br>
	 * If this node is e's source, the other node is the sink and is returned. */
	public List<Node> directedNeighbors() {
		List<Node> nn= new ArrayList<>();
		for (Edge e : exits) {
			if (e.getFirstExit() == this) {
				nn.add(e.getSecondExit());
			}
		}
		return nn;
	}

	/** Used when viewing this graph as a directed graph. <br>
	 * Return the edges for which this node is the source. */
	public List<Edge> directedEdges() {
		List<Edge> result= new ArrayList<>();
		for (Edge e : exits) {
			if (e.getSource() == this) result.add(e);
		}
		return result;
	}

	/** Return a copy of the set of edges leaving this node. <br>
	 * This is to prevent the addition of edges to exits without <br>
	 * proper clearance. */
	public List<Edge> getExits() {
		List<Edge> newExits= new ArrayList<>();
		synchronized (exits) {
			newExits.addAll(exits);
		}
		return newExits;
	}

	/** Return a map of neighboring nodes to the lengths of the edges <br>
	 * connecting them to this Node. <br>
	 * To iterate over a HashMap, use HashMap.entrySet(). */
	protected HashMap<Node, Integer> getNeighbors() {
		HashMap<Node, Integer> neighbors= new HashMap<>();
		synchronized (exits) {
			for (Edge e : exits) {
				neighbors.put(e.getOther(this), e.length);
			}
		}
		return neighbors;
	}

	/** Return a random edge leaving this node (its exists) */
	public Edge getRandomExit() {
		return Main.randomElement(exits);
	}

	/** Add e to this Node's set of exits */
	protected void addExit(Edge e) {
		if (!exits.contains(e)) {
			exits.add(e);
		}
	}

	/** Remove e from this Node's set of exits */
	protected void removeExit(Edge e) {
		exits.remove(e);
	}

	/** Add edges in s to this Node's set of edges (its exits). */
	protected void addExits(Collection<Edge> s) {
		for (Edge e : s) {
			if (!exits.contains(e)) {
				exits.add(e);
			}
		}
	}

	/** Return the number of exits from this node. */
	public int getExitsSize() {
		return exits.size();
	}

	/** Return true iff r is connected to this Node. */
	public boolean isExit(Edge r) {
		return exits.contains(r);
	}

	/** Create a new Edge with length len and add it as an exit to <br>
	 * this Node and node. */
	protected void connectTo(Node node, int len) {
		Edge r= new Edge(graph, this, node, len);
		addExit(r);
		node.addExit(r);
	}

	/** Return false if destination.equals(this). <br>
	 * Otherwise, Return true iff one of the edges in exits leads to Node <br>
	 * destination, (this is connected to destination via a single edge). */
	public boolean isConnectedTo(Node destination) {
		if (destination.equals(this))
			return false;

		synchronized (exits) {
			for (Edge r : exits) {
				if (r.isExit(destination)) { return true; }
			}
		}

		return false;
	}

	/** Return the edge that this node shares with node other <br>
	 * (null if not connected). */
	public Edge getEdge(Node other) {
		Edge n= null;

		synchronized (exits) {
			for (Edge r : exits) {
				if (r.getOther(this).equals(other)) {
					n= r;
					break;
				}
			}
		}

		return n;
	}

	/** Return the Circle that represents this node graphically. */
	public Circle getCircle() {
		return circle;
	}

	/** Set the Circle for this Node to c. */
	public void setCircle(Circle c) {
		circle= c;
	}

	/** Update the circle graphic. <br>
	 * Also update the location of the load if this truck is carrying one.
	 *
	 * @param x - the new X location of this Truck in the GUI
	 * @param y - the new Y location of this Truck in the GUI */
	@Override
	public void updateGUILocation(int x, int y) {
		circle.setX1(x);
		circle.setY1(y);
		circle.repaint();
		for (Edge e : exits) {
			e.updateGUILocation(x, y);
		}
	}

	/** Return true iff n is a Node and is equal to this one. <br>
	 * Two Nodes are equal if they have the same name <br>
	 * - guaranteed to be unique within the context of a single game */
	@Override
	public boolean equals(Object n) {
		if (n == null)
			return false;
		if (!(n instanceof Node))
			return false;
		return name.equals(((Node) n).name);
	}

	/** Return the hashCode of this node. Its hashCode is equal to the <br>
	 * hashCode of its name. <br>
	 * This is guaranteed to be unique within the context of a single game. */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/** Return the color of this Node. */
	@Override
	public Color getColor() {
		return circle.getColor();
	}

	/** Return false - the color of Nodes is not significant */
	@Override
	public boolean isColorSignificant() {
		return false;
	}

	/** Return the name of this Node. */
	@Override
	public String toString() {
		return name;
	}

	/** Return the string that is mapped when this Node is drawn. */
	@Override
	public String getMappedName() {
		return name;
	}

	/** Return the x location that this Node's string is mapped to relative <br>
	 * to its top right coordinate. */
	@Override
	public int getRelativeX() {
		return -Circle.DEFAULT_DIAMETER / 2;
	}

	/** Return the y location that this Node's string is mapped to relative <br>
	 * to its top right coordinate. */
	@Override
	public int getRelativeY() {
		return 0;
	}

	/** Return just this' name for the JSONString - relies on JSONs of <br>
	 * Edges and parcels to take care of themselves. */
	@Override
	public String toJSONString() {
		return "{\n" + Main.addQuotes(GraphElement.NAME_TOKEN) + ":" +
			Main.addQuotes(name) + ",\n" +
			Main.addQuotes(X_TOKEN) + ":" + x + ",\n" +
			Main.addQuotes(Y_TOKEN) + ":" + y + "\n}";
	}
}
