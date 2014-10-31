package EarthSim;
import EarthSim.SimulationSettings;
import EarthSim.EarthGrid;

public class EarthSurface {

    private SimulationSettings settings = null;
    private double[][] Grid;
    private double[][] OldGrid;
    public double[][] SurfaceArea;
    
    private double dLat = 0;
    private double dLong = 0;
    private int latGridSize = 0;
    private int longGridSize = 0;
    private int currentIteration = 0;
    
    private double radius = 6.371e6;
    private double surfaceArea = 4*Math.PI*radius*radius;

    private double kTransfer = 1e-11;
    private double kAbsorption = .1;
    private double kRadiation = 1e-4;
    
    private boolean isStable = false;
    private double[] averageTemperature = null;
    private double[] lowAvgTemp = null;
    private double[] highAvgTemp = null;
    private boolean newExtreme = false;
    private double changeThreshold = 0.01;


    public EarthSurface(SimulationSettings settings){
        this.settings = settings;
        constructGrids();
    }
    
    private void constructGrids(){
        this.latGridSize = settings.getLatGridSize();
        this.longGridSize = settings.getLongGridSize();
        

        double dAngle = settings.getGridSpacing()*Math.PI/180;
        this.dLat = dAngle;
        this.dLong = dAngle;
        
        this.kTransfer = 0.1*Math.pow(radius*dAngle*Math.sin(dAngle/2),2)/settings.getTimeStep();
       
        Grid = new double[this.latGridSize][this.longGridSize];
        OldGrid = new double[this.latGridSize][this.longGridSize];
        SurfaceArea = new double[this.latGridSize][this.longGridSize];
        averageTemperature = new double[this.latGridSize];
        lowAvgTemp = new double[this.latGridSize];
        highAvgTemp = new double[this.latGridSize];
        for(int i = 0; i < this.latGridSize; i++){
        	averageTemperature[i] = 288;
        	highAvgTemp[i] = 288;
        	lowAvgTemp[i] = 288;
            for(int j =0; j < this.longGridSize; j++){
            	Grid[i][j] = 288;
            	OldGrid[i][j] = 288;
            	//if(i > this.latGridSize/2)
            		//Grid[i][j] = 0;
            	
                SurfaceArea[i][j] = radius*radius*dAngle*dAngle*Math.sin((i+0.5)*dAngle);
            }
        }
    }
    
    public EarthGrid getEarthGridAfterNTimeSteps(int n){
    	for(int i=0; i < 1; i++){
    		oneTimeStep();
    	}
    	return getEarthGrid();
    }


    public void oneTimeStep(){
        this.UpdateOldGrid();
        double[][] conduction = getTempChangeFromConduction();
        double[][] absorption = getTempChangeFromAbsorption();
        double[][] emission = getTempChangeFromEmission();
        
        double totalAbsorption = 0.0;
        double totalEmission = 0.0;
        for(int i = 0; i < this.latGridSize; i++){
            for(int j = 0; j < this.longGridSize; j++){
               totalAbsorption += absorption[i][j];
               totalEmission += emission[i][j];
            }
        }
                
        double averageTemp = 0;
        newExtreme = false;
        double[] tempAvg = new double[latGridSize];
        for(int i = 0; i < this.latGridSize; i++){
        	tempAvg[i] = 0;
            for(int j = 0; j < this.longGridSize; j++){
            	 Grid[i][j] = OldGrid[i][j] + conduction[i][j];
            	 Grid[i][j] += totalEmission*absorption[i][j]/totalAbsorption;
            	 Grid[i][j] -= emission[i][j];
            	 averageTemp += Grid[i][j];
            	 tempAvg[i] += Grid[i][j];
            }
            tempAvg[i] /= longGridSize;
            //System.out.print(tempAvg[i] + ", ");
            if(tempAvg[i] < lowAvgTemp[i]){
            	lowAvgTemp[i] = tempAvg[i];
            	newExtreme = true;
            }
            
            if(tempAvg[i] > highAvgTemp[i]){
            	highAvgTemp[i] = tempAvg[i];
            	newExtreme = true;
            }
            //System.out.print("("+lowAvgTemp[i]+","+highAvgTemp[i]+"), ");
            
            	
        }
        isStable = !newExtreme;
        //System.out.println("");
        //System.out.println("Iteration-"+currentIteration+" 1Avg Temp: " + averageTemp/(this.latGridSize*this.longGridSize));
        currentIteration++;
    }
    
    
    private double[][] getTempChangeFromConduction(){
    	double[][] tempChange = new double[this.latGridSize][this.longGridSize];
    	double dAngle = this.dLat;
    	int dt = this.settings.getTimeStep();
    	
    	for(int i = 0; i < this.latGridSize; i++){
            for(int j = 0; j < this.longGridSize; j++){
                if(i == 0){
                	int jNew = (j-this.latGridSize/2)%this.latGridSize;
                	if(jNew < 0)
                		jNew += this.longGridSize;
                	tempChange[i][j] = dt*kTransfer*(OldGrid[i+1][j]-OldGrid[i][j])/Math.pow(radius*dAngle,2);
                } else if(i == this.latGridSize-1){
                	int jNew = (j-this.latGridSize/2)%this.latGridSize;
                	if(jNew < 0)
                		jNew += this.longGridSize;
                	tempChange[i][j] = dt*kTransfer*(OldGrid[i-1][j]-OldGrid[i][j])/Math.pow(radius*dAngle,2);
                } else {
                	tempChange[i][j] = dt*kTransfer*(OldGrid[i+1][j]+OldGrid[i-1][j]-2*OldGrid[i][j])/Math.pow(radius*dAngle,2);
                }
                int jLow = (j-this.latGridSize/2)%this.latGridSize;
                int jHigh = (j+1)%this.longGridSize;
                if(jHigh < 0)
                	jHigh += this.longGridSize;
            	if(jLow < 0)
            		jLow += this.longGridSize;
            	
                tempChange[i][j] += dt*kTransfer*(OldGrid[i][jHigh]+OldGrid[i][jLow]-2*OldGrid[i][j])/Math.pow(radius*dAngle*Math.sin((i-0.5)*dAngle),2);
                
            }
        }
    	return tempChange;
    }
    
