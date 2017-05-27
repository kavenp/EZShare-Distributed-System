package assist;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import dao.Resource;

public class Subscription {
	private String id;
	private Resource resource;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
	
	public Subscription(String id, Resource resource, DataInputStream dis, DataOutputStream dos, Socket socket) {
		super();
		this.id = id;
		this.resource = resource;
		this.dis = dis;
		this.dos = dos;
		this.socket = socket;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	
	
}
