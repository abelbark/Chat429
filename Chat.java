import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

import javax.print.attribute.standard.Destination;


public class Chat {
	
	private InetAddress ipAddress;   
	private int myPort; 
	private Map<Integer, Destination> destinationsHosts = new TreeMap<>();
    private int clientCounter = 1;
    private Server messageReciever ;


	private Chat(int myPort) {
		this.myPort = myPort;
	}
	

	//if the user enters a valid port # then the chat loop runs
	public static void main(String[] args) {

		if(args[0].trim().length()>1) {
			try {
				int listenPort = Integer.parseInt(args[0]);
				Chat chatApp = new Chat(listenPort);
				
			} catch (NumberFormatException nfe) {
				System.out.println("Enter valid port");
			}
		} else {
			System.out.println("Invalid Argument : run using 'java Chat <PortNumber>' .");
		}
		

	}
	
	//lets the user know about the different terminal commands
	public void help() {
		System.out.println("------Command Manual------");
		System.out.println("myip: display IP address");
		System.out.println("connect: connect to server");
		System.out.println("send: send messages to peers");
		System.out.println("myport: display the port");
		System.out.println("list: Display a numbered list of all the connections ");
		System.out.println("terminate: terminate the connection for listed id");
		System.out.println("exit: closes all connections");
	}
	
	//will retrieve the ip address of the computer
	 private String getmyIP(){
	        return ipAddress.getHostAddress();
	 }
	 
	 //return the port number entered
	 private int getMyPort() {
		 return myPort;
	 }
	 
	 //Display a numbered list of all the connections 
	 //this process is part of
	 private void list() {
		 System.out.println("ID:\tIP Address\tPort");
		 if(destinationsHosts.isEmpty()) {
			 System.out.println("No Destinations available");
		 } else {
			 for(Integer id : destinationsHosts.keySet()) {
				 Destination destinationHost = destinationsHosts.get(id);
				 System.out.println(id + "\t" + destinationHost.toString());
			 }
		 }
		 System.out.println();
	 }


 
     private void terminate(String[] commandArg){
        if(commandArg != null){
            System.out.println("Attempting to terminate Connection ID: " + commandArg[1]);
                try {
                    int id = Integer.parseInt(commandArg[1]);
                    if(destinationsHosts.containsKey(id) == false) {
                        System.out.println("Invalid connection ID, unable to terminate, try list");
                        return;
                    }	//continue if theres a valid id

                Destination destinationHost = destinationsHosts.get(id);
                    boolean closed = !destinationHost.closeConnection();
                    if(closed){
                        System.out.println("ConnectionID: "+ id + " was terminated.");
                        destinationsHosts.remove(id);
                    }

                }catch(NumberFormatException e){
            System.out.println("Invalid connection ID, unable to terminate. Try again!");
                                }
            }else {
                    System.out.println("Invalid command format , Correct format : terminate <connectionID>");
                }

     } // end of terminate()


}

 class Server {
	
}

 class Client {
	 
 }
// Destination class is for managing the socket connection and will wrap the socket and the output stream for the client to send a message

 class Destination{

    private InetAddress remoteHost;
    private int remotePort;
    private Socket connection;
    private PrintWriter out;
    private boolean isConnected;

    public Destination(InetAddress remoteHost, int remotePort) {

        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }
	// checks if client is connnected in order to receive messages
    public boolean initConnections(){
        try {
            this.connection = new Socket(remoteHost, remotePort);
            this.out = new PrintWriter(connection.getOutputStream(), true);
            isConnected = true;
        } catch (IOException e) {
			System.out.println("Error 404");
        }
        return isConnected;
    }
    public InetAddress getRemoteHost() {
        return remoteHost;
    }
    public void setRemoteHost(InetAddress remoteHost) {
        this.remoteHost = remoteHost;
    }
	// return port number of specififc destination
    public int getRemotePort() {
        return remotePort;
    }
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
	// outputs sent messages
    public void sendMessage(String message){
        if(isConnected){
            out.println(message);	// prints the message to the receiver
        }else{
			System.out.println("User isn't connected, message can't be sent.");
		}
    }
    public boolean closeConnection(){
		// closes the connection
        if(out != null)
            out.close();
        if(connection != null){
            try {
                connection.close();
            } catch (IOException e) {
            }
        }
        isConnected = false;
				return isConnected;
    }
    @Override
    public String toString() {
        return  remoteHost + "\t" + remotePort;
    }
}	//end of destination class
