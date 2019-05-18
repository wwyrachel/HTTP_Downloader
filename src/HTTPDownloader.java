import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class HTTPDownloader {
	private static final int BUFFER_SIZE = 1024;

    public static void downloadHTTPfrom(String fileURL, String saveDir, int s, int e) throws IOException{
        URL sourceUrl = new URL(fileURL);
        HttpURLConnection httpConnection = (HttpURLConnection) sourceUrl.openConnection();
        
        httpConnection.setRequestProperty("RequestHeader", "Group: 5");
        
        if(s!=-1&&e!=-1)httpConnection.setRequestProperty("Range", "Bytes="+s+"-"+e);
        
        int responseCode = 0;
        
        // set the value of time out
        httpConnection.setReadTimeout(5000);
        
        try{
        	responseCode = httpConnection.getResponseCode();
        }catch (UnknownHostException e2){
        	System.out.println("Host not found. Retry in 5 seconds..");
        	try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
        	httpConnection.disconnect();
        	downloadHTTPfrom(fileURL,saveDir,s,e);
        }catch (ConnectException e3){
        	System.out.println("Connection timed out. Retry in 5 seconds...");
        	try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
        	httpConnection.disconnect();
        	downloadHTTPfrom(fileURL,saveDir,s,e);
        }catch (NoRouteToHostException e4){
        	System.out.println("No route to host. Retry in 5 seconds...");
        	try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
        	httpConnection.disconnect();
        	downloadHTTPfrom(fileURL,saveDir,s,e);
        }catch (SocketTimeoutException e5){
        	System.out.println("Read timed out. Retry in 5 seconds...");
        	try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
        	httpConnection.disconnect();
        	downloadHTTPfrom(fileURL,saveDir,s,e);
        }catch (SocketException e6){
        	System.out.println("Connection aborted. Retry in 5 seconds...");
        	try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
        	httpConnection.disconnect();
        	downloadHTTPfrom(fileURL,saveDir,s,e);
        }
        
        // check HTTP response code
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_PARTIAL) {
            String targetFileName;
            String targetContentType = httpConnection.getContentType();
            int fileSize = httpConnection.getContentLength();
            
            targetFileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,fileURL.length());
            String destinationPath = saveDir + File.separator + targetFileName;
            int start;
            if(s==-1){start=0;}else{start=s;}
            
            // check if the file is already exist.
            FileTemp temp = new FileTemp(destinationPath);
            if(temp.exists()&&s==-1&&e==-1){// file already exists
            	start = temp.getDledSize();
            	if(start<fileSize){
	            	//System.out.println("File exists.");
	            	System.out.println("Range: Bytes="+start+"-"+fileSize);
	            	downloadHTTPfrom(fileURL, saveDir, start, fileSize);
	            	return;
            	}else{
            		System.out.println("File already downloaded.");
            		System.exit(0);
            	}
            }else{// partial download or the file does not exist
            	
            	// print out the initial properties
                System.out.println(httpConnection.getRequestProperty("RequestHeader"));
                System.out.println("Response Code:" + responseCode);
                System.out.println("File Name = " + targetFileName);
                System.out.println("Content-Type = " + targetContentType);
                System.out.println("File Size = " + fileSize);
            }
 
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConnection.getInputStream();
             
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(destinationPath,true);
 
            int received = -1;
            byte[] downloadBuffer = new byte[BUFFER_SIZE];
            int progress = 0;
            float percentage = 0;
            try{
	            while ((received = inputStream.read(downloadBuffer,0,BUFFER_SIZE)) != -1) {
	            	//for testing only
	            	/*try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}*/
	            	//System.out.println("BUFFER_SIZE = "+BUFFER_SIZE+" avail = "+inputStream.available());
	            	
	            	progress += received;
		            outputStream.write(downloadBuffer, 0, received);
		            percentage = (float)progress*100/fileSize;
		            
		            if(inputStream.available()!=0){
		            	System.out.print("Downloaded: "+(progress+start)+"/"+(fileSize+start)+", ");
		            	System.out.printf("%.2f%%",percentage);
		            	System.out.printf("\n");
		            }
		      
	            }
	            outputStream.close();
	            inputStream.close();
	            System.out.println("The file has finished the download.");
            }catch(SocketTimeoutException e2){
            	System.out.println("Connection timed out. Retrying...");
            	httpConnection.disconnect();
            	downloadHTTPfrom(fileURL,saveDir,-1,-1);
            }catch(SocketException e3){
            	System.out.println("Connection reset. Retrying...");
            	httpConnection.disconnect();
            	downloadHTTPfrom(fileURL,saveDir,-1,-1);
            }
            
        } else if(responseCode!=0) {
            System.out.println("Unable to download the file. HTTP server response code: " + responseCode);
            System.out.println("Retry in 5 seconds...");
            try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
            httpConnection.disconnect();
            downloadHTTPfrom(fileURL,saveDir,s,e);
        }
        httpConnection.disconnect();
    }
}
