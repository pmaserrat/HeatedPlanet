package PlanetSim;
import gui.widget.earth.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimulationProxy {

	Scheduler scheduler = null;
	Runnable engine = null;
	Runnable presentation = null;

	Thread engineThread = null; 
	Thread presThread = null;
	SimulationSettings settings = null;
	public SimulationProxy(SimulationBuffer buffer, SimulationSettings settings, EarthPanel panel)
	{
		scheduler = new Scheduler(buffer, settings);
		this.settings = settings;
//		scheduler.insert(new SimulationEngine(buffer, settings));
//		scheduler.insert(new SimulationPresentation(buffer, settings, panel));
		engine = new SimulationEngine(buffer, settings);
		presentation = new SimulationPresentation(buffer, settings, panel);
	}
	public void start()
	{
		produceData();
		consumeData();
	}
	public void produceData()
	{
		engineThread = new Thread(engine);
		engineThread.start();
	}
	public void consumeData()
	{
		presThread = new Thread(presentation);
		presThread.start();	
	}
	
	public void pause()
	{
		pauseProducer();
		pauseConsumer();
	}
	@SuppressWarnings("deprecation")
	public void pauseProducer()
	{
		engineThread.stop();
	}
	@SuppressWarnings("deprecation")
	public void pauseConsumer()
	{
		presThread.stop();
	}
	public class Scheduler
	{
		private BlockingQueue<MethodRequest> dispatchQueue = null; //acts as the ActivationQueue
		
		private SimulationBuffer buffer = null; SimulationSettings settings = null;
		public Scheduler(SimulationBuffer buffer, SimulationSettings settings)
		{
			this.buffer = buffer;
			this.settings = settings;
			dispatchQueue = new LinkedBlockingQueue<MethodRequest>(2);  //one for the producer and one for the consumer
		}
		public void insert(MethodRequest mr)
		{
			dispatchQueue.add(mr);
		}
		public void dispatch()
		{
			try {
				while (!dispatchQueue.isEmpty())
				{
					MethodRequest mr  = dispatchQueue.take();
					mr.run();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
