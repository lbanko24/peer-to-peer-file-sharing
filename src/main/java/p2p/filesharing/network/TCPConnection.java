package p2p.filesharing.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

// WIP
public class TCPConnection implements PeerConnection {
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	
	public TCPConnection(Socket socket) throws UnknownHostException, IOException {
		this.socket = socket;
		is = socket.getInputStream();
		os = socket.getOutputStream();
	}
	
	@Override
	public byte[] receive() throws IOException {
		byte[] lenBuffer = is.readNBytes(4);
		int len = ByteBuffer.wrap(lenBuffer).getInt();
		
		byte[] message = is.readNBytes(len);
		
		return message;
	}
	
	@Override
	public void send(byte[] data) throws IOException {
		byte[] lenBuffer = ByteBuffer.allocate(4).putInt(data.length).array();
		os.write(lenBuffer);
		
		os.write(data);
	}
	
	@Override
	public void close() throws IOException {
		socket.close();
		is.close();
		os.close();
	}

	@Override
	public int getPort() {
		return socket.getPort();
	}
}
