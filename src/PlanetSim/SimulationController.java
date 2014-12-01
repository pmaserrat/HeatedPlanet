package PlanetSim;

public class SimulationController implements Runnable {


	private SimulationSettings settings = null;
	private SimulationEngine engine;
	private SimulationBuffer buffer;
	private SimulationPresentation presenter;
	private Thread presentationThread;
	private Thread engineThread;
	
	volatile boolean running = false;
	
	
	public SimulationController(SimulationSettings settings){
		this.settings = settings;
		this.buffer = new SimulationBuffer(settings.getBufferSize());
		this.engine = new SimulationEngine(buffer,settings);
		this.presenter = new SimulationPresentation(buffer, settings);
	}

	
	public void run() {
		running = true;
		
		if(settings.isProducerThread() && settings.isConsumerThread()){
			
			presentationThread = new Thread(presenter);
			engineThread = new Thread(engine);
			presentationThread.start();
			engineThread.start();
		
			
		} else if (settings.isProducerThread() && !settings.isConsumerThread()) {
			
			engineThread = new Thread(engine);
			engineThread.start();
			while(running){
				try {
					presenter.presentationLoop();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		} else if (!settings.isProducerThread() && settings.isConsumerThread()) {
			
			presentationThread = new Thread(presenter);
			presentationThread.start();
			while(running){
				engine.simulationLoop();
			}
			
		} else {
			while(running){
				try {
					presenter.presentationLoop();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				engine.simulationLoop();
				
			}
		}
		
	}
	
	public void stop(){
		this.running = false;
		engine.stop();
		presenter.stop();
		if( engineThread != null)
			engineThread.interrupt();
		if( presentationThread != null)
			presentationThread.interrupt();
	}
}
