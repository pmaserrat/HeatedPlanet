package EarthSim;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimulationBuffer {

	private BlockingQueue<EarthGrid> grids = null;
	private BlockingQueue<Integer> requests = null;
	boolean fullBuffer = false;
	
	public SimulationBuffer(int bufferSize){
		grids = new LinkedBlockingQueue<EarthGrid>(bufferSize);
		requests = new LinkedBlockingQueue<Integer>(bufferSize);
	}
	
	public int remainingGridCapacity(){
		return grids.remainingCapacity();
	}
	
	public int remainingRequestCapacity(){
		return requests.remainingCapacity();
	}
	
	public synchronized void putGrid(EarthGrid grid) throws InterruptedException{
		while(grids.remainingCapacity() == 0){
			wait();
		}
		grids.put(grid);
		notify();
		if(grids.remainingCapacity()==0)
			fullBuffer = true;
	}
	
	public boolean offerGrid(EarthGrid grid){
		return grids.offer(grid);
	}
	
	public synchronized EarthGrid takeGrid() throws InterruptedException{
		notify();
		while(grids.isEmpty()){
			wait();
		}
		return grids.take();
	}
	
	public int gridSize(){
		return grids.size();
	}

	public boolean isGridBufferEmpty()
	{
		return grids.isEmpty();
	}
	
	public boolean isRequestBufferEmpty(){
		return requests.isEmpty();
	}

	public synchronized void putRequest(Integer iter) throws InterruptedException{
		while(requests.remainingCapacity() == 0){
			wait();
		}
		
		requests.put(iter);
		notify();
	}
	
	public boolean offerRequest(Integer iter){
		return requests.offer(iter);
	}
	
	public synchronized Integer takeRequest() throws InterruptedException{
		notify();
		while(requests.isEmpty()){
			wait();
		}
		return requests.take();
	}
	
	
	public int requestSize(){
		return requests.size();
	}
}
