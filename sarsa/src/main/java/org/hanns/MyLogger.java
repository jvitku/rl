package org.hanns;

/**
 * implements very simple unbuffered (file) logger
 */

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;

public class MyLogger {


	// logging
	private File outFile;
	private FileWriter flog;
	private String name;

	private boolean write;
	private boolean useConsole;
	private int level;
	
	public final int WARN = 1;
	public final int DEBUG = 9;
	
	
	/**
	 * creates instance of my simple Logger, the logger uses console by default
	 * @param name - name of file where to write out logs
	 * note: writing to the file must be set using the printToFile method
	 */
	public MyLogger(String name){

		// the lower number, the more important message! 
		this.level = 10;
		this.name = name;
		this.write = true;
		this.useConsole = true;
		
		if(write)
			this.initFile();
	}
	
	private void initFile(){
		// create file, write to it
		outFile = new File(name);
		// try to create new file writer with given name
		try {
			// append-true: append strings to the end of existing file
			flog= new FileWriter(outFile,false);
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void printToFile(boolean c){
		if(c && this.outFile == null){
			this.initFile();
		}
		this.useConsole = !c;
	}

	public void setLevel(int lev){ this.level = lev; }
	public int getLevel(){ return this.level; }
	public void globalWriting(boolean bo){ this.write = bo; }

	private void write(String s, int lev){
		if(write){
			// if the message is important enough
			if(lev<=this.level){
				
				if(useConsole)
					System.out.print(/*"l"+lev+"| "+*/s);
				else
					try{ 
						flog.write(/*"l"+lev+"| "+*/s);
						flog.flush();
					}
					catch(IOException e){ e.printStackTrace(); }
			}
		}
	}
	
	public void p(String cn, int lev, String s){
		this.write("lev: "+lev+" class "+cn+":\t"+s, lev);
	}
	
	public void pl(String cn, int lev, String s){
		this.write("lev: "+lev+" class "+cn+":\t"+s+"\n", lev);
	}
	
	public void p(int LEV, String s){
		this.write(s, LEV);
	}
	
	public void pl(int LEV, String s){
		this.write(s+"\n", LEV);
	}
	
	public void err(String cn, String s){
		this.write("ERROR: class "+cn+":\t"+s+"\n", 0);
	}
	
	public void end(){
		if(write && this.outFile!= null){
			try {
				flog.write("Closing the output stream, bye");
				flog.flush();
				flog.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * print inds (array of integers indexing matrix)
	 * @param LEV
	 * @param inds
	 */
	public void pids(int LEV, int[] inds){
		this.write(this.getIds(inds)+"\n", LEV);
	}
	
	public String getIds(int[] inds){
		String out = "";
		for(int i=0; i<inds.length; i++){
			if(i == 0)
				out="{"+inds[i]+",";
			else if(i==inds.length-1)
				out = out+" "+inds[i]+"}";
			else
				out = out+" "+inds[i]+",";
		}
		return out;
	}
	
}

