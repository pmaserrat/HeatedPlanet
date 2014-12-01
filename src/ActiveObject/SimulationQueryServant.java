package ActiveObject;

import java.util.Calendar;
import java.util.Date;

import persistence.MainDB;
import EarthSim.EarthGrid;
import EarthSim.EarthSurface;
import EarthSim.SimulationBuffer;
import EarthSim.SimulationSettings;

public class SimulationQueryServant implements Runnable {
	
	private SimulationBuffer buffer = null;
	private static SimulationQueryServant instance = null;

	private SimulationSettings settings = null;
	private Proxy proxy = null;
	private boolean isRunning = false;
	private Thread t = null;
	private String threadName = "SimulationQueryServant Thread";
	//private long lastProduced = System.currentTimeMillis();
	private MainDB db;
    
	SimulationQueryServant(SimulationSettings settings, SimulationBuffer buffer){
		this.buffer = buffer;
		this.settings = settings;
		db = new MainDB();
	}
	
	public SimulationQueryServant() {
		// TODO Auto-generated constructor stub
	}
	
	public synchronized void executeQueryEarthGrid(){
		
		
		while(this.buffer.remainingGridCapacity()==0){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		try{
			EarthGrid grid = this.grid;

		    buffer.putGrid(grid);
			
			
		} catch (InterruptedException e) {}
		notify();
	}
	
	
	
	public boolean canExecuteQuery(){
		return  !(this.buffer.isGridBufferEmpty() && simSet.; 
	}
	
	public void giveProxy(Proxy proxy){
		this.proxy = proxy;
	}
	
	public static SimulationQueryServant getInstance(SimulationSettings settings,SimulationBuffer buffer){
		if( instance == null)
			instance = new SimulationQueryServant(settings,buffer);
		return instance;
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

}
