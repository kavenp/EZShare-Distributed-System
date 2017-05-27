package assist;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SendThread extends Thread {
	private BufferedReader br;
	private DataOutputStream dos;
	private boolean isRunning = true;

	public SendThread() {
		br = new BufferedReader(new InputStreamReader(System.in));
	}

	public SendThread(Socket socket) {
		this();
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