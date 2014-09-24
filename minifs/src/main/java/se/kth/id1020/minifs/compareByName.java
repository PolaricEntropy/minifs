package se.kth.id1020.minifs;

import java.util.Comparator;

public class compareByName implements Comparator<INode>{

	public int compare(INode o1, INode o2) {
		return o1.getName().compareTo(o2.getName());
	}
}
