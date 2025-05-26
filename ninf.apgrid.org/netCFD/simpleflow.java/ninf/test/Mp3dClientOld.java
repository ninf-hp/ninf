/*
*/

import java.awt.*;
import java.applet.*;

import java.io.*;
import java.util.Vector;

import ninf.client.CallbackFunc;
import ninf.client.NinfClient;
import ninf.basic.NinfException;

public class Mp3dClientOld extends Applet implements Runnable{

  boolean isApplet = true;
  static String geometryFile = "test.geom";

  static final int MAX_MOL = 4000;
  static final int MAX_BCNT = 36;
  int tmpint[] = new int[MAX_BCNT * 3];
  float tmpdata[] = new float[MAX_BCNT * 6];
  int l_num_xcell, l_num_ycell, l_num_zcell, l_bc_cnt;
  int bc_count;
  CallbackFunc initFunc;
  CallbackFunc dispFunc;
  Thread ninf;
  NinfClient client;

  public void init() {
    ScanfInputStream is;

    try {
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

      setLayout(new BorderLayout());
      
      if (isApplet){
	String geometry = getParameter("geometry");
	is = new ScanfInputStream(new StringBufferInputStream(geometry));
      } else {
	is = new ScanfInputStream(new FileInputStream(geometryFile));
      }
      DrawPanelOld dp = new DrawPanelOld();    
      add("Center", dp);
      initFunc =  new DisplayInitFuncOld();
      dispFunc =  new DisplayFuncOld(dp);
      
      getGeometry(is);
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      String host = null;
      String portStr = null;
      int port = 3000;
      if (isApplet){
	String defaultServerHost = getCodeBase().getHost();
	if (defaultServerHost == null || defaultServerHost.equals("")) {
	    defaultServerHost = "hpc.etl.go.jp";
	}
	portStr = getParameter("port");
	if (portStr != null)
	  port = (new Integer(portStr)).intValue();
	client = new NinfClient(defaultServerHost, port);
      } else {
	client = new NinfClient();
      }
      client.verbose();
      client.
	callWith("mp3d/mp3dx", new Integer(l_num_xcell), new Integer(l_num_ycell),
		          new Integer(l_num_zcell), new Integer(l_bc_cnt),
		          new Integer(MAX_BCNT), (Object)tmpint, (Object)tmpdata, 
	                  new Integer(MAX_MOL), initFunc, dispFunc);

      System.err.println("mp3d: done");
    }catch(NinfException e){
      System.err.println("an error occurred in execution: " + e);
    }
  }

  public void start() {
    if (ninf == null){
      ninf = new Thread(this);
      ninf.setPriority(Thread.MIN_PRIORITY);
      ninf.start();
    } 
  }

  public void stop(){
    if (ninf != null){
      ninf.stop();
      if (client != null)
	client.disconnect();
      ninf = null;
      client = null;
    }
  }

  public static void main(String args[]) {
    args = NinfClient.parseArg(args);
    try{
      Frame f = new Frame("Mp3dClientOld");
      Mp3dClientOld client = new Mp3dClientOld();
      client.isApplet = false;
      client.init();
      f.add("Center", client);
      f.resize(300, 300);
      f.show();
      client.start();

    } catch (Exception e){
      e.printStackTrace();
    }
  }

  void getGeometry(ScanfInputStream is) throws Exception{
    int i, cx, cy, cz;
    int tmpintIndex = 0;
    int tmpdataIndex = 0;
    float f1, f2, f3, f4, f5, f6;
    l_num_xcell = is.readInt();
    l_num_ycell = is.readInt();
    l_num_zcell = is.readInt();
    l_bc_cnt    = is.readInt();

    for (bc_count = 0; bc_count < l_bc_cnt; bc_count++){
      tmpint[tmpintIndex++] = is.readInt();
      tmpint[tmpintIndex++] = is.readInt();
      tmpint[tmpintIndex++] = is.readInt();
      
      tmpdata[tmpdataIndex++] = is.readFloat();
      tmpdata[tmpdataIndex++] = is.readFloat();
      tmpdata[tmpdataIndex++] = is.readFloat();
      tmpdata[tmpdataIndex++] = is.readFloat();
      tmpdata[tmpdataIndex++] = is.readFloat();
      tmpdata[tmpdataIndex++] = is.readFloat();
    }
  }
}

class DisplayInitFuncOld implements CallbackFunc{
  public void callback(Vector args){
  }
}

class DisplayFuncOld implements CallbackFunc{
  DrawPanelOld dp;
  DisplayFuncOld(DrawPanelOld dp){
    this.dp = dp;
  }
  public void callback(Vector args){
    Toolkit.getDefaultToolkit().sync();
    int num = ((int[])args.elementAt(0))[0];
    float points[] = (float[])args.elementAt(1);
    dp.display(num, points);  
  }
}

class DrawPanelOld extends Panel{
  int num = 0;
  float points[];
  Image sub_im;
  Graphics sub_g;

  void display(int i, float points[]){
    num = i;
    this.points = points;
    System.out.println("repainting");
    // printThreads();
    repaint();

  }


  public void repaint(){
    super.repaint();
  }

  public void printThreads(){
    ThreadGroup tg = Thread.currentThread().getThreadGroup();
    ThreadGroup tmp;
    while ((tmp = tg.getParent())!= null)
      tg = tmp;
    Thread lists[] = new Thread[tg.activeCount()];
    int count = tg.enumerate(lists);
    for (int i = 0; i < count; i++)
      System.out.println(lists[i]);

  }

  public void update(Graphics g) {
    if (sub_g == null){
      sub_im = this.createImage(size().width, size().height);
      sub_g = sub_im.getGraphics();
    }
    sub_g.setColor(Color.white);
    sub_g.fillRect(0, 0, size().width, size().height);
    sub_g.setColor(getForeground());
    paint(sub_g);
    //	System.out.println(sub_g);
    g.drawImage(sub_im, 0, 0, this);
  }

  public void paint(Graphics g) {
    g.setColor(Color.blue);

    System.out.println("painting: num = "+ num 
		       + ", points.size = " + points.length); 
    for (int i = 0; i < Math.min(num, Mp3dClientOld.MAX_MOL) ; i++){
      int x = (int)(points[i * 6] * 20);
      int y = (int)(points[i * 6 + 1] * 20);
      g.drawLine(x,y,x,y);
    }

  }
}
