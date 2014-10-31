package EarthSim;

public abstract class MethodRequest implements Runnable {
	SimulationBuffer buffer = null;
	SimulationSettings settings = null;
	public MethodRequest(SimulationBuffer buffer, SimulationSettings settings) 
	{
		this.buffer = buffer;
		this.settings = settings;
	};
	public abstract boolean canRun() ;
	public abstract void run();
	public abstract void stop();
}
