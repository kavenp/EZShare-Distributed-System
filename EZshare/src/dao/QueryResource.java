package dao;

import com.google.gson.Gson;


/*
 * the information which are send when the client call query command
 */
public class QueryResource extends Gsonable{
	
	private String command;
	private boolean relay;
	private Resource resourceTemplate;
	
	
	public QueryResource(boolean relay, Resource resourceTemplate) {
		this.command = "QUERY";
		this.relay = relay;
		this.resourceTemplate = resourceTemplate;
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}

	
}
