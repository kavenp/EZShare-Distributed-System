package assist;
import org.apache.commons.cli.*;

public class ServerCLIOptions {
	public Options createOptions(){
		Options options = new Options();  

		options.addOption("advertisedhostname", true, "advertised hostname");
		options.addOption("connectionintervallimit", true, "connection interval limit in seconds");
		options.addOption("exchangeinterval", true, "exchange interval in seconds");
		options.addOption("port", true, "server port, an integer");
		options.addOption("sport", true, "secure server port, an integer");
		options.addOption("secret", true, "secret");
		options.addOption("debug", false, "print debug information");
		return options;
		
	}
}
