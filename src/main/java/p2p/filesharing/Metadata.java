package p2p.filesharing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class Metadata {
	
	public static Optional<String> readFilename() {
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get("metadata.txt"))) {
			
			String line = br.readLine();
			
			return Optional.of(line);
			
		} catch (IOException e) {
			System.out.println("Error reading metadata.");
			return Optional.empty();
		}
	}
	
	public static long readSize() {
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get("metadata.txt"))) {
			br.readLine(); // skip first line
			String line = br.readLine();
			
			return Long.parseLong(line);
			
		} catch (IOException e) {
			System.out.println("Error reading metadata.");
			return 0;
		}
	}
		
	public static void generate(int pieceSize, Path path, String filename) {
		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("metadata.txt"), StandardOpenOption.CREATE);
				InputStream is = Files.newInputStream(path, StandardOpenOption.READ);) {
			
			bw.write(filename);
			bw.write('\n');
			
			int l = 0;
			
			while (l != -1) {
				byte[] buffer = new byte[pieceSize];
				l = is.read(buffer);
				
				if (l == -1) break;
				
				bw.write(hash(buffer));
				bw.write('\n');
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private static String hash(byte[] data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		byte[] hash = md.digest(data);
		
		StringBuilder sb = new StringBuilder();
		
		for (byte b : hash) {
			String s = Integer.toHexString(b & 0xff);
			
			if (s.length() == 1) sb.append("0");
			
			sb.append(s);
		}
		
		return sb.toString();
	}
	
	public static boolean checkAllPieces(int pieceSize, Path path) {
		
		try (BufferedReader br = Files.newBufferedReader(Paths.get("metadata.txt"));
				InputStream is = Files.newInputStream(path, StandardOpenOption.READ);) {
			
			String line;
			
			br.readLine(); // skip first line
			
			while ((line = br.readLine()) != null) {
				byte[] buffer = new byte[pieceSize];
				
				if (is.read(buffer) == -1) {
					return false;
				}
				
				if (!line.equals(hash(buffer))) {
					return false;
				}
				
			}
			
			return true;
			
		} catch (IOException e) {
			System.out.println("Error verifying file.");
			return false;
		}
	}
}
