package assist;
import java.util.*;
import com.google.gson.*;
import dao.ExchangeResource;
import dao.ServerInfo;

public class ServerRecords {
	private ArrayList<ServerInfo> servers = new ArrayList<ServerInfo>();
	
	public void addServer(ServerInfo server) {
		servers.add(server);
	}
	
	public void rmServer(ServerInfo server) {
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

	public ArrayList<ServerInfo> getServers() {
		return servers;
	}

	public void setServers(ArrayList<ServerInfo> servers) {
		this.servers = servers;
	}
}
