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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

 /**
  * Mini file system that implements some basic file system operations.
  * @author Björn Ehrby
  */
public class MiniFs implements FileSystem {
	
	private final INodeDirectory m_root;
	private INodeDirectory m_workingDir; //We want to support state in form of a working directory.
	private String m_version = "2.0";
	
	/**
	 * Constructor. Creates a new MiniFs object with a root and home directory and write protects the root node.
	 */
	public MiniFs()
	{
		m_root = new INodeDirectory(g_pathDelimiter, null); //Null parent, since this is the root node.
		m_workingDir = m_root;
		m_root.createDirectory("home");
		m_root.setWriteProtect(true);	//Root node is write protected according to specs.
	}
	
	/**
	 * Creates a directory at the specified path.
	 * @param path Absolute or relative path to the new directory, the characters after the last path delimiter will be the directory name.
	 */
	public void mkdir(String path)
	{	
		//Need to separate the new node name from the rest of the path.
		String[] splitPath = SeparatePath(path);
		
		INode node = findNode(splitPath[1], true);
		
		if (node instanceof INodeDirectory)
			((INodeDirectory) node).createDirectory(splitPath[0]);
		else
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.", splitPath[1]));
	}

	/**
	 * Creates a file at the specified path.
	 * @param path Absolute or relative path to the new file, the characters after the last path delimiter will be the file name.
	 */
	public void touch(String path)
	{
		//Need to separate the new node name from the rest of the path.
		String[] splitPath = SeparatePath(path);
		
		INode node = findNode(splitPath[1], true);
		
		if (node instanceof INodeDirectory)
			((INodeDirectory) node).createFile(splitPath[0]);
		else
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.", splitPath[1]));
	}

	/**
	 * Appends data the specified file.
	 * @param path Absolute or relative path to the file we are going to append data to.
	 * @param data The data to be added to the file.
	 */
	public void append(String path, String data)
	{		
		try
		{
			INodeFile file = (INodeFile)findNode(path, true);
			file.addData(data);
			file.setAccessTime(System.currentTimeMillis());
		}
		catch (ClassCastException | NullPointerException ex)
		{
			throw new IllegalArgumentException(String.format("The file '%s' does not exist.", path));
		}
	}

	/**
	 * Lists the files in the specified directory.
	 * @param path Absolute or relative path to the directory we are going to list.
	 * @param param Supported parameters for this command is -t to sort by time and -s to sort by name.
	 * @return A formatted string with the directory listing.
	 */
	public String ls(String path, String param)
	{		
		try
		{
			List<INode> sortedINodes;
			INodeDirectory dir = (INodeDirectory) findNode(path, false); 
			
			//Sort depending on parameter.
			if (param.trim().equals("-t"))
				sortedINodes = dir.sortChildrenByTime();
			else if(param.trim().equals("-s"))
				sortedINodes = dir.sortChildrenByName();
			else
				throw new IllegalArgumentException(String.format("The parameter '%s' is not supported by ls.", param));
			
			return listFiles(dir, sortedINodes);
		}
		catch (ClassCastException | NullPointerException ex)
		{
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.", path));
		}
	}
  
	/**
	 * Calculates the disk usage of all the files and directories at the specified path.
	 * @param path Absolute or relative path to the directory we are going to calculate from.
	 * @return A formatted string with the results of the calculation.
	 */
	public String du(String path)
	{
		INode node = findNode(path, false);
		
		if (node instanceof INodeDirectory)
		{
			//Create a StringBuilder that the recursive function should use for adding stuff.
			StringBuilder sb = new StringBuilder();
			
			diskUsage((INodeDirectory) node, sb);
			return sb.toString();
		}
		else
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.", path));
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
			return ((INodeFile) node).getData();
		else
			throw new IllegalArgumentException(String.format("The file '%s' does not exist.",path));
	}
	
	/**
	 * Prints the working directory.
	 * @return The absolute path to the working directory.
	 */
	public String pwd()
	{
		return m_workingDir.getPath();
	}
	
