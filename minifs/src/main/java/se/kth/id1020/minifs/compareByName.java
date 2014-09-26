package se.kth.id1020.minifs;

import java.util.Comparator;

/**
 * Comparator used to sort INodes by name.
 * @author Björn Ehrby
 *
 */
public class compareByName implements Comparator<INode>{

	public int compare(INode o1, INode o2) {
		return o1.getName().compareToIgnoreCase(o2.getName());
	}
}
