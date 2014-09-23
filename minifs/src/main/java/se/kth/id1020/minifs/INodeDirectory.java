/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1020.minifs;

import java.util.Hashtable;

public class INodeDirectory extends INode {

	//Use Hashtable since it enforces the use of unique keys and optimizes searching.
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
	
	public void createDirectory(String name) throws IllegalArgumentException
	{
		//Create a new directory.
		INodeDirectory newdir = new INodeDirectory(name, this);
		
		//Make sure we don't have a child with this name before.
		if (children.containsKey(name) == false)
			children.put(name, newdir);
		else
			throw new IllegalArgumentException("A directory or file named " + name + " does already exist.");	
	}
	
	public void createFile(String name)
	{
		//Create a new directory.
		INodeFile newfile = new INodeFile(name, this);
		
		//Make sure we don't have a child with this name before.
		if (children.containsKey(name) == false)
			children.put(name, newfile);
		else
			throw new IllegalArgumentException("A directory or file named " + name + " does already exist.");	
		
	}
	
}
