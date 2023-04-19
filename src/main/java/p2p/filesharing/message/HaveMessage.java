package p2p.filesharing.message;

import java.nio.ByteBuffer;

public class HaveMessage extends Message {
	
	private int index;
	
	public HaveMessage(int index) {
		super(5, MessageType.HAVE);
		this.index = index;
		
		byte[] b = ByteBuffer.allocate(4).putInt(index).array();
		
		for (int i = 1, j = 0; i < data.length; i++, j++) {
			data[i] = b[j];
		}
	}

	public int getIndex() {
		return index;
	}
}
