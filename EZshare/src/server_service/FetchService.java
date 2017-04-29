package server_service;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import assist.ResourceStorage;
import assist.Response;
import assist.ServerErrorResponse;
import assist.ServerRecords;
import assist.ServerSuccessResponse;
import dao.Resource;

public class FetchService extends Service {

	public FetchService(ResourceStorage resourceStroage, ServerRecords serverRecords) {
		super(resourceStroage, serverRecords);
		// TODO Auto-generated constructor stub
	}
	
	public void response(Resource resource, DataOutputStream out, String HostnamePort, boolean relay){
		Response response = null;
		Gson gson = new GsonBuilder().serializeNulls().create();
		Resource matchResource = 
				resourceStroage.getFetchResource(resource.getChannel(), resource.getUri());
		try{
			if(matchResource == null){
				response = new ServerErrorResponse("cannot find the resource");
				out.writeUTF(response.toJson(gson));
				return;
			}else{
				response = new ServerSuccessResponse();
				out.writeUTF(response.toJson(gson));
				
			}
		}catch(IOException e){
			
		}
	}

	
}
