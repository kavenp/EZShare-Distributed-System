package server_service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
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

public class ShareService extends Service {

	public ShareService(ResourceStorage resourceStroage, ServerRecords serverRecords) {
		super(resourceStroage, serverRecords);
		// TODO Auto-generated constructor stub
	}
	
	public void response(Resource resource, DataOutputStream out){
		Response response = null;
		Gson gson = new GsonBuilder().serializeNulls().create();


		try {
			checkResource(resource);
			BufferedReader bReader =new BufferedReader(new FileReader(new File(URI.create(resource.getUri()).getPath()))); 
			
			if(resourceStroage.checkResource(resource.getResourceKey()) == true){
				resourceStroage.updateResource(resource);
			}
			
			if(resourceStroage.checkOwner(resource) == false){
				throw new MyException("cannot share resource");
			}
			
			resourceStroage.storeResource(resource);
			response = new ServerSuccessResponse();
			//resourceStroage.printResource();
		
			
		} catch (NullPointerException e){
			response = new ServerErrorResponse("missing uri");
		} catch (FileNotFoundException e){
			response = new ServerErrorResponse("missing resource and//or secret.");
		} catch (MyException e) {
			// TODO Auto-generated catch block
			response = new ServerErrorResponse(e.getMessage());
		} 
			
		
		

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
			throw new MyException("cannot share resource");
		}
		/*if(Pattern.matches(regEx, resource.getUri()) == false){
			throw new MyException("incorrect file uri format");
		}*/
		if ((!(URI.create(resource.getUri()).getScheme().equals("file"))) && !(resource.getUri().equals("")) ) {
			throw new MyException("missing resource and//or secret");
		}
		if (resource.getUri() != "" && !URI.create(resource.getUri()).isAbsolute()) {
			throw new MyException ("invalid resource");
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
