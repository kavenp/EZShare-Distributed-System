package assist;

import com.google.gson.Gson;

import dao.Gsonable;

public class ServerSuccessResponse extends Response {
	private String response = "success";
	
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}
	
}
