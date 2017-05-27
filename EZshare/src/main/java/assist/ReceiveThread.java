package assist;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveThread extends Thread {
	private DataOutputStream dos;
	private DataInputStream dis;
	private boolean isRunning = true;

	public ReceiveThread(Socket socket) {

		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			isRunning = false;
			try {
				dis.close();
				dos.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void recieve() {
		String msg = null;
		try {
			msg = dis.readUTF();
			System.out.println("Received: " + msg);
		} catch (IOException e) {
			isRunning = false;
			try {
				dis.close();
				dos.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	

	@Override
	public void run() {
		while (isRunning) {
			recieve();
		}
	}
}