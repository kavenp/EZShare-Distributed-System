package dao;

import com.google.gson.Gson;

/*
 * the information which are send when the client call fetch command
 */
public class FetchResource extends Gsonable {
	private String command;
	private Resource resourceTemplate;
	
	
	public FetchResource(Resource resourceTemplate) {
		this.command = "FETCH";
		this.resourceTemplate = resourceTemplate;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}

	
}
