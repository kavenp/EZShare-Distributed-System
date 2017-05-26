package server_service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import java.net.URI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import assist.*;
import dao.Resource;

public class PublishService extends Service {

	public PublishService(ResourceStorage resourceStroage, ServerRecords serverRecords) {
		super(resourceStroage, serverRecords);
		// TODO Auto-generated constructor stub
	}

	public void response(Resource resource, DataOutputStream out) {
		Response response = null;
		Gson gson = new GsonBuilder().serializeNulls().create();

		try {
			checkResource(resource);
			if (resourceStroage.checkResource(resource.getResourceKey()) == true) {
				resourceStroage.updateResource(resource);
			}
			if (resourceStroage.checkOwner(resource) == false) {
				throw new MyException("cannot publish resource");
			}
		} catch (MyException e) {
			// TODO Auto-generated catch block
			response = new ServerErrorResponse(e.getMessage());
		}
	
		resourceStroage.storeResource(resource);
		response = new ServerSuccessResponse();
		

		try {
			out.writeUTF(response.toJson(gson));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void checkResource(Resource resource) throws MyException {
		String regEx = "([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?";

		if (resource.getUri() == null) {
			throw new MyException("cannot publish resource");
		}
		if (resource.getUri() != "" && !URI.create(resource.getUri()).isAbsolute()) {
			throw new MyException ("invalid resource");
		}
		if (!(resource.getUri().equals("")) && ((URI.create(resource.getUri()).getScheme().equals("file"))) ) {
			throw new MyException("missing resource and//or secret");
		}
		if (Pattern.matches(regEx, resource.getUri()) == true) {
			throw new MyException("invalid resource");
		}
		if (resource.getUri().contains("\0")) {
			throw new MyException("invalid resource");
		}
		if (resource.getChannel().contains("\0")) {
			throw new MyException("invalid resource");
		}
		if (resource.getDescription().contains("\0")) {
			throw new MyException("invalid resource");
		}
		if (resource.getName().contains("\0")) {
			throw new MyException("invalid resource");
		}
		if (resource.getOwner().contains("\0") || resource.getOwner().equals("*")) {
			throw new MyException("invalid resource");
		}
		if (resource.getTags().toString().contains("\0")) {
			throw new MyException("invalid resource");
		}

	}

}
