/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.kth.id1020.minifs;

import java.util.Stack;

public class INodeFileMemoryEfficient extends INode {

	//Stack is more memory efficient then ArrayList as it doesn't have a load factor, it's 100% full all of the time.
	//Another thing to improve memory efficiency is to change the block size based on what sort of files that's likely going to be stored
	//in the file system.
	private Stack<Block> blocks;
	
	public INodeFileMemoryEfficient(String name, INodeDirectory parent)
	{
		super(name, parent);
		blocks = new Stack<Block>();
	}
	
	/**
	 * Allocates a number of blocks to hold the data for this file.
	 * @param data Data to be added to this file.
	 */
	public void addData(String data)
	{
		//TODO: Start appending from last used block.
		
		//Blocks have a fixed size.
		int startIndex = 0, endIndex = Block.BLOCK_SIZE-1;
		
		//If our data is shorter or equal to our block size, then all fits in one block.
		if (data.length() <= Block.BLOCK_SIZE)
			blocks.add(new Block(data));
		else
		{
			//While "endIndex" is smaller or equal to the index of the last char in the string we should be doing this since we are not done with the entire string.
			while (endIndex <= data.length()-1)
			{
				//endIndex is exclusive so we need to add 1 to it so we get that last char.
				blocks.add(new Block(data.substring(startIndex, endIndex+1)));
				startIndex += Block.BLOCK_SIZE; //Offset the start. Don't do -1 since we've already included that char in the previous line.
				endIndex += Block.BLOCK_SIZE-1; //Offset the end.
				
				//Check if we moved past our end of the data.
				if (endIndex >= data.length()-1)
				{
					//end index is exclusive so no need to do -1.
					endIndex = data.length();
					blocks.add(new Block(data.substring(startIndex, endIndex)));
				}
			}
		}
	}

	/**
	 * Reads the data stored in this file.
	 * @return The data as a String.
	 */
	public String getData()
	{
		StringBuilder sb = new StringBuilder();
		
		for (Block i : blocks)
			sb.append(i.getData());
		
		return sb.toString();
	}
	
	/**
	 * Get the size of the data in this file. Counts the number of characters and does not take allocated blocks in to account,
	 * even though they take up space. This is the behavior in Linux/Windows, only count the bytes the file take up and not slack
	 * space in partially filled blocks/clusters.
	 * @return The size of the file as an int.
	 */
	public int getSize()
	{
		int size = 0;
		
		for (Block i : blocks)
			size += i.getSize();
		
		return size;
	}
}
