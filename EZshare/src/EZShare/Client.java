package EZShare;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import com.google.gson.*;

import assist.ClientCLIOptions;
import dao.ExchangeResource;
import dao.FetchResource;
import dao.Gsonable;
import dao.PublishResource;
import dao.QueryResource;
import dao.RemoveResource;
import dao.Resource;
import dao.ServerInfo;
import dao.ShareResource;

import org.apache.commons.cli.*;
public class Client {

	public static final String defaultIpAddress = "127.0.0.1";
	public static final String defaultServerPort = "3780";

	public static void main(String args[]) {
		// arguments supply message and hostname

		Socket s = null;
		int serverPort = 3780;
		String ipAddress = null;
		ArrayList<ServerInfo> exchangeServers = new ArrayList<ServerInfo>();
		try {
			ipAddress = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		ClientCLIOptions cliOptions = new ClientCLIOptions();
		Options options = cliOptions.createOptions();
		DefaultParser parser = new DefaultParser();
		// Gson builder that includes null values
		Gson gson = new GsonBuilder().serializeNulls().create();
		String clJson = null;
		CommandLine cl = null;
		try {
			cl = parser.parse(options, args);
			ipAddress = cl.getOptionValue("host", defaultIpAddress);
			serverPort = Integer.parseInt(cl.getOptionValue("port", defaultServerPort));

			String name = cl.getOptionValue("name", "");
			String description = cl.getOptionValue("description", "");
			String tagsString = cl.getOptionValue("tags", null);
			ArrayList<String> tags = new ArrayList<String>();
			if(tagsString != null){
				String[] tagsList = tagsString.split(",");
				for(String eachTag : tagsList){
					tags.add(eachTag);
				}
			}
			String uri = cl.getOptionValue("uri", "");
			String channel = cl.getOptionValue("channel", "");
			String owner = cl.getOptionValue("owner", "");
			
			
			Resource resource = new Resource(name, description, tags, 
					uri, channel, owner);
			Gsonable sendResource = null;
			
			if (cl.hasOption("servers")) {
				String serverList = cl.getOptionValue("servers");
				String[] servers = serverList.split(",");
				for(String eachServer : servers){
					String hostname = eachServer.split(":")[0];
					String port = eachServer.split(":")[1];
					ServerInfo newServer = new ServerInfo(hostname, Integer.parseInt(port));
					exchangeServers.add(newServer);
				}
			}
			
			if(cl.hasOption("help")) {
				HelpFormatter hf = new HelpFormatter();
				hf.printHelp("Options", options);
				return;
			}
			if(cl.hasOption("publish")){
				sendResource = new PublishResource(resource);
			}
			if(cl.hasOption("remove")){
				sendResource = new RemoveResource(resource);
			}
			if(cl.hasOption("share")){
				String secret = cl.getOptionValue("secret", "");
				sendResource = new ShareResource(secret, resource);
			}
			if(cl.hasOption("query")){
				boolean relay = true;
				sendResource = new QueryResource(relay, resource);
				
			}
			if(cl.hasOption("fetch")){
				sendResource = new FetchResource(resource);
			}
			if(cl.hasOption("exchange")){
				sendResource = new ExchangeResource(exchangeServers);
			}
			try {
				clJson = sendResource.toJson(gson);
			} catch (NullPointerException e) {
				System.out.println("No arguments given");
			}

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			System.out.println("Unrecognized arguments, please check your command");
			System.exit(0);
			//e1.printStackTrace();
		} 
		
		try {
			System.out.println(ipAddress+"-----"+serverPort);
			s = new Socket(ipAddress, serverPort);
			System.out.println("Connection Established");
			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			System.out.println("Sending data\n"+clJson.toString());
			
			/*
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(clJson).getAsJsonObject();
			String command = jsonObject.get("command").getAsString();
			
			System.out.println(command);
			*/
			
			out.writeUTF(clJson); // UTF is a string encoding see Sn. 4.4
			
			out.flush();
			
			
			if(cl.hasOption("fetch")){
				String message1 = in.readUTF();
				System.out.println("Received: " + message1);
				
				String resource = in.readUTF();
				System.out.println("Received: " + resource);
				
				JsonParser jsonParser = new JsonParser();
				JsonObject command = (JsonObject) jsonParser.parse(resource);
				
				String message2 = in.readUTF();
				System.out.println("Received: " + message2);
				String path = command.get("uri").getAsString();
				System.out.println(path);
				String[] tokens = path.split("\\\\");
				String fileName = tokens[tokens.length-1];
				
				RandomAccessFile downloadingFile = new RandomAccessFile(fileName, "rw");
				
				// Find out how much size is remaining to get from the server.
				long fileSizeRemaining = command.get("resourceSize").getAsLong();
				
				int chunkSize = setChunkSize(fileSizeRemaining);
				
				// Represents the receiving buffer
				byte[] receiveBuffer = new byte[chunkSize];
				
				// Variable used to read if there are remaining size left to read.
				int num;
				
				System.out.println("Downloading "+fileName+" of size "+fileSizeRemaining);
				while((num=in.read(receiveBuffer))>0){
					// Write the received bytes into the RandomAccessFile
					downloadingFile.write(Arrays.copyOf(receiveBuffer, num));
					
					// Reduce the file size left to read..
					fileSizeRemaining-=num;
					
					// Set the chunkSize again
					chunkSize = setChunkSize(fileSizeRemaining);
					receiveBuffer = new byte[chunkSize];
					
					// If you're done then break
					if(fileSizeRemaining==0){
						break;
					}
				}
				System.out.println("File received!");
				downloadingFile.close();
			}
			
			
			while(true) {
				String data = in.readUTF(); // read a line of data from the stream
				
				System.out.println("Received: " + data);
			}
		} catch (UnknownHostException e) {
			System.out.println("Socket:" + e.getMessage());
		} catch (EOFException e) {
			//System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (IOException e) {
					System.out.println("close:" + e.getMessage());
				}
		}
	}
	public static int setChunkSize(long fileSizeRemaining){
		// Determine the chunkSize
		int chunkSize=1024*1024;
		
		// If the file size remaining is less than the chunk size
		// then set the chunk size to be equal to the file size.
		if(fileSizeRemaining<chunkSize){
			chunkSize=(int) fileSizeRemaining;
		}
		
		return chunkSize;
	}
}