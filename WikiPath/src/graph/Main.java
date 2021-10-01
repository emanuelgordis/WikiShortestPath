package graph;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.json.JSONObject;

import gui.GUI;
import gui.TextIO;

/** Game starting methods. Also serves as a util holder */
public class Main {

	/** Student directory */
	public static final String studentDirectory= "";

	/** Map being used ---null if a random map */
	public static File file= null;

	/** The graph */
	public static Graph graph= null;

	/** The GUI */
	public static GUI gui= null;

	/** If args is null or empty, start with a random map.<br>
	 * If it is not empty, read the map with name args[0] and print its name.<br>
	 * The board name has to be in directory data/Maps. */
	public static void main(String[] args) throws IllegalArgumentException {
		if (args == null || args.length == 0) {
			Graph b= Graph.randomBoard(Math.abs(new Random().nextLong()));
			gui= new GUI(b);
			b.setGUI(gui);
			return;
		}
		String name= "data/Maps/" + args[0];
		System.out.println("Using graph " + name);
		Graph g= getGraph(name);
		gui= new GUI(g);
		g.setGUI(gui);
	}

	/** Store in field graph a random board from seed s and create the GUI using it. */
	public void randomBoard(long s) {
		file= null;
		graph= Graph.randomBoard(s);
		gui= new GUI(graph);
	}

	/** Return s with quotes added around it. <br>
	 * Used in JSON creation methods throughout project. */
	public static String addQuotes(String s) {
		return "\"" + s + "\"";
	}

	/** Return a random element of elms (null if elms is empty). <br>
	 * Synchronizes on {@code elms} to prevent concurrent modification. */
	public static <T> T randomElement(Collection<T> elms) {
		T val= null;
		synchronized (elms) {
			if (elms.isEmpty())
				return null;
			Iterator<T> it= elms.iterator();
			for (int i= 0; i < (int) (Math.random() * elms.size()) + 1; i++ ) {
				val= it.next();
			}
		}
		return val;
	}

	/** Return a graph for file named s in directory data. <br>
	 * Precondition: s must be the path to the file relative to this project. */
	public static Graph getGraph(String s) {
		try {
			return Graph.getJsonGraph(new JSONObject(TextIO.read(new File(s))));
		} catch (IOException e) {
			throw new RuntimeException("IO Exception reading in graph " + s);
		}
	}

}
