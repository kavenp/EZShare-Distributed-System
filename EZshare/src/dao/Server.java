package dao;


/*
 * to display a server
 * 
 */
public class Server {
	private String hostname;
	private int port;
	
	public Server(String hostname, int port) {
		super();
		this.hostname = hostname;
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	
	
	
}
