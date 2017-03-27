/**
 * Contains code to start a new bot player. Must be run with two
 * arguments - the hostname and port number of the DoDServer
 * respectively.
 *
 * @author Jonathan Beaumont
 */
public class BotClientGUI {
	public static void main(String[] args){
		if (args.length != 2) {
            System.err.println("Usage: java BotClientGUI <hostname> <port number>");
            System.exit(1);
        }
		
		String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        new Thread(new BotPlayer(hostName, portNumber)).start();
	}
}
