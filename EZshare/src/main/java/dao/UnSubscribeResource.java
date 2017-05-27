package dao;

import com.google.gson.Gson;

public class UnSubscribeResource extends Gsonable{
	private String command;
	private String id;
	public UnSubscribeResource(String id) {
		super();
		this.command = "UNSUBSCRIBE";
		this.id = id;
	}
	
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}
}
