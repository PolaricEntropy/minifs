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
		this.m_name = name;
		this.m_accessTime = System.currentTimeMillis();
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
		//Other functions calling this function should sanitize user supplied input,
		//but just in case check for illegal characters.
		if (name.contains(FileSystem.g_pathDelimiter) == false)
			this.m_name = name;
		else
			throw new IllegalArgumentException("The filename or directory name syntax is incorrect.");
	}
	
	public INodeDirectory getParent()
	{
		return m_parent;
	}
}
