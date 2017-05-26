package server_service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import assist.ResourceStorage;
import assist.Response;
import assist.ServerErrorResponse;
import assist.ServerRecords;
import assist.ServerSuccessResponse;
import dao.FileResource;
import dao.Resource;

public class FetchService extends Service {

	public FetchService(ResourceStorage resourceStroage, ServerRecords serverRecords) {
		super(resourceStroage, serverRecords);
		// TODO Auto-generated constructor stub
	}
	
	public void response(Resource resource, DataOutputStream out, String HostnamePort){
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
				
				FileResource newResource = new FileResource();
				newResource.setChannel(matchResource.getChannel());
				newResource.setDescription(matchResource.getDescription());
				newResource.setEzserver(HostnamePort);
				newResource.setName(matchResource.getName());
				newResource.setTags(matchResource.getTags());
				newResource.setUri(matchResource.getUri());
				if(matchResource.getOwner() != ""){
					newResource.setOwner("*");
				}else{
					newResource.setOwner("");
				}
				File fi = new File(resource.getUri());
				newResource.setResourceSize(fi.length());
				
			
				out.writeUTF(newResource.toJson(gson));
				
				JsonObject resultSize = new JsonObject();
				resultSize.addProperty("resultSize", 1);
				out.writeUTF(resultSize.toString());
				
				RandomAccessFile byteFile = new RandomAccessFile(fi,"r");
				byte[] sendingBuffer = new byte[1024*1024];
				int num;
				// While there are still bytes to send..
				while((num = byteFile.read(sendingBuffer)) > 0){
					System.out.println(num);
					out.write(Arrays.copyOf(sendingBuffer, num));
				}
				byteFile.close();
				
			}
		}catch(IOException e){
			System.out.println("hahahahah");
		}
	}

	
}
