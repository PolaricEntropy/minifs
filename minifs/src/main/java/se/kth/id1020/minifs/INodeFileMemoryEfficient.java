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
	private Stack<Block> m_blocks;
	
	public INodeFileMemoryEfficient(String name, INodeDirectory parent)
	{
		super(name, parent);
		m_blocks = new Stack<Block>();
	}
	
	/**
	 * Allocates a number of blocks to hold the data for this file.
	 * @param data Data to be added to this file.
	 */
	public void addData(String data)
	{					
		Block currBlock;
		
		//If we have blocks for this file, find the last one to start adding to that. If not then just use a new empty block.
		if (m_blocks.size() > 0)
			currBlock = m_blocks.get(m_blocks.size()-1);
		else
		{
			currBlock = new Block();
			m_blocks.add(currBlock);
		}
			
		//If we start from an empty block endIndex would be BLOCK_SIZE-1 ex. 63.
		int startIndex = 0, endIndex = currBlock.getFreeSpace()-1;
		
		//If our data is shorter or equal to our current block's remaining space, then all fits in one block.
		if (data.length() <= currBlock.getFreeSpace())
			currBlock.setData(data);
		else
		{
			//While endIndex is smaller or equal to the index of the last char in the string we should be doing this since we are not done with the entire string.
			while (endIndex <= data.length()-1)
			{
				//We assume we already have a block to add data to, either a partially filled or a fresh one.
				//endIndex in substring is exclusive so we need to add 1 to it so we get that last char.
				currBlock.setData(data.substring(startIndex, endIndex+1));
				
				//Advance indexes for the next round.
				startIndex += endIndex +1; //Start at the next char after we left off.
				endIndex = startIndex + Block.g_BLOCK_SIZE-1; //Increment with a block of data.
				
				//Create a new block and add it to the list for the next round.
				currBlock = new Block();
				m_blocks.add(currBlock);
				
				//Check if we moved past our end of the data.
				if (endIndex >= data.length()-1)
				{
					//endIndex in substring is exclusive so no need to do -1 there, and setting endIndex to data.length() will cause the while loop to stop running.
					endIndex = data.length();
					currBlock.setData(data.substring(startIndex, endIndex));
					//No need to declare a new block since the while loop won't run more after this.
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
		
		for (Block i : m_blocks)
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
		
		for (Block i : m_blocks)
			size += i.getSize();
		
		return size;
	}
}