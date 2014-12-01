package ActiveObject;

import EarthSim.SimulationBuffer;
import EarthSim.SimulationSettings;

public class QueryEarthGridMethodRequest extends MethodRequest {
	private SimulationBuffer buffer = null;
	private SimulationSettings settings = null;
	
	public QueryEarthGridMethodRequest(SimulationSettings settings, SimulationBuffer buffer){
		this.buffer = buffer;
		this.settings = settings;
	}
	
	
	public synchronized void execute() {
		SimulationQueryServant s = SimulationQueryServant.getInstance(settings, buffer);
		while(!s.canExecuteQuery()){
			try{
				System.out.println("QueryEarthGridMethodRequest - waiting");
				wait();
			} catch (InterruptedException e){}
		}
		notifyAll();
		s.executeQueryEarthGrid();
		
	}
	
	public boolean canExecute(){
		SimulationQueryServant s = SimulationQueryServant.getInstance(settings, buffer);
		return s.canExecuteQuery();
	}

	@Override
	public void run() {
		execute();
	}
}


