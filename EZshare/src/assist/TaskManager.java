package assist;
import java.util.Timer;
import java.util.TimerTask;

public class TaskManager {
	private Timer timer = new Timer();
	private ConnectionTracker tracker;
	private static int period = 10 * 60; //1 minute while testing,10 minute period converted to seconds
	
	public TaskManager(ConnectionTracker tracker) {
		this.tracker = tracker;
	}
	
	public void startTasks() {
		timer.schedule(new PeriodicTask(), period *1000);
		//start 10 minutes after calling startTasks
	}
	
	private class PeriodicTask extends TimerTask {
		@Override
		public void run() {
			tracker.cleanTracker();
			//Exchange command send task can be here as well
			timer.schedule(new PeriodicTask(), period * 1000);
			//reschedule task again
		}
	}
	
}