    private double[][] getTempChangeFromAbsorption(){
    	double[][] tempChange = new double[this.latGridSize][this.longGridSize];
    	double dAngle = this.dLat;
    	int dt = this.settings.getTimeStep();
    	for(int i = 0; i < this.latGridSize; i++){
            for(int j = 0; j < this.longGridSize; j++){
            	 double currentAngle = (currentIteration*settings.getTimeStep()*settings.getAngularVelocity()+(j+0.5)*this.dLong*180/Math.PI)%360;
            	 if (currentAngle < 0)
            		 currentAngle += 360;
                 if(currentAngle >= 270 || currentAngle <=90){
                 	 tempChange[i][j]= dt*kAbsorption*SurfaceArea[i][j]*Math.sin((i+0.5)*dAngle)*Math.cos(currentAngle*Math.PI/180);//*this.latGridSize*this.longGridSize*/this.surfaceArea;
                 } else {
                	 tempChange[i][j] = 0.0;
                 }
            }
            
    	}
    	return tempChange;
    }
    
    private double[][] getTempChangeFromEmission(){
    	double[][] tempChange = new double[this.latGridSize][this.longGridSize];
    	double dAngle = this.dLat;
    	int dt = this.settings.getTimeStep();
    	for(int i = 0; i < this.latGridSize; i++){
            for(int j = 0; j < this.longGridSize; j++){
            	tempChange[i][j] = dt*kRadiation*OldGrid[i][j];
            }
          
    	}
    	return tempChange;
    }

    public void updateSettings(SimulationSettings simSet)
    {
    	settings = simSet;
    	constructGrids();
    }
    
    public EarthGrid getEarthGrid(){
    	int timestep = this.currentIteration*this.settings.getTimeStep();
    	double sunLongitude = (currentIteration*settings.getTimeStep()*settings.getAngularVelocity())%360;
    	if(sunLongitude > 180.0)
    		sunLongitude -= 360.0;
    	double[][] tempGrid = new double[latGridSize][longGridSize];
    	for(int i = 0; i < latGridSize; i++){
    		for(int j = 0; j < longGridSize; j++){
    			if(j < longGridSize/2)
    				tempGrid[i][j] = Grid[i][j+longGridSize/2];
    			else
    				tempGrid[i][j] = Grid[i][j-longGridSize/2];
    		}
    	}
        EarthGrid grid = new EarthGrid(timestep,tempGrid,sunLongitude,isStable,settings);
        //printGrid(Grid, 1.0);
    	return grid;
    }

    public int getCurrentIteration(){
        return this.currentIteration;
    }

    private void UpdateOldGrid(){
        double[][] temp = OldGrid;
        OldGrid = Grid;
        Grid = temp;
    }

    private void printGrid(double[][] grid2,double scale){
		for(int i = 0; i < grid2.length; i++){
			for(int j = 0; j < grid2[0].length; j++){
				System.out.print(grid2[i][j]*scale + " ");
			}
			System.out.println("");
			System.out.println("");
		}
    }
}

