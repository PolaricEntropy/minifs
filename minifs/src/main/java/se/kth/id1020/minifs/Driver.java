/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1020.minifs;

import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.StdOut;

import java.io.File;

/**
 *
 */
public class Driver {

  static String NEW_LINE = System.getProperty("line.separator");
  static boolean bShouldExit = false;
  
  public static void main(String[] args) {
    FileSystem fs = new MiniFs();
    
    while (bShouldExit == false)
    	StdOut.println(processCmd(fs, StdIn.readLine()));	

    //String fileResult = processCmdFile(fs, args[0]);
    //StdOut.println(fileResult);
  }

  public static String processCmdFile(FileSystem fs, String path) {
    In input = new In(new File(path));
    StringBuilder builder = new StringBuilder();

    while (!input.isEmpty()) {
      String line = input.readLine().trim();
      builder.append(">> " + line).append(NEW_LINE);
      String result = processCmd(fs, line);
      if (result != null) {
        builder.append(result).append(NEW_LINE);
      }
    }
    return builder.toString();
  }
  
  public static String processCmd(FileSystem fs, String line)
  {
	  String[] comp = line.split(" ", 2);
	  String cmd = comp[0].trim().toLowerCase();
	  
	  String result = "";
	  
	  try
	  {	  
		  if (cmd.equals("mkdir")){
			  if (comp.length <= 1)
				  throw new IllegalArgumentException("Syntax error. First argument of mkdir can not be empty.");
			  
			  fs.mkdir(comp[1].trim());
		  } else if (cmd.equals("touch")) {
			  if (comp.length <= 1)
				  throw new IllegalArgumentException("Syntax error. First argument of touch can not be empty.");
			  
			  fs.touch(comp[1].trim());
		  } else if (cmd.equals("append")) {
			  if (comp.length <= 1)
				  throw new IllegalArgumentException("Syntax error. First argument of append can not be empty.");
			  
			  String[] subComp = comp[1].split(" ", 2);
			  
			  if (subComp.length <= 1)
				  throw new IllegalArgumentException("Syntax error. Second argument of append can not be empty.");
			  
			  fs.append(subComp[0].trim(), subComp[1].trim());
		  } else if (cmd.equals("ls")) {
			  
			  if (comp.length <= 1)
				  throw new IllegalArgumentException("Syntax error. First argument of ls can not be empty.");
			  
			  String[] subComp = comp[1].split(" ", 2);
			  String param = subComp[0].trim().toLowerCase();
			  String path;
			  
			  //If second argument is empty, send an empty path to the function.
			  if (subComp.length <= 1)
				  path = "";
			  else
				  path = subComp[1].trim();
			  
			  if (param.equals("-t")) {
				  result = fs.lsByTime(path);
			  } else if (param.equals("-s")) {
				  result = fs.lsByName(path);
			  } else {
				  result = param + ": parameter not recognized for ls";
			  }
		  } else if (cmd.equals("du")) {
			  if (comp.length <= 1)
				  throw new IllegalArgumentException("Syntax error. First argument of du can not be empty.");
			  
			  result = fs.du(comp[1].trim());
		  } else if (cmd.equals("cat")) {
			  if (comp.length <= 1)
				  throw new IllegalArgumentException("Syntax error. First argument of cat can not be empty.");
			  
			  result = fs.cat(comp[1].trim());
		  } else if (cmd.equals("pwd")) {
			  result = fs.pwd();
		  } else if (cmd.equals("cd")) {
			  if (comp.length <= 1)
				  throw new IllegalArgumentException("Syntax error. First argument of cd can not be empty.");
			  
			  fs.cd(comp[1].trim());
		  } else if (cmd.equals("ver")) {
			  result = fs.ver();
		  } else if (cmd.equals("exit")) {
			  bShouldExit = true;
		  } else {
			  result = cmd + ": command not found";
		  }
	}
	catch (IllegalArgumentException ex)
	{
    	result = ex.getMessage();
    }
    
    
	return result;
  }
  
}
