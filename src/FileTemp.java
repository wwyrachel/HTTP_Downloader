import java.io.File;

public class FileTemp {
	String dir;
	private int dledSize;
	public FileTemp(String dir) {
		this.dir = dir;
		this.dledSize = 0;
	}
	public int getDledSize(){
		File f = new File(this.dir);
		this.dledSize = (int)f.length();
		return this.dledSize;
	}
	public boolean exists(){
		File f = new File(this.dir);
		return f.exists() && !f.isDirectory();
	}
}
