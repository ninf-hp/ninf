package silf.rjava;

import silf.util.*;
import java.io.*;
import java.lang.reflect.*;

public class RJavaServerCore {
  static Logger dbg = new Logger("RJavaServerCore");
  public static RJavaServerCore instance = null;

  StreamMultiplexer smi;
  StreamMultiplexer smo;

  EnhancedDataOutputStream fromServer;
  EnhancedDataInputStream toServer;

  RJavaClassLoader loader;

  RJavaServerCore(InputStream is, OutputStream os) throws IOException{
    instance = this;

    smi = new StreamMultiplexer(is);
    smo = new StreamMultiplexer(os);

    InputStream  stdin  = smi.getInputStream("stdin");
    OutputStream stdout = smo.getOutputStream("stdout", true);
    OutputStream stderr = smo.getOutputStream("stderr", true);

    System.setIn(stdin);
    System.setOut(new PrintStream(stdout));
    System.setErr(new PrintStream(stderr));
    
    fromServer = new EnhancedDataOutputStream(smo.getOutputStream("fromServer"));
    toServer   = new EnhancedDataInputStream(smi.getInputStream("toServer"));

    loader = new RJavaClassLoader(this);
  }

  String [] recvArgs() throws IOException{
    fromServer.writeString("args");
    fromServer.flush();

    int length = toServer.readInt();
    String [] args = new String[length];
    for (int i = 0; i < length; i++)
      args[i] = toServer.readString();
    return args;
  }

  void invokeMain(Class cls, String [] args) 
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
    Method m = cls.getMethod("main", new Class[]{args.getClass()});
    // System.err.println("method is " + m);
    m.invoke(null, new Object[]{args});
    // System.err.println("invocation done ");
  }
  
  void invoke(String [] args) 
      throws ClassNotFoundException,NoSuchMethodException, IllegalAccessException, InvocationTargetException{
    Class target = loader.findClass(args[0]);
    //    Class target = Class.forName(args[0]);
    String [] nargs = new String[args.length - 1];
    for (int i = 0; i < args.length - 1; i++)
      nargs[i] = args[i + 1];
    invokeMain(target, nargs);
  }

  void start() {
    try {
      // System.err.println("recv args");
      String [] args = recvArgs();
      // System.err.println("invoke main");
      invoke(args);
    } catch (Exception e){
      e.printStackTrace();
    } finally {
      try {
	//	System.err.println("sending done");
	fromServer.writeString("done");
	fromServer.flush();
	smo.shutdown();
      } catch (IOException e){
      }
    }
  }

  synchronized byte[] loadClassData(String name) 
       throws ClassNotFoundException {
    try {
      fromServer.writeString("class");
      fromServer.writeString(name);
      fromServer.flush();

      int ava = toServer.available(); 
      int length = toServer.readInt();

      if (length < 0)
	throw new ClassNotFoundException();
      byte [] buffer = new byte[length];
      int alen = toServer.readfully(buffer, 0, length);
      return buffer;
    } catch (IOException e){
      dbg.printStack(e);
      throw new ClassNotFoundException();
    }
  }


  byte [] getFileBytes(String filename) throws FileNotFoundException {
    try {
      fromServer.writeString("inputFile");
      fromServer.writeString(filename);
      fromServer.flush();

      int length = toServer.readInt();

      if (length < 0)
	throw new FileNotFoundException();

      byte [] buffer = new byte[length];
      toServer.readfully(buffer, 0, length);
      return buffer;
    } catch (IOException e){
      dbg.printStack(e);
      throw new FileNotFoundException();
    }
  }

  void putRemoteFile(String remoteFilename, byte [] buffer) 
       throws IOException {
    fromServer.writeString("outputFile");
    fromServer.writeString(remoteFilename);
    fromServer.writeInt(buffer.length);
    fromServer.write(buffer);
  }

}

