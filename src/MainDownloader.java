import java.io.IOException;
import java.util.Scanner;

public class MainDownloader {
	public static void main(String[] args) throws InterruptedException{
	    Scanner scanner = new Scanner(System.in);
	    System.out.println("Please input URL here:");
	    String fileURL = scanner.nextLine();
        System.out.println("Please input the path to save the file:");
        String saveDir = scanner.next();
        try {
        	//Download the file by using 
        	//HTTPDownloader.downloadHTTPfrom(fileURL, saveDir, starting byte, finishing byte);
        	//If you want to download the whole file, or you do not know the file size,
        	//use
        	HTTPDownloader.downloadHTTPfrom(fileURL, saveDir, -1, -1);
        	
        	//If you only want to download part of the file, use
        	//HTTPDownloader.downloadHTTPfrom(fileURL, saveDir, 0, 98);

        	//Notice: changing the starting and finishing byte parameters is for testing only
        	//since the method will start writing from the last byte of the file 
        	//if it is already existed, without checking whether 
        	//the existing content has been modified or the starting byte is in the right
        	//position.
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
