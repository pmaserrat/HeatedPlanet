package EarthSimTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import EarthSim.EarthGrid;
import EarthSim.EarthSurface;
import EarthSim.SimulationSettings;

public class RunSimulation {

	public static void main(String[] args) {
		SimulationSettings settings = new SimulationSettings();
		settings.setGridSpacing(30);
		settings.setTimeStep(144);
		EarthSurface earth = new EarthSurface(settings);
		EarthGrid Grid = earth.getEarthGrid();
		double[][] grid = Grid.getTempGrid();
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[0].length; j++){
				System.out.print(grid[i][j] + " ");
			}
			System.out.println("");
			System.out.println("");
		}
		
		for(int i = 0; i < grid.length; i++){
			for(int j = 0; j < grid[0].length; j++){
				System.out.print(earth.SurfaceArea[i][j] + " ");
			}
			System.out.println("");
			System.out.println("");
		}
		boolean again = true;
		while(again){
			System.out.print("Continue: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String response = null;
			try{
				response = br.readLine();
			} catch (IOException ioe){
				
				
			}
			
			if(response.equals("n")){
				again = false;
			} else {
				int n = Integer.parseInt(response);
				for(int i = 0; i < n; i++){
					earth.oneTimeStep();
				}
				EarthGrid earthGrid = earth.getEarthGrid();
				System.out.println("");
				System.out.println(earthGrid.getLatitude(3) + " , " + earthGrid.getLongitude(settings.getLongGridSize()/2+1) + " , " + settings.getLongGridSize()/2);
				System.out.println("");
				grid = earthGrid.getTempGrid();
				System.out.println("");
				for(int i = 0; i < grid.length; i++){
					for(int j = 0; j < grid[0].length; j++){
						System.out.print(grid[i][j] + " ");
					}
					System.out.println("");
					System.out.println("");
					
				}
				System.out.println(earth.getEarthGrid().getSunLongitude());
			}
		}
	}

}
