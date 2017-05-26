package server_service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import assist.MyException;
import assist.ResourceStorage;
import assist.Response;
import assist.ServerErrorResponse;
import assist.ServerRecords;
import assist.ServerSuccessResponse;
import dao.Resource;

public class RemoveService extends Service {

	public RemoveService(ResourceStorage resourceStroage, ServerRecords serverRecords) {
		super(resourceStroage, serverRecords);
		// TODO Auto-generated constructor stub
	}
	public void response(Resource resource, DataOutputStream out){
		Response response = null;
		Gson gson = new GsonBuilder().serializeNulls().create();
		try {
			checkResource(resource);
			resourceStroage.removeResource(resource.getResourceKey());
			
			response = new ServerSuccessResponse();
			
		} catch (MyException e) {
			// TODO Auto-generated catch block
			response = new ServerErrorResponse(e.getMessage());
		}
		
		
		//resourceStroage.printResource();
		
		try {
			out.writeUTF(response.toJson(gson));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void checkResource(Resource resource) throws MyException{
		String regEx = "([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?";
		
		if(resource.getUri() == null){
			throw new MyException("cannot remove resource");
		}
		if(Pattern.matches(regEx, resource.getUri()) == true){
			throw new MyException("invalid resource");
		}
		if(resource.getUri().contains("\0")){
			throw new MyException("invalid resource");
		}
		if(resource.getChannel().contains("\0")){
			throw new MyException("invalid resource");
		}
		if(resource.getDescription().contains("\0")){
			throw new MyException("invalid resource");
		}
		if(resource.getName().contains("\0")){
			throw new MyException("invalid resource");
		}
		if(resource.getOwner().contains("\0") || resource.getOwner().equals("*")){
			throw new MyException("invalid resource");
		}
		if(resource.getTags().toString().contains("\0")){
			throw new MyException("invalid resource");
		}
		
	}
} 

