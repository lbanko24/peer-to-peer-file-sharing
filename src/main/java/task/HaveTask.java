package task;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;

import p2p.filesharing.Peer;

public class HaveTask implements Runnable {

	private List<Peer> peers;
	private int index;
	private DatagramSocket socket;

	public HaveTask(DatagramSocket socket, int index, List<Peer> peers) {
		this.socket = socket;
		this.index = index;
		this.peers = peers;
	}
	
	@Override
	public void run() {
		ByteBuffer buffer = ByteBuffer.allocate(5);
		buffer.put((byte) 3);
		buffer.putInt(index);
		
		for (Peer p : peers) {
			try {
				DatagramPacket packet = new DatagramPacket(buffer.array(), 5, InetAddress.getByName(p.getHost()), p.getPort());
				socket.send(packet);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/*for (Peer p : peers) {
			try {
				p.getConnection().send(buffer.array());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}
}
