/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1020.minifs;

import java.util.Hashtable;

public class INodeDirectory extends INode {

	//Use Hashtable since it enforces the use of unique keys.
	private Hashtable<String, INode> children;
	
	public INodeDirectory(String name, INodeDirectory parent)
	{
		super(name, parent);
		children = new Hashtable<String, INode>();
    }

	public Hashtable<String, INode> getChildren()
	{
		return children;
	}
	
	public void createDirectory(String name)
	{
		//Create a new directory.
		INodeDirectory newdir = new INodeDirectory(name, this);
		
		//Make sure we don't have a child with this name before.
		if (children.containsValue(name) == false)
			children.put(name, newdir);
		
		//TODO: throw an exception if we are trying to create a new directory with a name that exists.	
	}
	
	public void createFile(String name)
	{
		
	}
	
	
	
}
