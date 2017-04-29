package dao;

import com.google.gson.Gson;

/*
 * the information which are send when the client call share command
 */
public class ShareResource extends Gsonable{

	private String command;
	private String secret;
	private Resource resource;
	public ShareResource(String secret, Resource resource) {
		super();
		this.command = "SHARE";
		this.secret = secret;
		this.resource = resource;
	}
	
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}
	
	
	

}
