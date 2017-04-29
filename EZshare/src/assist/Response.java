package assist;

import com.google.gson.Gson;

import dao.Gsonable;

public class Response extends Gsonable {
	
	public String toJson(Gson gson) {
		// TODO Auto-generated method stub
		
		return gson.toJson(this);
	}
}
