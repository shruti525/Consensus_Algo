

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Leader {
	public static final int MaxNodes = 5 ;
	public static int currentTerm ;
	public static String selfId ;
	public Record writeRecord ;
	public static LogEntry prevCommitLogEntry ;
	public static LogEntry newLogEntry ;
	public static int newLogIndex;
	public static int prevCommitIndex;
	public static int appendEntryCount ;
	
	public void writeLocalLog (int currentTerm ,int newLogIndex , Record writeRecord){
		newLogEntry = new LogEntry(currentTerm , newLogIndex , writeRecord);
	}
	
	public static void main(String[] args) {
		String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry();
            RaftRequestInterface stub = (RaftRequestInterface) registry.lookup("RaftRequest");
            int appendEntryRequest = stub.appendEntryRequest(newLogEntry,newLogIndex, selfId, prevCommitLogEntry ,prevCommitIndex);
            System.out.println("append : " + appendEntryRequest);
            if (appendEntryRequest > 0){
            	appendEntryCount++ ;
            }else{
            	appendEntryRequest = stub.appendEntryRequest(newLogEntry,newLogIndex, selfId, prevCommitLogEntry ,prevCommitIndex);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
	}
}

