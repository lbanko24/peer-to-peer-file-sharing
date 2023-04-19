package p2p.filesharing.message;

public abstract class Message {
	
	protected byte[] data;
	protected MessageType type;
	
	public Message(int size, MessageType type) {
		data = new byte[size];
		data[0] = type.getId();
		
		this.type = type;
	}
	
	public MessageType getType() {
		return type;
	}
	
	
}
