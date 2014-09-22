/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1020.minifs;

public abstract class INode {
	
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
		// TODO: Add logic for illegal characters, namely /.
		// Also make sure somewhere that we don't create files and/or directories with the same
		// name as something that already exists.
		this.name = name;
	}

}
