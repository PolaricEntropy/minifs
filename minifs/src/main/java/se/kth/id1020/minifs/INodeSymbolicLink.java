package se.kth.id1020.minifs;

/**
 * INodeSymbolicLink represents a symbolic link in the file system.
 * @author Björn Ehrby
 */
public class INodeSymbolicLink extends INode {
	
	private String m_target;
	
	/**
	 * Creates a new symbolic link object.
	 * @param name The name of the symbolic link.
	 * @param parent The parent to add the INodeDirectory this symbolic link to. 
	 * @param target The path this symbolic link points to, can either be a relative or absolute path.
	 */
	public INodeSymbolicLink(String name, INodeDirectory parent, String target)
	{
		super(name, parent);
		m_target = target;
	}

	/**
	 * Get the path to the target this link is pointing to.
	 * @return Returns an absolute or relative path to the target, depending on what's stored in the file.
	 */
	public String getTarget()
	{
		return m_target;
	}
	
}
