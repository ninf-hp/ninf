package ninf.basic;

import java.io.*;

public
class XDRInputStream extends FilterInputStream {

  private static int XDRLIMIT = 4;
  public boolean debug = false;

  public XDRInputStream(InputStream i){
    super(i);
  }
  public String readString() throws IOException {
    int length = this.readInt();
    byte cont[] = new byte[length];
    int rest = length % XDRLIMIT;
    rest = (rest != 0)? XDRLIMIT - rest: rest;
    for (int i = 0; i < length; i++){
      cont[i] = this.readByte();
    }
    for (int i = 0; i < rest; i++){
      byte tmp = this.readByte();
    }
    return new String(cont, 0);
  }

  public final int readBytes(byte b[])  throws IOException {
    return readBytes(b, 0, b.length);
  }
  
  public final int readBytes(byte b[], int off, int len)  throws IOException {
    InputStream in = this.in;
    int i, n = 0;
    while (n < len) {
      i = in.read(b, off + n, len - n);
      if (debug)
	for (int li = 0;  li < i; li++)
	  System.out.println(b[off+n+li]+ " ");
      if (i < 0) {
	return (n > 0) ? n : -1;
      }
      n += i;
    }
    return n;
  }

  public final int readFully(byte b[])  throws IOException {
    return readBytes(b, 0, b.length);
  }
  
  public final int readFully(byte b[], int off, int len)  throws IOException {
    InputStream in = this.in;
    int i, n = 0;
    while (n < len) {
      i = in.read(b, off + n, len - n);
      if (debug)
	for (int li = 0;  li < i; li++)
	  System.out.println(b[off+n+li]+ " ");
      if (i < 0) {
	return (n > 0) ? n : -1;
      }
      n += i;
    }
    return n;
  }
  
  public final int skipBytes(int n)  throws IOException {
    InputStream in = this.in;
    for (int i = 0 ; i < n ; i += in.skip(n - i));
    return n;
  }

  public final boolean readBoolean()  throws IOException {
    return in.read() != 0;
  }

    /**
     * Reads an 8 bit byte.
     */
    public final byte readByte()  throws IOException {
      int tmp = in.read();
      if (tmp == -1) throw(new IOException());
      return (byte)tmp;
    }

    /**
     * Reads 16 bit short.
     */
    public final short readShort()  throws IOException {
      InputStream in = this.in;
      int tmp1 = in.read();
      if (tmp1 == -1) throw(new IOException());
      int tmp2 = in.read();
      if (tmp2 == -1) throw(new IOException());
	return (short)(((tmp1 << 8) & (0xFF << 8)) |
		       ((tmp2 << 0) & (0xFF << 0)));
    }

    /**
     * Reads a 16 bit char.
     */
    public final char readChar()  throws IOException {
	InputStream in = this.in;
      int tmp1 = in.read();
      if (tmp1 == -1) throw(new IOException());
      int tmp2 = in.read();
      if (tmp2 == -1) throw(new IOException());
	return (char)(((tmp1 << 8) & (0xFF << 8)) |
		      ((tmp2 << 0) & (0xFF << 0)));

    }

    /**
     * Reads a 32 bit int.
     */
    public final int readInt()  throws IOException {
	InputStream in = this.in;
      int tmp1 = in.read();
      if (tmp1 == -1) throw(new IOException());
      int tmp2 = in.read();
      if (tmp2 == -1) throw(new IOException());
      int tmp3 = in.read();
      if (tmp3 == -1) throw(new IOException());
      int tmp4 = in.read();
      if (tmp4 == -1) throw(new IOException());
	return ((tmp1 << 24) & (0xFF << 24)) |
	       ((tmp2 << 16) & (0xFF << 16)) |
	       ((tmp3 <<  8) & (0xFF <<  8)) |
	       ((tmp4 <<  0) & (0xFF <<  0));
    }

    /**
     * Reads a 64 bit long.
     */
    public final long readLong()  throws IOException {
	InputStream in = this.in;
      int tmp1 = in.read();
      if (tmp1 == -1) throw(new IOException());
      int tmp2 = in.read();
      if (tmp2 == -1) throw(new IOException());
      int tmp3 = in.read();
      if (tmp3 == -1) throw(new IOException());
      int tmp4 = in.read();
      if (tmp4 == -1) throw(new IOException());
      int tmp5 = in.read();
      if (tmp5 == -1) throw(new IOException());
      int tmp6 = in.read();
      if (tmp6 == -1) throw(new IOException());
      int tmp7 = in.read();
      if (tmp7 == -1) throw(new IOException());
      int tmp8 = in.read();
      if (tmp8 == -1) throw(new IOException());
	return (((long)tmp1 << 56) & ((long)0xFF << 56)) |
	       (((long)tmp2 << 48) & ((long)0xFF << 48)) |
	       (((long)tmp3 << 40) & ((long)0xFF << 40)) |
	       (((long)tmp4 << 32) & ((long)0xFF << 32)) |
	       (((long)tmp5 << 24) & ((long)0xFF << 24)) |
	       (((long)tmp6 << 16) & ((long)0xFF << 16)) | 
	       (((long)tmp7 <<  8) & ((long)0xFF <<  8)) |
	       (((long)tmp8 <<  0) & ((long)0xFF <<  0));
    }

    /**
     * Reads a 32 bit float.
     */
    public final float readFloat()  throws IOException {
      return (new DataInputStream(in).readFloat());
    }

    /**
     * Reads a 64 bit double.
     */
    public final double readDouble()  throws IOException {
      return (new DataInputStream(in).readDouble());
    }

    /**
     * Reads a line terminated by a '\n' or EOF.
     */
    public final String readLine()  throws IOException {
	InputStream in = this.in;
	StringBuffer input = new StringBuffer();
	int c;

	while (((c = in.read()) != -1) && (c != '\n')) {
	    input.append((char)c);
	}
	if ((c == -1) && (input.length() == 0)) {
	    return null;
	}
	return input.toString();
    }

    /**
     * Reads a UTF format string.
     */
    public final String readUTF()  throws IOException {
	InputStream in = this.in;
	int utflen = ((in.read() << 8) & 0xFF00) |
	             ((in.read() << 0) & 0x00FF);
	char str[] = new char[utflen];
	int strlen = 0;


	for (int i = 0 ; i < utflen ;) {
	    int c = in.read();
	    if ((c & 0x80) == 0) {
		str[strlen++] = (char)c;
		i++;
	    } else if ((c & 0xE0) == 0xC0) {
		str[strlen++] = (char)(((c & 0x1F) << 6) | (in.read() & 0x3F));
		i += 2;
	    } else {
		str[strlen++] = (char)(((c & 0x0F) << 12) |
				       ((in.read() & 0x3F) << 6) |
				       (in.read() & 0x3F));
		i += 3;
	    } 
	}
	return new String(str, 0, strlen);
    }

}

