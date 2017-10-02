package model;


public class SyncCommand extends Command {
	public Type type;
	public int timestamp;
	public int processId;
	
	public SyncCommand(Type type, int timestamp, int processId) {
		this.type = type;
		this.timestamp = timestamp;
		this.processId = processId;
	}

	public static enum Type {
		REQUEST,
		REPLY
	}
	
	@Override
	public String toString() {
		return String.format("Sync(%s, %d, %d)", type, timestamp, processId);
	}
}
