package dao;

import com.google.gson.Gson;

/*
 * the information which are send when the client call remove command
 */
public class RemoveResource extends Gsonable {
	
	private String command;
	private Resource resource;
	
	public RemoveResource(Resource resource) {
		super();
		this.command = "REMOVE";
		this.resource = resource;
	}
	
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}
}
