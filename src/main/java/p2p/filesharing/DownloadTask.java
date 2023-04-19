package p2p.filesharing;

import java.net.DatagramSocket;
import java.util.List;

public class DownloadTask implements Runnable {

	private Peer peer;
	private List<Peer> neighbours;
	private DatagramSocket socket;
	
	public DownloadTask(DatagramSocket socket, Peer peer, List<Peer> neighbours) {
		this.socket = socket;
		this.peer = peer;
		this.neighbours = neighbours;
	}
	
	@Override
	public void run() {
		find();
		/*while (!peer.isComplete()) {
			find();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}*/
	}
	
	private void find() {
		for (int i = 0; i < peer.getPossesion().length; i++) {
			if (!peer.has(i)) {
				for (Peer p : neighbours) {
					if (p.has(i)) {
						new RequestTask(socket, p, i).run();

						break;
					}
				}
			}
		}
	}
}
