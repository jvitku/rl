package org.hanns.rl.discrete.ros;

public class Topic {
	
	public static String baseIn = "in";
	public static String baseConf = "conf";
	public static String baseOut = "out";
	
	
	public static String inNo(int no){ 		return baseIn+no; }
	public static String confNo(int no){ 	return baseConf+no; }
	public static String outNo(int no){ 	return baseOut+no; }

}
