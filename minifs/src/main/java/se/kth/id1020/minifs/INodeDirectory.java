/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1020.minifs;

import java.util.ArrayList;
import java.util.Comparator;

public class INodeDirectory extends INode {

	private ArrayList<INode> children;
	
	public INodeDirectory(String name, INodeDirectory parent)
	{
		super(name, parent);
		children = new ArrayList<INode>();
		
		//Parent is null if root node or special dir.
		if (parent != null)
		{
			//Add the special directories. They have no parents since they aren't real nodes.
			//If they had parents infinite recursion would occur.
			children.add(new INodeDirectory(".", null));
			children.add(new INodeDirectory("..", null));
		}
    }
	
	
	public ArrayList<INode> getChildren()
	{
		return children;
	}
	
	public INode getChild(String name)
	{
		for (INode i : children)
		{
			if (i.getName().equals(name))
				return i;
		}
		
		return null;
	}
	
	public void createDirectory(String name) throws IllegalArgumentException
	{
		//Create a new directory.
		INodeDirectory newdir = new INodeDirectory(name, this);
		
		//Make sure we don't have a child with this name before.
		if (getChild(name) == null)
			children.add(newdir);
		else
			throw new IllegalArgumentException("A directory or file named " + name + " does already exist.");	
		
	}
	
	public void createFile(String name)
	{
		//Create a new directory.
		INodeFile newfile = new INodeFile(name, this);
		
		//Make sure we don't have a child with this name before.
		if (getChild(name) == null)
			children.add(newfile);
		else
			throw new IllegalArgumentException("A directory or file named " + name + " does already exist.");	
	}
	
	public void sortChildrenByTime()
	{		
		quickSort(children, 0, children.size()-1, new compareByTime());
	}
	
	public void sortChildrenByName()
	{		
		quickSort(children, 0, children.size()-1, new compareByName());
	}
		
	
    private void quickSort(ArrayList<INode> input, int low, int high, Comparator<INode> comp) {
        if(low >= high) return;
        
        int pivot = partition(input, low, high, comp);
        quickSort(input, low, pivot-1, comp);
        quickSort(input, pivot+1, high, comp);
    }

    private int partition(ArrayList<INode> input, int low, int high, Comparator<INode> comp) {
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

    private void exchange(ArrayList<INode> a, int i, int j) {
        INode tmp = a.get(i);
        a.set(i,a.get(j));
        a.set(j, tmp);
    }
}
