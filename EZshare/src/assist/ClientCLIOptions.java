package assist;


import org.apache.commons.cli.*;

public class ClientCLIOptions {
	
	public Options createOptions(){
		Options options = new Options();  

		options.addOption("host", true, "server host, a domain name or IP address");
		options.addOption("port", true, "server port on server");

		options.addOption("name", true, "resource name");
		options.addOption("description", true, "resource description");
		options.addOption("tags", true, "resource tags, tag1, tag2, tag3,...");
		options.addOption("uri", true, "resource URI");
		options.addOption("channel", true, "channel");
		options.addOption("owner", true, "owner");
		
		options.addOption("secret", true, "secret");
		options.addOption("secure", false, "secure connection");
		options.addOption("servers", true, "server list, host1:port1, host2:port2,...");

		options.addOption("exchange", false, "exchange server list with server");
		options.addOption("fetch", false, "fetch resources from server");
		options.addOption("publish", false, "publish resourve on server");
		options.addOption("query", false, "query for resources from server");
		options.addOption("remove", false, "remove resourve from server");
		options.addOption("help", false, "list help informations");
		options.addOption("debug", false, "print debug inforamtion");
		options.addOption("share", false, "share resource on server");
		options.addOption("subscribe", false, "subscribe the resource");
		options.addOption("unsubscribe", false, "unsubscribe the resource");
		return options;
		
	}
	
}

