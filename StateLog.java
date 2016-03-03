
import java.util.*;

public class StateLog {
	Vector<LogEntry> log ;
	
    public StateLog (){
    	this.log   = new Vector<LogEntry>();
    }
	
    public boolean commitLogEntry(int leaderID , LogEntry newLogEntry){
    	log.addElement(newLogEntry);
		return true;
    }

}


    
    
