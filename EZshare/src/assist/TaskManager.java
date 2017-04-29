package assist;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dao.ExchangeResource;
import dao.ServerInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class TaskManager {
	private Timer timer = new Timer();
	private ConnectionTracker tracker;
	private int exchangeT;
	private ServerRecords serverRecords;
	//private int period = exchangeT * 1000; //1 minute while testing,10 minute period converted to seconds
	
	public TaskManager(ConnectionTracker tracker, int exchangeT, ServerRecords servers) {
		this.tracker = tracker;
		this.exchangeT = exchangeT;
		this.serverRecords = servers;
	}
	
	public void startTasks() {
		/*DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		Date date = new Date();
		System.out.println("Starting tasks " + df.format(date));*/
		timer.schedule(new PeriodicTask(), exchangeT * 1000);
		//start 10 minutes after calling startTasks
	}
	
	private class PeriodicTask extends TimerTask {
		@Override
		public void run() {
			/*DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
			Date date = new Date();
			System.out.println("Task period = " + exchangeT * 1000);
			System.out.println("Running tasks " + df.format(date));*/
			sendExchange();
			//exchange command
			tracker.cleanTracker();
			timer.schedule(new PeriodicTask(), exchangeT *1000);
			//reschedule task again
		}
	}
	
	private void sendExchange() {
		Random r = new Random(System.currentTimeMillis());
		ArrayList<ServerInfo> exchangeServers = serverRecords.getServers();
		if(!exchangeServers.isEmpty()) {
			ServerInfo exchangeServer = exchangeServers.get(r.nextInt(exchangeServers.size()));
			ExchangeResource resource = new ExchangeResource(exchangeServers);
			Socket s = null;
			try{
				s = new Socket(exchangeServer.getHostname(), exchangeServer.getPort());
				System.out.println("Connection Established");
				
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				Gson gson = new GsonBuilder().serializeNulls().create();

				out.writeUTF(resource.toJson(gson)); // UTF is a string encoding see Sn. 4.4
				
				out.flush();
				
			}catch(IOException e){
				System.out.println(e.getMessage());
				serverRecords.rmServer(exchangeServer);
			}finally {
				if (s != null)
					try {
						s.close();
					} catch (IOException e) {
						System.out.println("close:" + e.getMessage());
					}
			}
		}
	}
	
}
