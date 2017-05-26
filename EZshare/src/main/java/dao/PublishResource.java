package dao;

import com.google.gson.Gson;

/*
 * the information which are send when the client call publish command
 */

public class PublishResource extends Gsonable{
	private String command;
	private Resource resource;
	public PublishResource(Resource resource) {
		super();
		this.command = "PUBLISH";
		this.resource = resource;
	}
	
	@Override
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}

}

