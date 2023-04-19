package p2p.filesharing.task;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import p2p.filesharing.Main;

public class SendTask implements Runnable {
	
	private DatagramSocket socket;
	private int index;
	private int offset;
	private int size;
	private int port;
	
	public SendTask(DatagramSocket socket, int index, int offset, int size, int port) {
		this.socket = socket;
		this.index = index;
		this.offset = offset;
		this.size = size;
		this.port = port;
	}
	
	@Override
	public void run() {

		try (RandomAccessFile raf = new RandomAccessFile(Main.getPath().toFile(), "r")) {
    		int l = 0;
    		byte[] buffer = new byte[size - offset];
    		raf.seek(index * size + offset);
			l = raf.read(buffer);

			if (l == -1) return;

			ByteBuffer payload = ByteBuffer.allocate(l + 5);
			payload.put((byte) 2);
			payload.putInt(index);
			payload.put(buffer, 0, l);
            
            DatagramPacket packet = new DatagramPacket(payload.array(), l + 5, InetAddress.getByName("localhost"), port);
            /*for (byte b : packet.getData()) {
                System.out.print(b);
                System.out.print(" ");
            }
            System.out.println("\n");*/
			
			socket.send(packet);
		} catch (IOException e) {
			System.out.println("IO Error: " + Main.getPath());
		}
	}
}
