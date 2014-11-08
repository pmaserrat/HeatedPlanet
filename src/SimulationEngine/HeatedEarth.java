package SimulationEngine;

import util.IHeatedEarth;

import util.InitializeEarth;


public class HeatedEarth implements IHeatedEarth {
 
	private double[][] oldEarth;
	private double[][] sunHeat;
	private double[][] coolingLoss;
	private double [][] neighborsHeat;
	private double[][] newEarth;
	private double averageArea;
	private double averageTemp=0;
	private double totalTemp=0;
	private double[] rowAverage;


	private double[][] sizeRatios;
	private double totalSunHeating=0;
	private InitializeEarth initialEarth;
	private long current_iteration = 0;
	private int degreesPrecision;
	private int timeStep;
	private int sunIn;
	private int row;
	private int col;
	private Runtime runtime;
	private long startTime;
	private long stopTime;
	private double heatPerMinute;


	public HeatedEarth(InitializeEarth earth) {
		

		this.initialEarth = earth;
		this.degreesPrecision=earth.getDegrees();
		this.averageArea=earth.getAverageArea();
		this.timeStep=earth.getTimeStep();
		this.sunIn=HeatedEarth.SUN_IN;
		this.heatPerMinute=HeatedEarth.HEAT_PER_MINUTE;
		this.row=earth.getRows();
		this.col=earth.getCols();
		this.sizeRatios=earth.getInitialRatios();
		this.oldEarth=earth.getInitialEarth();
		this.neighborsHeat=new double[row][col];
		this.sunHeat=new double[row][col];
		this.coolingLoss=new double[row][col];
		this.newEarth=new double[row][col];
		this.rowAverage= new double[row];
		//initialize();
		calculate();
	}


	private void swap() {
		double oldTotal=0;
		double newTotal=0;
		for (int i =0;i<row;i++){
			for (int j=0; j<col;j++){
				oldTotal=oldTotal+this.oldEarth[i][j];
				newTotal=newTotal+this.newEarth[i][j];
				this.oldEarth[i][j]= this.newEarth[i][j];
			}
		}








	}


	private int getShift(){
		double elapsedTime = this.timeStep * this.current_iteration;
		double degreesRotated = elapsedTime/4;


		//once a rotation has been completed, reset to 0 to make sure the number of columns rotated is correct. 
		while (degreesRotated>=360){
			degreesRotated=degreesRotated-360;
		}
		int colsRotated = (int) (degreesRotated / this.degreesPrecision);
		return colsRotated;
	}


	private double getAdditionalDegrees(){
		double elapsedTime = this.timeStep * this.current_iteration;
		double degreesRotated = elapsedTime/4;
		while (degreesRotated>=360){
			degreesRotated=degreesRotated-360;
		}
		while (degreesRotated>=this.degreesPrecision){
			degreesRotated=degreesRotated-this.degreesPrecision;
		}
		return degreesRotated;
	}


	private void heatIn(){


		double totalAbsorbedCoefficent;
		double sunCoefficient=0;
		double degreesRotated= this.getAdditionalDegrees();
		double cellDegrees;
		double attenuationTime;


		int colsRotated=getShift();


		for (int i=0; i<row;i++){
			for (int j=0;j<col;j++){
				this.sunHeat[i][j]=0;
				//set all to 0 to begin. will set actual values for cells facing the sun next.
			}


		}




		for (int i=0; i<row;i++){
			double attenuationLatitude = this.sizeRatios[i][7];
			//the values of j select the appropriate cells that are facing the sun. note there are no cells partially facing the sun
			for (int j=0+colsRotated-(this.row/2);j<(this.row/2)+colsRotated;j++){
				int k=j;
				if(j<0){
					k=j+this.col;
				}
				if(j>=this.col){
					k=j-this.col;
				}
				int ratioIndex=j+(this.row/2)-colsRotated;					
				//most accurately reflects the "average" degree angle based on time. also allows to continue modeling rotation until the next cell is in view.
				cellDegrees= -90 - degreesRotated/2 + (ratioIndex*this.degreesPrecision) + (this.degreesPrecision/2);
				cellDegrees=Math.toRadians(cellDegrees);
				attenuationTime = Math.cos(cellDegrees);


				this.sunHeat[i][k]= this.timeStep * this.heatPerMinute  * attenuationLatitude * attenuationTime ;
			}
		}
			totalSunHeating=0;	
		//if (current_iteration==0){
			for (int i=0;i<row;i++){
				for (int j=0;j<col;j++){
					totalSunHeating=totalSunHeating+sunHeat[i][j];
				}
			}


		//}






	}


