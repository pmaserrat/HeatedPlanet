package ActiveObject;

import java.util.concurrent.LinkedBlockingQueue;

import EarthSim.SimulationBuffer;
import EarthSim.SimulationSettings;

public class ActivationQueue {

	LinkedBlockingQueue<MethodRequest> consumeQueue = new LinkedBlockingQueue<MethodRequest>();
	LinkedBlockingQueue<MethodRequest> produceQueue = new LinkedBlockingQueue<MethodRequest>();
	
	public ActivationQueue() {}
	
	public boolean canTakeProduceRequest(){
		if(!this.produceQueue.isEmpty()){
			MethodRequest p = this.produceQueue.peek();
			if(p == null)
				return false;
			else
				return p.canExecute();
		}
			
		return false;
	}
	
	public boolean canTakeConsumeRequest(){
		if(!this.consumeQueue.isEmpty()){
			MethodRequest c = this.consumeQueue.peek();
			if(c == null)
				return false;
			else
				return c.canExecute();
		}
			
		return false;
	}
	
	public synchronized MethodRequest takeProduceRequest(){
		MethodRequest request = null;
		while(this.produceQueue.isEmpty()){
			try{
				wait();
			} catch (InterruptedException e){}
		}
		
		try{
			request = this.produceQueue.take();
		} catch (InterruptedException e){}
		notifyAll();
		return request;
	}
	
	public synchronized void insertProduceRequest(MethodRequest request){
		try {
			this.produceQueue.put(request);
		} catch (InterruptedException e) {}
		notifyAll();
	}
	
	public synchronized MethodRequest takeConsumeRequest(){
		MethodRequest request = null;
		
		while(this.consumeQueue.isEmpty()){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		try {
			request = this.consumeQueue.take();
		} catch (InterruptedException e) {}
		notifyAll();
		
		return request;
	}
	
	public synchronized void insertConsumeRequest(MethodRequest request){
		try {
			this.consumeQueue.put(request);
		} catch (InterruptedException e) {}
		notifyAll();
	}
	
}
