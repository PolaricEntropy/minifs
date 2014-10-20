package se.kth.id1020.minifs;

public class INodeSymbolicLink extends INode {
	
	private INode m_target;
	
	public INodeSymbolicLink(String name, INodeDirectory parent, INode target) {
		super(name, parent);
		m_target = target;
	}

	public INode getTarget()
	{
		return m_target;
	}
	
}
