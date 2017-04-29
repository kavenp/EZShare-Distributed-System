package server_service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import assist.ResourceStorage;
import assist.Response;
import assist.ServerRecords;
import assist.ServerSuccessResponse;
import dao.Server;

public class ExchangeService extends Service {

	public ExchangeService(ResourceStorage resourceStroage, ServerRecords serverRecords) {
		super(resourceStroage, serverRecords);
		// TODO Auto-generated constructor stub
	}

	public void response(JsonElement resource, DataOutputStream out) {
		Response response = null;
		Gson gson = new GsonBuilder().serializeNulls().create();
		
		JsonArray servers = resource.getAsJsonArray();
		
		for(int i=0; i<servers.size(); i++){
			Server server = gson.fromJson(servers.get(i), Server.class);
			Socket s;
			try {
				s = new Socket(server.getHostname(), server.getPort());
				if(s != null){
					serverRecords.addServer(server);
					s.close();
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				
			} catch (IOException e) {
				// TODO Auto-generated catch block	
			}
		}
		System.out.println("!!!!!1");
		for(Server s : serverRecords.getServers()){
			System.out.println(s.getPort());
		}
		response = new ServerSuccessResponse();
		try {
			out.writeUTF(response.toJson(gson));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
