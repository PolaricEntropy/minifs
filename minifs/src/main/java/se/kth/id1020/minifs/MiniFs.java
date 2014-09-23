/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.kth.id1020.minifs;

/**
 *
 */
public class MiniFs implements FileSystem {

	private final INodeDirectory root;
	private INodeDirectory workingDir; //We want to support state in form of a working directory.
  
	public MiniFs()
	{
		root = new INodeDirectory("/", null); //Null parent, since this is the root node.
		workingDir = root;
	}
	
	public void mkdir(String path) throws IllegalArgumentException
	{	
		String[] values = SeparatePath(path);
		
		//Find the directory in the path, so we can add a new directory to it.
		INodeDirectory dir = findDir(values[1]);
		
		//We have a directory, so we found it.
		if (dir != null)
			dir.createDirectory(values[0]);
		else
			throw new IllegalArgumentException("The directory " + values[1] + " does not exist");
	}

	public void touch(String path) throws IllegalArgumentException
	{
		String[] values = SeparatePath(path);
		
		//Find the directory in the path, so we can add a new file to it.
		INodeDirectory dir = findDir(values[1]);
		
		//We have a directory, so we found it.
		if (dir != null)
			dir.createFile(values[0]);
		else
			throw new IllegalArgumentException("The directory " + values[1] + " does not exist");
	}

  
	public void append(String path, String data)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

  
	public String lsByTime(String path)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

  
	public String lsByName(String path)
	{
		//Find the directory in the path, so we can process it.
		INodeDirectory dir;
		
		//If we have zero length we have no arguments and should work from the working directory.
		if (path.length() == 0)
			dir = workingDir;
		else
			dir = findDir(path);
		
		
		
		return null;
	}

  
	public String du(String path)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

  
	public String cat(String path)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	public String pwd()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	public void cd(String cmd)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	/**
	 * Separates an input path into the nodeName and the path value.
	 * @param path The input that needs to be separated.
	 * @return Returns the nodeName and pathToFolder in a String[] array, in that order.
	 */
	private String[] SeparatePath(String path)
	{	
		//Get the index of the last "/", we get -1 if we have no "/".
		int index = path.lastIndexOf("/");
		
		//If the slash is the last character, if the path looks like "home/".
		if (index == path.length()-1 && path.length() > 0)
		{
			//Remove the "/" and get the last "/" again.
			path = path.substring(0, index);
			index = path.lastIndexOf("/");
		}
		
		//If we have a "/" in the string.
		if (index != -1)
		{
			//Get the stuff after the last "/", the node name.
			//Also separate out the path to the directory from the argument.			
			return(new String[] {path.substring(index + 1), path.substring(0, index)});
		}
		else //We don't have a "/" in the string so we just have "home". The node name is the path.
			return(new String[] {path, ""});
		
	}
	
	/**
	 * Finds and returns the specified directory.
	 * If the path is prefixed with "/" we search from the root directory, otherwise we are searching from the current directory.
	 * 
	 * @param path Directory path to search for.
	 * @return Returns the directory that matches the directory path.
	 */
	private INodeDirectory findDir(String path)
	{
		INodeDirectory cur;
		
		//If path doesn't end with a "/", then add one. This way we will have at least one "/".
		if (path.contains("/") == false)
			path += "/";
		
		//Start from root dir if path begins with "/" else we start from our working directory.
		if (path.startsWith("/"))
			cur = root;
		else
			cur = workingDir;
		
		//Split the input path into an array so we can process each directory individually.
		String[] dirTree = path.split("/");
		
		//Search for each directory in the path.
		for (int i = 0; i < dirTree.length; i++)
		{
			//If we have a empty array position then just skip that. This happens if a string begins with "/".
			if (dirTree[i].isEmpty())
				continue;
			
			//Search the children of the current directory.
			INode result = cur.getChildren().get(dirTree[i]);
		
			//If we didn't find anything we get a null result.
			if (result != null)
			{				
				//If it's a directory we'll update cur to search for the text thing.
				if (result.getClass().getName() == root.getClass().getName())
					cur = (INodeDirectory)result;
				else //We found a file, stop and return null.
					return null;
			}
			else //Null result. We didn't find the directory in the current directory, return null.
				return null;
		}
		
		//We've found all the directories in the path, it's time to return.
		return cur;
		
	}

	
	
}
