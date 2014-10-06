/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1020.minifs;

/**
 *
 */
public interface FileSystem {

	public String pathDelimiter = "/";

	public void mkdir(String path);

	public void touch(String path);

	public void append(String path, String data);

	public String ls(String path, String param);

	public String du(String path);

	public String cat(String path);

	public String pwd();

	public void cd(String path);

	public String ver();

	public void rm(String path, String param);
}
