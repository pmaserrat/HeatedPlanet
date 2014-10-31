package EarthSim;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EarthGrid {
	private double[][] tempGrid;
	private int timestep;;
	private double sunLongitude;
	private SimulationSettings settings;
	private boolean isStable = false;
	
	public EarthGrid(){
		tempGrid = null;
		timestep = -1;
	}
	
	public EarthGrid(int timestep,double[][] grid, double sunLongitude, boolean isStable, SimulationSettings settings){
		this.tempGrid = grid;
		this.timestep = timestep;
		this.setSunLongitude(sunLongitude);
		this.settings = settings;
		this.isStable = isStable;
	}
	
	public void setTimestep(int timestep){
		this.timestep = timestep;
	}
	
	public boolean isStable(){
		return this.isStable;
	}
	
	public int getTimestep(){
		return this.timestep;
	}
	
	public void setTempGrid(double[][] grid){
		this.tempGrid = grid;
	}
	
	public double[][] getTempGrid(){
		return this.tempGrid;
	}
	
	public double[][] getInvertedTempGrid(){
		
		
		return reverse(tempGrid);
	}
	
    private  double[][] reverse(double[][] arrayToReverse){
 	   List listOfType = Arrays.asList(arrayToReverse);
 	   Collections.reverse(listOfType);
 	   return (double[][])listOfType.toArray();
 	}
	
	public int getTempGridHeight(){
		return this.tempGrid.length;
	}
	
	public int getTempGridWidth(){
		return this.tempGrid[0].length;
	}
	
	public double getAverageTemperature(){
		double avgTemp = 0.0;
		for(int i = 0; i < tempGrid.length; i++){
			for(int j = 0; j < tempGrid[0].length; j++){
				avgTemp += tempGrid[i][j];
			}
		}
		return avgTemp/(getTempGridHeight()*getTempGridWidth());
	}
	public String toString()
	{
		return "Step " + timestep + " with an average temp of " + getAverageTemperature(); 
	}

	public double getSunLongitude() {
		return sunLongitude;
	}

	public void setSunLongitude(double sunLongitude) {
		this.sunLongitude = sunLongitude;
	}
	
	public Double getLatitude(int i){
		if( i >= 0 & i < settings.getLatGridSize())
			return new Double(i*settings.getGridSpacing()-90);
		
		return null;
		
	}
	
	public int getLatGridSize(){
		return settings.getLatGridSize();
	}
	
	public int getLongGridSize(){
		return settings.getLongGridSize();
	}
	
	public Double getLongitude(int j){
		if(j >= 0 && j < settings.getLongGridSize()){
			double angle = j*settings.getGridSpacing()-180;
			if (angle == -180)
				angle = 180;
			return new Double(angle);
		}
		return null;
	}
}
