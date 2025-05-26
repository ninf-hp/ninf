package silf.util;

import java.io.*;

public class RedirectRunnable implements Runnable{
  InputStream  is;
  OutputStream os;
  public RedirectRunnable(InputStream is, OutputStream os) throws IOException{
    this.is = is;
    this.os = os;
  }
  public void run() {
    byte [] buffer = new byte[4096];
    int read;
    try {
      while ((read = is.read(buffer)) >= 0){
	os.write(buffer, 0, read);
      }
	
    } catch (IOException e){
      e.printStackTrace();
    } 
  }
}
