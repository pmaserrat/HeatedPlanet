package ActiveObject;

public abstract class MethodRequest implements Runnable {

	public abstract void execute();
	public abstract boolean canExecute();
	
}
