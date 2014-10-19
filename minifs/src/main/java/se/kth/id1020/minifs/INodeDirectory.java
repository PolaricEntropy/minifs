/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1020.minifs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a directory entity.
 * @author Björn Ehrby
 *
 */
public class INodeDirectory extends INode {

	private HashMap<String, INode> children;
	
	public INodeDirectory(String name, INodeDirectory parent)
	{
		super(name, parent);
		children = new HashMap<String, INode>();
	}
	
	public List<INode> getChildren()
	{
		List<INode> output = new ArrayList<INode>();
		output.addAll(children.values());
		return output;
	}
	
	public INode getChild(String name)
	{
		return children.get(name);	
	}
	
	public void createDirectory(String name) throws IllegalArgumentException
	{
		INodeDirectory newdir = new INodeDirectory(name, this);
		
		//Check for INodes with this name, no dupe allowed.
		if (getChild(name) == null)
			children.put(name, newdir);
		else
			throw new IllegalArgumentException(String.format("A directory or file named %s does already exist.", name));	
		
	}
	
	public void createFile(String name)
	{
		INodeFile newfile = new INodeFile(name, this);
		
		//Check for INodes with this name, no dupe allowed.
		if (getChild(name) == null)
			children.put(name, newfile);
		else
			throw new IllegalArgumentException(String.format("A directory or file named %s does already exist.", name));	
	}
	
	public List<INode> sortChildrenByTime()
	{		
		ArrayList<INode> output = new ArrayList<INode>();
		output.addAll(children.values());
		
		quickSort(output, 0, children.size()-1, new compareByTime());
		
		return output;
	}
	
	public List<INode> sortChildrenByName()
	{	
		ArrayList<INode> output = new ArrayList<INode>();
		output.addAll(children.values());
		
		quickSort(output, 0, children.size()-1, new compareByName());
		
		return output;
	}
		
	
    private void quickSort(ArrayList<INode> input, int low, int high, Comparator<INode> comp)
    {
        if(low >= high)
        	return;
        
        int pivot = partition(input, low, high, comp);
        quickSort(input, low, pivot-1, comp);
        quickSort(input, pivot+1, high, comp);
    }

    private int partition(ArrayList<INode> input, int low, int high, Comparator<INode> comp)
    {
        int i = low + 1;
        int j = high;
        
        while(i <= j) {
            if(comp.compare(input.get(i), input.get(low)) <= 0) { 
                i++; 
            }
            else if(comp.compare(input.get(j), input.get(low)) > 0) { 
                j--;
            }
            else if(j < i) {
                break;
            }
            else
                exchange(input, i, j);
        }
        exchange(input, low, j);
        return j;
    }

    private void exchange(ArrayList<INode> a, int i, int j)
    {
        INode tmp = a.get(i);
        a.set(i,a.get(j));
        a.set(j, tmp);
    }
}
