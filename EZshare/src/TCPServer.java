import java.net.*;
import java.io.*;
import org.json.simple.*;
import org.apache.commons.cli.*;
 
public class TCPServer {
   public static void main (String args[]) {
     try{
       int serverPort = 7899; // the server port
       ServerSocket listenSocket = new ServerSocket(serverPort);
       int i = 0;
       while(true) {
         System.out.println("Server listening for a connection");
         Socket clientSocket = listenSocket.accept();
         i++;
         System.out.println("Received connection " + i );
         Connection c = new Connection(clientSocket);
        }
      } 
      catch(IOException e) 
      {
        System.out.println("Listen socket:"+e.getMessage());
      }
   }
}
   
class Connection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    public Connection (Socket aClientSocket) {
       try {
         clientSocket = aClientSocket;
         in = new DataInputStream( clientSocket.getInputStream());
         out =new DataOutputStream( clientSocket.getOutputStream());
         this.start();
       } catch(IOException e) {
          System.out.println("Connection:"+e.getMessage());
       }
    }
    public void run(){
    	try {           // an echo server
    		System.out.println("server reading data");
    		String data = in.readUTF();  // read a line of data from the stream
    		System.out.println("server writing data");
    		out.writeUTF(data);
    	}catch (EOFException e){
    		System.out.println("EOF:"+e.getMessage());
    	} catch(IOException e) {
    		System.out.println("readline:"+e.getMessage());
    	} finally{ 
    		try {
    			clientSocket.close();
    		}catch (IOException e){/*close failed*/}
	    }
	}
}
