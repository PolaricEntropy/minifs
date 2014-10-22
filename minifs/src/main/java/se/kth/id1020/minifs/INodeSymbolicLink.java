package se.kth.id1020.minifs;

public class INodeSymbolicLink extends INode {
	
	private String m_target;
	
	public INodeSymbolicLink(String name, INodeDirectory parent, String target) {
		super(name, parent);
		m_target = target;
	}

	public String getTarget()
	{
		return m_target;
	}
	
}
