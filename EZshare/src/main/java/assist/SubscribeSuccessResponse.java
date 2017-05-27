package assist;

import com.google.gson.Gson;

public class SubscribeSuccessResponse extends Response {
	private String response = "success";
	private String id;
	public SubscribeSuccessResponse(String id) {
		this.id = id;
	}
	
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}
	
}
