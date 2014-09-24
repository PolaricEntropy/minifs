/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.kth.id1020.minifs;

import java.util.ArrayList;

public class INodeFile extends INode {

	private ArrayList<Block> blocks;
	
	public INodeFile(String name, INodeDirectory parent)
	{
		super(name, parent);
		blocks = new ArrayList<Block>();
	}
	
	public void addBlock(Block in)
	{
		blocks.add(in);
	}
	
	public ArrayList<Block> getBlocks()
	{
		return blocks; 	
	}
	
	public int getSize()
	{
		int size = 0;
		
		for (Block i : blocks)
		{
			size = i.BLOCK_SIZE;
		}
		
		return size;
	}
}
