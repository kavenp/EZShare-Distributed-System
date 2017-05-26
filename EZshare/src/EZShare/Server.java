package EZShare;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.google.gson.*;

import assist.ConnectionTracker;
import assist.MyException;
import assist.RandomString;
import assist.ResourceStorage;
import assist.Response;
import assist.ServerCLIOptions;
import assist.ServerErrorResponse;
import assist.ServerRecords;
import assist.TaskManager;

import org.apache.commons.cli.*;

import dao.*;
import server_service.ExchangeService;
import server_service.FetchService;
import server_service.PublishService;
import server_service.QueryService;
import server_service.RemoveService;
import server_service.Service;
import server_service.ShareService;
 
public class Server {
    //the default server port
	private static int defaultServerPort = 3780;
	//the default secure port
	private static int defaultSecurePort = 3781;
    //generates random alphanumeric strings of length 26
	private static RandomString secretGenerator = new RandomString(26);
	//default connection interval limit (in seconds)
	private static int defaultConnectionLimit = 1;
	//default exchange interval (in seconds)
	private static int defaultExchangeT = 600;
    
	private static String serverSecret;
	private static String HostnamePort;
	
	public static ResourceStorage resourceStroage;
	public static ServerRecords serverRecords;
	public static ServerRecords secureServerRecords;

	
	
	
    public static void main (String args[]) { 
    	System.setProperty("javax.net.ssl.keyStore", "Keystores/server");
    	System.setProperty("java.net.ssl.trustStore", "Keystores/server");
    	System.setProperty("javax.net.ssl.keyStorePassword", "123123123");
    	System.setProperty("javax.net.debug", "all");
    	Server TCPServer = new Server();
    	String serverHostname = null;
    	//generate a default secret
    	serverSecret = secretGenerator.genString();
    	
    	int counter = 0;
    	int secureCounter = 0;
    	int exchangeT = defaultExchangeT;
    	int connectionLimit = defaultConnectionLimit;
    	int serverPort = defaultServerPort; 
    	int securePort = defaultSecurePort;
    	boolean debug = false;
    	
    	//set default first, overwrite only when option flag is set
    	resourceStroage = new ResourceStorage();
    	serverRecords = new ServerRecords();
    	//separate severRecords that holds only IP and secure port pairs
    	secureServerRecords = new ServerRecords();
    	
    	//setup command line options parser
    	ServerCLIOptions cliOptions = new ServerCLIOptions();
		Options options = cliOptions.createOptions();
		DefaultParser parser = new DefaultParser();
		
		//initialize socket factory
		ServerSocketFactory sock_factory = ServerSocketFactory.getDefault();
		//initialize SSLsocket factory
		SSLServerSocketFactory ssl_sock_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		
		CommandLine cl;
    	try {
    		cl = parser.parse(options, args);
    		//set exchange interval
    		if (cl.hasOption("exchangeinterval")) {
    			exchangeT = Integer.parseInt(cl.getOptionValue("exchangeinterval"));
    		}
    		//set connection limit
    		if (cl.hasOption("connectionintervallimit")) {
    			connectionLimit = Integer.parseInt(cl.getOptionValue("connectionintervallimit"));
    		}
    		//overwrite port value if port option is flagged
    		if (cl.hasOption("port")) {
    			serverPort = Integer.parseInt(cl.getOptionValue("port"));
    		}
    		//overwrite secure port value if sport is flagged
    		if (cl.hasOption("sport")) {
    			securePort = Integer.parseInt(cl.getOptionValue("sport"));
    		}
    		if (cl.hasOption("debug")) {
    			debug = true;
    		}
    		//get default local host name
        	String defaultServerHostname = InetAddress.getLocalHost().getHostName();
        	
        	//overwrite hostname to advertisedhostname if flagged
        	serverHostname = cl.getOptionValue("advertisedhostname", defaultServerHostname);
        	//set secret if flagged
        	serverSecret = cl.getOptionValue("secret", serverSecret);
        	HostnamePort = serverHostname + ":" + Integer.toString(serverPort);
        	//returns ezserver string of local hostname and port no.
	    }
	    catch(UnknownHostException | ParseException e)
	    {
	    	e.printStackTrace();
	    }
    	
    	//initialize classes that depend on possible command line arguments here
    	//initialize connection tracker
    	ConnectionTracker tracker = new ConnectionTracker(connectionLimit);
		//initialize task manager for timed tasks
		TaskManager manager = new TaskManager(tracker, exchangeT, serverRecords);
		//starts timer for timed tasks, will run cleanTracker and send exchange command (still need to implement exchange sending)
		manager.startTasks();
    	try(SSLServerSocket secureSock = (SSLServerSocket) ssl_sock_factory.createServerSocket(securePort)) {
			System.out.println("Starting the EZShare Server.");
			System.out.println("using secret: "+ serverSecret);
			System.out.println("using advertised hostname: "+ serverHostname);
			System.out.println("using connection interval: "+ connectionLimit + " seconds.");
			System.out.println("bound to secure port: "+ securePort);
			System.out.println("started.");
			
			while (true) {
	    		SSLSocket sslclient = (SSLSocket) secureSock.accept();
	    		secureCounter++;
	    		InetSocketAddress secureEndpoint = (InetSocketAddress) sslclient.getRemoteSocketAddress();
	    		if (tracker.checkConnection(secureEndpoint.getHostString())) {
	    			//passes tracker check for interval
	    			if (debug) {
	    				System.out.println("Secure client " + secureCounter + " connected.");
	    			}
	    			//start new thread to handle secure connection
	    			if (debug) {
	    				Thread t = new Thread(() -> clientConnection(sslclient, true));
	    				t.start();
	    			} else {
	    				Thread t = new Thread(() -> clientConnection(sslclient, false));
	    				t.start();
	    			}
	    		} else {
	    			//has tried to connect within interval, reject
					if (debug) {
						System.out.println("Secure client " + counter + ": " + secureEndpoint.getHostString() + " Tried to connect within connection interval, rejected.");
					}
					sslclient.close();
	    		}
			}
    	} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try(ServerSocket server = sock_factory.createServerSocket(serverPort)){
			System.out.println("Starting the EZShare Server.");
			System.out.println("using secret: "+ serverSecret);
			System.out.println("using advertised hostname: "+ serverHostname);
			System.out.println("using connection interval: "+ connectionLimit + " seconds.");
			System.out.println("bound to port: "+ serverPort);
			System.out.println("started.");
			
			// Wait for connections.
			while(true){
				Socket client = server.accept();
				counter++;
				InetSocketAddress endpoint = (InetSocketAddress) client.getRemoteSocketAddress();
				if (tracker.checkConnection(endpoint.getHostString())) {
					//passes tracker check for interval
					if (debug) {
						System.out.println("Client "+counter+" connected.");
					}
					// Start a new thread for a connection
					if (debug) {	
						Thread t = new Thread(() -> clientConnection(client, true));
						t.start();
					} else {
						Thread t = new Thread(() -> clientConnection(client, false));
						t.start();
					}
				} else {
					//has tried to connect within interval, reject
					if (debug) {
						System.out.println("Client " + counter + ": " + endpoint.getHostString() + " Tried to connect within connection interval, rejected.");
					}
					client.close();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
	
    private static void clientConnection(Socket client, boolean debug) {
    	
    	
    	try(Socket clientSocket = client) {
    		
        	Service service = null;
        	
    		// Gson builder that includes null values, used to parse and build JSON strings
    		Gson gson = new GsonBuilder().serializeNulls().create();
    		// Input stream
    		DataInputStream input = new DataInputStream(clientSocket.getInputStream());
    		// Output Stream
    		DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
			while(true){
				//if(input.available() > 0){
				try {
					String jsonString = input.readUTF();
					//String jsonString = input.readUTF();
					// Attempt to convert read data to JSON
		    		JsonObject commandObject = gson.fromJson(jsonString, JsonObject.class);
		    		
		    		JsonElement commandElement = commandObject.get("command");
		    		String command = commandElement.getAsString();
		    		JsonElement result = null;
		    		
		    		try{
		    			switch (command) {
		    			case "PUBLISH":
		    				if (debug) {
		    					System.out.println("PUBLISH command: " + jsonString);
		    				}
		    				result = commandObject.get("resource");
		    				Resource publishResource = gson.fromJson(result, Resource.class);
		    				
		    				service = new PublishService(resourceStroage, serverRecords);
		    			    service.response(publishResource, output);
		    				break;
		    			
		    			case "REMOVE":
		    				if (debug) {
		    					System.out.println("REMOVE command: " + jsonString);
		    				}
		    				result = commandObject.get("resource");
		    				Resource removeResource = gson.fromJson(result, Resource.class);
		    				service = new RemoveService(resourceStroage, serverRecords);
		    			    service.response(removeResource, output);
		    			
		    				break;
		    				
		    			case "SHARE":
		    				if (debug) {
		    					System.out.println("SHARE command: " + jsonString);
		    				}
		    				String secret = commandObject.get("secret").getAsString();
		    				if(secret == null){
		    					throw new MyException("missing secret");
		    				}
		    				if(!serverSecret.equals(secret)){
		    					throw new MyException("incorrect secret");
		    				}
		    				result = commandObject.get("resource");
		    				Resource shareResource = gson.fromJson(result, Resource.class);
		    				
		    				service = new ShareService(resourceStroage, serverRecords);
		    				service.response(shareResource, output);
		    				break;
		    				
		    			case "QUERY":
		    				if (debug) {
		    					System.out.println("QUERY command: " + jsonString);
		    				}
		    				result = commandObject.get("resourceTemplate");
		    				boolean relay = commandObject.get("relay").getAsBoolean();
		    				Resource queryResource = gson.fromJson(result, Resource.class);
		    				service = new QueryService(resourceStroage, serverRecords);
		    				
		    			    service.response(queryResource, output, HostnamePort, relay);
		    				break;
		    			
		    			case "FETCH":
		    				if (debug) {
		    					System.out.println("FETCH command: " + jsonString);
		    				}
		    				result = commandObject.get("resourceTemplate");
		    				Resource fetchResource = gson.fromJson(result, Resource.class);
		    				
		    				service = new FetchService(resourceStroage, serverRecords);
		    			    service.response(fetchResource, output, HostnamePort);
		    			    
		    				break;
		    				
		    			case "EXCHANGE":
		    				if (debug) {
		    					System.out.println("EXCHANGE command: " + jsonString);
		    				}
		    				result = commandObject.get("serverList");
		    				service = new ExchangeService(resourceStroage, serverRecords);
		    			    service.response(result, output);
		    			    
		    				break;
		    			
		    			default:
		    				// default error message for the command
		    				Response response = new ServerErrorResponse();
		    				//Invalid command handle here
				    		output.writeUTF(response.toJson(gson));
		    				break;
		    			}
		    		} catch(JsonSyntaxException e){
		    			ServerErrorResponse response = new ServerErrorResponse("missing resource fields");
		    			output.writeUTF(response.toJson(gson));
		    		} catch(MyException e){
		    			ServerErrorResponse response = new ServerErrorResponse(e.getMessage());
		    			output.writeUTF(response.toJson(gson));
		    		}
				} catch(IOException e) {
					e.printStackTrace();
					//client.close();
				}
		    		//client.close();
				}
	    	} catch (IOException e) {
				e.printStackTrace();
			} 
		}

	}
