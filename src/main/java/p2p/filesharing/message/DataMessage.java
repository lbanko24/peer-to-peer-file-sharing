package p2p.filesharing.message;

import java.nio.ByteBuffer;

public class DataMessage extends Message {

	private static final int HEADER_SIZE = 5;
	
	private int index;

	public DataMessage(int size, int index, byte[] payload) {
		super(HEADER_SIZE + size, MessageType.DATA);
		
		this.index = index;
		
		byte[] b = ByteBuffer.allocate(4).putInt(index).array();
		
		for (int i = 1, j = 0; i < 5; i++, j++) {
			data[i] = b[j];
		}
	}
	
	public int getIndex() {
		return index;
		//return ByteBuffer.wrap(data, 1, 5).getInt();
	}
}