	/**
	 * Change the working directory.
	 * @param path Absolute or relative path we should change the working directory to.
	 */
	public void cd(String path)
	{
		INode node = findNode(path, true);
		
		if (node instanceof INodeDirectory)
			m_workingDir = (INodeDirectory) node;
		else
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.",path));
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
		INode node = findNode(path, false);
		
		if (node == null)
			throw new IllegalArgumentException(String.format("The path '%s' does not exist.", path));
		
		if (node == m_root)
			throw new IllegalArgumentException(String.format("You can not remove the root directory.", path));
		
		//If we have no parameters we need to check if the node we are going to remove does not have any children.
		if (param.isEmpty())
		{				
			if (node instanceof INodeDirectory)
			{
				INodeDirectory dir = (INodeDirectory) node;
				if (!(dir.getChildren().isEmpty()))
					throw new IllegalArgumentException(String.format("The directory '%s' is not empty.", path));
			}
		}
		else if(!param.trim().equals("-rf")) //If param is NOT "-rf". The "-rf" param just skips the check above.
			throw new IllegalArgumentException(String.format("The parameter '%s' is not supported by rm.", param));
	
		node.getParent().removeNode(node.getName());
	}
	
	/**
	 * Creates a symbolic link from the SrcPath to the DestPath.
	 * @param SrcPath The path the the link that is to be created.
	 * @param DestPath The path to the destination of the symbolic link. 
	 * @return Returns a the result of the link creation, if it created a cycle it will return the cycle.
	 */
	public String ln(String SrcPath, String DestPath)
	{	
		//Need to separate the new node name from the rest of the path.
		String[] splitPath = SeparatePath(SrcPath);
		
		INodeDirectory srcDir;
		
		try
		{
			srcDir = (INodeDirectory)findNode(splitPath[1], true);
		}
		catch (Exception ex)
		{
			throw new IllegalArgumentException(String.format("The directory '%s' does not exist.", SrcPath));
		}
		
		srcDir.createSymlink(splitPath[0], DestPath);
		
		//Check for cycles.
		LinkedList<String> cycleStack = findCycles(srcDir, new HashSet<String>(), new LinkedList<String>());
		
		if (cycleStack.isEmpty())
			return String.format("Symbolic link created: %s --> %s", SrcPath, DestPath);
		else
		{
			//Remove the newly created symlink as it caused a cycle.
			srcDir.removeNode(splitPath[0]);
			
			StringBuilder sb = new StringBuilder();
			sb.append("Symlink created a cycle: ");
			
			while(cycleStack.isEmpty() == false)
			{
				sb.append(cycleStack.pop());
				
				if (cycleStack.isEmpty() == false)
					sb.append(" -> ");
			}
				
			sb.append("\nAborting symlink creation.");
			return sb.toString();
		}
	}

	/**
	 * Searches all INodes and returns a list of the path to those that match the criteria.
	 * @param criteria The search criteria to match files against.
	 * @return The paths to all the INodes that meets the search criteria.
	 */
	public String find(String criteria)
	{
		StringBuilder sb = new StringBuilder();
		
		//Scan in every directory for INodes meeting the criteria, add results in the StringBuilder.
		findInDir(m_root, criteria, sb, false);
		
		return sb.toString();
	}

	/**
	 * Searches all INodes and returns a list of the path to those that match the criteria.
	 * @param criteria The search criteria to match files against, it can contain the wilcard *.
	 * @return The paths to all the INodes that meets the search criteria.
	 */
	public String findc(String criteria)
	{
		StringBuilder sb = new StringBuilder();
		
		//Scan in every directory for INodes meeting the criteria, add results in the StringBuilder.
		findInDir(m_root, criteria, sb, true);
		
		return sb.toString();
	}

