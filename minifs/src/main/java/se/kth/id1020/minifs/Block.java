/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.kth.id1020.minifs;

/**
 * Block of data for an INodeFile.
 * @author Björn Ehrby
 */
public class Block {

	//Fixed block size to optimize memory efficiency, can be adjusted as needed for file systems with
	//small files (should have low block size) and large files (should have large block size). There is
	//a memory overhead of many blocks due to low block size and large file; and there are a of a lot of
	//slack space if we have a large block size with small files.
	public static final int g_BLOCK_SIZE = 64;
	private char[] m_data;
	private int m_nextFreeDataIndex = 0; //Index for the next free position in the data array. For empty blocks this is 0, for filled blocks this is 64 (although index 64 does not exist).
	
	public Block()
	{
		this.m_data = new char[g_BLOCK_SIZE];
	}
	
	public Block(String input)
	{
		this.m_data = new char[g_BLOCK_SIZE];
		setData(input);
	}

	/**
	 * Returns all of the data in the block. For half full blocks it stops at the end of our data,
	 * to avoid getting empty array elements that we have not used. 
	 * @return Returns the data in the block as a String.
	 */
	public String getData()
	{
		//Last parameter in valueOf is the count, thus an index for the next free data also serves as length.
		return String.valueOf(m_data, 0, m_nextFreeDataIndex);
	}

	/**
	 * Adds data to a block starting from the end of the latest added data. If the block is empty data is added at the start of the block.
	 * @param input The data to be added.
	 */
	public void setData(String input)
	{
		char [] inputArr = input.toCharArray();
	
		//Copy each char from our input data array to our storage array.
		//Begin from the nextFreeDataIndex pointer so we can add to half full blocks.
		//It should be up to other classes to supply the correct amount of data, but we have a check non the less (not that buffer overruns can happen in Java due to coder error anyway).
		for (int i = 0; i < inputArr.length && m_nextFreeDataIndex != g_BLOCK_SIZE; i++)
		{
			m_data[m_nextFreeDataIndex] = inputArr[i];
			m_nextFreeDataIndex++;
		}
	}

	/**
	 * Gets the number of characters in the block.
	 * @return The number of characters.
	 */
	public int getSize()
	{
		return m_nextFreeDataIndex;
	}

	public int getFreeSpace()
	{
		return g_BLOCK_SIZE - m_nextFreeDataIndex;
	}
}
