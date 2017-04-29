package server_service;


import java.io.DataOutputStream;

import assist.ResourceStorage;
import assist.Response;
import assist.ServerErrorResponse;
import assist.ServerRecords;
import assist.ServerSuccessResponse;
import dao.Resource;

public class Service {
	
	protected  ResourceStorage resourceStroage;
	protected ServerRecords serverRecords;
	

	public Service(ResourceStorage resourceStroage, ServerRecords serverRecords) {
		super();
		this.resourceStroage = resourceStroage;
		this.serverRecords = serverRecords;
	}




	public void response(Resource resource, DataOutputStream out){
	
	}




	public void response(Resource removeResource, DataOutputStream output, String hostnamePort, boolean relay) {
		// TODO Auto-generated method stub
		
	}
	
	//public String response()
	
	
}
