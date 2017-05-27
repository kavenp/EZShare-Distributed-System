package dao;

import java.util.ArrayList;

import com.google.gson.Gson;

import assist.ResourceStorage;
import assist.Tuple;


/*
 * base resource information
 */
public class Resource extends Gsonable{
	private String name = "";
	private ArrayList<String> tags = new ArrayList<String>();
	private String description = "";
	private String uri = "";
	private String channel = "";
	private String owner = "";
	private String ezserver = null;
	
	public Resource() {
		super();
	}
	public Resource(String name ,String description ,ArrayList<String> tags, 
			        String uri , String channel , String owner) {
		this.name = name;
		this.description = description;
		this.tags = tags;
		this.uri = uri;
		this.channel = channel;
		this.owner = owner;
		this.ezserver = null;
	}
	
	public boolean match (Resource template) {
		if(template.getChannel().equals(this.getChannel()))
		{
			if(template.getOwner() != "")
			{
				if(template.getOwner().equals(this.getOwner()) 
					&& ResourceStorage.matchTags(template.getTags(), this.getTags()))
				{
					if(template.getUri() != "")
					{
						if( template.getUri().equals(this.getUri()) && 
							(template.getName().equals("") && template.getDescription().equals(""))
							|| this.getName().contains(template.getName()) || 
							this.getDescription().contains(template.getDescription())) 
						{
							return true;
						}
					}else
					{
						if( template.getUri().equals(this.getUri()) && 
							(template.getName().equals("") && template.getDescription().equals(""))
							|| this.getName().contains(template.getName()) || 
							this.getDescription().contains(template.getDescription())) 
						{
							return true;
						}
					}
				}
			}else
			{
				if(ResourceStorage.matchTags(template.getTags(), this.getTags()))
				{
					if(template.getUri() != "")
					{
						if( template.getUri().equals(this.getUri()) && 
							(template.getName().equals("") && template.getDescription().equals(""))
							|| this.getName().contains(template.getName()) || 
							this.getDescription().contains(template.getDescription()))
						{
							return true;
						}
					}else
					{
						if( template.getUri().equals(this.getUri()) && 
							(template.getName().equals("") && template.getDescription().equals(""))
							|| this.getName().contains(template.getName()) || 
							this.getDescription().contains(template.getDescription( )))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
		
	
	public Tuple<String, String, String> getResourceKey() {
		return new Tuple<String, String, String>(owner,channel,uri);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
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
	
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}


}
