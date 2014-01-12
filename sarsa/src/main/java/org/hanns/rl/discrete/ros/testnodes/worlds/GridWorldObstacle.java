package org.hanns.rl.discrete.ros.testnodes.worlds;


/**
 * The same as {@link GridWorld}, but here, the value of reinforcement of -1 represents
 * an obstacle. 
 * 
 * @author Jaroslav Vitku
 */
public class GridWorldObstacle extends GridWorld{

	public static final String OBST = "XXX";
	public static final String FREE = " . ";


	/**
	 * Visualize the map, axes are as usual, x is horizontal increasing to the right,
	 * y is vertical increasing towards up. 
	 * @param map array of reward values
	 * @return String representing the map
	 */
	public static String vis(float[][] map){
		String line = "------------------------------------\n";
		for(int i=map[0].length-1; i>=0; i--){
			for(int j=0; j<map.length; j++){

				if(map[j][i] == -1){
					line = line + "\t "+OBST;
				}else if(map[j][i] == 0){
					line = line + "\t "+FREE;
				}else{
					line = line + "\t "+map[j][i];
				}
			}
			line = line+"\n";
		}
		return line+"\n-------------------------------------";
	}

	public static int[] makeStep(int sx, int sy, int action, int[] current){
		System.err.println("GridWorld ERROR: use the method makeStep()"
				+ "where the map is passed as argument!");
		return null;
	}

	/**
	 * Place obstacle to the given coordinate
	 * @param coords where to place the obstacle
	 * @param map environment map
	 */
	public static void placeObst(int[] coords, float[][] map){
		map[coords[0]][coords[1]] = -1;
	}

	/**
	 * Draw a filled obstacle of a given rectangle shape  
	 * @param xrange range defining starting and ending coordinate of the obstacle on the x axis
	 * @param yrange range defining starting and ending coordinate on the y axis
	 * @param map map to draw the obstacle to
	 */
	public static void drawObstacle(int[] xrange, int[] yrange,float[][]map){
		if(xrange[0]>xrange[1] || yrange[0]>yrange[1]){
			System.err.println("drawObstacle: incorrect ranges, ignoring obstacle");
			return;
		}
		if(xrange[0]>=map.length || yrange[0]>=map[0].length){
			System.err.println("drawObstacle: obstacle cannot fit into the map, ignoring obstacle");
			return;
		}
		int sx = xrange[0];					// start x
		int distx = sx+xrange[1]-xrange[0];	// how many tales to draw

		int sy = yrange[0];
		int disty = sy+yrange[1]-yrange[0];
		
		for(int i=sx; i<=distx; i++){
			for(int j=sy; j<=disty; j++){
				map[i][j] = -1;
			}	
		}
	}
	
	/**
	 * Move in the environment surrounded by walls, also -1 means obstacle. 
	 * 
	 * @param map 2D array of reward values, where -1 means an obstacle 
	 * @param action 0-left, 1-right, 2-up, 3-down
	 * @param current current position on the map
	 * @return new position
	 */
	public static int[] makeStep(float[][] map, int action, int[] current){
		int[] coords = current.clone();

		if(action==0){ 					// left
			if(current[0] > 0){
				if(map[current[0]-1][current[1]] != -1){ // if obstacle, position remains the same
					coords[0] = current[0]-1;
				}
			}
		}else if(action==1){ 			// right
			if(current[0] < map.length-1){
				if(map[current[0]+1][current[1]] != -1)
					coords[0] = current[0]+1;
			}
		}else if(action==2){ 			// up 
			if(current[1] < map[0].length-1){
				if(map[current[0]][current[1]+1] != -1)
					coords[1] = current[1]+1;
			}
		}else if(action==3){ 			// down
			if(current[1] > 0){
				if(map[current[0]][current[1]-1] != -1)
					coords[1] = current[1]-1;
			}
		}else if(action==-1){ 			// NOOP
			return coords;
		}else{
			System.err.println("unrecognized action! "+action);
		}
		return coords;
	}

}


