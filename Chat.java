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
	

}

 class Server {
	
}

 class Client {
	 
 }
