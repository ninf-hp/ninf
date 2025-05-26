package silf.util;

import java.io.*;
import java.util.*;

public class StreamMultiplexer {
  boolean debug = false;

  boolean inMode;
  EnhancedDataInputStream is;
  EnhancedDataOutputStream os;
  Hashtable isTable = new Hashtable();
  Hashtable osTable = new Hashtable();
  

  static final int NONE = 0;
  static final int ERR = -2;
  static final int EOF = -1;

  /** means all the streams will shut down */
  static final int STREAM_CLOSE = -3;


  public StreamMultiplexer(InputStream is) throws IOException{
    inMode = true;
    this.is = new EnhancedDataInputStream(is);
    Thread t = new Thread(new StreamMultiplexerReaderRunnable());
    t.start();
  }

  public StreamMultiplexer(OutputStream os)throws IOException{
    inMode = false;
    this.os = new EnhancedDataOutputStream(os);
  }

  public InputStream getInputStream(String tag) throws IOException{
    if (!inMode)
      throw new IOException("StreamMultiplexer: invalid mode"); 
    synchronized (isTable){
      
      StreamMultiplexerInputStream smis 
	= (StreamMultiplexerInputStream)isTable.get(tag);
      if (smis == null){
	smis = new StreamMultiplexerInputStream(this, tag);
	isTable.put(tag, smis);
      }
      return smis;
    }
  }

  public StreamMultiplexerInputBuffer getInputBuffer(String tag) 
       throws IOException {
    StreamMultiplexerInputStream smis = 
      (StreamMultiplexerInputStream)getInputStream(tag);
    return smis.smib;

  }

  public OutputStream getOutputStream(String tag) throws IOException {
    return getOutputStream(tag, false);
  }


  public OutputStream getOutputStream(String tag, boolean autoFlush)
       throws IOException {
    if (inMode)
      throw new IOException("StreamMultiplexer: invalid mode"); 
    synchronized (osTable){
      StreamMultiplexerOutputStream smos 
	= (StreamMultiplexerOutputStream)osTable.get(tag);
      if (smos == null){
	smos = new StreamMultiplexerOutputStream(this, tag, autoFlush);
	osTable.put(tag, smos);
      }
      return smos;
    }
  }


  public void shutdown() throws IOException{
    if (!inMode){
      synchronized (osTable){
	Iterator it = osTable.keySet().iterator();
	while (it.hasNext()){
	  Object key = it.next();
	  StreamMultiplexerOutputStream smos =
	    (StreamMultiplexerOutputStream)osTable.get(key);
	  smos.flush();
	}
      }

      //System.err.println("send stream close ");
      os.writeInt(STREAM_CLOSE);
      os.flush();
    }
  }



  synchronized public void send(String tag, int code, 
				byte[] buffer, int off, int len) throws IOException{
  if (debug)				  
     System.err.println("send [" + tag +"]" + len + "(" + code + ")") ;
  //    for (int i = 0; i <len; i++){
  //      char tmp = (char)(buffer[i]);
  //      if (tmp < 0x30 || tmp > 0x80)
  //tmp  = ' ';
			  //      System.err.print(buffer[i] + "(" + tmp +") ");
				  //    }
				  //    System.err.print("\n");

    os.writeInt(code);
    os.writeString(tag);
    os.writeInt(len);
    os.write(buffer, off, len);
    os.flush();
  }

  public void write(String tag,
		    byte[] buffer, int off, int len) throws IOException{
    send(tag, NONE, buffer, off, len);
  }

  public void close(String tag) throws IOException{
    send(tag, EOF, new byte[0], 0, 0);
  }

  class StreamMultiplexerReaderRunnable implements Runnable{
    byte [] buffer = new byte[4096];
    public void run(){
      try {
	while (true){
	  int code = is.readInt();
	  if (code == STREAM_CLOSE){
	    //	    System.err.println("recv stream close ");
	    break;
	  }
	  String tag = is.readString();
	  int len =  is.readInt();
	  if (debug)
	 	  System.err.println("read [" + tag + "] "+ len);
	  byte [] buffer = null;
	  if (len >= 0){
	    buffer = new byte[len];
	    is.readFully(buffer, 0, len);
	  }
	  StreamMultiplexerInputBuffer smib = getInputBuffer(tag);
	  if (code == EOF)
	    smib.eof();
	  else
	    smib.put(buffer, 0, len);
	}

      } catch (Exception e){
	e.printStackTrace();
      }
    }
  }



  static class StreamMultiplexerInputBuffer {
    byte[] buffer = new byte[4096];
    int currentPosition;
    boolean closed = false;
    boolean eof = false;

    StreamMultiplexerInputBuffer(){
      currentPosition = 0;
    }

    int available(){
      return currentPosition;
    }

    synchronized int read(byte[] b, int off, int len) throws IOException {
      if (closed)
	throw new IOException("StreamMultiplexerInputStream already closed.");
      if (eof && currentPosition == 0)
	return -1;
      
      int alen;

      while (currentPosition == 0){
	try {
	  wait();
	} catch(InterruptedException e){}
      }

      if (currentPosition < len)
	alen = currentPosition;
      else
	alen = len;

      System.arraycopy(buffer, 0, b, off, alen);
      System.arraycopy(buffer, alen, buffer, 0, currentPosition - alen);
      currentPosition -= alen;
      return alen;
    }

