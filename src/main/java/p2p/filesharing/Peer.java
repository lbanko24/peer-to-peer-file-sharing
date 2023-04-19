package p2p.filesharing;

import p2p.filesharing.network.PeerConnection;

public class Peer {

	private boolean[] possesion;
	private PeerConnection connection;
	private String host;
	private int port;
	
	public Peer(String host, int port, int pieces) {
		this.setHost(host);
		this.setPort(port);
		
		possesion = new boolean[pieces];
	}

	synchronized public boolean[] getPossesion() {
		return possesion;
	}

	synchronized public void setPossesion(boolean[] possesion) {
		this.possesion = possesion;
	}
	
	synchronized public void setPiece(int index, boolean has) {
		possesion[index] = has;
	}
	
	synchronized public boolean has(int index) {
		return possesion[index];
	}

	synchronized public boolean isComplete() {
		for (boolean b : possesion) {
			if (!b) return false;
		}
		
		return true;
	}

	synchronized public PeerConnection getConnection() {
		return connection;
	}
	
	synchronized public void setConnection(PeerConnection connection) {
		this.connection = connection;
	}

	public int getPort() {
		return port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
