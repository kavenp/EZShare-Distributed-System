import java.net.*;
import java.io.*;
import java.util.*;
import javax.net.ServerSocketFactory;
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
 
public class TCPServer {
    //the default server port
	private static int defaultServerPort = 3780;  
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

	
	
	
    public static void main (String args[]) { 
    	TCPServer TCPServer = new TCPServer();
    	String serverHostname = null;
    	//generate a default secret
    	serverSecret = secretGenerator.genString();
    	
    	//serverSecret = "zj7acor85pg2kc8gtktfuquara";
    	
    	int counter = 0;
    	int exchangeT = defaultExchangeT;
    	int connectionLimit = defaultConnectionLimit;
    	int serverPort = defaultServerPort; 
    	
    	
    	//set default first, overwrite only when option flag is set
    	//System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8");
    	//System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
    	resourceStroage = new ResourceStorage();
    	serverRecords = new ServerRecords();
    	
    	//setup command line options parser
    	ServerCLIOptions cliOptions = new ServerCLIOptions();
		Options options = cliOptions.createOptions();
		DefaultParser parser = new DefaultParser();
		
		//initialize socket factory
		ServerSocketFactory sock_factory = ServerSocketFactory.getDefault();
		//initialize empty server records
		ServerRecords servers = new ServerRecords();
		
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
		TaskManager manager = new TaskManager(tracker, exchangeT);
		//starts timer for timed tasks, will run cleanTracker and send exchange command (still need to implement exchange sending)
		manager.startTasks();
    	
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
					System.out.println("Client "+counter+" connected.");
					// Start a new thread for a connection
					Thread t = new Thread(() -> clientConnection(client));
					t.start();
				} else {
					//has tried to connect within interval, reject
					System.out.println("Client " + counter + ": " + endpoint.getHostString() + " Tried to connect within connection interval, rejected.");
					client.close();
				}
				//self exchange command
				Timer timer = new Timer();  
		        timer.schedule(TCPServer.new MyTask(), 1000*exchangeT, 1000*exchangeT);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
	
    private static void clientConnection(Socket client) {
    	
    	
    	try(Socket clientSocket = client) {
    		
        	Service service = null;
        	
    		// Gson builder that includes null values, used to parse and build JSON strings
    		Gson gson = new GsonBuilder().serializeNulls().create();
    		// Input stream
    		DataInputStream input = new DataInputStream(clientSocket.getInputStream());
    		// Output Stream
    		DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
			while(true){
				if(input.available() > 0){
					String jsonString = input.readUTF();
					// Attempt to convert read data to JSON
		    		JsonObject commandObject = gson.fromJson(jsonString, JsonObject.class);
		    		
		    		JsonElement commandElement = commandObject.get("command");
		    		String command = commandElement.getAsString();
		    		JsonElement result = null;
		    		
		    		try{
		    			switch (command) {
		    			case "PUBLISH":
		    				System.out.println("PUBLISH command: " + jsonString);
		    				result = commandObject.get("resource");
		    				Resource publishResource = gson.fromJson(result, Resource.class);
		    				
		    				service = new PublishService(resourceStroage, serverRecords);
		    			    service.response(publishResource, output);
		    				break;
		    			
		    			case "REMOVE":
		    				System.out.println("REMOVE command: " + jsonString);
		    				result = commandObject.get("resource");
		    				Resource removeResource = gson.fromJson(result, Resource.class);
		    				service = new RemoveService(resourceStroage, serverRecords);
		    			    service.response(removeResource, output);
		    			
		    				break;
		    				
		    			case "SHARE":
		    				System.out.println("SHARE command: " + jsonString);
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
		    				System.out.println("QUERY command: " + jsonString);
		    				result = commandObject.get("resourceTemplate");
		    				boolean relay = commandObject.get("relay").getAsBoolean();
		    				Resource queryResource = gson.fromJson(result, Resource.class);
		    				service = new QueryService(resourceStroage, serverRecords);
		    				
		    			    service.response(queryResource, output, HostnamePort, relay);
		    				break;
		    			
		    			case "FETCH":
		    				System.out.println("FETCH command: " + jsonString);
		    				result = commandObject.get("resourceTemplate");
		    				Resource fetchResource = gson.fromJson(result, Resource.class);
		    				
		    				service = new FetchService(resourceStroage, serverRecords);
		    			    service.response(fetchResource, output, HostnamePort);
		    			    
		    				break;
		    				
		    			case "EXCHANGE":
		    				System.out.println("EXCHANGE command: " + jsonString);
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
		    		
		    		client.close();
				}
			}
    	} catch (IOException e) {
			//e.printStackTrace();
		} 
	}
    class MyTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Random r = new Random(System.currentTimeMillis());
			ArrayList<Server> exchangeServers = serverRecords.getServers();
			Server exchangeServer = exchangeServers.get(r.nextInt(exchangeServers.size()));
			
			ExchangeResource resource = new ExchangeResource(exchangeServers);
			Socket s = null;
			try{
				s = new Socket(exchangeServer.getHostname(), exchangeServer.getPort());
				System.out.println("Connection Established");
				
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				Gson gson = new GsonBuilder().serializeNulls().create();

				out.writeUTF(resource.toJson(gson)); // UTF is a string encoding see Sn. 4.4
				
				out.flush();
				
			}catch(IOException e){
				System.out.println(e.getMessage());
				serverRecords.rmServer(exchangeServer);
			}finally {
				if (s != null)
					try {
						s.close();
					} catch (IOException e) {
						System.out.println("close:" + e.getMessage());
					}
			}
		}
		
	}
}