	private double averageTemp(){
		averageTemp=0;
		totalTemp=totalTemp();
		averageTemp=totalTemp/row/col;
		return averageTemp;
	}
	private double totalTemp(){


		totalTemp=0;
		for (int i=0; i<row;i++){
			for (int j=0;j<col;j++){
				totalTemp=totalTemp + this.oldEarth[i][j];
			}
		}
		return totalTemp;
	} 


	private void heatOut(){


		double currentTotal=totalTemp(); 
		double tempOut;




		//first calculate the sum of all areaRatio*tempRatio this will be needed to determine how much heat each cell sheds
		double totalCooling=0;
		for (int i=0; i<row;i++){


			for (int j=0;j<col;j++){
				tempOut=0;
				tempOut=oldEarth[i][j]*this.totalSunHeating/this.totalTemp;
				this.coolingLoss[i][j]=tempOut;
				totalCooling=totalCooling+tempOut;
			}


		}








	}


	private void heatShared(){


		for (int i=0; i<row;i++){


			double weightLeft=this.sizeRatios[i][0];
			double weightRight=this.sizeRatios[i][1];
			double weightBottom=this.sizeRatios[i][2];
			double weightTop=this.sizeRatios[i][3];


			for (int j=0;j<col;j++){
				int top=i+1;
				int bottom=i-1;
				int left = j-1;
				int right = j+1;


				//if there is no row above, use this row. top length will be 0 so impact on weighted temp
				if (top==row){
					top=row-1;
				}
				//if there is no row below, use this row. bottom length will be 0 so impact on weighted temp
				if (bottom<0){
					bottom=0;
				}
				//if there is no col to the left, use the last col (wraps around)
				if (left<0){
					left=col-1;
				}
				//if there is no col to the right, use the first col (wraps around)
				if (right==col){
					right=0;
				}


				this.neighborsHeat[i][j]= this.oldEarth[i][left]*weightLeft + this.oldEarth[i][right]*weightRight+this.oldEarth[top][j]*weightTop + this.oldEarth[bottom][j]*weightBottom;




			}
		}




	}
	private void newTemps(){
	//the new temp of each cell is the sum of the heat transfer from neighbors, heat infrom sun, and heat out from cooling
		double averageNewTemp=0;
		double totalNewTemp=0;
		double adjustmentRatio;
		double newTemp=0;
		for (int i =0;i<this.row;i++){
			for (int j=0; j<this.col;j++){
				newTemp=this.sunHeat[i][j]+this.neighborsHeat[i][j]-this.coolingLoss[i][j];
				//no temperature can be below 0.01 degrees Kelvin
				if(newTemp<=0){
					newTemp=0.01;
				}
				this.newEarth[i][j]=newTemp; 


				totalNewTemp=totalNewTemp+this.newEarth[i][j];
			}
		}
			averageNewTemp=totalNewTemp/row/col;
			double oldAverage=averageTemp();
			adjustmentRatio=oldAverage/averageNewTemp;
		//temps seem to be climbing slightly due to rounding error...this will force average temp to be stable
		for (int i =0;i<this.row;i++){
			for (int j=0; j<this.col;j++){
				this.newEarth[i][j]=adjustmentRatio*this.newEarth[i][j]; 
			}
		}






	}


	private boolean checkStability(){
		boolean isStable=false;
		for (int i=0;i<row;i++){
			double rowavg=0;
			for (int j=0;j<col;j++){
				rowavg=rowavg+oldEarth[i][j];
			}
			rowavg=rowavg/col;
			System.out.print ("Average for row #");
			System.out.print(i);
			System.out.print(" is:");
			System.out.println(rowavg);
			if (Math.abs(rowAverage[i]-rowavg)>.01) {
				isStable=true;
			}
			rowAverage[i]=rowavg;
		}
		if (isStable==false){
			System.out.print ("Rows stabilized after ");
			System.out.print(current_iteration);
			System.out.println (" iterations.");
		}


		return isStable;
	}


	private void calculate() {


		//need to get center for attenuation...need to track minutes elapsed


		for (int i=0;i<row;i++){
			rowAverage[i]=averageTemp();
		}


		boolean keepGoing=true;
		while(keepGoing){
		heatIn();
		heatOut();
		heatShared();
		newTemps();
		swap();
		this.current_iteration+=1; //increment the number of iterations
		System.out.print("Iteration#:");
		System.out.println(current_iteration);


		keepGoing=checkStability();


		if (this.current_iteration==500){
			System.out.print("5000");
		}
		if (this.current_iteration==1250){
			System.out.print("12500");
		}
		if (this.current_iteration==2500){
			System.out.print("25000");
		}


		if (this.current_iteration>5000){
			keepGoing=false;
		}
		}
	}




	private void initialize() {
		}






	public double[][] getTemps() {
		return this.newEarth;
	}


	@Override
	public void displayResults() {
		System.out.print( "done");


	}


}
