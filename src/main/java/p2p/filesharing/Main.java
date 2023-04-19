package p2p.filesharing;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
	private static final int PIECE_SIZE = 512;
	private static final int WORKERS = 1;
	
	private static BlockingQueue<Runnable> writingTasks = new LinkedBlockingQueue<>();
	private static BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
	
	private static Peer me;
	private static Path path;
	
    public static void main(String[] args) throws IOException {
    	int port = Integer.parseInt(args[0]);
    	
    	if (args.length >= 2) {
    		path = Paths.get(args[1]);
    	} else {
    		Optional<String> filename = Metadata.readFilename();
    		if (filename.isPresent()) {
    			path = Path.of(filename.get());
    		} else {
    			System.out.println("No file found in metadata.");
    			System.exit(0);
    		}
    	}
    	
    	long size = fileInit();
    	
    	int numOfPieces = (int) (size / PIECE_SIZE + 1);
		me = new Peer("localhost", port, numOfPieces);
    	
    	List<Peer> neighbours = new ArrayList<>();
    	
    	
    	
    	Thread[] workers = new Thread[WORKERS];
    	
    	for (int i = 0; i < workers.length; i++) {
    		workers[i] = new Thread(new TaskExecution(tasks));
    	}
    	
    	for (Thread t : workers) t.start();
    	
    	Thread writingThread = new Thread(new TaskExecution(writingTasks));
		writingThread.start();
		
		DatagramSocket s = new DatagramSocket(port);
		
		
		
		Peer ne = null;
		
		// Init neighbours
		if (args.length >= 2) {
			ne = testInit(true, numOfPieces);
    	} else {
			ne = testInit(false, numOfPieces);
	    	
	    	try {
				tasks.put(new DownloadTask(s, me, neighbours));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
		
		neighbours.add(ne);
		
		loop(s, neighbours);
		//s.close();
    }

    public static Path getPath() {
    	return path;
    }
    
    private static long fileInit() throws IOException {
    	long size;
    	
    	if (!path.toFile().exists()) {
    		// Create file if it does not exist
    		
    		path.toFile().createNewFile();
    		size = Metadata.readSize();
    		
    		try (OutputStream os = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING)) {
    			for (int i = 0; i < size; i++) {
    				//allocate file
    				os.write(0);
    			}
    		} catch (IOException ex) {
    			System.out.println("Error allocating file.");
    		}
    	} else {
    		// Get existing file size
    		size = Files.size(path);
    	}
    	
    	return size;
    }
    
    private static Peer testInit(boolean haveAll, int numOfPieces) {
    	if (haveAll) {
    		
    		for (int i = 0; i < me.getPossesion().length; i++) {
    			me.setPiece(i, true);
    		}
			
			Peer ne = new Peer("localhost", 8081, numOfPieces);
			
	    	return ne;
    	} else {

    		Peer ne = new Peer("localhost", 8082, numOfPieces);
    		
    		for (int i = 0; i < ne.getPossesion().length; i++) {
    			ne.setPiece(i, true);
    		}

        	return ne;        	
    	}
    }
    
    private static void loop(DatagramSocket s, List<Peer> neighbours) throws IOException {
    	byte[] buffer = new byte[1025];
		DatagramPacket packet = new DatagramPacket(buffer, 1025);
    	
    	while (true) {
			s.receive(packet);
			System.out.println("recv: " + buffer[0] + " " + packet.getLength());
			int index;
			
			switch (buffer[0]) {
			case 2:
				index = ByteBuffer.wrap(buffer, 1, 5).getInt();
				
				// Skip if piece already full
				if (me.has(index)) continue;
				
				System.out.println("Writing " + index);				
				byte[] body = Arrays.copyOfRange(buffer, 5, packet.getLength());
				
				try {
					writingTasks.put(new WriteTask(body, index, 0, PIECE_SIZE, me, s, neighbours));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 1:
				index = ByteBuffer.wrap(buffer, 1, 5).getInt();
				System.out.println("Sending " + index);

				try {
					tasks.put(new SendTask(s, index, 0, PIECE_SIZE, packet.getPort()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case 3:
				index = ByteBuffer.wrap(buffer, 1, 5).getInt();
				
				for (Peer p : neighbours) {
					if (packet.getPort() == p.getPort()) {
						System.out.println("HAVE: id=" + index);
						p.setPiece(index, true);
					}
				}
				
				break;
			}
		}
    }
}
