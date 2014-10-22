/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1020.minifs;

/**
 * Base class for INodes. 
 * @author Björn Ehrby
 *
 */
public abstract class INode{
	
	private String m_name;
	private long m_accessTime;
	private INodeDirectory m_parent;

	public INode(String name, INodeDirectory parent)
	{
		//This is the root folder, special naming rules for that.
		if (parent == null)
			this.m_name = name;
		else
			setName(name);
		
		setAccessTime(System.currentTimeMillis());
		this.m_parent = parent;
	}

	public long getAccessTime()
	{
		return m_accessTime;
	}

	public void setAccessTime(long accessTime)
	{
		this.m_accessTime = accessTime;
	}

	public String getName()
	{
		return m_name;
	}

	public void setName(String name)
	{
		//Check for invalid characters.
		if (!(name.contains(FileSystem.g_pathDelimiter) || name.equals(".") || name.equals("..")))
			this.m_name = name;
		else
			throw new IllegalArgumentException("The name syntax is incorrect.");
	}
	
	public INodeDirectory getParent()
	{
		return m_parent;
	}
	
	/**
	 * Returns the path of the current INode.
	 * @return Returns the absolute path to the INode.
	 */
	public String getPath()
	{
		StringBuilder sb = new StringBuilder();
		INode node = this;
		
		
		//If we start from the root we'll just print the delimiter. 
		if (node.getParent() == null)
			sb.append(FileSystem.g_pathDelimiter);
		else
		{
			//If we hit the root just stop, no need to print the root, we've already printed the delimiter for the first directory.
			while (node.getParent() != null)
			{	
				//Since we are going backwards we need to add each name at the start of the string.
				sb.insert(0, String.format("%s%s", FileSystem.g_pathDelimiter, node.getName()));
				node = node.getParent();
			}
		}
		return sb.toString();
	}
}
