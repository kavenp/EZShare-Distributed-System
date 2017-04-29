package dao;

import java.util.ArrayList;

import com.google.gson.Gson;

public class FileResource extends Gsonable{
	private String name = "";
	private ArrayList<String> tags = new ArrayList<String>();
	private String description = "";
	private String uri = "";
	private String channel = "";
	private String owner = "";
	private String ezserver = null;
	private long resourceSize = 0;
	
	
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public ArrayList<String> getTags() {
		return tags;
	}


	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getUri() {
		return uri;
	}


	public void setUri(String uri) {
		this.uri = uri;
	}


	public String getChannel() {
		return channel;
	}


	public void setChannel(String channel) {
		this.channel = channel;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getEzserver() {
		return ezserver;
	}


	public void setEzserver(String ezserver) {
		this.ezserver = ezserver;
	}


	public long getResourceSize() {
		return resourceSize;
	}


	public void setResourceSize(long l) {
		this.resourceSize = l;
	}
	
}
