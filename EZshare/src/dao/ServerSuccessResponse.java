package dao;

import com.google.gson.Gson;

public class ServerSuccessResponse extends Gsonable {
	private String response = "success";
	
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}
	
}
