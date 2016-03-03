

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;



public class Node  implements RaftRequestInterface {
	
	public static final int MaxNodes = 5 ;
	private static final int min = 2;
	private static final int max = 20;
    private static final int delay = 5000;
	
	public int currentTerm ;
	 // public static int selfId ;
	public Record writeRecord ;
	public static int prevLogEntryIndex;
	public static int newLogEntryIndex;
	public static LogEntry prevCommitLogEntry ;
	public static LogEntry newRecvLogEntry ;
	public 	Vector<LogEntry>localLog   ;
	public 	Vector<LogEntry>stateLog   ;
	public static int appendEntryCount ;
	public static int recvVoteCount ;
	public RaftTimer timer_ ;
	 // Range for the period to check if heard from leader, [1,5]

	private int silencePeriod; 
	private int messagePeriod       = 0;

	private String host;

	private String name;
	private String leaderId;
    private int leaderTerm;
	private int ignoreElection 	   = 0;
	public boolean heardFromLeader = false;
	public boolean noLeaderFound   = true;
	public int     votedCurrTerm;
	public LogEntry prevCandidateLogEntry = null;

    @Override
    public void find_sum(int x, int y) throws RemoteException {
            int sum=x+y;
            System.out.println("The sum is"+sum);
    }
	enum State {
					LEADER,
					FOLLOWER,
					CANDIDATE,
					NONE
				}   
	public State state_;
	
	@SuppressWarnings("unused")
	private Node() throws RemoteException {super();}
	
	public int ComputeElectionTimeout(){
		silencePeriod =  (min + (int)(Math.random() * ((max - min) + 1))) * 1000;
		return silencePeriod;
	}

	public Node(String nameIn, String hostIn) throws RemoteException {
		//super();
		
		this.name 			= nameIn;
		this.host 			= hostIn;
		this.state_ 		= State.FOLLOWER;
		this.localLog 		= new Vector<LogEntry>();
		this.votedCurrTerm  = 0;
		this.currentTerm    = 0;
		System.out.println("\n Inside constructor.." + this.name );
		this.silencePeriod  = ComputeElectionTimeout();
		// Make sure that messages are getting sent more frequently then
		// the node checks for silence
		/*while (messagePeriod < silencePeriod) {
			messagePeriod = (min + (int) (Math.random() * ((max - min) + 1))) * 1000;
		}
		*/
		messagePeriod = 1000;
		System.out.println("\n "+name + "initiated.message sending interval " + messagePeriod);
		System.out.println("\n "+name + "initiated.Silence  interval " + silencePeriod);
		timer_ = new RaftTimer(); 
			
	}

	class RaftTimer extends Timer {

		    private RaftElectionTask electionTask_;
		    private RaftHearbeatTask appendEntryTask_;
		    
			public RaftTimer() {
				//electionTask_    = new RaftElectionTask();
				appendEntryTask_ = new RaftHearbeatTask();
				System.out.println("\n Scheduling election task.");
				//schedule(electionTask_, 0, silencePeriod); 
				System.out.println("\n Scheduling message sending.");
				schedule(appendEntryTask_, delay, messagePeriod); 
			}
	}
	
	class RaftElectionTask extends TimerTask {
			public void run(){
				try {
					Registry reg = LocateRegistry.getRegistry();
					//RaftRequestInterface stub = (RaftRequestInterface) reg.lookup("RaftRequest");
					System.out.println("\n Inside Election task.....");
					votedCurrTerm =0;
					recvVoteCount =0;
				   // if (!state_.equals(State.LEADER) && !heardFromLeader ){    //&& votedCurrTerm == 0) {
					if (state_.equals(State.CANDIDATE)) { 
								//state_      = State.CANDIDATE ;
								currentTerm ++;
								votedCurrTerm ++;     // vote Flag
								recvVoteCount ++;     // ballot
								int prevCandidateLogEntryIndex  = localLog.size();
								System.out.println("\n Candidate log size :" + prevCandidateLogEntryIndex);
								if(prevCandidateLogEntryIndex > 0){
								   prevCandidateLogEntry = localLog.lastElement();   
								}
								System.out.println("\n Start Election with Candidate node : "+ name + "\t The election term is :"+ currentTerm);
						        System.out.println("\n Starting Election");
							//	Registry reg = LocateRegistry.getRegistry(host);
							//	RaftRequestInterface stub = (RaftRequestInterface) reg.lookup("RaftRequest");
								for (String nodeName : reg.list()) {
									System.out.println("\n nodeName :"+ nodeName);
										RaftRequestInterface otherNodeStub = (RaftRequestInterface) reg.lookup(nodeName);
										int voteReply = otherNodeStub.voteRequest(currentTerm , name , prevCandidateLogEntry , prevCandidateLogEntryIndex );
										if (voteReply > 0){
											recvVoteCount ++ ;
										}
									}
								System.out.println("\n Candidate node : "+ name + "\t The election term is :"+ currentTerm +"\t The received vote this term :"+ recvVoteCount);
								if(recvVoteCount >=  (MaxNodes/2) +1) {
										state_    = State.LEADER;	
										leaderId  = name ;
										leaderTerm = currentTerm;
                                                                                RaftRequestInterface stub = (RaftRequestInterface) reg.lookup(leaderId);
                                                                                //int logValue=stub.find_sum(x, y);
                                                                                //stub.appendEntryRequest(logValue, newLogEntryIndex, leaderId, prevCommitLogEntry, prevLogEntryIndex);
                                                                                
										for (String nodeName1 : reg.list()) {
											    RaftRequestInterface otherNodeStub = (RaftRequestInterface) reg.lookup(nodeName1);
												int appendEntryRequest = otherNodeStub.appendEntryRequest(0,0, leaderId, prevCommitLogEntry ,prevLogEntryIndex);
												System.out.println("declared new leader \t "+name + " is the new leader for term "+ currentTerm);
											}
										timer_.electionTask_.cancel(); // election over!
									
									}
								votedCurrTerm = 0;
						}
				    /*else if(heardFromLeader){
							state_          = State.FOLLOWER;
							System.out.println(name + " stepped down.");
							heardFromLeader = false;
					    }*/
				    			
				    }catch (RemoteException | NotBoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
				}
			}
}
	
