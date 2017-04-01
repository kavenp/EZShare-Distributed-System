import java.util.ArrayList;

public class Resource {
	private String name = "";
	private String desc = "";
	private ArrayList<String> tags = new ArrayList<String>();
	private String uri = "";
	private String channel = "";
	private String owner = "";
	private String ezserver = "";
	
	public Resource(String name ,String desc ,ArrayList<String> tags, 
			        String uri , String channel , String owner, String ezserver) {
		this.name = name;
		this.desc = desc;
		this.tags = tags;
		this.uri = uri;
		this.channel = channel;
		this.owner = owner;
		this.ezserver = ezserver;
	}
	
	public Tuple<String, String, String> getResourceKey() {
		return new Tuple<String, String, String>(owner,channel,uri);
	}
}
