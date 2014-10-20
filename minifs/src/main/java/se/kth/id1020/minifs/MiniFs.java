/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.kth.id1020.minifs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

 /**
  * Mini file system that implements some basic file system operations.
  * @author Björn Ehrby
  */
public class MiniFs implements FileSystem {
	
	private final INodeDirectory m_root;
	private INodeDirectory m_workingDir; //We want to support state in form of a working directory.
	private String m_version = "1.1";
	
	/**
	 * Constructor. Creates a new MiniFs object with a root directory.
	 */
	public MiniFs()
	{
		m_root = new INodeDirectory(g_pathDelimiter, null); //Null parent, since this is the root node.
		m_workingDir = m_root;
		m_root.createDirectory("home");
		m_root.setWriteProtect(true);
	}
	
	/**
	 * Creates a directory at the specified path.
	 * @param path Absolute or relative path to the new directory, the characters after the last path delimiter will be the directory name.
	 */
	public void mkdir(String path)
	{	
		//Need to separate the new node name from the argument.
		String[] values = SeparatePath(path);
		
		//Find the directory in the path, so we can add a new directory to it.
		INodeDirectory dir = findDir(values[1]);
		
		if (dir == null)
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.", values[1]));
		
		dir.createDirectory(values[0]);	
	}

	/**
	 * Creates a file at the specified path.
	 * @param path Absolute or relative path to the new file, the characters after the last path delimiter will be the file name.
	 */
	public void touch(String path)
	{
		//Need to separate the new node name from the argument.
		String[] values = SeparatePath(path);
		
		//Find the directory in the path, so we can add a new file to it.
		INodeDirectory dir = findDir(values[1]);
		
		if (dir == null)
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.", values[1]));
		
		dir.createFile(values[0]);
	}

	/**
	 * Appends data the specified file.
	 * @param path Absolute or relative path to the file we are going to append data to.
	 * @param data The data to be added to the file.
	 */
	public void append(String path, String data)
	{
		INode node = findNode(path, true);
		
		if (node instanceof INodeFile)
		{
			INodeFile file = (INodeFile) node;
			
			file.addData(data);
			file.setAccessTime(System.currentTimeMillis());
		}
		else //Catches both if node is null or is an INodeDirectory.
			throw new IllegalArgumentException(String.format("The file '%s' does not exist.", path));
	}

	/**
	 * Lists the files in the specified directory.
	 * @param path Absolute or relative path to the directory we are going to list.
	 * @param param Supported parameters for this command is -t to sort by time and -s to sort by name.
	 * @return A formatted string with the directory listing.
	 */
	public String ls(String path, String param)
	{
		INodeDirectory dir = findDir(path);
		
		if (dir == null)
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.", path));
		
		List<INode> sortedINodes;
		
		//Sort depending on parameter.
		if (param.trim().equals("-t"))
			sortedINodes = dir.sortChildrenByTime();
		else if(param.trim().equals("-s"))
			sortedINodes = dir.sortChildrenByName();
		else
			throw new IllegalArgumentException(String.format("The parameter '%s' is not supported by ls.", param));
		
		return listFiles(dir, sortedINodes);		
	}
  
	/**
	 * Calculates the disk usage of all the files and directories at the specified path.
	 * @param path Absolute or relative path to the directory we are going to calculate from.
	 * @return A formatted string with the results of the calculation.
	 */
	public String du(String path)
	{
		INodeDirectory dir = findDir(path);
		
		if (dir == null)
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.", path));
		
		//Create a StringBuilder that the recursive function should use for adding stuff.
		StringBuilder sb = new StringBuilder();
		
		diskUsage(dir, sb);
		return sb.toString();		
	}
	
	/**
	 * Prints the contents of the specified file.
	 * @param path Absolute or relative path to the file we are going to print.
	 * @return The contents of the specified file.
	 */
	public String cat(String path)
	{
		INode node = findNode(path, true);
		
		if (node instanceof INodeFile)
		{
			return ((INodeFile) node).getData();
		}
		else //Catches both if node is null or is an INodeDirectory.
			throw new IllegalArgumentException(String.format("The file '%s' does not exist.",path));
			
	}
	
	/**
	 * Prints the working directory.
	 * @return The absolute path to the working directory.
	 */
	public String pwd()
	{
		return getPath(m_workingDir);
	}
	
