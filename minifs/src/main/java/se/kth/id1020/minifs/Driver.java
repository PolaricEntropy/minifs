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

	public static void main(String[] args)
	{
		FileSystem fs = new MiniFs();

		while (bShouldExit == false)
		{
			StdOut.print(">> ");
			StdOut.println(processCmd(fs, StdIn.readLine(), args));
		}
	}

	public static String processCmdFile(FileSystem fs, String param, String[] args)
	{
		String filepath = "";
	
		if (param.equals("0"))
			filepath = args[0];
		else
			filepath = param;
		
		In input = new In(new File(filepath));
		StringBuilder sb = new StringBuilder();

		if (input.exists())
		{
			while (!input.isEmpty())
			{
				String line = input.readLine().trim();
				sb.append(">> " + line).append(NEW_LINE);
				String result = processCmd(fs, line, args);
				
				if (result != null)
					sb.append(result).append(NEW_LINE);
			}
			
			input.close();
		}
		
		return sb.toString();
	}

	public static String processCmd(FileSystem fs, String line, String[] args)
	{
		String[] comp = line.split(" ", 2);
		String cmd = comp[0].trim().toLowerCase();
		String params = "";
		String result = "";

		//If we have params then lowercase them.
		if (comp.length > 1)
			params = comp[1].toLowerCase();

		try
		{	  
			if (cmd.equals("mkdir"))
				fs.mkdir(processOneArg(params));
			else if (cmd.equals("touch"))
				fs.touch(processOneArg(params));
			else if (cmd.equals("append"))
			{
				String[] subComp = processTwoArgs(params);
				fs.append(subComp[0], subComp[1]);
			}
			else if (cmd.equals("ls"))
			{
				//Check for params in general.
				params = processOneArg(params);

				String[] subComp = params.split(" ", 2);
				String paramLS = subComp[0].trim();
				String path;

				//If second argument is empty, send an empty path to the function.
				if (subComp.length <= 1)
					path = "";
				else
					path = subComp[1].trim();

				result = fs.ls(path, paramLS);
			}
			else if (cmd.equals("rm"))
			{  
				//Check for params in general.
				params = processOneArg(params);

				String[] subComp = params.split(" ", 2);
				String paramRM = subComp[0].trim();
				String path;

				//If second argument is empty that means that the first argument was the was the path.
				if (subComp.length <= 1)
				{
					path = paramRM;
					paramRM = "";
				}
				else
					path = subComp[1].trim();

				fs.rm(path, paramRM);
			}
			else if (cmd.equals("du"))
				result = fs.du(processOneArg(params));
			else if (cmd.equals("cat"))
				result = fs.cat(processOneArg(params));
			else if (cmd.equals("pwd"))
				result = fs.pwd();
			else if (cmd.equals("cd"))
				fs.cd(processOneArg(params));
			else if (cmd.equals("cd.") || cmd.equals("cd.."))
				fs.cd(cmd.substring(2));
			else if (cmd.equals("ver"))
				result = fs.ver();
			else if (cmd.equals("exit"))
				bShouldExit = true;  
			else if (cmd.equals("ln"))
			{
				String[] subComp = processTwoArgs(params);
				fs.ln(subComp[0], subComp[1]);
			}
			else if (cmd.equals("find"))
				result = fs.find(processOneArg(params));
			else if (cmd.equals("findc"))
				result = fs.findc(processOneArg(params));
			else if (cmd.equals("cycles"))
				result = fs.cycles();
			else if (cmd.equals("exec"))
				result = processCmdFile(fs, processOneArg(params), args);
			else
				result = cmd + ": command not found";
		}
		catch (FirstArgumentMissingException ex)
		{
			result = String.format("Syntax error. First argument of %s can not be empty.", cmd);
		}
		catch (SecondArgumentMissingException ex)
		{
			result = String.format("Syntax error. Second argument of %s can not be empty.", cmd);
		}
		catch (IllegalArgumentException ex)
		{
			result = ex.getMessage();
		}

		return result;
	}

	private static String processOneArg(String param)
	{
		if (param.isEmpty())
			throw new FirstArgumentMissingException();

		return param.trim();
	}

	private static String[] processTwoArgs(String params)
	{		
		if (params.isEmpty())
			throw new FirstArgumentMissingException();

		String[] subComp = params.split(" ", 2);

		if (subComp.length <= 1)
			throw new SecondArgumentMissingException();

		subComp[0] = subComp[0].trim();
		subComp[1] = subComp[1].trim();

		return subComp;
	}
}
