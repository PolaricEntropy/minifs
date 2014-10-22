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
 * Represents a directory entity.
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
	
	public List<INode> getChildren()
	{
		List<INode> output = new ArrayList<INode>();
		output.addAll(m_children.values());
		return output;
	}
	
	public INode getChild(String name)
	{
		return m_children.get(name);	
	}
	
	public boolean getWriteProtect()
	{
		return m_WriteProtected;
	}
	
	public void setWriteProtect(boolean value)
	{
		m_WriteProtected = value;
	}
	
	public void createDirectory(String name) throws IllegalArgumentException
	{
		addChild(name, new INodeDirectory(name, this));
	}
	
	public void createFile(String name)
	{
		addChild(name, new INodeFile(name, this));	
	}
	
	public void createSymlink(String name, String dest)
	{
		addChild(name, new INodeSymbolicLink(name, this, dest));
	}
	
	public void removeNode (String name)
	{
		m_children.remove(name);
	}
	
	public List<INode> sortChildrenByTime()
	{		
		ArrayList<INode> output = new ArrayList<INode>();
		output.addAll(m_children.values());
		
		QuickSort<INode> qs = new QuickSort<INode>();
		
		qs.quickSort(output, 0, m_children.size()-1, new compareByTime());
		
		return output;
	}
	
	public List<INode> sortChildrenByName()
	{	
		ArrayList<INode> output = new ArrayList<INode>();
		output.addAll(m_children.values());
		
		QuickSort<INode> qs = new QuickSort<INode>();
		
		qs.quickSort(output, 0, m_children.size()-1, new compareByName());
		
		return output;
	}
	
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
