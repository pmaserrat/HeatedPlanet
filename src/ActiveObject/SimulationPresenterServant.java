package ActiveObject;

import gui.widget.earth.EarthPanel;
import gui.widget.earth.TempEarthGrid;
import EarthSim.Demo;
import EarthSim.EarthGrid;
import EarthSim.SimulationBuffer;
import EarthSim.SimulationSettings;

public class SimulationPresenterServant implements Runnable {

	private SimulationBuffer buffer = null;
	private static SimulationPresenterServant instance = null;
	private SimulationSettings settings = null;
	private Proxy proxy = null;
	private boolean isRunning = false;
	private Demo gui = null;
	private Thread t = null;
	private String threadName = "SimulationEngineServant Thread";
	private long lastConsumed = System.currentTimeMillis();
	
	protected SimulationPresenterServant(SimulationSettings settings, SimulationBuffer buffer){
		this.buffer = buffer;
		this.settings = settings;
		
	}
	
	public static SimulationPresenterServant getInstance(SimulationSettings settings,SimulationBuffer buffer){
		if( instance == null)
			instance = new SimulationPresenterServant(settings,buffer);
		return instance;
	}
	
	public synchronized void executeConsumeEarthGrid(){
		try{
			EarthGrid grid = this.buffer.takeGrid();
			if(gui != null){
				gui.updateDisplay(grid);
			}
			lastConsumed = System.currentTimeMillis();
			//System.out.println("Took Grid For Timestep " + grid.getTimestep());
		} catch (InterruptedException e) {}
		notifyAll();
	}
	
	public boolean canExecuteConsume(){
		return !this.buffer.isGridBufferEmpty() && (System.currentTimeMillis()-lastConsumed) > 1000/settings.getPresentationRate();
	}
	
	public void giveProxy(Proxy proxy){
		this.proxy = proxy;
	}
	
	public void giveGUI(EarthSim.Demo demoGUI){
		this.gui = demoGUI;
	}
	
	public void start(){
		if(t == null){
			t = new Thread(this,this.threadName);
			t.start();
		}
	}
	
	@Override
	public void run() {
		long lastConsumed = System.currentTimeMillis();
		isRunning = true;
		while(true){
			synchronized(this){
				while(!isRunning){
					try {
						wait();
					} catch (InterruptedException e) {}
				}
				if(settings.isConsumerThread()){
					//System.out.println("Producer Thread Running");
					if(settings.isMasterConsumer()){
						//System.out.println("Master Consumer");
						boolean canConsume = (System.currentTimeMillis()-lastConsumed) > 1000.0/settings.getPresentationRate();
						if(canConsume){
							try{
								if(buffer.remainingRequestCapacity() >0){
									buffer.putRequest(1);
									//System.out.println("Put Request - " + buffer.remainingRequestCapacity());
								} 
								if(!buffer.isGridBufferEmpty()){
									lastConsumed = System.currentTimeMillis();
									EarthGrid grid = this.buffer.takeGrid();
									if(gui != null){
										gui.updateDisplay(grid);
									}
									//System.out.println("MP - Took Grid For Timestep " + grid.getTimestep());
								}
							} catch (InterruptedException e){}
						}
					} else if( settings.isMasterProducer()){
						//System.out.println("Master Producer");
						boolean canConsume = (System.currentTimeMillis()-lastConsumed) > 1000.0/settings.getPresentationRate();
						if(canConsume){
							try {
								lastConsumed = System.currentTimeMillis();
								EarthGrid grid = this.buffer.takeGrid();
								if(gui != null){
									gui.updateDisplay(grid);
								}
								//System.out.println("MP - Took Grid For Timestep " + grid.getTimestep());
							} catch (InterruptedException e) {}
						}
					} else {
						
					}
						
				}
			}
		}
	}
	
	public void runMasterProducer(){
		int i = 0;
		lastConsumed = System.currentTimeMillis();
		//System.out.println("No Consumer Thread - Master Producer");
		while(i < settings.getSimulationIterations()){
			//System.out.println("Curent i - " + i + " " + buffer.gridSize());
			boolean canConsume = (System.currentTimeMillis()-lastConsumed) > 1000/settings.getPresentationRate();
			if(!buffer.isGridBufferEmpty() && canConsume){
				lastConsumed = System.currentTimeMillis();
				EarthGrid grid;
				try {
					grid = this.buffer.takeGrid();
					if(gui != null){
						//System.out.println("not null");
						gui.updateDisplay(grid);
					}
					i++;
				} catch (InterruptedException e) {}
				
			}
		}
		
	}
	
	public void runMasterConsumer(){
		int i = 0;
		lastConsumed = System.currentTimeMillis();
		//System.out.println("No Consumer Thread - Master Consumer");
		
		while(i < settings.getSimulationIterations()){
			boolean canConsume = (System.currentTimeMillis()-lastConsumed) > 1000/settings.getPresentationRate();
			if(!buffer.isGridBufferEmpty() && canConsume){
				EarthGrid grid;
				 
				try {
					grid = buffer.takeGrid();
					if(gui != null){
						gui.updateDisplay(grid);
					}
					i++;
					lastConsumed = System.currentTimeMillis();
				} catch (InterruptedException e) {}
			}
			if(buffer.remainingRequestCapacity() > 0){
				try {
					//System.out.println("Put Request");
					buffer.putRequest(1);
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
	
	public SimulationPresenterServant reset(SimulationSettings settings, SimulationBuffer buffer){
		instance = new SimulationPresenterServant(settings, buffer);
		return instance;
	}
}
