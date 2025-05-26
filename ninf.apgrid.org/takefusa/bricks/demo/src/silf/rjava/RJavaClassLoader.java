package silf.rjava;

class RJavaClassLoader extends ClassLoader{
  RJavaServerCore core;
  RJavaClassLoader(RJavaServerCore core){
    this.core = core;
  }
  public Class findClass(String name) throws ClassNotFoundException {
    byte[] b = null;
    try {
      b = loadClassData(name);
      return defineClass(name, b, 0, b.length);
    } catch (java.lang.ClassFormatError e){
      System.out.print("blen = " + b.length);
      for (int i = 0; i < 10; i++){
       System.out.print( (int)b[b.length - 10 + i] + " ");
      }
      System.out.println("");
      e.printStackTrace();
      throw new ClassNotFoundException();
    }
  }

  private byte[] loadClassData(String name) throws ClassNotFoundException {
    byte [] buffer = core.loadClassData(name);
    return buffer;
  }

}
