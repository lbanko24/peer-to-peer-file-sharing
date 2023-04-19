package p2p.filesharing.task;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramSocket;
import java.util.List;

import p2p.filesharing.Main;
import p2p.filesharing.Peer;

public class WriteTask implements Runnable {
	
	private byte[] buffer;
	private int index;
	private int offset;
	private int size;
	private Peer peer;
	private List<Peer> peers;
	private DatagramSocket socket;
	
	public WriteTask(byte[] data, int index, int offset, int size, Peer peer, DatagramSocket socket, List<Peer> peers) {
		buffer = data;
		this.index = index;
		this.offset = offset;
		this.size = size;
		this.peer = peer;
		this.socket = socket;
		this.peers = peers;
	}
	
	@Override
	public void run() {
		try (RandomAccessFile raf = new RandomAccessFile(Main.getPath().toFile(), "rw")) {
			raf.seek(index * size + offset);
			raf.write(buffer);
		} catch (IOException ex) {
			System.out.println("Error writing file");
			return;
		}
		
		peer.setPiece(index, true);
		new HaveTask(socket, index, peers).run();
		
		/*for (byte b : buffer) {
            System.out.print(b);
            System.out.print(" ");
        }
        System.out.println("\n");*/
	}
}
