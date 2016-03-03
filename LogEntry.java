public class LogEntry {
	public int term;
	public int logIndex;
	public Record record;
	
	public LogEntry(int term , int logIndex ,Record rec_){
		this.term 			= term ;
		this.logIndex		= logIndex ;
		this.record         = rec_ ;
	}
	
	public void logRead(){
		System.out.println("\nLog information :");
		System.out.println("\nLog Index =" + logIndex);
		System.out.println("\nLog Term =" + term);
		System.out.println("\nLog Record  : ");
		record.printValues();
	}
			
}