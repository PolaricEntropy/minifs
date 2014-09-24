/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.kth.id1020.minifs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

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
			throw new IllegalArgumentException(String.format("The directory %s does not exist.", values[1]));
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
			throw new IllegalArgumentException(String.format("The directory %s does not exist.", values[1]));
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
		INodeDirectory dir;	
		
		//If we have zero length we have no arguments and should work from the working directory.
		if (path.length() == 0)
			dir = workingDir;
		else
			dir = findDir(path);
		
		//TODO: Sort. They are actually sorted by default, but if we sort by time first they are not any more.
		
		//Create a ref var for this for easy access.
		TreeMap<String, INode> children;
		
		if (dir != null)
			 children = dir.getChildren();
		else
			throw new IllegalArgumentException(String.format("The path %s does not exist", path));
		
		return listFiles(children, dir);
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
		return getPath(workingDir);
	}
	
	public void cd(String path)
	{
		INodeDirectory dir = findDir(path);
		
		if (dir != null)
			workingDir = dir;
	}
	
	public String ver()
	{
		return "Ehrby FileSystem v1.0";
	}
	
	private String getPath(INodeDirectory dir)
	{
		StringBuilder sb = new StringBuilder();
		
		//If we are starting with the root, then the while loop won't run, so print just a "/".
		if (dir == root)
			sb.append("/");
		
		//If we hit the root just stop, no need to print the root, we've already printed the "/" for the previous dir.
		while (dir != root)
		{	
			sb.insert(0, String.format("/%s",dir.getName()));
			dir = dir.getParent();
		}

		return sb.toString();
		
	}
	
	
	/**
	 * Builds a formatted string of the INodes in the supplied collection. INodes will be in the same order as in the collection.
	 * @param children TreeMap of INodes to build a string of.
	 * @return Returns a formatted string of all the INodes in a collection.
	 */
	private String listFiles(TreeMap<String, INode> children, INodeDirectory dir)
	{
		StringBuilder sb = new StringBuilder();
		int files = 0, folders = 0;
		
		sb.append(String.format("Directory of %s\n\n", getPath(dir)));
		
		
		//Iterate over all INodes of this directory.
		for(INode i : children.values())
		{
			//Format the date.
			Date date = new Date(i.getAccessTime());
			DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss:SSS");
			
			sb.append(String.format("%s	", formatter.format(date)));
			
			//Check if its a dir.
			if (i.getClass().getName() == root.getClass().getName())
			{
				sb.append("  <DIR>	");
				folders++;
			}
			else
			{
				sb.append("	");
				files++;
			}
			
			sb.append(String.format("%s\n", i.getName()));
		}
		
		
		sb.append(String.format("		%s File(s)\n", files));
		sb.append(String.format("		%s Dir(s)\n", folders));
		
		return sb.toString();
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
		//Assume working dir for now, anything else discovered will change this to what it should be.
		INodeDirectory cur = workingDir;
		
		//If path doesn't end with a "/", then add one. This way we will have at least one "/".
		if (path.endsWith("/") == false)
			path += "/";
		
		//If we start with . or .. get the right node and trim the path to remove the dots.
		while (path.startsWith("."))
		{	
			 if (path.startsWith("../")) //Start from working dirs parent.
			{
				cur = cur.getParent();
				path = path.substring(3);
				
				//If we stepped beyond the root node, then go back and work from root.
				if (cur == null)
					cur = root;
			}
			else if (path.startsWith("./")) //Start from working dir.
				path = path.substring(2);
		}
		
		if (path.startsWith("/"))
		{
			cur = root;
			path = path.substring(1); //Trim away the "/".
		}
		
		
		//Split the input path into an array so we can process each directory individually.
		String[] dirTree = path.split("/");
		
		//Search for each directory in the path.
		for (int i = 0; i < dirTree.length; i++)
		{
			//If we have a empty array position then just skip that. This happens if path is "" or "/", then split produces an array with an empty position.
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
