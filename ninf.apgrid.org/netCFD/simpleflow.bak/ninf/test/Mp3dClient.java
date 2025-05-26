/*
*/

import java.awt.*;
import java.applet.*;

import java.io.*;
import java.util.Vector;

import ninf.client.CallbackFunc;
import ninf.client.NinfClient;
import ninf.basic.NinfException;

public class Mp3dClient extends Applet implements Runnable{

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
  Font font = new Font("Helvetica", Font.BOLD, 12);

  Matrix3D amat = new Matrix3D(), tmat = new Matrix3D();
  int prevx, prevy;
  float xtheta, ytheta;
  DrawPanel dp;

  Button zoomUp, zoomDown, up, down, left, right;
  Button restart, pause;

  Panel initButtonPanel(){
    Panel dummyRoot = new Panel();
    dummyRoot.setLayout(new GridLayout(3,1));

    Panel root = new Panel();
    root.setLayout(new BorderLayout());
    root.setBackground(Color.white);
    
    Panel startPanel = new Panel();
    restart = new Button("Restart");
    pause = new Button("Stop");
    startPanel.add(pause);
    startPanel.add(restart);

    Panel zoomPanel = new Panel();
    zoomPanel.setLayout(new GridLayout(2,1));

    zoomUp = new Button("Zoom Up");
    zoomDown = new Button("Zoom Down");
    zoomPanel.add(zoomUp);
    zoomPanel.add(zoomDown);

    Panel tmp = new Panel();
    up = new Button("/\\");
    down = new Button("\\/");
    left = new Button("<");
    right = new Button(">");
    up.setBackground(Color.gray);
    up.setForeground(Color.white);
    down.setBackground(Color.gray);
    down.setForeground(Color.white);
    left.setBackground(Color.gray);
    left.setForeground(Color.white);
    right.setBackground(Color.gray);
    right.setForeground(Color.white);

    tmp.setLayout(new GridLayout(3,3));
    tmp.add(new Panel());    
    tmp.add(up);
    tmp.add(new Panel());    

    tmp.add(left);    
    tmp.add(new Panel());    
    tmp.add(right);    

    tmp.add(new Panel());    
    tmp.add(down);
    tmp.add(new Panel());    

    root.add("Center", tmp);
    root.add("South", startPanel);
    root.add("North", zoomPanel);

    Panel dummy1 = new Panel();
    dummy1.setBackground(Color.gray);
    Label dummy2 = new Label("MP3D", Label.CENTER);
    dummy2.setFont(new Font("Helevetica", Font.BOLD, 20));
    dummy2.setBackground(Color.gray);
    dummy2.setForeground(Color.yellow);
    dummyRoot.add(dummy2);
    dummyRoot.add(dummy1);
    dummyRoot.add(root);

    return dummyRoot;
  }

