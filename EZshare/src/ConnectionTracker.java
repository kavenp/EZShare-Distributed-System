import java.util.HashMap;
import java.util.Map;

/* A class to track connections that try to connect to server
 * will reject all connections that try to connect within interval
 * the approach used is a read/write only when checked to lessen the amount of
 * calls to this class. Therefore a clean method is defined to be called
 * in a set amount of time to clean up expired trackers to lessen storage usage
 */
public class ConnectionTracker {
	private int interval;
	private HashMap<String, Long> cache = new HashMap<String, Long>();
	
	//create tracker with ConnectionIntervalLimit
	public ConnectionTracker (int interval) {
		this.interval = interval;
	}
	
	public void addConnection(String ip) {
		//times 1000 interval since interval is in seconds
		long liveUntil = updateTime();
		cache.put(ip, liveUntil);
	}
	
	public boolean checkConnection(String ip) {
		if (cache.containsKey(ip)) {
			//already has record of this connection stored
			long expiration = cache.get(ip);
			if (expiration - System.currentTimeMillis() > 0) {
				//within interval update interval timer
				long newLiveUntil = updateTime();
				//update connection's interval timer for failed connection
				cache.put(ip, newLiveUntil);
				return false;
			} else {
				//it's ok to connect
				long newLiveUntil = updateTime();
				//update connection's timer for successful connection
				cache.put(ip, newLiveUntil);
				return true;
			}
		} else {
			//new connection, store and track it
			long liveUntil = updateTime();
			cache.put(ip, liveUntil);
			return true;
		}
	}
	
	/* This method will be called by timer in main to clear cache
	 * every few minutes to lessen the amount of stored connection trackers
	 * without cleaning the tracker map there will be a lot of expired trackers stored */
	public void cleanTracker() {
		for (Map.Entry<String, Long> connection : cache.entrySet()) {
			if(connection.getValue() - System.currentTimeMillis() < 0) {
				//connection timer has expired, no need to keep tracking
				cache.remove(connection.getKey());
			}
			//ignore unexpired connection trackers
		}
	}
	
	private long updateTime() {
		return System.currentTimeMillis() + interval * 1000;
	}
}
