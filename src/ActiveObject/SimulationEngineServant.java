package ActiveObject;

import EarthSim.EarthGrid;
import EarthSim.EarthSurface;
import EarthSim.SimulationBuffer;
import EarthSim.SimulationSettings;

public class SimulationEngineServant implements Runnable {

	private SimulationBuffer buffer = null;
	private static SimulationEngineServant instance = null;
	private EarthSurface earth = null;
	private SimulationSettings settings = null;
	private Proxy proxy = null;
	private boolean isRunning = false;
	private Thread t = null;
	private String threadName = "SimulationEngineServant Thread";
	private long lastProduced = System.currentTimeMillis();
	
	protected SimulationEngineServant(SimulationSettings settings, SimulationBuffer buffer){
		this.buffer = buffer;
		this.settings = settings;
		this.earth = new EarthSurface(settings);
	}
	
	public static SimulationEngineServant getInstance(SimulationSettings settings,SimulationBuffer buffer){
		if( instance == null)
			instance = new SimulationEngineServant(settings,buffer);
		return instance;
	}
	
	public void start(){
		if(t == null){
			t = new Thread(this,this.threadName);
			t.start();
		}
	}
	
	public synchronized void executeProduceEarthGrid(){
		
		while(this.buffer.remainingGridCapacity()==0){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		try{
			EarthGrid grid = earth.getEarthGridAfterNTimeSteps(1);
			buffer.putGrid(grid);
			lastProduced = System.currentTimeMillis();
			//System.out.println("Put Grid For Timestep " + grid.getTimestep());
		} catch (InterruptedException e) {}
		notify();
	}
	
	public boolean canExecuteProducer(){
		return this.buffer.remainingGridCapacity() > 0 && (System.currentTimeMillis()-lastProduced) > 1000/settings.getSimulationRate();
	}
	
	public void giveProxy(Proxy proxy){
		this.proxy = proxy;
	}

	@Override
	public void run() {
		isRunning = true;
		long lastProduced = System.currentTimeMillis();
		while(true){
			synchronized(this){
				int i = 0;
				while(!isRunning){
					try {
						wait();
					} catch (InterruptedException e) {}
				}
				if(settings.isProducerThread()){
					if(settings.isMasterConsumer()){
						boolean canProduce = (System.currentTimeMillis()-lastProduced) > 1000.0/settings.getPresentationRate();
						if(canProduce && buffer.remainingGridCapacity() > 0){
							try {
								int j = buffer.takeRequest();
								//System.out.println("MC - Take Request: " + j);
								buffer.putGrid(earth.getEarthGridAfterNTimeSteps(j));
								//System.out.println("Put Grid - " + buffer.gridSize());
								lastProduced = System.currentTimeMillis();
							} catch (InterruptedException e) {}
						} else {
							//System.out.println("Still Running");
						}
					} else if( settings.isMasterProducer()){
						boolean canProduce = (System.currentTimeMillis()-lastProduced) > 1000.0/settings.getSimulationRate();
						if(canProduce){
							try {
								//System.out.println("Consumer Thread Produced");
								lastProduced = System.currentTimeMillis();
								buffer.putGrid(earth.getEarthGridAfterNTimeSteps(1));
							} catch (InterruptedException e) {}
						}
					} else {
						
					}
						
				}
			}
		}
	}
	
	public void runMasterConsumer(){
		int i = 0;
		lastProduced = System.currentTimeMillis();
		//System.out.println("No Consumer Thread - Master Producer");
		while(i < settings.getSimulationIterations()){
			//System.out.println("Curent i - " + i + " " + buffer.gridSize());
			boolean canProduce = (System.currentTimeMillis()-lastProduced) > 1000/settings.getSimulationRate();
			if(canProduce && !buffer.isRequestBufferEmpty() && buffer.remainingGridCapacity() > 0){
				try {
					buffer.putGrid(earth.getEarthGridAfterNTimeSteps(buffer.takeRequest()));
				} catch (InterruptedException e) {}
				i++;
			}
		}
		
	}
	
	public void runMasterProducer(){
		int i = 0;
		lastProduced = System.currentTimeMillis();
		//System.out.println("No Producer Thread - Master Producer");
		
		while(i < settings.getSimulationIterations()){
			
			boolean canProduce = (System.currentTimeMillis()-lastProduced) > 1000/settings.getSimulationRate();
			if(buffer.remainingGridCapacity() > 0 && canProduce){
				//System.out.println(i);
				try {
					buffer.putGrid(earth.getEarthGridAfterNTimeSteps(1));
					i++;
					lastProduced = System.currentTimeMillis();
				} catch (InterruptedException e) {}
			}
			
		}
	}
	
	public void pause(){
		isRunning = false;
	}
	
	public synchronized void resume(){
		isRunning = true;
		notify();
	}
	
	public boolean isRunning(){
		return this.isRunning;
	}
	
	public SimulationEngineServant reset(SimulationSettings settings, SimulationBuffer buffer){
		instance = new SimulationEngineServant(settings,buffer);
		return instance;
	}
	
}
