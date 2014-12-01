package ActiveObject;

import persistence.MainDB;
import EarthSim.SimulationBuffer;
import EarthSim.SimulationSettings;

public class Proxy {
	private Scheduler scheduler = null;
	private SimulationBuffer dataBuffer = null;
	private SimulationSettings settings = null;
	private Thread sThread = null;
	private Thread pThread = null;
	
	private MainDB db = null;
	
	public Proxy(SimulationSettings settings, SimulationBuffer buffer){
		this.dataBuffer = buffer;
		this.settings = settings;
		this.scheduler = new Scheduler(settings,buffer);
		this.db = new MainDB();
		db.createDB();
		db.createTables();
		
		SimulationPresenterServant p = SimulationPresenterServant.getInstance(settings, dataBuffer);
		p = p.reset(settings, buffer);
		
		db = new MainDB();
		
		String results = db.readSimulations(settings.getObliquity(), settings.getEccentricity());
		if(results.equalsIgnoreCase("")){
			settings.setisQueryFound(false);
		}
		else{
			settings.setisQueryFound(true);
			settings.setSimulationName(results);
		}
		
	 if(!settings.isQueryFound())
	  {	 
		SimulationEngineServant s = SimulationEngineServant.getInstance(settings, dataBuffer);
		s = s.reset(settings, buffer);
	  }
	 else
	 {	 
		SimulationQueryServant q = SimulationQueryServant.getInstance(settings, dataBuffer);
	 }	
	}
	
	public void consumeEarthGrid(){
		ConsumeEarthGridMethodRequest c = new ConsumeEarthGridMethodRequest(this.settings,this.dataBuffer);
		scheduler.insertConsume(c);
		
	}
	
	public void produceEarthGrid(){
		ProduceEarthGridMethodRequest p = new ProduceEarthGridMethodRequest(this.settings,this.dataBuffer);
		scheduler.insertProducer(p);
		
	}
	
	public void queryEarthGrid(){
		QueryEarthGridMethodRequest p = new QueryEarthGridMethodRequest

(this.settings,this.dataBuffer);
		scheduler.insertProducer(p);
		
	}
	

	
	public void startScheduler(){
		scheduler.start();
		if(settings.isConsumerThread() || settings.isProducerThread()){
			if(settings.isMasterConsumer() || settings.isMasterProducer()){
				SimulationPresenterServant p = SimulationPresenterServant.getInstance(settings, dataBuffer);
				SimulationEngineServant s = SimulationEngineServant.getInstance(settings, dataBuffer);
				s.giveProxy(this);
				p.giveProxy(this);
				
				if(settings.isProducerThread() && settings.isConsumerThread()){
					p.start();
					s.start();
				} else if(settings.isProducerThread() && !settings.isConsumerThread()){
					s.start();
					if(settings.isMasterProducer()){
						System.out.println("No Consumer Thread - Master Producer");
						p.runMasterProducer();
					} else {
						System.out.println("No Consumer Thread - Master Consumer");
						p.runMasterConsumer();
					}
				} else if(!settings.isProducerThread() && settings.isConsumerThread()){
					p.start();
					if(settings.isMasterProducer()){
						System.out.println("No Producer Thread - Master Producer");
						s.runMasterProducer();
					} else {
						System.out.println("No Prodcuer Thread - Master Consumer");
						s.runMasterConsumer();
					}
				} else {
					
				}
			}
		} else if(!settings.isMasterController()){
			for(int i = 0; i < settings.getSimulationIterations(); i++){
				produceEarthGrid();
				consumeEarthGrid();
				queryEarthGrid();
			}
		}
	}
	
	public void pause(){
		scheduler.pause();
		SimulationPresenterServant p = SimulationPresenterServant.getInstance(settings, dataBuffer);
		p.pause();
		SimulationEngineServant s = SimulationEngineServant.getInstance(settings, dataBuffer);
		s.pause();
	}
	
	public void resume(){
		scheduler.resume();
		SimulationPresenterServant p = SimulationPresenterServant.getInstance(settings, dataBuffer);
		p.resume();
		SimulationEngineServant s = SimulationEngineServant.getInstance(settings, dataBuffer);
		s.resume();
	}

	
	
}

