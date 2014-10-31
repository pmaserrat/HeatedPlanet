package ActiveObject;

import EarthSim.SimulationBuffer;
import EarthSim.SimulationSettings;

public class Scheduler implements Runnable{
	
	private ActivationQueue aq = null;
	private SimulationBuffer buffer = null;
	private SimulationSettings settings = null;
	private boolean isRunning = false;
	private Thread t = null;
	private String threadName = "Schedule Thread";
	private long lastProduced = 0;
	private long lastConsumed = 0;
	
	public Scheduler(SimulationSettings settings, SimulationBuffer buffer){
		this.buffer = buffer;
		this.settings = settings;
		this.aq = new ActivationQueue();
	}
	
	public void insertProducer(MethodRequest request){
		aq.insertProduceRequest(request);

	}
	
	public void insertConsume(MethodRequest request){
		aq.insertConsumeRequest(request);

	}
	
	public void start(){
		if(t == null){
			t = new Thread(this,this.threadName);
			t.start();
		}
	}

	@Override
	public void run() {
		isRunning = true;
		while(true) {
			synchronized(this){
				while(!isRunning){
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			boolean canConsume = aq.canTakeConsumeRequest();
			//canConsume = canConsume && (System.currentTimeMillis()-lastConsumed) > 1000.0/settings.getSimulationRate();
			//System.out.println(System.currentTimeMillis()-lastConsumed);
			if(canConsume){
				//if(settings.isConsumerThread())
				//	new Thread(aq.takeConsumeRequest()).run();
				//else
				//System.out.println("Consume + " + 1000/24);
				aq.takeConsumeRequest().execute();
				lastConsumed = System.currentTimeMillis();
			}
			boolean canProduce = aq.canTakeProduceRequest();
			//canProduce = canProduce && (System.currentTimeMillis()-lastProduced) > 1000.0/settings.getPresentationRate();
			if(canProduce){
				//if(settings.isProducerThread())
				//	new Thread(aq.takeProduceRequest()).run();
				//else
				aq.takeProduceRequest().execute();	
				lastProduced = System.currentTimeMillis();
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
	
	
}
