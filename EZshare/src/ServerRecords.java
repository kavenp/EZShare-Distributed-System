import java.util.*;
import com.google.gson.*;
import dao.ExchangeResource;
import dao.Server;

public class ServerRecords {
	private ArrayList<Server> servers = new ArrayList<Server>();
	
	public void addServer(Server server) {
		servers.add(server);
	}
	
	public void rmServer(Server server) {
		servers.remove(server);
	}
	
	/*public void printServerList() {
		Iterator<String> iter = servers.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}*/
	
	public String createExchangeCommand() {
		// Gson builder that includes null values
		Gson gson = new GsonBuilder().serializeNulls().create();
		ExchangeResource exchange = new ExchangeResource(servers);
		return gson.toJson(exchange);
	}
}
