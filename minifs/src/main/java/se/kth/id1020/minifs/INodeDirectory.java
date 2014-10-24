/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1020.minifs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * INodeDirectory represents a directory entity in the file system.
 * @author Björn Ehrby
 *
 */
public class INodeDirectory extends INode {

	private HashMap<String, INode> m_children;
	private boolean m_WriteProtected;
	
	public INodeDirectory(String name, INodeDirectory parent)
	{
		super(name, parent);
		m_children = new HashMap<String, INode>();
	}
	
	/**
	 * Gets all of the INode objects that is stored in this directory.
	 * @return A list of INodes.
	 */
	public List<INode> getChildren()
	{
		List<INode> output = new ArrayList<INode>();
		output.addAll(m_children.values());
		return output;
	}
	
	/**
	 * Gets the INode with the specified name.
	 * @param name The name of the INode to return.
	 * @return Returns the INode matching the specified name, if no matches it will return null.
	 */
	public INode getChild(String name)
	{
		return m_children.get(name);	
	}
	
	/**
	 * Get the write protect status of the directory.
	 * @return True if directory is write protected, false if not.
	 */
	public boolean getWriteProtect()
	{
		return m_WriteProtected;
	}
	
	/**
	 * Sets the write protect status of the directory.
	 * @param value True to write protect and false to unprotect. 
	 */
	public void setWriteProtect(boolean value)
	{
		m_WriteProtected = value;
	}
	
	/**
	 * Creates a new directory with the specified name.
	 * @param name The name of the new directory, must be unique in the current directory.
	 */
	public void createDirectory(String name)
	{
		addChild(name, new INodeDirectory(name, this));
	}
	
	/**
	 * Creates a new file with the specified name.
	 * @param name The name of the new file, must be unique in the current directory.
	 */
	public void createFile(String name)
	{
		addChild(name, new INodeFile(name, this));	
	}
	
	/**
	 * Creates a new symbolic link with the specified name.
	 * @param name The name of the new symbolic link, must be unique in the current directory.
	 * @param destPath The path to the destination of the symbolic link.
	 */
	public void createSymlink(String name, String destPath)
	{
		addChild(name, new INodeSymbolicLink(name, this, destPath));
	}
	
	/**
	 * Removes the node with the specified name from the directory.
	 * @param name The name of the node to remove.
	 */
	public void removeNode (String name)
	{
		m_children.remove(name);
	}
	
	/**
	 * Returns a list sorted by time of INodes in this directory.
	 * @return A sorted list.
	 */
	public List<INode> sortChildrenByTime()
	{		
		ArrayList<INode> output = new ArrayList<INode>();
		output.addAll(m_children.values());
		
		QuickSort<INode> qs = new QuickSort<INode>();
		
		qs.quickSort(output, 0, m_children.size()-1, new compareByTime());
		
		return output;
	}
	
	/**
	 * Returns a list sorted by name of INodes in this directory.
	 * @return A sorted list.
	 */
	public List<INode> sortChildrenByName()
	{	
		ArrayList<INode> output = new ArrayList<INode>();
		output.addAll(m_children.values());
		
		QuickSort<INode> qs = new QuickSort<INode>();
		
		qs.quickSort(output, 0, m_children.size()-1, new compareByName());
		
		return output;
	}

	/**
	 * Adds a child to the current directory.
	 * @param name The name of the child to add.
	 * @param child The INode to add.
	 */
	private void addChild(String name, INode child)
	{
		if (m_WriteProtected)
			throw new IllegalArgumentException(String.format("The directory '%s' is write protected.", getName()));
			
		//Check for INodes with this name, no dupe allowed.
		if (getChild(name) == null)
			m_children.put(name, child);
		else
			throw new IllegalArgumentException(String.format("A directory or file named '%s' does already exist.", name));	
	}
    
}
