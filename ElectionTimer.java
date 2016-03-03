import java.util.*;
import java.lang.*;


public class ElectionTimer extends Timer{
	   public int max_;
	   public int min_;
	   public Scanner input = new Scanner(System.in);
	   public boolean heartbeatReceived_;
	   public int x =0;
	   
	   private ElectionTask task_;
	   public int rdelay_; 
	   
       public int computeElectionTimeout() {
            long diff = max_ - min_;
            return (int)((Math.random() * 1000013
            		) % diff) + min_;
       }
    
       ElectionTimer() {
    	   max_ = 300;
    	   min_ = 100;
    	   rdelay_ = computeElectionTimeout();
    	   task_ = new ElectionTask();
    	   scheduleAtFixedRate(task_,0, rdelay_);
       }
       
       class ElectionTask extends TimerTask{
            public void run(){
            	
            	System.out.println("\nEnter value of x: ");
            	x = input.nextInt();
            	
            	if (x >10){
            		heartbeatReceived_ =true;
            		System.out.println("\n Heartbeat :" + heartbeatReceived_);
              //  task_.cancel();
            //	rdelay_ = computeElectionTimeout();
            	System.out.println("\n Random task for election"+ new Date() +"\t"+ rdelay_);
                
                task_ = new ElectionTask();
                scheduleAtFixedRate(task_,0,rdelay_);
            	}else{
            		heartbeatReceived_ = false;
            	    System.out.println("x < 10 so timer is reset");
            	}
            }
       }
            
       public static void main(String[] args) {
            	
    	   ElectionTimer timer = new ElectionTimer();
    	   while (true) {}
 
       }
            
}

