package p2p.filesharing.message;

public enum MessageType {
	REQUEST((byte) 1),
	DATA((byte) 2),
	HAVE((byte) 3);
	
	private final byte id;
	
	private MessageType(byte id) {
		this.id = id;
	}
	
	public byte getId() {
		return id;
	}
}
