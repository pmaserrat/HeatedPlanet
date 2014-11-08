package util;
import SimulationEngine.HeatedEarth;
import util.IHeatedEarth;


public class InitializeEarth {


	private int degreesPrecision;
	private int timeStep;
	private double eccentricity;
	private String errorMessage="";
	private double[][] initialEarth;
	private double[][] initialRatios;
	private double totalArea=0;
	private double averageArea=0;
	private int row;
	private int col;


	public InitializeEarth(int degrees, int time, double eccentricity) {


		if (degrees <= 0) {
			this.errorMessage = "Degrees precision must be an integer greater than 0";
			return;
		}
		if (time <= 0) {
			this.errorMessage = "Time elapsed between iterations must be an integer greater than 0";
			return;
		}

		if (eccentricity < 0) {
			this.errorMessage = "Eccentricity must be geq 0";
			return;
		}
		
		if (eccentricity > 1) {
				this.errorMessage = "Eccentricity must be leq 1";
				return; 
		}

		this.timeStep=time;
		this.degreesPrecision=correctDegrees(degrees);
		this.eccentricity = eccentricity;
		this.initialize();
		return;
		}








	public InitializeEarth(String[] args) {
		
		this((int)recognizeInputs(args, "-d"), (int)recognizeInputs(args, "-t"), recognizeInputs(args, "-e"));
	}


	private static double recognizeInputs(String[] args, String arg) {
		for (int i = 0; i <6 ; i += 2) {
			if (args[i].toLowerCase().equals(arg))
				return Double.parseDouble(args[i + 1]);
		}
		return -1;
	}
	private void initialize(){
	row =	HeatedEarth.EARTH_DEGREES/2/this.degreesPrecision;
	col = HeatedEarth.EARTH_DEGREES/this.degreesPrecision;
	this.initialRatios= new double[row][8];	
	this.initialEarth=new double[row][col];


	/* Note these are only needed once for each row and not for each col 
	 because the values for each cell in a "stripe" will be the same
	 initialEarth[row][0]= Weight of Left Edge
	 initialEarth[row][1]= Weight of Right Edge
	 initialEarth[row][2]= Weight of Bottom Edge
	 initialEarth[row][3]= Weight of Top Edge
	 initialEarth[row][4]= Height
	 initialEarth[row][5]= Perimeter 
	 initialEarth[row][6]= Area
	 initialEarth[row][7]= latitude attenuation
	 * 
	 */


		double l=HeatedEarth.EARTH_CIRCUMFERENCE*(this.degreesPrecision/360.0);
	for (int i=0; i<row;i++ ){
		double angleBottom=-90 +(i*this.degreesPrecision);
		double angleTop=-90 + this.degreesPrecision + (i*this.degreesPrecision);
		angleBottom=Math.toRadians(angleBottom);
		angleTop=Math.toRadians(angleTop);
		double bottom = Math.cos(angleBottom)*l;
		double top= Math.cos(angleTop) *l;
		double triangleLength= Math.abs(bottom-top)/2;
		double height = Math.sqrt(Math.pow(l,2) - Math.pow(triangleLength,2));
		double area = (bottom+top)*height/2;
		double perimeter= bottom+top+l+l;
		double angleAttenuation = Math.toRadians(-90+ (this.degreesPrecision/2) + (i*this.degreesPrecision)); 
		double attenuation = Math.cos(angleAttenuation);


		this.initialRatios[i][0]= l/perimeter;
		this.initialRatios[i][1]=l/perimeter;
		this.initialRatios[i][2]=bottom/perimeter;
		this.initialRatios[i][3]=top/perimeter;
		this.initialRatios[i][4]=height;
		this.initialRatios[i][5]=perimeter;
		this.initialRatios[i][6]=area;
		this.initialRatios[i][7]=attenuation;


		this.totalArea=this.totalArea+area; //keep a running total of the area of all the cells


		for (int j=0 ; j<col; j++){


			this.initialEarth[i][j]=HeatedEarth.START_TEMP;


		}


	}


		this.averageArea=this.totalArea/row; //average area of all cells
	}




	private int correctDegrees(int degrees){


	//if the selected degrees entered does not divide evenly into 180, choose the next lowest evenly divisible number	
	while(180%degrees>0){
		degrees=degrees-1;
		}
		return degrees;


	}
	public double[][] getInitialEarth(){
		return this.initialEarth;
	}


	public double[][] getInitialRatios(){
		return this.initialRatios;
	}
	public int getDegrees() {
		return this.degreesPrecision;
	}


	public int getTimeStep() {
		return this.timeStep;
	}


	public int getRows(){
		return this.row;
	}
	public int getCols(){
		return this.col;
	}
	public double getAverageArea(){
		return this.averageArea;
	}
	
	public double getEccentricity(){
		return this.eccentricity;
	}
	public String getErrorMessage() {
		return this.errorMessage;
	}
}