    private void grow(){
      byte [] tmp = new byte[buffer.length * 2];
      System.arraycopy(buffer, 0, tmp, 0, currentPosition);
      buffer = tmp;
      System.err.println("grew to "+ buffer.length);

    }

    synchronized int put(byte[] b, int off, int len){
      //      System.err.println("putting len"+ len);
      while (len + currentPosition > buffer.length){
	grow();
      }
      System.arraycopy(b, off, buffer, currentPosition, len);
      currentPosition += len;
      //      System.err.println("current = " + currentPosition);
      notify();
      return len;
    }
    
    synchronized long skip(long n){
      long tmp = currentPosition;
      currentPosition = 0;
      return tmp;
    }

    synchronized void close(){
      closed = true;
    }
    synchronized void eof(){
      eof = true;
    }
  }


  static class StreamMultiplexerInputStream extends java.io.InputStream{
    StreamMultiplexer sm;
    StreamMultiplexerInputBuffer smib;
    String tag;
    static final int bufSize = 4096;
    byte buffer[] = new byte[bufSize];
    int currentPosition = 0;

    StreamMultiplexerInputStream(StreamMultiplexer sm, String tag){
      this.sm = sm;
      this.smib = new StreamMultiplexerInputBuffer();
      this.tag = tag;
    }
    
    public int available(){
      return smib.available();
    }

    public void close(){
      smib.close();
    }

    public int read() throws IOException{
      byte[] b = new byte[1]; 
      if (read(b, 0, 1) < 0)
	return -1;
      return (0xff & (int)b[0]);
    }

    public int read(byte[] b) throws IOException{
      return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
      int tmp = smib.read(b, off, len);
      return tmp;
    }

    public long skip(long n){
      return smib.skip(n);
    }
    
    
    public void mark(int readlimit){}
    public boolean markSupported(){return false;}
    public void reset(){}


  }
  

  static class StreamMultiplexerOutputStream extends java.io.OutputStream{
    StreamMultiplexer sm;
    String tag;
    static final int bufSize = 4096;
    byte buffer[] = new byte[bufSize];
    int currentPosition = 0;
    boolean autoFlush = false;

    StreamMultiplexerOutputStream(StreamMultiplexer sm, String tag){
      this.sm = sm;
      this.tag = tag;
    }

    StreamMultiplexerOutputStream(StreamMultiplexer sm, String tag, 
				  boolean autoFlush){
      this.sm = sm;
      this.tag = tag;
      this.autoFlush = autoFlush;
    }

    public void close() throws IOException{
      sm.close(tag);
    }
    synchronized public void flush() throws IOException{
      sm.write(tag, buffer, 0, currentPosition);
      currentPosition = 0;
    }
    public void write(byte[] b) throws IOException{
      write(b, 0, b.length);
    }
    public void write(int b) throws IOException {
      write(new byte[]{(byte)b}, 0, 1);
    }

    synchronized public void write(byte[] b, int off, int len)
	 throws IOException{
      if (len > bufSize){
	flush();
	sm.write(tag, b, off, len);
	return;
      }
      if (len + currentPosition > bufSize)
	flush();

      System.arraycopy(b, off, buffer, currentPosition, len);
      currentPosition += len;
      if (autoFlush)
	flush();
    }

  }


  public static void main(String args[]){
    class TestRunnable implements Runnable{
      String tag;
      ObjectInputStream s;
      InputStream is;
      
      TestRunnable(String tag, StreamMultiplexer s) throws IOException{
	this.tag = tag;
	is = s.getInputStream(tag);
      }
      public void run() {
	try {
	  //	  System.err.println("testrunnable");
	  this.s = new ObjectInputStream(is);
	  //	  System.err.println("testrunnable");
	} catch (IOException e){
	  e.printStackTrace();
	} 

        while (true){
	  try {
	    Object tmp = s.readObject();
	    //	    System.err.println("------ " + tag + " : " + tmp);
	  } catch (IOException e){
	    e.printStackTrace();
	    break;
	  } catch (ClassNotFoundException e){
	    e.printStackTrace();
	  }
	}
      }
    }
    class PutRunnable implements Runnable{
      OutputStream os;
      PutRunnable(OutputStream os){
	this.os = os;
      }
      public void run(){
	try {
	  StreamMultiplexer som = new StreamMultiplexer(os);    
	  ObjectOutputStream oos1 = 
	  	    new ObjectOutputStream(som.getOutputStream("tag1", true));
	  ObjectOutputStream oos2 = 
	    new ObjectOutputStream(som.getOutputStream("tag2", true));
	  
	  //	  System.err.println("3");      
	  
	  oos1.writeObject("test1");
	  oos2.writeObject("test2");
	  oos1.writeObject("test1-2");
	  oos2.writeObject("test2-2");
	  
	  oos1.close();
	  oos2.close();
	} catch (IOException e){
	  e.printStackTrace();
	}
	
      }

    }

    try {
      PipedInputStream  pis = new PipedInputStream();
      PipedOutputStream pos = new PipedOutputStream(pis);    
      
      (new Thread(new PutRunnable(pos))).start();

      StreamMultiplexer sim = new StreamMultiplexer(pis);
      
      TestRunnable r1 = new TestRunnable("tag1", sim);
      TestRunnable r2 = new TestRunnable("tag2", sim);
      (new Thread(r1)).start();
      (new Thread(r2)).start();

    } catch (IOException e){
      e.printStackTrace();
    }


  }


}

