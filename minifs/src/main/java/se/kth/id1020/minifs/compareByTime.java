package se.kth.id1020.minifs;

import java.util.Comparator;

/**
 * Comparator used to sort INodes by time.
 * @author Björn Ehrby
 *
 */
public class compareByTime implements Comparator<INode> {

	public int compare(INode o1, INode o2) {
		if (o1.getAccessTime() < o2.getAccessTime())
			return -1;
		else if(o1.getAccessTime() > o2.getAccessTime())
			return 1;
		else		
			return 0;
		
	}

}
