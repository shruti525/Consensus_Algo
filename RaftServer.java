

import java.lang.*;
import java.util.*;

//import raft.ElectionTimer.ElectionTask;

import java.io.*;
import java.net.*;

public class RaftServer {
	
		public Record rec_;
		public Socket client_socket_;
		public int port_;
		public RaftTimer timer_ ;
		public int hb_;
		public int app_entries_;
		public int rqv_;
		
		enum State {
			LEADER,
			FOLLOWER,
			CANDIDATE,
			NONE
		}   
		public State state_;

	    public RaftServer() {
	    	client_socket_ = null;
	    	rec_ = null;
	    	port_ = 9990;
	    	timer_ = new RaftTimer();
	    	hb_ = 0;
	    	state_ = State.FOLLOWER;
	    }

	    class RaftTimer extends Timer {
	
	 	   public int max_;
		   public int min_;
		   private RaftReminder task_;
		   public int rdelay_;
		   
			public RaftTimer() {
				max_ = 300;
				min_ = 100;
				rdelay_ = computeElectionTimeout();
				task_ = new RaftReminder();
				schedule(task_, 0, rdelay_); 
			}
			
			public int computeElectionTimeout() {
		        long diff = max_ - min_;
		        return (int)((Math.random() * 100000) % diff) + min_;
		    }
	   
			class RaftReminder extends TimerTask {
				
				public void run() {
					
					switch (state_) {
				
					case LEADER:
						System.out.println("State = LEADER\n");
						if (rec_ != null)
							sendClientResponse(client_socket_, rec_);
						break;
					
					case CANDIDATE:
						System.out.println("State = CANDIDATE\n");
						getClientRecord(client_socket_);
						if (rqv_ > 1) {
							state_ = State.LEADER;
						} else {
							state_ = State.FOLLOWER;
						}
						break;
					
					case FOLLOWER:
						System.out.println("State = FOLLOWER\n");
						getClientRecord(client_socket_);
						if (app_entries_ > 0) {
							app_entries_ = 0;
						} /*else {
							state_ = State.CANDIDATE;
							rqv_ = 1;
							rec_.x++; // Term
							// Issue Request For Votes
							sendClientResponse(client_socket_, rec_);
						}	*/
						break;
						
					default:
						break;
					}
						
					rdelay_ = computeElectionTimeout();
					task_.cancel();
					task_ = new RaftReminder();
					schedule(task_,0, rdelay_);
				}
			}
	    }
	    
	    public void getClientRecord(Socket clientSocket){
	    	try {
	    		if (clientSocket == null)
	    			return;
	    		ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
				rec_ = (Record)in.readObject();
				rec_.printValues();
				app_entries_++;
				/*
				switch(state_) {
				case FOLLOWER:
					app_entries_++;
					break;
				case CANDIDATE:
					rqv_++;
					break;
				default:
					break;
				}	*/
				
				
					
			} catch (ClassNotFoundException | IOException e) {
				e.getMessage();
				e.printStackTrace();
			}
	    }
	
	    public void sendClientResponse(Socket clientSocket, Record rec){
	    	try{
	    		if (null == clientSocket)
	    			return;
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				out.writeObject(rec); 
				out.flush();
	    	}catch (IOException e) {
				e.printStackTrace();
	    	}
	    }

	    public static void main(String[] args) {
	
	    	RaftServer server = new RaftServer(); 
	    	System.out.println("Alive Server");
	    	try{
	    		ServerSocket raftServer = new ServerSocket(server.port_);
	    		while(true){
	    			server.client_socket_ = raftServer.accept(); // blocked
	    		//	server.getClientRecord(server.client_socket_);
	    		}
	    	}catch(Exception e){
	    		System.out.println("\nException :"+ e.getMessage());
	    	}
	    }
}
	

	
