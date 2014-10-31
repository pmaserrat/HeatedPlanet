package ActiveObject;

import EarthSim.SimulationBuffer;
import EarthSim.SimulationSettings;

public class ProduceEarthGridMethodRequest extends MethodRequest{

	private SimulationBuffer buffer = null;
	private SimulationSettings settings = null;
	
	public ProduceEarthGridMethodRequest(SimulationSettings settings, SimulationBuffer buffer){
		this.buffer = buffer;
		this.settings = settings;
	}
	
	
	public synchronized void execute() {
		SimulationEngineServant s = SimulationEngineServant.getInstance(settings, buffer);
		while(!s.canExecuteProducer()){
			try{
				System.out.println("ProduceEarthGridMethodRequest - waiting");
				wait();
			} catch (InterruptedException e){}
		}
		notifyAll();
		s.executeProduceEarthGrid();
		
	}
	
	public boolean canExecute(){
		SimulationEngineServant s = SimulationEngineServant.getInstance(settings, buffer);
		return s.canExecuteProducer();
	}

	@Override
	public void run() {
		execute();
	}
}
