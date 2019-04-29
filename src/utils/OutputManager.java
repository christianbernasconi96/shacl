package utils;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class OutputManager {
	private PrintWriter writer;
	private String path;
	
	public OutputManager(String path) {
		this.path = path;
	}
	
	public void write(String filename, String output) throws FileNotFoundException {
		writer = new PrintWriter(path + "/" + filename);
		// writer.flush();
		writer.println(output);
		writer.close();
	}
	
	
}