	/**
	 * Change the working directory.
	 * @param path Absolute or relative path we should change the working directory to.
	 */
	public void cd(String path)
	{
		INodeDirectory dir = findDir(path);
		
		if (dir == null)
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.",path));
			
		m_workingDir = dir;
	}

	/**
	 * Prints the version number of this file system.
	 */
	public String ver()
	{
		return "Ehrby FileSystem v" + m_version;
	}
	
	/**
	 * Removes the specified file or directory.
	 * @param path Absolute or relative path to the directory or file we are going to remove.
	 * @param If no parameter is specified and if the object we are going to remove is a directory, it must be empty. If the parameter -rf is supplied this check is skipped.
	 */
	public void rm(String path, String param)
	{	
		//We don't want to follow symlinks, cause then we can't ever remove them.
		INode node = findNode(path, false);
		
		if (node == null)
			throw new IllegalArgumentException(String.format("The file/directory '%s' does not exist.", path));
			
		//If we have no parameters we need to check if the node we are going to remove does not have any children.
		if (param.isEmpty())
		{				
			if (node instanceof INodeDirectory)
			{
				INodeDirectory dir = (INodeDirectory) node;
				if (dir.getChildren().isEmpty() == false)
					throw new IllegalArgumentException(String.format("The directory '%s' is not empty.", path));
			}
		}
		else if(!param.trim().equals("-rf")) //If param is NOT "-rf". The "-rf" param just skips the check above.
			throw new IllegalArgumentException(String.format("The parameter '%s' is not supported by rm.", param));
	
		
		node.getParent().removeNode(node.getName());
	}
	
	public String ln(String SrcPath, String DestPath)
	{
		//Need to separate path and name from the argument.
		String[] values = SeparatePath(SrcPath);
		
		//Find the source dir.
		INodeDirectory srcDir = findDir(values[1]);
		
		//Find the Destination.
		INode dest = findNode(DestPath, true);
		
		srcDir.createSymlink(values[0], dest);
		
		//TODO: Cycle check if the new symlink has created a cycle.
		
		return String.format("Symbolic link created: %s --> %s", SrcPath, DestPath);
	}

	public String find(String criteria)
	{
		StringBuilder sb = new StringBuilder();
		
		findInDir(m_root, criteria, sb, false);
		
		return sb.toString();
	}

	public String findc(String criteria)
	{
		StringBuilder sb = new StringBuilder();
		
		findInDir(m_root, criteria, sb, true);
		
		return sb.toString();
	}

	public String cycles()
	{
		return null;
	}
	
	
	private void findInDir(INodeDirectory dir, String criteria, StringBuilder sb, boolean useWildcards)
	{
		List<INode> children = dir.getChildren();
		
		for (INode child : children)
		{
			String name = child.getName();
			
			if (criteria.contains("*"))
			{
				if (useWildcards)
				{
					if (matchesWildcard(name, criteria))
					{
						sb.append(getPath(child));
						sb.append("\n");
					}
				}
			}
			else
			{
				if (name.equals(criteria))
				{
					sb.append(getPath(child));
					sb.append("\n");
				}
			}
			
		}
		
		for (INode child : children)
		{
			if (child instanceof INodeDirectory)
				findInDir((INodeDirectory) child, criteria, sb, useWildcards);
		}
	}
	
	private boolean matchesWildcard (String input, String criteria)
	{
		//Contains just the wildcard.
		if (criteria.length() == 1)
			return true;
		
		String[] searchStrings = criteria.split("\\*");
		
		for (int i = 0; i < searchStrings.length; i++)
		{
			//If it's the first argument we need to check startsWith, unless first element is a star.
			if (i == 0)
			{
				//If the element is empty 'criteria' started with a * ex. *test, so just skip this element.
				if (searchStrings[i].isEmpty())
					continue;
				else
				{
					if (!input.startsWith(searchStrings[i])) //Criteria is for example stu*nt, check if we start with stu.
						return false;
				}
			}
			else if (i == searchStrings.length-1) //If last element.
			{
				//If our criteria ends with star we don't need to end the 
				if (!criteria.endsWith("*"))
				{
					if(input.endsWith(searchStrings[i])) //Criteria is for example stu*nt, check if we end with nt.
						return true;
					else
						return false;
				}
			}
		
			//Find index for the element in input.
			int index = input.indexOf(searchStrings[i]);	
			
			
			if (index == -1)
				return false;
			
			//We've searched for this, so just remove that part of the string.
			input = input.substring(index + searchStrings[i].length());
		}
		
		return true;
	}
	
