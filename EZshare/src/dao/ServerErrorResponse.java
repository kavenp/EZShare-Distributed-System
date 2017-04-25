package dao;

import com.google.gson.Gson;

public class ServerErrorResponse extends Gsonable {
	private String response = "error";
	private String errorMessage = "invalid command";
	//default to invalid command
	
	//manually set errorMessage since there are too many different errors
	public void setErrorMessage(String msg) {
		this.errorMessage = msg;
	}
	
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}
	
}
