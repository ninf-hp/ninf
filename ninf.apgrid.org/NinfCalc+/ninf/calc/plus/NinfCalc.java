package ninf.calc.plus;
import java.awt.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;

public class NinfCalc extends Frame {
    static String serverHost = "ninf.etl.go.jp";
    static int serverPort = 3000;
    static String getSizeServerHost = "ninf.etl.go.jp";
    static int getSizeServerPort = 3000;
    static String paradiseURLString = "http://ninf.etl.go.jp/paradise/";

    static String[] bookmarks = {
	"http://ninf.etl.go.jp/~nakada/testdata/testdata.mmf", 
	"http://www.is.ocha.ac.jp:8080/~g9320536/cgi-bin/exec_matgen.cgi?Arg-1-n=100&Exname=Example3_6&format=mm&Content-Type=x-math%2Fmatrixmarket",
	"http://www.is.ocha.ac.jp:8080/~g9320536/cgi-bin/exec_matgen.cgi?Arg-1-n=500&Exname=Example3_6&format=mm&Content-Type=x-math%2Fmatrixmarket",
/*************
	"http://janus.etl.go.jp/testdata.coordination",

	"http://127.0.0.1/~nakada/testdata/20x20_tri4.coordination",
	"http://127.0.0.1/~nakada/testdata/20x20_tri5.coordination",
	"http://127.0.0.1/~nakada/testdata/30x30_tri4.coordination",
	"http://127.0.0.1/~nakada/testdada/30x30_tri5.coordination",
	"http://127.0.0.1/~nakada/testdata/494_bus.coordination",
	"http://127.0.0.1/~nakada/testdata/50x50.coordination"


	"http://r212-h136-65.sj.supercomp.org/~nakada/testdata/20x20_tri4.coordination",
	"http://r212-h136-65.sj.supercomp.org/~nakada/testdata/20x20_tri5.coordination",
	"http://r212-h136-65.sj.supercomp.org/~nakada/testdata/30x30_tri4.coordination",
	"http://r212-h136-65.sj.supercomp.org/~nakada/testdada/30x30_tri5.coordination",
	"http://r212-h136-65.sj.supercomp.org/~nakada/testdata/494_bus.coordination",
	"http://r212-h136-65.sj.supercomp.org/~nakada/testdata/50x50.coordination"
****************/
    };
    static URL paradiseURL;
    
    protected MenuBar menubar;
    protected Menu fileMenu, editMenu, bookmarksMenu;
    protected MenuItem closeMenuItem;
    protected Vector bookmarksMenuItems = new Vector();
    protected NinfCalcStack stack;
    protected NinfCalcStackPanel stackPanel;
    protected NinfCalcControlPanel controlPanel;
    protected String defaultServerHost;
    public static Applet applet;
    
    public NinfCalc(Applet applet, String defaultServerHost) {
	super("NinfCalc+");
	this.applet = applet;

	try {
	    paradiseURL = new URL(paradiseURLString);
	} catch (MalformedURLException e) {}

	fileMenu = new Menu("File");
	editMenu = new Menu("Edit");
	bookmarksMenu = new Menu("Bookmarks");
	fileMenu.add(closeMenuItem = new MenuItem("Close"));
	initBookmarksMenu();
	menubar = new MenuBar();
	menubar.add(fileMenu);
	menubar.add(editMenu);
	menubar.add(bookmarksMenu);
	setMenuBar(menubar);

	resize(500, 500);
	show();
	
	this.defaultServerHost = defaultServerHost;
	initModel();
	initView();
    }
    protected void initModel() {
	stack = new NinfCalcStack();
    }
    protected void initView() {
	setLayout(new BorderLayout());
        controlPanel = new NinfCalcControlPanel(stack, defaultServerHost);
	add("North", controlPanel);
	stackPanel = new NinfCalcStackPanel(stack);
	stack.setView(stackPanel);
	add("Center", stackPanel);
    }

    protected void initBookmarksMenu() {
	for (int i = 0; i < bookmarks.length; i++) {
	    MenuItem m = new MenuItem(NinfCalc.bookmarks[i]);
	    bookmarksMenu.add(m);
	    bookmarksMenuItems.addElement(m);
	}
    }

    public boolean action(Event ev, Object o) {
	if (ev.target == closeMenuItem) {
	    dispose();
	    return true;
	} else if (bookmarksMenuItems.contains(ev.target)) {
	    String selectedURL = (String)o;
	    controlPanel.setDataURL(selectedURL);
	    return true;
	}
	return false;
    }
}
