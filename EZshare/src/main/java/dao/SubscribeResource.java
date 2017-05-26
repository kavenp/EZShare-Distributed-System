package dao;

import com.google.gson.Gson;
import assist.RandomString;

public class SubscribeResource extends Gsonable {
	private String command;
	private boolean relay;
	private Resource resourceTemplate;
	private String id;
	
	public SubscribeResource(boolean relay, Resource resourceTemplate) {
		this.command = "SUBSCRIBE";
		this.relay = relay;
		this.resourceTemplate = resourceTemplate;
		this.id = new RandomString(10).genString();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}
}
