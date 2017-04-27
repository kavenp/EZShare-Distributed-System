package EZShare;

import org.apache.commons.cli.CommandLine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import dao.*;


public class ClientServices {

    public static void publishCommand(CommandLine cmd, String ip, int port){
        try (Socket socket = new Socket(ip, port);) {

            System.out.print("publishing to "+ip+":"+port);
            // Output and Input Stream
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            JSONObject publishCommand = new JSONObject();

            publishCommand.put("command", "PUBLISH");

            publishCommand.put("resource", generateResourceJSON(cmd));

            output.writeUTF(publishCommand.toJSONString());
            output.flush();
            System.out.println("SENT: " + publishCommand.toJSONString());
            // Print out results received from server..
            String result = input.readUTF();
            System.out.println("RECEIVED: " + result);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
    }

    /**
     * Remove the file or uri from server
     *
     * @param uri
     */
    public static void remove(String uri, String ip, int port) {
        try (Socket socket = new Socket(ip, port);) {
            // Output and Input Stream
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("Processing remove action");
            out.flush();

            JSONObject newCommand = new JSONObject();
            newCommand.put("uri", uri);

            System.out.println(newCommand.toJSONString());

            // Read hello from server..
            String message = input.readUTF();
            System.out.println(message);

            // Send RMI to Server
            output.writeUTF(newCommand.toJSONString());
            output.flush();

            // Print out results received from server..
            String result = input.readUTF();
            System.out.println("Received from server: " + result);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }

    }

}