	class RaftHearbeatTask extends TimerTask {
		public void run(){
			
			try {
				Registry reg = LocateRegistry.getRegistry(host);
				//RaftRequestInterface stub = (RaftRequestInterface) reg.lookup("RaftRequest");
			
			if (state_ == State.LEADER){
				if (leaderTerm < currentTerm) { // revisit
					state_ = State.FOLLOWER;
					
				} else {
					 for (String nodeName1 : reg.list()) {
						 System.out.println("\n nodeName1 :"+ nodeName1);
						 RaftRequestInterface otherNodeStub = (RaftRequestInterface) reg.lookup(nodeName1);
						 int appendEntryRequest = otherNodeStub.appendEntryRequest(0,0, leaderId, prevCommitLogEntry ,prevLogEntryIndex);
						 System.out.println(name + " is the new leader for term "+ currentTerm);
					 }
				}	 
			
			} else if (state_ == State.CANDIDATE) { 
				if (heardFromLeader) {
					timer_.electionTask_.cancel();
					heardFromLeader = false;
				    state_ = State.FOLLOWER;
				    System.out.println(name + " stepped down.");
				   
				} 
				 
			} else if (state_ == State.FOLLOWER) {   
				if (heardFromLeader) {
					heardFromLeader = false;
				} else {
					state_ = State.CANDIDATE;
					System.out.println(name + " No Hearbeat from leader "+ currentTerm);
					timer_.electionTask_    = new RaftElectionTask();
					timer_.schedule(timer_.electionTask_, 0, silencePeriod);
				}
			}
						
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	}
	
	
	public int appendEntryRequest(int newLogEntry, int newLogIndex, String leaderName, LogEntry lastCommitedEntry,
			int lastCommitedLogIndex) throws RemoteException {
		System.out.println(" leader id "+ leaderName);
		heardFromLeader = true;
		return 0;
	}
      

	@Override
	public int voteRequest(int newTerm, String candidateName, LogEntry lastCandidateCommitedEntry,
			int lastCandidateCommitedIndex) throws RemoteException {
		int currentLogIndex  = localLog.size();
         if (newTerm < currentTerm ||  (newTerm == currentTerm && currentLogIndex > lastCandidateCommitedIndex) || votedCurrTerm != 0) {
					System.out.println("\n Vote denied by " + name + "\t to Candidate "+ candidateName +"\t for term "+ newTerm);
			        return 0;
		 }else{  
			      currentTerm    = newTerm;
			      votedCurrTerm ++;
			      System.out.println("\n Vote given by " + name + "\t to Candidate "+ candidateName +"\t for term "+ newTerm);
		          return 1;
		 }
	}
	
	public static void main(String[] args) {
		//String name = (args.length < 1 || args[0].equals("!")) ? 
			//	"Node-" + System.currentTimeMillis() : args[0];
		        String name = "Node-" + System.currentTimeMillis();
		        
			    String hostname = (args.length < 2) ? null : args[1];
				try {
					   Node class_obj = new Node(name,hostname);
					   Registry registry = LocateRegistry.getRegistry();
					   RaftRequestInterface server_if = (RaftRequestInterface) UnicastRemoteObject.exportObject(class_obj, 0);
                                            registry.rebind(name, server_if);
					   System.out.println("RMI_Hello Server is ready." + name);
				
				} catch (Exception e) {
					System.out.println("Node Error: " + e.toString());
					e.printStackTrace();
				}
	}
	
}
