import java.net.*;
import java.io.*;
import java.util.*;
import javax.net.ServerSocketFactory;
import com.google.gson.*;
import org.apache.commons.cli.*;

import dao.*;
 
public class TCPServer {
    //the default server port
	private static int defaultServerPort = 3780;  
    //generates random alphanumeric strings of length 26
	private static RandomString secretGenerator = new RandomString(26);
	//default connection interval limit (in seconds)
	private static int defaultConnectionLimit = 1;
	//default exchange interval (in seconds)
	private static int defaultExchangeT = 600;
    
    public static void main (String args[]) { 
    	String HostnamePort;
    	//generate a default secret
    	String secret;
    	int counter = 0;
    	int exchangeT = defaultExchangeT;
    	int connectionLimit = defaultConnectionLimit;
    	int serverPort = defaultServerPort; 
    	//set default first, overwrite only when option flag is set
    	//System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8");
    	//System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
    	//setup command line options parser
    	ServerCLIOptions cliOptions = new ServerCLIOptions();
		Options options = cliOptions.createOptions();
		DefaultParser parser = new DefaultParser();
		//initialize socket factory
		ServerSocketFactory sock_factory = ServerSocketFactory.getDefault();
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
        	String serverHostname;
        	//overwrite hostname to advertisedhostname if flagged
        	serverHostname = cl.getOptionValue("advertisedhostname", defaultServerHostname);
        	//set secret if flagged
        	secret = cl.getOptionValue("secret", secretGenerator.genString());
        	HostnamePort = serverHostname + ":" + Integer.toString(serverPort);
        	//returns ezserver string of local hostname and port no.
	    }
	    catch(UnknownHostException | ParseException e)
	    {
	    	e.printStackTrace();
	    }
    	try(ServerSocket server = sock_factory.createServerSocket(serverPort)){
			System.out.println("Waiting for client connection..");
			
			// Wait for connections.
			while(true){
				Socket client = server.accept();
				counter++;
				System.out.println("Client "+counter+" connected.");
				
				
				// Start a new thread for a connection
				Thread t = new Thread(() -> clientConnection(client));
				t.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
	
    private static void clientConnection(Socket client) {
    	try(Socket clientSocket = client) {
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
		    		
		    		switch (command) {
		    			case "PUBLISH":
		    				System.out.println("PUBLISH command: " + jsonString);
		    				output.writeUTF(jsonString);
		    				break;
		    			
		    			case "REMOVE":
		    				System.out.println("REMOVE command: " + jsonString);
		    				output.writeUTF("Received REMOVE command.\n");
		    				break;
		    				
		    			case "SHARE":
		    				System.out.println("SHARE command: " + jsonString);
		    				output.writeUTF("Received SHARE command.\n");
		    				break;
		    				
		    			case "QUERY":
		    				System.out.println("QUERY command: " + jsonString);
		    				output.writeUTF("Received QUERY command.\n");
		    				break;
		    			
		    			case "FETCH":
		    				System.out.println("FETCH command: " + jsonString);
		    				output.writeUTF("Received FETCH command.\n");
		    				break;
		    				
		    			case "EXCHANGE":
		    				System.out.println("EXCHANGE command: " + jsonString);
		    				output.writeUTF("Received EXCHANGE command.\n");
		    				break;
		    			
		    			default:
		    				System.out.println("Invalid command: " + jsonString);
		    				output.writeUTF("Received invalid command.\n");
		    				//Invalid command handle here
		    				break;
		    		}
		    		client.close();
				}
			}
    	} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
