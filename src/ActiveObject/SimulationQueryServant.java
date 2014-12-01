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

	private SimulationSettings settings;
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
		
		
		while(!canExecuteQuery()){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		try{
			
			EarthGrid grid = db.readGrid(settings.getSimulationName());  //<-- place query result here

		    buffer.putGrid(grid);
			
			
		} catch (InterruptedException e) {}
		notify();
	}
	
	public SimulationQueryServant reset(SimulationSettings settings, SimulationBuffer buffer){
					instance = new SimulationQueryServant(settings,buffer);
					return instance;
				}	
	
	public boolean canExecuteQuery(){
		return  ( !(this.buffer.remainingGridCapacity() ==0) && settings.isQueryFound()); 
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
		while(canExecuteQuery()){
			synchronized(this){
				int i = 0;
				while(!isRunning){
					try {
						wait();
					} catch (InterruptedException e) {}
				}
				
				executeQueryEarthGrid();
				
					}
						
				}
	}	

}