	/**
	 * Check for cycles in our file system and returns the first cycle found if any.
	 * @return Returns the symbolic links that caused the cycle.
	 */
	public String cycles()
	{
		//Search for cycles.
		LinkedList<String> cycleList = findCycles(m_root, new HashSet<String>(), new LinkedList<String>());
				
		if (cycleList.isEmpty())
			return "No cycle found!";	
		else
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Cycle detected: ");
			
			while(cycleList.isEmpty() == false)
			{
				//The thing stored in our cycleList is the name of each node involved in the cycle.
				sb.append(cycleList.pop());
				
				if (cycleList.isEmpty() == false)
					sb.append(" -> ");
			}
				
			return sb.toString();
		}
	}
	
	/**
	 * Scans the file system to check for cycles caused by symbolic links.
	 * @param curDir The directory to start to search for cycles from.
	 * @param visitedSymlinks The symbolic links we have visited.
	 * @param symlinkList An empty LinkedList that will hold the symbolic links we've found so far.
	 * @return A LinkedList containing all the symbolic links that caused the cycle. 
	 */
	private LinkedList<String> findCycles(INodeDirectory curDir, HashSet<String> visitedSymlinks, LinkedList<String> symlinkList)
	{
		//Seach all children in the directory we are currently in to find other directories or symlinks.
		for (INode child: curDir.getChildren())
		{
			//If we've found a directory we need to check this for symlinks.
			if (child instanceof INodeDirectory)
			{
				//Set the result of this cycle check to the list, if we've found anything we need to return.
				symlinkList = findCycles((INodeDirectory) child, visitedSymlinks, symlinkList);
				if (!symlinkList.isEmpty())
					return symlinkList;
			}
			else if (child instanceof INodeSymbolicLink)
			{					
				INodeSymbolicLink symlink = (INodeSymbolicLink) child;
				INode target = findNode(symlink.getTarget(), false);
				
				//Add the source to the list.
				symlinkList.addLast(symlink.getPath());
				
				
				int SymlinksFollowed = 0;
				
				//If the target is a symlink follow it until it's not and add each step to the list.
				while (target instanceof INodeSymbolicLink)
				{
					//Then add any intermediate symlinks.
					symlinkList.addLast(target.getPath());
					target = findNode(((INodeSymbolicLink) target).getTarget(), false);
					SymlinksFollowed++;
				
					//If we've followed symlinks in this loop a thousand times we are probably in a cycle and need to break out.
					if (SymlinksFollowed == 1000)
					{
						for (int i = 0; i < 998; i++)
							symlinkList.pop(); //We need to remove all the links we added during our cycle.
						return symlinkList;
					}
				}
				
				
				//If the symlink now targets a directory we need to search this dir for symlinks.
				//This is the only place a cycle could occur really, after we've followed a symlink to a directory.
				if (target instanceof INodeDirectory)
				{	
					//Then add the destination.
					symlinkList.addLast(target.getPath());
					
					//Add this symlink to the list of visited symlinks.
					if (!visitedSymlinks.add(symlink.getPath()))
						return symlinkList;
					
					symlinkList = findCycles((INodeDirectory) target, visitedSymlinks, symlinkList);
					if (!symlinkList.isEmpty())
						return symlinkList;
				}
			}
		}
		
		//No symlinks found in this directory, return a new LinkedList so it will be empty.
		return new LinkedList<String>();
	}

	/**
	 * Searches all directories that's in the specified directory after any INodes that matches the specified critieria.
	 * @param dir The INodeDirectory to start searching from.
	 * @param criteria The search criteria to match INodes to.
	 * @param sb StringBuilder to store the results to.
	 * @param useWildcards If we should seach with wildcards or not.
	 */
	private void findInDir(INodeDirectory dir, String criteria, StringBuilder sb, boolean useWildcards)
	{
		List<INode> children = dir.getChildren();
		List<INodeDirectory> dirList = new ArrayList<INodeDirectory>(); //We save the directories we find in a separate list so it will be faster to iterate through later.
		
		//First see if any of the children matches the criteria.
		for (INode child : children)
		{
			String name = child.getName();
			
			if (criteria.contains("*") && useWildcards)
			{
				if (StringMethods.matchesWildcard(name, criteria))
				{
					sb.append(child.getPath());
					sb.append("\n");
				}
			}
			else //If criteria contains * but we shouldn't use wildcards, vice verse or neither.
			{
				if (name.equals(criteria))
				{
					sb.append(child.getPath());
					sb.append("\n");
				}
			}	
			
			if (child instanceof INodeDirectory)
				dirList.add((INodeDirectory)child);
		}
		
		//Scan any subdirs in this directory for INodes matching the criteria.
		for (INodeDirectory subDir : dirList)
			findInDir(subDir, criteria, sb, useWildcards);
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
		List<INodeFile> fileList = new ArrayList<INodeFile>();
		int dirTotalSize = 0;
		
		//Loop through all directories first to get their results first in the print out.
		for (INode child : children)
		{
			//Do diskUsage on the subdirs adding their total size to this dirs total size.
			if (child instanceof INodeDirectory)
				dirTotalSize += diskUsage((INodeDirectory) child, sb);
			else if (child instanceof INodeFile)
				fileList.add((INodeFile) child);
		}
		
		//Loop through all files and add them to the print out.
		for (INodeFile file : fileList)
		{
			sb.append(String.format("%s %s \n", file.getSize(), file.getPath()));
			dirTotalSize += file.getSize();
		}
				
		//We've now done all files and directories for this directory. Print total for this dir.
		sb.append(String.format("%s %s \n", dirTotalSize, dir.getPath()));
		
		return dirTotalSize;
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
		sb.append(String.format("Directory of %s\n\n", dir.getPath()));
		
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
			//Format the accessTime and add it to the front of the line.
			accessTime = new Date(i.getAccessTime());
			sb.append(String.format("%s	", formatter.format(accessTime)));
			
			//Check type and write custom stuff.
			if (i instanceof INodeDirectory)
			{
				sb.append("  <DIR>		");
				directories++;
			}
			else if (i instanceof INodeSymbolicLink)
			{
				INodeSymbolicLink symlink = (INodeSymbolicLink) i;
				INode target = findNode(symlink.getTarget(), false);
				
				//Symlinks counts as dirs if they link to a dir, else it counts as a file.
				if (target instanceof INodeDirectory)
				{
					sb.append("  <SYMLINKD>	");
					directories++;
				}
				else
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
			
			//If it's a symlink we print the target in brackets.
			if (i instanceof INodeSymbolicLink)
				sb.append(String.format("%s [%s]\n", i.getName(), ((INodeSymbolicLink) i).getTarget()));
			else
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
		//If we have an empty path then we are referring to the working dir.
		if (path.isEmpty())
			return m_workingDir;
		
		//Assume working dir for now, anything else discovered will change this to what it should be.
		INode cur = m_workingDir;
		
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
		
		//Split the input path into an array so we can process each node individually.
		String[] dirTree = path.split(g_pathDelimiter);
		
		//Search for each node in the path.
		for (int i = 0; i < dirTree.length; i++)
		{
			//If we have a empty array position then just skip that. This happens if path is "" or a delimiter, then split produces an array with an empty position.
			if (dirTree[i].isEmpty())
				continue;
			
			//If we don't have a directory that means that we've found something else with the matching name and the loop is not over, hence we didn't find the correct thing.
			if (!(cur instanceof INodeDirectory))
				return null;
			
			//Search the children of the current directory. cur will always be a directory at this point.
			INode result = ((INodeDirectory)cur).getChild(dirTree[i]);
		
			//Null result. We didn't find the node in the current directory, return null and let calling functions handle this.
			if (result == null)
				return null;
			else
				cur = result;
			
			if (result instanceof INodeSymbolicLink && traverseSymlinks)
			{
				INodeSymbolicLink symlink = (INodeSymbolicLink) result;
				INode target = findNode(symlink.getPath(), false);
				
				//We will break out of this, I promise! We can't point to symlinks in all eternity without having a cycle.
				while(target instanceof INodeSymbolicLink)
				{
					//Follow symlink.
					symlink = (INodeSymbolicLink) target;
					target = findNode(symlink.getTarget(), false);
				}
				
				cur = target;
			}
		}
		
		//We've found all the nodes in the path, it's time to return.
		return cur;
	}
}
