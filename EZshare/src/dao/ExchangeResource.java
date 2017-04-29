package dao;

import java.util.ArrayList;

import com.google.gson.Gson;

/*
 * the information which are send when the client call exchange command
 */
public class ExchangeResource extends Gsonable {
	private String command;
	private ArrayList<ServerInfo> serverList;
	
	
	public ExchangeResource(ArrayList<ServerInfo> serverList) {
		this.command = "EXCHANGE";
		this.serverList = serverList;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}
}
