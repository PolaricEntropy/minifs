/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.kth.id1020.minifs;

/**
 *
 */
public class Block {

	//Fixed block size to optimize memory efficiency, can be adjusted as needed for file systems with
	//small files (should have low block size) and large files (should have large block size). There is
	//a memory overhead of many blocks due to low block size and large file; and there are a of a lot of
	//slack space if we have a large block size with small files.
	public static final int BLOCK_SIZE = 64;
	private char[] data;
	private int endOfDataIndex = 0;
	
	public Block(String input)
	{
		this.data = new char[BLOCK_SIZE];
		setData(input);
	}

	/**
	 * Returns all of the data in the block. For half full blocks it stops at the end of our data, to avoid getting empty array
	 * elements that we have not used. 
	 * @return Returns the data in the block as a String.
	 */
	public String getData()
	{
		return String.valueOf(data, 0, endOfDataIndex);
	}

	/**
	 * Adds data to a block starting from the end of the latest added data. If the block is empty data is added at the start of the block.
	 * @param input The data to be added.
	 */
	public void setData(String input)
	{
		char [] inputArr = input.toCharArray();
	
		//Copy each char from our input data array to our storage array.
		//Begin from the endOfDataIndex pointer so we can add to half full blocks. It's up to other classes to make sure we send in appropriate amount of data.
		for (int i = 0; i < inputArr.length; i++)
		{
			data[endOfDataIndex] = inputArr[i];
			endOfDataIndex++;
		}
	}

	/**
	 * Gets the number of characters in the block.
	 * @return An the number of characters as an int.
	 */
	public int getSize()
	{
		return endOfDataIndex;
	}

}
