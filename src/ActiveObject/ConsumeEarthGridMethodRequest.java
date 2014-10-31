package ActiveObject;

import EarthSim.SimulationBuffer;
import EarthSim.SimulationSettings;

public class ConsumeEarthGridMethodRequest extends MethodRequest{

	private SimulationBuffer buffer = null;
	private SimulationSettings settings = null;
	
	public ConsumeEarthGridMethodRequest(SimulationSettings settings, SimulationBuffer buffer){
		this.buffer = buffer;
		this.settings = settings;
	}
	
	
	public synchronized void execute() {
		SimulationPresenterServant s = SimulationPresenterServant.getInstance(settings, buffer);
		while(!s.canExecuteConsume()){
			try{
				wait();
			} catch (InterruptedException e){}
		}
		notify();
		s.executeConsumeEarthGrid();
	}
	
	public boolean canExecute(){
		SimulationPresenterServant s = SimulationPresenterServant.getInstance(settings, buffer);
		return s.canExecuteConsume();
	}
	
	@Override
	public void run() {
		execute();
	}

}
