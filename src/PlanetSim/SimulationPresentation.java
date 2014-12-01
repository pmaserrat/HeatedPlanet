package PlanetSim;
import gui.widget.earth.*;

public class SimulationPresentation extends MethodRequest {
	boolean master = false;
	volatile boolean isRunning = false;
	EarthPanel panel  = null;
	
	public SimulationPresentation(SimulationBuffer buffer, SimulationSettings settings){
		super(buffer, settings);
		this.master = settings.isMasterConsumer();
	}
	public SimulationPresentation(SimulationBuffer buffer, SimulationSettings settings, EarthPanel panel)
	{
		this(buffer, settings);
		this.panel = panel; 
	}
	public void presentationLoop() throws InterruptedException{
		isRunning = true;
		if( master ){
			while(isRunning){
				emptyEarthGridQueue();
				if(buffer.gridSize() == 0)
					fillRequestQueue();
			}
		} else {
			while(isRunning){
				emptyEarthGridQueue();
				isRunning = false;
			}
		}
		if (buffer.isGridBufferEmpty())
			fillRequestQueue();
		else
			isRunning = true;
	}
	
	private void useData(EarthGrid grid){
		System.out.println("Used Grid - " + grid.getTimestep());
		//panel.draw(grid).  whatever you do to get this data on the earth
	}
	
	private void emptyEarthGridQueue() throws InterruptedException{
		if (buffer.isGridBufferEmpty())
		{
			//System.out.println("Buffer is empty");
			fillRequestQueue();
		}
		else
		{
			try {
				useData(buffer.takeGrid());
			} catch (InterruptedException e) {
				System.out.println("InterruptedException e");
			}
		}
	}
	
	private void fillRequestQueue() throws InterruptedException{
		boolean offer = false;
		while(buffer.requestSize() < settings.getBufferSize() && !offer){
			offer = buffer.offerRequest(1);
			if(offer)
				System.out.println("Added Request of Size 1");
			else
				System.out.println("Request Ignored");
		}
	}
	
	@Override
	public void run() {
		try {
			presentationLoop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void stop()
	{
		isRunning = false;
	}

	@Override
	public boolean canRun() {
		// TODO Auto-generated method stub
		return false;
	}
}
