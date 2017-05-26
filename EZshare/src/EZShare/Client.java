package EZShare;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

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
import dao.SubscribeResource;

import org.apache.commons.cli.*;
public class Client {

	public static final String defaultIpAddress = "127.0.0.1";
	public static final int defaultServerPort = 3780;
	public static final int defaultSecurePort = 3781;

	public static void main(String args[]) {
    	System.setProperty("javax.net.ssl.keyStore", "Keystores/client");
    	System.setProperty("java.net.ssl.trustStore", "Keystores/client");
    	System.setProperty("javax.net.ssl.keyStorePassword", "123123123");
    	//System.setProperty("javax.net.debug", "all");
		// arguments supply message and hostname
		boolean debug = false;
		// secure flag
		boolean secure = false;
		Socket s = null;
		int serverPort = defaultServerPort;
		int securePort = defaultSecurePort;
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
			// confirm secure flag before port option, so we can tell if port is secure or not
			if (cl.hasOption("secure")) {
				secure = true;
			}
			if (cl.hasOption("port")) {
				if (secure) {
					securePort = Integer.parseInt(cl.getOptionValue("port"));
				} else {
					serverPort = Integer.parseInt(cl.getOptionValue("port"));
				}
			}
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
			if(cl.hasOption("debug")) {
				debug = true;
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
			if(cl.hasOption("subscribe")){
				boolean relay = true;
				sendResource = new SubscribeResource(relay, resource);
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
		
		SSLSocketFactory ssl_sock_factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		
		
		try {
			System.out.println(ipAddress+"-----"+serverPort);
			if (secure) {
				s = (SSLSocket) ssl_sock_factory.createSocket(ipAddress, securePort);
			} else {
				s = new Socket(ipAddress, serverPort);
			}
			//System.out.println("Connection Established");
			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			if (debug) {
				System.out.println("Sending data\n"+clJson.toString());
			}
			out.writeUTF(clJson); // UTF is a string encoding see Sn. 4.4
			
			out.flush();
			
			
			if(cl.hasOption("fetch")){
				String message1 = in.readUTF();
				if (debug) {
					System.out.println("Received: " + message1);
				}
				
				String resource = in.readUTF();
				if (debug) {
					System.out.println("Received: " + resource);
				}
				JsonParser jsonParser = new JsonParser();
				JsonObject command = (JsonObject) jsonParser.parse(resource);
				
				String message2 = in.readUTF();
				if (debug) {
					System.out.println("Received: " + message2);
				}
				String path = command.get("uri").getAsString();
				if (debug) {
					System.out.println(path);
				}
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
				if (debug) {
					System.out.println("Downloading "+fileName+" of size "+fileSizeRemaining);
				}
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
				if (debug) {
					System.out.println("File received!");
				}
				downloadingFile.close();
			}
			
			
			while(true) {
				String data = in.readUTF(); // read a line of data from the stream
				if (debug) {
					System.out.println("Received: " + data);
				}
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