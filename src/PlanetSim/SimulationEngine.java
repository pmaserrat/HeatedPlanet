package PlanetSim;

import java.util.concurrent.BlockingQueue;

public class SimulationEngine extends MethodRequest  {

	private boolean master = false;
	private EarthSurface earth = null;
	private boolean isRunning = false;
	
	public SimulationEngine(SimulationBuffer buffer, SimulationSettings settings){
		super(buffer, settings);
		this.master = settings.isMasterProducer();
		earth = new EarthSurface(settings);
	}
	
	public void stop(){
		isRunning=false;
	}
	
	public void simulationLoop(){
		isRunning = true;
		
		if( master ){
			while(isRunning)
			{
				fillEarthGridQueue();
				if(buffer.gridSize() == settings.getBufferSize())
					isRunning = false;
			}
		} else {
			while(isRunning)
			{
				emptyRequestQueue();
				fillEarthGridQueue();
				if(buffer.requestSize() == 0)
					isRunning = false;
			}
			
		}
		if (buffer.isGridBufferEmpty())
			isRunning = true;
	}
	private void emptyRequestQueue(){
		EarthGrid grid = null;
		while(buffer.gridSize() < settings.getBufferSize() && buffer.requestSize() > 0){
			Integer timeStep = null;
			try {
				timeStep = buffer.takeRequest();
				System.out.println("Took Request of Size - " + timeStep);
			} catch (InterruptedException e) {}
			grid = earth.getEarthGridAfterNTimeSteps(timeStep);
			boolean offer = buffer.offerGrid(grid);
			if(offer){
				System.out.println("emptyRequestQueue Added Grid At " + grid.getTimestep());
			} else {
				System.out.println("emptyRequestQueue Grid not added At " + grid.getTimestep());
			}
		}
	}

	private void fillEarthGridQueue(){
		EarthGrid grid = null;
		while(buffer.gridSize() < settings.getBufferSize()){
			if(grid == null)
				grid = earth.getEarthGridAfterNTimeSteps(1);
			boolean offer = buffer.offerGrid(grid);
			if(offer){
				System.out.println("fillEarthGridQueue Added Grid At " + grid.getTimestep());
				grid = null;
			} else {
				System.out.println("fillEarthGridQueue Grid not added At " + grid.getTimestep());
			}
		}
	}
	
	@Override
	public void run() {
		simulationLoop();
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return false;
	}

}
