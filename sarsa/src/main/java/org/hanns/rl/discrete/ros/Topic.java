package org.hanns.rl.discrete.ros;

public class Topic {
	
	private static String baseIn = "in";
	private static String baseConf = "conf";
	private static String baseOut = "out";
	
	
	public static String inNo(int no){ 		return baseIn+no; }
	public static String confNo(int no){ 	return baseConf+no; }
	public static String outNo(int no){ 	return baseOut+no; }

}
