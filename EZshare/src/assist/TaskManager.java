package assist;
import java.util.Timer;
import java.util.TimerTask;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskManager {
	private Timer timer = new Timer();
	private ConnectionTracker tracker;
	private int exchangeT;
	//private int period = exchangeT * 1000; //1 minute while testing,10 minute period converted to seconds
	
	public TaskManager(ConnectionTracker tracker, int exchangeT) {
		this.tracker = tracker;
		this.exchangeT = exchangeT;
	}
	
	public void startTasks() {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		Date date = new Date();
		System.out.println("Starting tasks " + df.format(date));
		timer.schedule(new PeriodicTask(), exchangeT * 1000);
		//start 10 minutes after calling startTasks
	}
	
	private class PeriodicTask extends TimerTask {
		@Override
		public void run() {
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
			Date date = new Date();
			System.out.println("Task period = " + exchangeT * 1000);
			System.out.println("Running tasks " + df.format(date));
			tracker.cleanTracker();
			//Exchange command send task can be here as well
			timer.schedule(new PeriodicTask(), exchangeT *1000);
			//reschedule task again
		}
	}
	
}
