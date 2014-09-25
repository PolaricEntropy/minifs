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
	
	public void addData(String data)
	{
		//Blocks have a fixed size of 64 characters, 0-63 is 64 characters.
		int start = 0, end = 63;
		
		//If our data is shorter or equal to our block size, then all fits in one block.
		if (data.length()-1 <= end)
			blocks.add(new Block(data));
		else
		{
			while (data.length()-1 > end)
			{
				blocks.add(new Block(data.substring(start, end)));
				start += 63; //Offset the start.
				end += 63; //Offset the end.
				
				//Check if we moved past our end of the data.
				if (data.length()-1 <= end)
				{
					//end index is exclusive so no need to do -1.
					end = data.length();
					blocks.add(new Block(data.substring(start, end)));
				}
				
			}
		}
	}

	
	public String getData()
	{
		StringBuilder sb = new StringBuilder();
		
		for (Block i : blocks)
			sb.append(i.getData());
		
		return sb.toString();
	}
	
	public int getSize()
	{
		int size = 0;
		
		for (Block i : blocks)
			size =+ i.getSize();
		
		return size;
	}
}
