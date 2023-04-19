package p2p.filesharing.network;

import java.io.IOException;

public interface PeerConnection {
	void send(byte[] data) throws IOException;
	
	byte[] receive() throws IOException;
	
	void close() throws IOException;

	int getPort();
}
