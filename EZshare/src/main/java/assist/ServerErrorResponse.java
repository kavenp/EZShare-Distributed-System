package assist;

import com.google.gson.Gson;

import dao.Gsonable;

public class ServerErrorResponse extends Response {
	private String response = "error";
	private String errorMessage = "invalid command";
	//default to invalid command
	
	//manually set errorMessage since there are too many different errors
	public void setErrorMessage(String msg) {
		this.errorMessage = msg;
	}
	
	public ServerErrorResponse(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public ServerErrorResponse() {
		// TODO Auto-generated constructor stub
	}

	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}
	
}
