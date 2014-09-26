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
	
	private String name;
	private long accessTime;
	private INodeDirectory parent;

	public INode(String name, INodeDirectory parent)
	{
		this.name = name;
		this.accessTime = System.currentTimeMillis();
		this.parent = parent;
	}

	public long getAccessTime()
	{
		return accessTime;
	}

	public void setAccessTime(long accessTime)
	{
		this.accessTime = accessTime;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		//Other functions calling this function should sanitize user supplied input, but just in case,
		//check for illegal characters.
		if (name.contains(FileSystem.lineSeparator) == false)
			this.name = name;
		else
			throw new IllegalArgumentException("The filename or directory name syntax is incorrect.");
	}
	
	/**
	 * 
	 * @return
	 */
	public INodeDirectory getParent()
	{
		return parent;
	}
}
