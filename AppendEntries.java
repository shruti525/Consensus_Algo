/*Append entries requests are at the core of the replication protocol. 
Leaders send append requests to followers to replicate and commit
log entries*/

import com.sun.istack.internal.Nullable;
import java.io.IOException;
import java.util.*; 
import javax.media.jai.remote.Serializer;
class AppendEntries implements java.io.Serializable {
    
    public  int term;
    public  int leaderId;
    public  int commitIndex=-1;
    public  int logIndex;
    public  int logTerm;
    //Record rec;
    //List<Object> entry = new ArrayList<Object>();
    
    public AppendEntries(int term, int leaderId, int commitIndex, int logIndex, int logTerm)
    {
        this.term=term;
        this.leaderId=leaderId;
        this.commitIndex=commitIndex;
        this.logIndex=logIndex;
        this.logTerm=logTerm;
       // this.entry=entry;
    }

    public long getTerm() {
        return term;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public long getCommitIndex() {
        return commitIndex;
    }

    public long getLogIndex() {
        return logIndex;
    }

    public long getLogTerm() {
        return logTerm;
    }

   // public List getEntry() {
   //     return entry;
   // }
    
    public void setTerm(int term) {
        if(term>0)
            System.out.println("Term cannot be negative");
        else    
            this.term = term;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public void setCommitIndex(int commitIndex) {
        this.commitIndex = commitIndex;
    }

    public void setLogIndex(int logIndex) {
        if(logIndex<0)
            System.out.println("LogIndex cannot be negative");
        else
        this.logIndex = logIndex;
    }

    public void setLogTerm(int logTerm) {
        this.logTerm = logTerm;
    }

   //  public void setEntries(List<Object> entry) {
      //   this.entry = entry;
   // }
    
 
    
    
}
 
    





