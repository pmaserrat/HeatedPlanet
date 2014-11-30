package EarthSim;

public class SimulationSettings {

    private int gridSpacing = 15; //number of degrees
    private int timeStep = 1; //number of minutes
    private double angularVelocity = 0.25; //Starts with timestep 1 minute
    private int bufferSize = 1000;
    private boolean masterConsumer = false;
	private boolean masterProducer = true;
	private boolean masterController = false;
	private boolean consumerThread = true;
	private boolean producerThread = true;
	private int presentationRate = 60;
	private int simulationRate = 60;
	private int simulationIterations = 1000000;
	//private double eccentricity = .0167; //default value
	//private double obliquity = 23.44; //default value
	private double eccentricity;// = 0.0; //default value
	private double obliquity;// = 0.0; //default value
	private String name;
    public SimulationSettings(){

    }
    
    public void setSimulationName(String name){
    	this.name = name;
    }
    
    public String getSimulationName(){
    	return this.name;
    }
    public int getSimulationIterations(){
    	return this.simulationIterations;
    }
    
    public void setSimulationIterations(int i){
    	this.simulationIterations = i;
    }

    public void setGridSpacing(int spacing){
        if(spacing >= 1 && spacing <= 180){
            while(180%spacing !=0){
                spacing -= 1;
            }
            this.gridSpacing = spacing;
        }
    }
    
    public void setSimulationRate(int rate){
    	if(rate >= 1 && rate <=60){
    		this.simulationRate = rate;
    	}
    }
    
    public int getSimulationRate(){
    	return this.simulationRate;
    }
    
    public void setPresentationRate(int rate){
    	if(rate >= 1 && rate <=60){
    		this.presentationRate = rate;
    	}
    }
    
    public int getPresentationRate(){
    	return this.presentationRate;
    }
    

    public int getGridSpacing(){
        return this.gridSpacing;
    }
    
    public int getLatGridSize(){
    	return 180/this.gridSpacing;
    }
    
    public int getLongGridSize(){
    	return 360/this.gridSpacing;
    }

    public void setTimeStep(int step){
        if(step >= 1 && step <= 1440){
            this.timeStep = step;
        }
    }

    public int getTimeStep(){
        return this.timeStep;
    }
    
    public double getAngularVelocity(){
    	return this.angularVelocity;
    }
    
    public void setAngularVelocity(double angularVelocity){
    	this.angularVelocity = angularVelocity;
    }

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public boolean isMasterProducer() {
		return masterProducer;
	}

	public void setMasterProducer(boolean masterProducer) {
		this.masterProducer = masterProducer;
		if( this.masterProducer){
			this.masterConsumer = false;
			this.masterController = false;
		} else {
			if(this.masterController)
				this.masterConsumer = false;
			else if (!this.masterConsumer)
				this.masterController = true;
				
		}
	}

	public boolean isProducerThread() {
		return producerThread;
	}

	public void setProducerThread(boolean producerThread) {
		this.producerThread = producerThread;
	}

	public boolean isMasterController() {
		return masterController;
	}

	public void setMasterController(boolean masterController) {
		this.masterController = masterController;
		if(this.masterController){
			this.masterConsumer = false;
			this.masterProducer = false;
		} else {
			if(this.masterProducer)
				this.masterConsumer = false;
			else if(!this.masterConsumer)
				this.masterController = true;
		}
	}

	public boolean isMasterConsumer() {
		return masterConsumer;
	}

	public void setMasterConsumer(boolean masterConsumer) {
		this.masterConsumer = masterConsumer;
		if(this.masterConsumer){
			this.masterController = false;
			this.masterProducer = false;
		} else {
			if(this.masterController)
				this.masterProducer = false;
			else if(!this.masterProducer)
				this.masterConsumer = true;
		}
	}

	public boolean isConsumerThread() {
		return consumerThread;
	}

	public void setConsumerThread(boolean consumerThread) {
		this.consumerThread = consumerThread;
	}

	public double getEccentricity() {
		return eccentricity;
	}

	public void setEccentricity(double eccentricity) {
		if (eccentricity >= 0 && eccentricity <1 ){
		this.eccentricity = eccentricity; }
	}

	public double getObliquity() {
		
		return obliquity; 
	}

	public void setObliquity(double obliquity) {
		if (obliquity >=-180 && obliquity <=180){
			this.obliquity = obliquity;}
	}

}
