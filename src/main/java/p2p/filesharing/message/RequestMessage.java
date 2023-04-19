package p2p.filesharing.message;

import java.nio.ByteBuffer;

public class RequestMessage extends Message {
	
	private static final int HEADER_SIZE = 5;
	
	private int index;

	public RequestMessage(int index) {
		super(HEADER_SIZE, MessageType.REQUEST);
		
		this.index = index;
		
		byte[] b = ByteBuffer.allocate(4).putInt(index).array();
		
		for (int i = 1, j = 0; i < data.length; i++, j++) {
			data[i] = b[j];
		}

	}

	public int getIndex() {
		return index;
		//return ByteBuffer.wrap(data, 1, 5).getInt();
	}
	
}
