//package raft;

import java.rmi.RemoteException;

public class RaftRequest  {

	public int appendEntryRequest(LogEntry newLogEntry, int leaderID, LogEntry lastCommitedEntry) throws RemoteException {
		System.out.println("\nAppendEntry Request Recived");
		LogEntry addLogEntry = newLogEntry ;
		if (addLogEntry == null){
		   return 1;
		}
		return 0;
	}

	
	public int appendEntryReply() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int voteRequest() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int voteReply() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

    
	

}

