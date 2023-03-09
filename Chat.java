import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

//import javax.print.attribute.standard.Target_Room;


public class Chat {
	
	private InetAddress myIP;   
	private int myPort; 
	private Map<Integer, Target_Room> roomHosts = new TreeMap<>();
    private int clientCounter = 1;
    private Server messageReciever ;


	private Chat(int myPort) {
		this.myPort = myPort;
	}
	

	//if the user enters a valid port # then the chat loop runs
	public static void main(String[] args) {

		if(args!= null && args.length > 0) {
			try {
				int listenPort = Integer.parseInt(args[0]);
				Chat chatApp = new Chat(listenPort);
				chatApp.startChat();
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
	        return myIP.getHostAddress();
	 }
	 
	 //return the port number entered
	 //in server class
	 private int getmyPort() {
		 return myPort;
	 }
	 
	 //Display a numbered list of all the connections 
	 //this process is part of
	 private void list() {
		 System.out.println("ID:\tIP Address\tPort");
		 if(roomHosts.isEmpty()) {
			 System.out.println("No destination available");
		 } else {
			 for(Integer id : roomHosts.keySet()) {
				 Target_Room roomHost = roomHosts.get(id);
				 System.out.println(id + "\t" + roomHost.toString());
			 }
		 }
		 System.out.println();
	 }
	 
	 // connect and sendMessage class location will be here, to be done later. server class and final touches left after

     private void terminate(String[] commandArg){
        if(commandArg != null){
            System.out.println("Attempting to terminate Connection ID: " + commandArg[1]);
                try {
                    int id = Integer.parseInt(commandArg[1]);
                    if(roomHosts.containsKey(id) == false) {
                        System.out.println("Invalid connection ID, unable to terminate, try list");
                        return;
                    }	//continue if theres a valid id

                Target_Room roomHost = roomHosts.get(id);
                    boolean closed = !roomHost.closeConnection();
                    if(closed){
                        System.out.println("ConnectionID: "+ id + " was terminated.");
                        roomHosts.remove(id);
                    }

                }catch(NumberFormatException e){
            System.out.println("Invalid connection ID, unable to terminate. Try again!");
                                }
            }else {
                    System.out.println("Invalid command format , Correct format : terminate <connectionID>");
                }

     } // end of terminate()
     
     // how to start a chat
     private void startChat(){


        Scanner scanner = new Scanner(System.in);
        try{

        	 myIP = InetAddress.getLocalHost();
             messageReciever = new Server();
             new Thread(messageReciever).start();

            // print out each possible command
            // get correct output to print out in terminal
            while(true){
                System.out.print("Enter the command: ");
                String command = scanner.nextLine();
                if(command != null && command.trim().length() > 0){
                    command = command.trim();
										//common help args..
                    if(command.equalsIgnoreCase("help") || command.equalsIgnoreCase("/h") || command.equalsIgnoreCase("-h")){
                    	help();
                    }else if(command.equalsIgnoreCase("myip")){
                        System.out.println(getmyIP());
                    }else if(command.equalsIgnoreCase("myport")){
                        System.out.println(getmyPort());
                    }else if(command.startsWith("connect")){
                        String[] commandArg = command.split("\\s+");
                        connect(commandArg);
                    }
                    else if(command.equalsIgnoreCase("list")){
                    	 list();
                    }
                    else if(command.startsWith("terminate")){
                    	String[] args = command.split("\\s+");
                        terminate(args);
                    }
                    else if(command.startsWith("send")){
                    	String[] commandArg = command.split("\\s+");
                        sendMessage(commandArg);
                    }
                    else if(command.startsWith("exit")){

											  System.out.println("Closing all connections...");
                        System.out.println("Chat Exited!");
                        closeAll();
                        System.exit(0);
                    }else{
                        System.out.println("Invalid command, try again.");
                        System.out.println();
                    }
                }else{
                    System.out.println("Invalid command, try again.");
                    System.out.println();
                }

            }
        }catch (UnknownHostException e) {
            e.printStackTrace();
        }finally{
            if(scanner != null)
                scanner.close();
            closeAll();
        }
    } // end of startChat
    // closes all chats currently open
    private void closeAll(){
        for(Integer id : roomHosts.keySet()){
            Target_Room roomHost = roomHosts.get(id);
            roomHost.closeConnection();
        }
        roomHosts.clear();
        messageReciever.stopChat();
    } // end of closeAll
    
    private void sendMessage(String[] commandArg) {
    	if(commandArg.length > 2) {
    		try {
    			int id = Integer.parseInt(commandArg[1]);
    			Target_Room roomHost = roomHosts.get(id);
    			System.out.println("id====" +roomHosts.get(id));
    			if(roomHost != null) {
    				StringBuilder message = new StringBuilder();
    				for(int i = 2; i < commandArg.length; i++) {
    					message.append(commandArg[i]);
    					message.append(" ");
    				}
    				roomHost.sendMessage(message.toString());
    				System.out.println("Message sent successfully");
    			} else {
    				System.out.println("No connection available");
    			}
    		} catch(NumberFormatException ne) {
    			System.out.println("Invalid Connection id");
    		}
    	} else {
    		System.out.println("Invalid command");
    	}
    }
    
    
    private void connect(String[] commandArg) {
    	//
    	if(commandArg != null && commandArg.length == 3) {
    		try {
    			//determines the ip address of the host given the hosts name
    			InetAddress remoteAddress = InetAddress.getByName(commandArg[1]);
    			int remotePort = Integer.parseInt(commandArg[2]);
    			System.out.println("Connecting to: "+ remoteAddress + " on port " + remotePort );
    			Target_Room roomHost = new Target_Room(remoteAddress, remotePort);
    			
    			if(roomHost.initConnections()) {
    				//associates a specified value with a specified key
    				roomHosts.put(clientCounter, roomHost);
    				System.out.println("Connected successfully, client id: " + clientCounter++);
    			} else {
    				System.out.println("Unable to establish a connection, try again");
    			}
    		} catch(NumberFormatException ne) {
    			System.out.println("Invalid Remote Host Port, unable to connect");
    		} catch(UnknownHostException e) {
    			System.out.println("Invalid remote Host Address, unable to connect");
    		}
    		
    	} else {
    		System.out.println("Invalid command format, follow: connect <destination> <port #>");
    	}
    }
	 
	 private class Server implements Runnable {
		 
			//reads input stream of characters
			BufferedReader in = null;
			Socket socket = null;
			boolean isStopped;
			//creates an arraylist of the available clients**
			List<Clients> clientList = new ArrayList<Clients>();
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ServerSocket s;
				try {
					s = new ServerSocket(getmyPort());
					System.out.println("Waiting for the client");
					while(!isStopped) {
						try {
							//listens for a connection to be made to this socket
							//and accepts it
							socket = s.accept();
							in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							System.out.println(socket.getInetAddress().getHostAddress() + ":" + 
							socket.getPort() + " : client connected successfully");
							
							//
							Clients clients = new Clients(in, socket);
							//
							new Thread(clients).start();
							clientList.add(clients);
						} catch (IOException e) {
							//Something
							e.printStackTrace();
						}
					}
				} catch(IOException e1) {
					
				}
			}
			
			public void stopChat() {
				isStopped = true;
				for(Clients clients : clientList) {
					clients.stop();
				}
				//Something
				Thread.currentThread().interrupt();
			}

		} //end of server class
	 
	 private class Clients implements Runnable{

	        private BufferedReader in = null;
	        private Socket clientSocket = null;
	        private boolean isStopped = false;
	        private Clients(BufferedReader in,Socket ipAddress) {
	            this.in = in;
	            this.clientSocket = ipAddress;
	        }

	        @Override
	        public void run() {

	            while(!clientSocket.isClosed() && !this.isStopped)
	            {
	                String st;
	                try {
	                    st = in.readLine();
											if(st == null){
												 stop();	//the connection was closed.
												 System.out.println("Connection was terminated by: "
												+clientSocket.getInetAddress().getHostAddress()
												+":"+clientSocket.getPort()+". ");

												 return;
											 }

	                    System.out.println("Message from "
											+clientSocket.getInetAddress().getHostAddress()
											+":"+clientSocket.getPort()+" : "+st);

	                } catch (IOException e) {
	                	e.printStackTrace();
	                }
	            }
	        }

	        public void stop(){

	            if(in != null)
	                try {
	                    in.close();
	                } catch (IOException e) {
	                }

	            if(clientSocket != null)
	                try {
	                    clientSocket.close();
	                } catch (IOException e) {
	                }
	            isStopped = true;
	            Thread.currentThread().interrupt();
	        }

	    } //end of client class
} // end pf Chat class

class Target_Room{

    private InetAddress remoteHost;
    private int remotePort;
    private Socket connection;
    private PrintWriter out;
    private boolean isConnected;

    public Target_Room(InetAddress remoteHost, int remotePort) {

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