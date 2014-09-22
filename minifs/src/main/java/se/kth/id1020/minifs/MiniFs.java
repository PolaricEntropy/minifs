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
  
	public void mkdir(String path)
	{	
		String folderName = "", pathToFolder = "";
		
		//Filter out stuff that doesn't contain "/" like "home".
		if (path.contains("/"))
		{
			int index = path.lastIndexOf("/");
			
			//If the slash is not the last character, if the path doesn't look like "home/".
			if (index != path.length()-1)
			{
				//Trim it and move on.
				path = path.substring(0, index);
				index = path.lastIndexOf("/");
			}
			
				//Get the stuff after the last "/", the folder name.
				//Also separate out the path to the folder from the argument.
				folderName = path.substring(index + 1);
				pathToFolder = path.substring(0, index);
		}
		else //We have a path that looks like this: "home" without slashes.
			folderName = path;
		
		//Find the directory in the path, so we can add a directory to it.
		INodeDirectory dir = findDir(pathToFolder);
		
		//We have a directory, so we found it.
		if (dir != null)
		{
			dir.createDirectory(folderName);
		}
	}

  
	public void touch(String path)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
	
	/**
	 * Finds and returns the specified directory.
	 * If the path is prefixed with "/" we search from the root directory, otherwise we are searching from the current directory.
	 * 
	 * @param path Directory path to search for.
	 * @return Returns the directory that matches the directory path.
	 */
	private INodeDirectory findDir(String path)
	{
		//If path doesn't end with a "/", then add one.
		if (path.contains("/") == false)
			path += "/";
		
		//Split the input path into an array so we can process each directory individually.
		String[] dirTree = path.split("/");
		
		INodeDirectory cur;
		
		//Start from root dir if path begins with "/" else we start from our working directory.
		if (path.startsWith("/"))
			cur = root;
		else
			cur = workingDir;
		
		//Search for each directory.
		for (int i = 0; i < dirTree.length; i++)
		{
			//Search the children of the directory after our directory.
			INode result = cur.getChildren().get(dirTree[i]);
		
			//If we didn't find anything we get a null result.
			if (result != null)
			{	
				//If it's a directory we'll update cur to search for the text thing.
				if (result.getClass().getName() == "se.kth.id1020.minifs.INodeDirectory")
					cur = (INodeDirectory)result;
				else //We found a file, stop and return null.
					return null;
			}
			else //Null result, we didn't find anything, return null.
				return null;
		}
		
		//We've found all the directories, it's time to return.
		return cur;
		
	}

}
