package silf.rjava;

import silf.util.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.security.*;

public class ClassFileLoader {
  String [] classPathes;

  public ClassFileLoader () {
    Vector v = new Vector();
    String tmp = System.getProperty("java.class.path");    
    StringTokenizer st = new StringTokenizer(tmp, ":");
    while (st.hasMoreTokens()){
      v.add(st.nextToken());
    }
    classPathes = new String[v.size()];
    for (int i = 0; i < v.size(); i ++)
      classPathes[i] = (String)v.elementAt(i);
  }

  private byte[] tryJar(File file, String name)
       throws IOException, ClassNotFoundException{
    JarFile jar = new JarFile(file.getPath());
    JarEntry jarEntry = jar.getJarEntry(name);
    if (jarEntry == null)
      throw new ClassNotFoundException();
    InputStream is = jar.getInputStream(jarEntry);

    int length = (int)jarEntry.getSize();
    byte [] buffer = new byte[length];
    int read = 0;
    while (read < length){
      int tmp = is.read(buffer, read, length - read);
      read += tmp;
    }
    return buffer;
  }

  private byte[] tryFile(File file, String name)
       throws IOException, ClassNotFoundException{
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

  public byte[] loadClassData(String name) 
       throws IOException, ClassNotFoundException{
    byte [] buffer;
    name = name.replace('.', '/') + ".class";
    for (int i = 0; i < classPathes.length; i++){
      File file = new File(classPathes[i]);
      if (file.isFile()){
	try {
	  return tryJar(file, name);
	} catch (ClassNotFoundException e){
	  continue;
	}
      }
      
      String fileName = classPathes[i] + "/" + name;
      file = new File(fileName);
      if (file.isFile())
	return tryFile(file, name);
    }
    throw new ClassNotFoundException();
  }
}