  public void init() {
    setFont(font);
    setBackground(Color.white);
    ScanfInputStream is;
    Panel buttonPanel = initButtonPanel();
    try {
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

      setLayout(new BorderLayout());
      
      if (isApplet){
	String geometry = getParameter("geometry");
	is = new ScanfInputStream(new StringBufferInputStream(geometry));
      } else {
	is = new ScanfInputStream(new FileInputStream(geometryFile));
      }
      dp = new DrawPanel(this);    
      add("Center", dp);
      add("East", buttonPanel);
      initFunc =  new DisplayInitFunc();
      dispFunc =  new DisplayFunc(dp);
      
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
      ninf = null;
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

    public boolean mouseDown(Event e, int x, int y) {
	prevx = x;
	prevy = y;
	return true;
    }
    public boolean mouseDrag(Event e, int x, int y) {
	tmat.unit();
	float xtheta = (prevy - y) * 360.0f / size().width;
	float ytheta = (x - prevx) * 360.0f / size().height;
	tmat.xrot(xtheta);
	tmat.yrot(-ytheta);
	amat.mult(tmat);
	repaint();

	prevx = x;
	prevy = y;
	return true;
    }

  public boolean action(Event evt, Object arg){
    if (evt.target == zoomUp){
      amat.scale((float)1.2);
      repaint();
      return true;
    } else if (evt.target == zoomDown){  
      amat.scale((float)(1.0/1.2));
      repaint();
      return true;
    } else if (evt.target == up){  
      tmat.unit();
      tmat.xrot(10.0f);
      amat.mult(tmat);
      repaint();
      return true;
    } else if (evt.target == down){  
      tmat.unit();
      tmat.xrot(-10.0f);
      amat.mult(tmat);
      repaint();
      return true;
    } else if (evt.target == left){  
      tmat.unit();
      tmat.yrot(10.0f);
      amat.mult(tmat);
      repaint();
      return true;
    } else if (evt.target == right){  
      tmat.unit();
      tmat.yrot(-10.0f);
      amat.mult(tmat);
      repaint();
      return true;
    } else if (evt.target == pause){  
      stop();
      return true;
    } else if (evt.target == restart){  
      start();
      return true;
    } else {
      return false;
    }
  }

  public static void main(String args[]) {
    args = NinfClient.parseArg(args);
    try{
      Frame f = new Frame("Mp3dClient");
      Mp3dClient client = new Mp3dClient();
      client.isApplet = false;
      client.init();
      f.add("Center", client);
      f.resize(600, 500);
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
  public void repaint(){
    dp.repaint();
  }
}

class DisplayInitFunc implements CallbackFunc{
  public void callback(Vector args){
  }
}

class DisplayFunc implements CallbackFunc{
  DrawPanel dp;
  DisplayFunc(DrawPanel dp){
    this.dp = dp;
  }
  public void callback(Vector args){
    Toolkit.getDefaultToolkit().sync();
    int num = ((int[])args.elementAt(0))[0];
    float points[] = (float[])args.elementAt(1);
    dp.display(num, points);  
  }
}

class DrawPanel extends Panel{
  static float verts[][] = {
    { 2.0f,  2.0f, 2.0f},
    { 2.0f, 22.0f, 2.0f},
    {12.0f,  2.0f, 2.0f},
    {12.0f, 22.0f, 2.0f},
    { 2.0f,  2.0f, 5.0f},
    { 2.0f, 22.0f, 5.0f},
    {12.0f,  2.0f, 5.0f},
    {12.0f, 22.0f, 5.0f}};

  static int edges[][] = {
    {0,1},{0,2},{2,3},{1,3},
    {4,5},{4,6},{6,7},{5,7},
    {0,4},{1,5},{2,6},{3,7}};

  int num = 0;
  float points[];
  Image sub_im;
  Graphics sub_g;
  Mp3dClient parent;
  Color colorArray[][] = {
    {new Color(0.35f,0.0f, 0.35f),
     new Color(0.55f,0.0f, 0.55f),
     new Color(0.75f,0.0f, 0.75f),
     new Color(1.0f, 0.0f, 1.0f)},
    {new Color(0.0f, 0.35f,0.35f),
     new Color(0.0f, 0.55f,0.55f),
     new Color(0.0f, 0.75f,0.75f),
     new Color(0.0f, 1.0f, 1.0f)}};

  Color backColor = Color.black;

  DrawPanel(Mp3dClient parent){
    this.parent = parent;
    setBackground(backColor);
  }

  void display(int i, float points[]){
    num = i;
    this.points = points;
    //    System.out.println("repainting");
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
  public void reshape(int x, int y, int width, int height){
    sub_g = null;
    super.reshape(x, y, width, height);
  }
  public void update(Graphics g) {
    if (sub_g == null){
      sub_im = this.createImage(size().width, size().height);
      sub_g = sub_im.getGraphics();
    }
    sub_g.setColor(backColor);
    sub_g.fillRect(0, 0, size().width, size().height);
    sub_g.setColor(getForeground());
    paint(sub_g);
    //	System.out.println(sub_g);
    g.drawImage(sub_im, 0, 0, this);
  }

  public void paint(Graphics g) {
    Matrix3D mat = new Matrix3D();
    mat.unit();
    //    mat.translate(-(md.xmin + md.xmax) / 2,
    //		     -(md.ymin + md.ymax) / 2,
    //		     -(md.zmin + md.zmax) / 2);
    mat.translate(-7, -12, (float)-3.5);
    mat.mult(parent.amat);
//	    md.mat.scale(xfac, -xfac, 8 * xfac / size().width);


    mat.scale(20,20,20);
    //    mat.scale(xfac, -xfac, 16 * xfac / size().width);
    //    mat.translate(size().width / 2, size().height / 2, 8);
    mat.translate(size().width / 2, size().height / 2, 0);

    drawEdges(g, mat);
    g.setColor(Color.black);

    //    System.out.println("painting: num = "+ num 
    //		       + ", points.size = " + points.length); 
    float ret[] = new float[3];
    for (int i = 0; i < Math.min(num, Mp3dClient.MAX_MOL) ; i++){


      // int x = (int)(points[i * 6] * 20);
      // int y = (int)(points[i * 6 + 1] * 20);
      mat.transform(points[i * 6], points[i * 6 + 1], points[i * 6 + 2], ret);
      g.setColor(decideColor(ret[2], points[i*6 +3]));
      int x = (int)ret[0];
      int y = (int)ret[1];
      g.drawLine(x,y,x,y);
    }
  }

  Color decideColor(float pos, float vx){
    int index = 0;
    if (vx < 0)
      index = 1;
    if (pos > 50)
      return colorArray[index][0];
    else if (pos > 32)
      return colorArray[index][1];
    else if (pos > 15)
      return colorArray[index][2];
    else
      return colorArray[index][3];
  }

  void drawEdges(Graphics g, Matrix3D mat){
    g.setColor(Color.gray);
    for (int i = 0; i < edges.length; i++){
      int p1 = edges[i][0];
      int p2 = edges[i][1];
      float ret1[] = new float[3];
      float ret2[] = new float[3];
      mat.transform(verts[p1][0], verts[p1][1], verts[p1][2], ret1);
      mat.transform(verts[p2][0], verts[p2][1], verts[p2][2], ret2);
      double tmp =  (ret1[2] + ret2[2])/2.0;
      if (tmp > 0.9)
	g.setColor(Color.gray);
      else if (tmp > 0.5)
	g.setColor(Color.darkGray);
      else if (tmp > 0.1)
	g.setColor(Color.lightGray);
      else
	g.setColor(Color.white);
      g.drawLine((int)ret1[0], (int)ret1[1], (int)ret2[0], (int)ret2[1]);
    }
  }
}