	/**
	 * Gets the disk usage of all the directories and files in the specified directory. This method recursively searches all sub-directories of a given directory.
	 * @param dir The directory to begin searching from.
	 * @param sb Reference to a StringBuilder to add the result of the printout to.
	 * @return Returns the total size of all sub-directories currently processed. 
	 */
	private int diskUsage(INodeDirectory dir, StringBuilder sb)
	{
		List<INode> children = dir.getChildren();
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
		
		//If we start from the root we'll just print the delimiter. 
		if (node == m_root)
			sb.append(g_pathDelimiter);
		else
		{
			//If we hit the root just stop, no need to print the root, we've already printed the delimiter for the first directory.
			while (node != m_root)
			{	
				//Since we are going backwards we need to add each name at the start of the string.
				sb.insert(0, String.format("%s%s", g_pathDelimiter, node.getName()));
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
	private String listFiles(INodeDirectory dir, List<INode> sortedINodes)
	{
		StringBuilder sb = new StringBuilder();
		int files = 0, directories = 0;
		DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss:SSS");
		
		//Add info about what directory we are printing.
		sb.append(String.format("Directory of %s\n\n", getPath(dir)));
		
		Date accessTime;
		
		//The root folder should not have . and .. in printout.
		if (dir != m_root)
		{
			//Get the accessTime for our directory, it's used for the . and .. directories.
			accessTime = new Date(dir.getAccessTime());
			
			//Add our special folders to the list.
			sb.append(String.format("%s   <DIR>		.\n", formatter.format(accessTime)));
			sb.append(String.format("%s   <DIR>		..\n", formatter.format(accessTime)));
			directories += 2;
		}
		
		//Iterate over all INodes of this directory.
		for(INode i : sortedINodes)
		{
			//Format the accessTime.
			accessTime = new Date(i.getAccessTime());
			sb.append(String.format("%s	", formatter.format(accessTime)));
			
			//Check if it's a dir.
			if (i instanceof INodeDirectory)
			{
				sb.append("  <DIR>		");
				directories++;
			}
			else if (i instanceof INodeSymbolicLink)
			{
				INodeSymbolicLink symlink = (INodeSymbolicLink) i;
				INode target = symlink.getTarget();
				
				if (target instanceof INodeDirectory)
				{
					sb.append("  <SYMLINKD>	");
					directories++;
				}
				else if (target instanceof INodeFile || target instanceof INodeSymbolicLink)
				{
					sb.append("  <SYMLINK>	");
					files++;
				}
			}
			else if (i instanceof INodeFile)
			{
				sb.append("  		");
				files++;
			}
			
			//TODO: Print path to target for symlink like cmd.
			sb.append(String.format("%s\n", i.getName()));
		}
		
		//Add some stats for this folder.
		sb.append(String.format("		%s File(s)\n", files));
		sb.append(String.format("		%s Dir(s)\n", directories));
		
		return sb.toString();
	}
	
	/**
	 * Separates an input path into the nodeName and the path value.
	 * @param path The input that needs to be separated.
	 * @return Returns the nodeName and pathToFolder in a String[] array, in that order.
	 */
	private String[] SeparatePath(String path)
	{	
		//If our path ends with the delimiter i.e. if the path looks like "home/", then remove the delimiter so we won't get confused later on.
		if (path.endsWith(g_pathDelimiter))
			path = path.substring(0, path.length()-1);
		
		//Get the index of the last delimiter, we get -1 if we have no delimiter.
		int index = path.lastIndexOf(g_pathDelimiter);
		
		if (index == -1)
			//We don't have a delimiter in the string, so the argument looks like this: "home". Thus the argument is the node name and we have no (absolute) path.
			return(new String[] {path, ""});
		else
			//Since we have a delimiter the stuff after that is our node name and the thing before is the path. Argument looks like this: "home/test".
			return(new String[] {path.substring(index + 1), path.substring(0, index)});
	}
	
	/**
	 * Finds and returns the specified node.
	 * @param path Absolute or relative path to the node we should find.
	 * @return The INode corresponding to the path.
	 */
	private INode findNode(String path, boolean traverseSymlinks)
	{
		//Separate the path into its components, path and node name.
		String[] values = SeparatePath(path);
		
		//Find the directory, so we can find the node. If we are following a symlink we will follow it to the correct folder.
		INodeDirectory dir = findDir(values[1]);
		
		if (dir == null)
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.", values[1]));
		
		INode child = dir.getChild(values[0]);
		
		if (child == null)
			throw new IllegalArgumentException(String.format("The file/directory '%s' does not exist.", values[0]));
		
		if (child instanceof INodeSymbolicLink && traverseSymlinks)
		{
			INodeSymbolicLink symlink = (INodeSymbolicLink) child;
			INode target = symlink.getTarget();
			
			//We will break out of this, I promise! We can't point to symlinks in all eternity without having a cycle.
			while(target instanceof INodeSymbolicLink)
			{
				//Follow symlink.
				symlink = (INodeSymbolicLink) target;
				target = symlink.getTarget();
			}
			
			child = target;
		}
		
		return child;
	}
	
	/**
	 * Finds and returns the specified directory.
	 * 
	 * @param path Absolute or relative path to the directory we should find.
	 * @return Returns the INodeDirectory that matches the directory path.
	 */
	private INodeDirectory findDir(String path)
	{
		//Assume working dir for now, anything else discovered will change this to what it should be.
		INodeDirectory cur = m_workingDir;
		
		//If we have an empty path then we should start form working dir.
		if (path.isEmpty())
			return cur;
		
		//If path doesn't end with a delimiter, then add one. This way we will have at least one delimiter.
		if (path.endsWith(g_pathDelimiter) == false)
			path += g_pathDelimiter;
		
		//If we start with a delimiter we should work from the root.
		if (path.startsWith(g_pathDelimiter))
		{
			cur = m_root;
			path = path.substring(1); //Trim away the delimiter.
		}
		
		//If we start with . or .. get the right node and trim the path to remove the dots.
		//Do this iteratively as we can have multiple dots like ../../test/.
		while (path.startsWith("."))
		{	
			if (path.startsWith("..")) //Start from working dir's parent.
			{
				cur = cur.getParent();
				path = path.substring(3); //Remove three chars as we are guaranteed to have a delimiter at the end. Ex ../ or ../test/. 
				
				//If we stepped beyond the root node, then go back and work from root.
				if (cur == null)
					cur = m_root;
			}
			else if (path.startsWith(".")) //Start from working dir.
				path = path.substring(2); //Same as above, we are guaranteed to have a delimiter at the end.
		}
		
		//Split the input path into an array so we can process each directory individually.
		String[] dirTree = path.split(g_pathDelimiter);
		
		//Search for each directory in the path.
		for (int i = 0; i < dirTree.length; i++)
		{
			//If we have a empty array position then just skip that. This happens if path is "" or a delimiter, then split produces an array with an empty position.
			if (dirTree[i].isEmpty())
				continue;
			
			//Search the children of the current directory.
			INode result = cur.getChild(dirTree[i]);
		
			//Null result. We didn't find the directory in the current directory, return null and let callings functions handle this.
			if (result == null)
				return null;
						
			//If it's a directory we'll update cur to search for the text thing.
			if (result instanceof INodeDirectory)
				cur = (INodeDirectory)result;
			else if (result instanceof INodeSymbolicLink)
			{
				INodeSymbolicLink symlink = (INodeSymbolicLink) result;
				INode target = symlink.getTarget();
				
				//We will break out of this, I promise! We can't point to symlinks in all eternity without having a cycle.
				while(target instanceof INodeSymbolicLink)
				{
					//Follow symlink.
					symlink = (INodeSymbolicLink) target;
					target = symlink.getTarget();
				}
				
				if (target instanceof INodeDirectory)
					cur = (INodeDirectory) target;
				else if (target instanceof INodeFile) //Points to a normal file.
					return null;
			}
			else if (result instanceof INodeFile)
				//We found a file, stop and return null.
				return null;
		
		}
		
		//We've found all the directories in the path, it's time to return.
		return cur;
		
	}
}
