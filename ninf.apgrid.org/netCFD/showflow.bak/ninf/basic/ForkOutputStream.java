package ninf.basic;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;


public class ForkOutputStream extends OutputStream{
  
  Vector outputStreams = new Vector();
    
  public void addStream(OutputStream os){
    outputStreams.addElement(os);
  }
    
  
    public void write(int b) throws IOException {
      Vector failed = new Vector();
      Enumeration enum = outputStreams.elements();
      while (enum.hasMoreElements()){
	OutputStream os = (OutputStream)enum.nextElement();
	try {
	  os.write(b);
	} catch (IOException e){
	  failed.addElement(os);
	}
      }
      Enumeration enum2 = failed.elements();
      while (enum.hasMoreElements()){
	OutputStream os = (OutputStream)enum.nextElement();
	outputStreams.removeElement(os);
      }      
    }

    public void write(byte b[], int off, int len) throws IOException {
      Vector failed = new Vector();
      Enumeration enum = outputStreams.elements();
      while (enum.hasMoreElements()){
	OutputStream os = (OutputStream)enum.nextElement();
	try {
	  os.write(b, off, len);
	} catch (IOException e){
	  failed.addElement(os);
	}
      }
      Enumeration enum2 = failed.elements();
      while (enum.hasMoreElements()){
	OutputStream os = (OutputStream)enum.nextElement();
	outputStreams.removeElement(os);
      }      
    }
    public void flush() throws IOException {
      Vector failed = new Vector();
      Enumeration enum = outputStreams.elements();
      while (enum.hasMoreElements()){
	OutputStream os = (OutputStream)enum.nextElement();
	try {
	  os.flush();
	} catch (IOException e){
	  failed.addElement(os);
	}
      }
      Enumeration enum2 = failed.elements();
      while (enum.hasMoreElements()){
	OutputStream os = (OutputStream)enum.nextElement();
	outputStreams.removeElement(os);
      }      
    }
    public void close() throws IOException {
      Vector failed = new Vector();
      Enumeration enum = outputStreams.elements();
      while (enum.hasMoreElements()){
	OutputStream os = (OutputStream)enum.nextElement();
	try {
	  os.close();
	} catch (IOException e){
	  failed.addElement(os);
	}
      }
      Enumeration enum2 = failed.elements();
      while (enum.hasMoreElements()){
	OutputStream os = (OutputStream)enum.nextElement();
	outputStreams.removeElement(os);
      }      
    }

  public static void main(String strs[]){
    ForkOutputStream os = new ForkOutputStream();
    os.addStream(System.out);
    os.addStream(System.out);
    PrintStream tmp = new PrintStream(os);
    tmp.println("sarusaru");
    
  }
}
