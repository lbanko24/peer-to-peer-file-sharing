package p2p.filesharing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class RequestTask implements Runnable {
	
	private Peer peer;
	private int index;
	private DatagramSocket socket;
	
	public RequestTask(DatagramSocket socket, Peer peer, int index) {
		this.socket = socket;
		this.peer = peer;
		this.index = index;
	}
	
	@Override
	public void run() {
		ByteBuffer buffer = ByteBuffer.allocate(5);
		buffer.put((byte) 1);
		buffer.putInt(index);

		try {
			socket.send(new DatagramPacket(buffer.array(), 5, InetAddress.getByName(peer.getHost()), peer.getPort()));
		} catch (IOException e) {
			System.out.println("Could not send request.");
		}
	}

}
