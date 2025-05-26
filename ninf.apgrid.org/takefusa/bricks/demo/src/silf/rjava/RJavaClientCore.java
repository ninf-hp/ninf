package silf.rjava;

import silf.util.*;
import java.io.*;

public class RJavaClientCore {
  StreamMultiplexer smi;
  StreamMultiplexer smo;
  public static Logger dbg = new Logger("RJavaClientCore");  

  EnhancedDataOutputStream toServer;
  EnhancedDataInputStream  fromServer;
  ClassFileLoader loader;

  RJavaClientCore(InputStream is, OutputStream os) throws IOException{
    smi = new StreamMultiplexer(is);
    smo = new StreamMultiplexer(os);

    OutputStream  stdin  = smo.getOutputStream("stdin");
    InputStream stdout = smi.getInputStream("stdout");
    InputStream stderr = smi.getInputStream("stderr");

    Thread outThread = new Thread(new RedirectRunnable(stdout, System.out));
    Thread errThread = new Thread(new RedirectRunnable(stderr, System.err));
    outThread.setDaemon(true);
    errThread.setDaemon(true);
    outThread.start();
    errThread.start();

    toServer    = new EnhancedDataOutputStream(smo.getOutputStream("toServer"));
    fromServer  = new EnhancedDataInputStream(smi.getInputStream("fromServer"));

    loader = new ClassFileLoader();
  }

  private byte[] getFile(String name) throws IOException{
    File file = new File(name);
    int length = (int)file.length();
    byte [] buffer = new byte[length];
    FileInputStream fis = new FileInputStream(file.getPath());
    int read = 0;
    while (read < length){
      int tmp = fis.read(buffer, read, length - read);
      read += tmp;
    }
    return buffer;
  }

  private void putFile(String name, byte[] buffer) throws IOException{
    FileOutputStream fos = new FileOutputStream(name);
    fos.write(buffer);
    fos.close();
  }

  void start(String args[]) throws IOException{
    while (true){
      String header = fromServer.readString();
      //      dbg.println(header + " recved");

      if (header.equals("args")){
	toServer.writeInt(args.length);
	for (int i = 0; i < args.length; i++)
	  toServer.writeString(args[i]);
      } 
      else if (header.equals("done")){
	break;
      } 
      else if (header.equals("inputFile")){
	String filename = fromServer.readString();
	try {
	  //  System.err.println(filename);
	  byte[] buffer = getFile(filename);
	  toServer.writeInt(buffer.length);
	  toServer.write(buffer, 0, buffer.length);
	} catch (FileNotFoundException e){
	  e.printStackTrace();
	  toServer.writeInt(-1);
	}
      }
      else if (header.equals("outputFile")){
	String filename = fromServer.readString();
	try {
	  // System.err.println(filename);
	  int length = fromServer.readInt();
	  byte[] buffer = new byte[length];
	  fromServer.readFully(buffer);
	  putFile(filename, buffer);
	} catch (FileNotFoundException e){
	  e.printStackTrace();
	  toServer.writeInt(-1);
	}
      }
      else if (header.equals("class")){
	String name = fromServer.readString();
	try {
	  byte[] buffer = loader.loadClassData(name);
	  toServer.writeInt(buffer.length);
	  toServer.write(buffer, 0, buffer.length);
	} catch (ClassNotFoundException e){
	  toServer.writeInt(-1);
	}
      } else {
	dbg.println("unknown command");
      }
      toServer.flush();
    }

    smo.shutdown();

    //    dbg.println("done");
  }
}
