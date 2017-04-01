import java.util.*;
import com.google.gson.*;

public class ServerRecords {
	private ArrayList<String> servers = new ArrayList<String>();
	
	public void addServer(String server) {
		servers.add(server);
	}
	
	public void rmServer(String server) {
		servers.remove(server);
	}
	
	public void printServerList() {
		Iterator<String> iter = servers.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
	
	public void packageServerList() {
		//TODO: packages server list into JSON
	}
}
