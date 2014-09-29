/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.kth.id1020.minifs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//Please note that some of the code in this assignment could be more optimized,
//streamlined and standardized. However due to time constraints such refactoring
//will have to wait, the code is still fully functional.


 /**
  * Mini file system that implements some basic file system operations.
  * @author Björn Ehrby
  *
  */
public class MiniFs implements FileSystem {
	
	private final INodeDirectory root;
	private INodeDirectory workingDir; //We want to support state in form of a working directory.
  
	public MiniFs()
	{
		root = new INodeDirectory(pathDelimiter, null); //Null parent, since this is the root node.
		workingDir = root;
	}
	
	public void mkdir(String path)
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

	public void touch(String path)
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
		INodeFile file = findFile(path);
		
		//Find file checks for nulls, so we don't have to here. findDir should also do that.
		file.addData(data);
		file.setAccessTime(System.currentTimeMillis());
	}

	public String ls(String path, String param)
	{
		INodeDirectory dir;	
		
		//Find the directory we are going to ls.
		dir = findDir(path);
		
		if (dir == null)
			throw new IllegalArgumentException(String.format("The directory %s does not exist.", path));
		
		//Sort depending on parameter.
		if (param.trim().equals("-t"))
			dir.sortChildrenByTime();
		else if(param.trim().equals("-s"))
			dir.sortChildrenByName();
		else
			throw new IllegalArgumentException(String.format("The parameter %s is not supported by ls.", param));
		
		return listFiles(dir);
	}
  
	public String du(String path)
	{	
		//Find the directory we should calculate.
		INodeDirectory dir = findDir(path);
		
		if (dir == null)
			throw new IllegalArgumentException(String.format("The directory %s does not exist.", path));
		
		//Create a StringBuilder that the recursive function should use for adding stuff.
		StringBuilder sb = new StringBuilder();
		
		//Do the calculations, the result will be in the StringBuilder we sent in as reference.
		diskUsage(dir, sb);
		
		return sb.toString();
	}
	
	public String cat(String path)
	{		
		return findFile(path).getData();
	}
	
	public String pwd()
	{
		return getPath(workingDir);
	}
	
	public void cd(String path)
	{
		//Find the dir from the path we have supplied.
		INodeDirectory dir = findDir(path);
		
		if (dir != null)
			workingDir = dir;
		else
			throw new IllegalArgumentException(String.format("The path %s does not exist.",path));
	}

	public String ver()
	{
		return "Ehrby FileSystem v1.0";
	}
	
	public void rm(String path, String param)
	{
		//TODO: Should be able to remove files as well.
		
		//Find the directory we should remove.
		INode node = findDir(path);
		
		if (node == null)
			throw new IllegalArgumentException(String.format("The directory %s does not exist.", path));
		
		//If we have no parameters we need to check if the node we are going to remove does not have any children.
		if (param.trim().isEmpty())
		{
			INodeDirectory parent = node.getParent();
			
			if (node instanceof INodeDirectory)
			{
				INodeDirectory dir1 = (INodeDirectory) node;
				if (dir1.getChildren().isEmpty() == false)
					throw new IllegalArgumentException(String.format("The directory %s is not empty.", path));
			}
			
			parent.getChildren().remove(node);
		
		}
		else if(param.trim().equals("-rf"))
		{
			//Find the parent, remove the child from the parent.
			INodeDirectory parent = node.getParent();
			parent.getChildren().remove(node);
		}
		else
			throw new IllegalArgumentException(String.format("The parameter %s is not supported by rm.", param));
	}
	
	/**
	 * Gets the disk usage of all the directories and files in the specified folder. This method recursively searches all sub-directories of a given directory.
	 * @param dir The directory to begin searching from.
	 * @param sb Reference to a StringBuilder to add the result of the printout to.
	 * @return Returns an object array with position[0] as the StringBuilder reference and position[1] as the total size of all sub-directories. 
	 */
	private int diskUsage(INodeDirectory dir, StringBuilder sb)
	{
		//Children and total size of the current directory.
		ArrayList<INode> children = dir.getChildren();
		int dirTotalSize = 0;
		
		//Loop through all directories first to get their results first in the print out.
		for (INode child : children)
		{
			//Do diskUsage on the subdirs adding their total size to this dirs total size.
			if (child instanceof INodeDirectory)
				dirTotalSize += diskUsage((INodeDirectory) child, sb);	
		}
		
		//Loop through all files and add them to the print out.
		for (INode child : children)
		{
			if (child instanceof INodeFile)
			{
				INodeFile file = (INodeFile)child;
				sb.append(String.format("%s %s \n", file.getSize(), getPath(child)));
				dirTotalSize += file.getSize();
			}
		}
		
		
		//We've now done all files and directories for this directory. Print total for this dir.
		sb.append(String.format("%s %s \n", dirTotalSize, getPath(dir)));
		
		return dirTotalSize;
	}
	
	/**
	 * Returns the path to a specified INode.
	 * @param node The INode to get the path for.
	 * @return Returns the absolute path to the INode.
	 */
	private String getPath(INode node)
	{
		StringBuilder sb = new StringBuilder();
		
		//If we are starting with the root, then the while loop won't run, so print just a "/".
		if (node == root)
			sb.append(pathDelimiter);
		else
		{
			//If we hit the root just stop, no need to print the root, we've already printed the "/" for the first dir.
			while (node != root)
			{	
				sb.insert(0, String.format("/%s",node.getName()));
				node = node.getParent();
			}
		}
		return sb.toString();
	}
	
	/**
	 * Builds a formatted string of the INodes that belong to the specified INodeDirectory.
	 * @param dir INodeDirectory to list.
	 * @return Returns a formatted string of all the INodes in the directory.
	 */
	private String listFiles(INodeDirectory dir)
	{
		//StringBuilder for print out, variables to keep count of folders, formatter to format our time/date properly.
		StringBuilder sb = new StringBuilder();
		int files = 0, folders = 0;
		DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss:SSS");
		
		//Add info about what directory we are printing.
		sb.append(String.format("Directory of %s\n\n", getPath(dir)));
		
		//Get the accessTime for our folder, it's used for the . and .. folders.
		Date accessTime = new Date(dir.getAccessTime());
		
		//The root folder should not have . and .. in printout.
		if (dir != root)
		{
			//Add our special folders to the list.
			sb.append(String.format("%s   <DIR>	.\n", formatter.format(accessTime)));
			sb.append(String.format("%s   <DIR>	..\n", formatter.format(accessTime)));
			folders += 2;
		}
		
		//Iterate over all INodes of this directory.
		for(INode i : dir.getChildren())
		{
			//Format the date.
			accessTime = new Date(i.getAccessTime());
			sb.append(String.format("%s	", formatter.format(accessTime)));
			
			//Check if its a dir.
			if (i instanceof INodeDirectory)
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
		
		//Add some stats for this folder.
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
		int index = path.lastIndexOf(pathDelimiter);
		
		//If the slash is the last character, if the path looks like "home/".
		if (index == (path.length()-1) && path.length() > 0)
		{
			//Remove the "/" and get the last "/" again.
			path = path.substring(0, index);
			index = path.lastIndexOf(pathDelimiter);
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
	 * Finds and returns the specified file.
	 * @param path The path to the file we want to find.
	 * @return The INodeFile corresponding to the path.
	 */
	private INodeFile findFile(String path)
	{
		//Separate the path into its components, path and node name.
		String[] values = SeparatePath(path);
		
		//Find the directory in the path, so we can find the file.
		INodeDirectory dir = findDir(values[1]);
		
		if (dir == null)
			throw new IllegalArgumentException(String.format("The directory %s does not exist.", values[1]));
		
		//Finds our node.
		INode child = dir.getChild(values[0]);
		
		//Check if the node is indeed a file.
		if (child instanceof INodeFile)
			return (INodeFile) child;
		else
			throw new IllegalArgumentException(String.format("The file %s does not exist.", values[0]));
	}
	
	/**
	 * Finds and returns the specified directory.
	 * If the path is prefixed with "/" we search from the root directory, otherwise we are searching from the working directory.
	 * 
	 * @param path Directory path to search for.
	 * @return Returns the INodeDirectory that matches the directory path.
	 */
	private INodeDirectory findDir(String path)
	{
		//Assume working dir for now, anything else discovered will change this to what it should be.
		INodeDirectory cur = workingDir;
		
		//If we have an empty path then we should start form working dir.
		if (path.isEmpty())
			return cur;
		
		//If path doesn't end with a "/", then add one. This way we will have at least one "/".
		if (path.endsWith(pathDelimiter) == false)
			path += pathDelimiter;
		
		//If we start with . or .. get the right node and trim the path to remove the dots.
		while (path.startsWith("."))
		{	
			 if (path.startsWith("..")) //Start from working dirs parent.
			{
				cur = cur.getParent();
				path = path.substring(3);
				
				//If we stepped beyond the root node, then go back and work from root.
				if (cur == null)
					cur = root;
			}
			else if (path.startsWith(".")) //Start from working dir.
				path = path.substring(2);
		}
		
		if (path.startsWith(pathDelimiter))
		{
			cur = root;
			path = path.substring(1); //Trim away the "/".
		}
		
		
		//Split the input path into an array so we can process each directory individually.
		String[] dirTree = path.split(pathDelimiter);
		
		//Search for each directory in the path.
		for (int i = 0; i < dirTree.length; i++)
		{
			//If we have a empty array position then just skip that. This happens if path is "" or "/", then split produces an array with an empty position.
			if (dirTree[i].isEmpty())
				continue;
			
			//Search the children of the current directory.
			INode result = cur.getChild(dirTree[i]);
		
			//If we didn't find anything we get a null result.
			if (result != null)
			{				
				//If it's a directory we'll update cur to search for the text thing.
				if (result instanceof INodeDirectory)
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
