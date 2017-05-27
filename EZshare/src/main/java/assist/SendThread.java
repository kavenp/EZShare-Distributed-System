package assist;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dao.Gsonable;
import dao.UnSubscribeResource;

public class SendThread extends Thread {
	private BufferedReader br;
	private DataOutputStream dos;
	private boolean isRunning = true;
	private String id;

	public SendThread() {
		br = new BufferedReader(new InputStreamReader(System.in));
	}

	public SendThread(Socket socket, String id) {
		this();
		this.id = id;
		try {
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			isRunning = false;
			try {
				br.close();
				dos.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	public void send() {
		String msg = getMsgFromConsole();
		if (null != msg && !msg.equals("")) {
			try {
				dos.writeUTF(msg);
			} catch (IOException e) {
				isRunning = false;
				try {
					br.close();
					dos.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		if(msg.equals("")){
			Gson gson = new GsonBuilder().serializeNulls().create();
			Gsonable  sendResource = new UnSubscribeResource(id);
			try {
				dos.writeUTF(sendResource.toJson(gson));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private String getMsgFromConsole() {
		try {
			return br.readLine();
		} catch (IOException e) {
			return "";
		}
	}

	@Override
	public void run() {
		while (isRunning) {
			send();
		}
	}
}