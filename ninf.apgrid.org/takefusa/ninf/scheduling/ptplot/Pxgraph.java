/* Java implementation of the X11 pxgraph plotting program

 Copyright (c) 1997 The Regents of the University of California.
 All rights reserved.
 Permission is hereby granted, without written agreement and without
 license or royalty fees, to use, copy, modify, and distribute this
 software and its documentation for any purpose, provided that the above
 copyright notice and the following two paragraphs appear in all copies
 of this software.

 IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
 FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.
 
                                        PT_COPYRIGHT_VERSION_2
                                        COPYRIGHTENDKEY
*/

package ptplot;

import java.awt.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.lang.Thread;
import java.lang.InterruptedException; 

import java.util.Properties;
import java.util.StringTokenizer;

//////////////////////////////////////////////////////////////////////////
//// Pxgraph
/** 
This class is a Java application that uses the Plot Java applet to
simulate the <code>pxgraph</code> X Windows system program.  
<p>
The <code>pxgraph</code> script is a Bourne shell script that
attempts to call Java with the proper environment.  The 
<code>pxgraph</code> script has the following usage:
<br>
<code>pxgraph <i>[ options ]  [ =WxH+X+Y ] [file . . .]</i></code>
<p>
Below we describe the <code>pxgraph</code> arguments.  The
text is based on the <code>xgraph</code> Unix man page written
by David Harrison (University of California).
To see the command line options, you can type
<code>pxgraph -help</code>.
<p>
The <code>pxgraph</code> program draws a graph on a display given data
read from either data files or from standard input if no
files are specified. It can display up to 64 independent
data sets using different colors and/or line styles for each
set. It annotates the graph with a title, axis labels,
grid lines or tick marks, grid labels, and a legend. There
are options to control the appearance of most components of
the graph.
<p>
The input format is similar to <code>graph(<i>1G</i>)</code> but differs
slightly. The data consists of a number of <I>data</I> <I>sets</I>. Data
sets are separated by a blank line. A new data set is also
assumed at the start of each input file. A data set consists
of an ordered list of points of the form <code><i>directive</i>
X Y</code>.
The directive is either <code>draw</code> or <code>move</code> and can be
omitted. If the directive is <code>draw</code>, a line will be drawn
between the previous point and the current point (if a line
graph is chosen). Specifying a <code>move</code> directive tells
xgraph not to draw a line between the points. If the directive
is omitted, <code>draw</code> is assumed for all points in a data
set except the first point where <code>move</code> is assumed. The
<code>move</code> directive is used most often to allow discontinuous
data in a data set. 

After <code>pxgraph</code> has read the data, it will create a new window
to graphically display the data.

Once the window has been opened, all of the data sets will
be displayed graphically (subject to the options explained
below) with a legend in the upper right corner of the
screen. To zoom in on a portion of the graph, depress a
mouse button in the window and sweep out a region. <code>pxgraph</code>
will then the window will be redrawn with just that portion of
the graph. <code>pxgraph</code> also presents four control buttons in
the lower left corner of each window: <code>Exit</code>, 
<code>Print</code>, <code>HTML</code> and <code>About</code>.
<p>The <code>Exit</code> button will exit the process.  You can also
type <code>Control-D</code>, <code>Control-C</code> or <code>q</code>
to exit.
<p>The <code>Print</code> button brings up a print dialog window.
<p>The <code>About</code> button brings up a message about 
<code>pxgraph</code>.
<p>The <code>HTML</code> button prints an HTML file to stdout that
can be used to display the file with applet <code>Plot</code> classes
(Experimental).
<p>
<code>pxgraph</code> accepts a large number of commmand line options.
A list of these options is given below.
<p>

<dl>
<dt><code>=<i>W</i>x<i>H</i>+<i>X</i>+<i>Y</i></code>
<dd>Specifies the initial size and location of the pxgraph
window.

<dt> <code>-<i>&lt;digit&gt; &lt;name&gt;</i></code>
<dd> These options specify the data
set name for the corresponding data set. The digit
should be in the range 0 to 63. This name will be
used in the legend.

<dt><code>-bar</code>
<dd>Specifies that vertical bars should be drawn from the
data points to a base point which can be specified with
<code>-brb</code>.
Usually, the <code>-nl</code> flag is used with this option.
The point itself is located at the center of the bar.

<dt><code>-bb</code>
<dd>Draw a bounding box around the data region. This is
very useful if you prefer to see tick marks rather than
grid lines (see <code>-tk</code>).
<b>Unsupported in the Java version.</b>

<dt><code>-bd</code> <code><i>&lt;color&gt;</i></code>
<dd>This specifies the border color of the <code>pxgraph</code> window.
<b>Unsupported in the Java version.</b>

<dt><code>-bg</code> <code><i>&lt;color&gt;</i></code>
<dd>Background color of the <code>pxgraph</code> window.
<b>In the Java version, this argument takes hexadecimal color values 
(<code>fffff</code>), not color names.</b>

<dt><code>-binary</code>
<dd>Data files are in binary format.  The <code>-binary</code>
argument is the primary difference between <code>xgraph</code>
and <code>pxgraph</code>.  The 
<A HREF="http://ptolemy.eecs.berkeley.edu">Ptolemy Project</A> software
makes extensive use of <code>-binary</code>.
<br>The plot commands are encoded as single characters, and the numeric data 
is a 4 byte float.  
 <br>The commands are encoded as follows:
  <dl>
  <dt> <code>d <I>&lt;4byte float&gt; &lt;4byte float&gt;</I></code>
  <dd> Draw a X,Y point
  <dt> <code>e</code>
  <dd> End of dataset
  <dt> <code>n <I>&lt;dataset name&gt;</I>&#92n</code>
  <dd> New dataset name, ends in <code>&#92n</code>
  <dt> <code>m <I>&lt;4byte float&gt; &lt;4byte float&gt;</I></code>
  <dd> Move to a X,Y point.
  </dl>
 <br>To view a binary plot file under unix, we can use the 
<code>od</code> command.  Note that the first character is a <code>d</code>
followed by eight bytes of data consisting of two floats of four bytes.
<pre>
cxh@carson 324% od -c data/integrator1.plt
0000000   d  \0  \0  \0  \0  \0  \0  \0  \0   d   ? 200  \0  \0   ? 200
0000020  \0  \0   d   @  \0  \0  \0   @   , 314 315   d   @   @  \0  \0

</pre>

<dt><code>-brb</code> <code><i>&lt;base&gt;</i></code>
<dd>This specifies the base for a bar graph. By default,
the base is zero.
<b>Unsupported in the Java version.</b>

<dt><code>-brw</code> <code><i>&lt;width&gt;</i></code>
<dd>This specifies the width of bars in a bar graph. The
amount is specified in the user units. By default,
a bar one pixel wide is drawn.

<dt><code>-bw</code> <code><i>&lt;size&gt;</i></code>
<dd>Border width (in pixels) of the <code>pxgraph</code> window.
<b>Unsupported in the Java version.</b>

<dt><code>-db</code>
<dd>Causes xgraph to run in synchronous mode and prints out
the values of all known defaults.

<dt><code>-fg</code> <code><i>&lt;color&gt;</i></code>
<dd>Foreground color. This color is used to draw all text
and the normal grid lines in the window.
<b>In the Java version, this argument takes hexadecimal color values 
(<code>fffff</code>), not color names.</b>

<dt><code>-gw</code> <dd>
Width, in pixels, of normal grid lines.
<b>Unsupported in the Java version.</b>

<dt><code>-gs</code> <dd>
Line style pattern of normal grid lines.

<dt><code>-impulses</code> <dd>
Draw a line from any plotted point down to the x axis.
(This argument is not present in the X11 <code>pxgraph</code>,
but it is similar to <code>-nl -bar</code>).

<dt><code>-lf</code> <code><i>&lt;fontname&gt;</i></code>
<dd>Label font. All axis labels and grid labels are drawn
using this font. 
<b>Note that the Java version does not use X11 style font specification.</b>
In the Java version, fonts may be specified as
<menu>
<li><code><i>fontname</i></code>, where 
<code><i>fontname</i></code> is one of <code>helvetica</code>,
 <code>TimesRoman</code>, <code>Courier</code>,  <code>Dialog</code>,
<code>DialogInput</code>, <code>ZapfDingbats</code>.

<li><code><i>fontname</i>-<i>style</i></code>, where
<code><i>style</i></code> is one of
<code>PLAIN</code>, <code>ITALIC</code>, <code>BOLD</code>,
 i.e. <code>helvetica-ITALIC</code>
<li><code><i>fontname</i>-<i>size</i></code>, or
<li><code><i>fontname</i>-<i>style</i>-<i>size</i></code>, where
<code><i>size</i></code> is an integer font size in points.
</menu>
The default is <code>helvetica-PLAIN-12</code>.

<dt><code>-lnx</code> <dd>
Specifies a logarithmic X axis. Grid labels represent
powers of ten.
<b>Unsupported in the Java version.</b>

<dt><code>-lny</code> <dd>
Specifies a logarithmic Y axis. Grid labels represent
powers of ten.
<b>Unsupported in the Java version.</b>

<dt><code>-lw</code> <code><i>width</i></code> <dd>
Specifies the width of the data lines in pixels. The
default is zero.
<b>Unsupported in the Java version.</b>

<dt><code>-lx</code> <code><i>&lt;xl,xh&gt;</i></code> <dd>
This option limits the range of the X axis to the
specified interval. This (along with <code>-ly</code>) can be used
to zoom in on a particularly interesting portion of a
larger graph.

<dt><code>-ly</code> <code><i>&lt;yl,yh&gt;</i></code> <dd>
This option limits the range of the Y axis to the
specified interval.

<dt><code>-m</code> <dd>
Mark each data point with a distinctive marker. There
are eight distinctive markers used by xgraph. These
markers are assigned uniquely to each different line
style on black and white machines and varies with each
color on color machines.

<dt><code>-M</code>
<dd>Similar to <code>-m</code> but markers are assigned uniquely to each
eight consecutive data sets (this corresponds to each
different line style on color machines).

<dt><code>-nl</code>
<dd>Turn off drawing lines. When used with <code>-m</code>,
<code>-M</code>, <code>-p</code>, or <code>-P</code> this can be used
to produce scatter plots. When used with -bar, it can be used to
produce standard bar graphs.

<dt><code>-p</code>
<dd>Marks each data point with a small marker (pixel
sized). This is usually used with the -nl option for
scatter plots.

<dt><code>-P</code>
<dd>Similar to <code>-p</code> but marks each pixel with a large dot.

<dt><code>-rv</code>
<dd>Reverse video. On black and white displays, this will
invert the foreground and background colors. The
behaviour on color displays is undefined.

<dt><code>-t</code> <code><i>&lt;string&gt;</i></code>
<dd>Title of the plot. This string is centered at the top
of the graph.

<dt><code>-tf</code> <code><i>&lt;fontname&gt;</i></code>
<dd>Title font. This is the name of the font to use for
the graph title.  See the <code>-lf</code> description above
for how to specify fonts.
The default is <code>helvetica-BOLD-14</code>

<dt><code>-tk</code>
<dd>This option causes <code>pxgraph</code> to draw tick marks rather
than full grid lines. The <code>-bb</code> option is also useful
when viewing graphs with tick marks only.

<dt><code>-x</code>  <code><i>&lt;unitname&gt;</i></code>
<dd>This is the unit name for the X axis. Its default is "X".

<dt><code>-y</code> <code><i>&lt;unitname&gt;</i></code>
<dd>This is the unit name for the Y axis. Its default is "Y".

<dt><code>-zg</code> <code><i>&lt;color&gt;</i></code>
<dd>This is the color used to draw the zero grid line.
<b>Unsupported in the Java version.</b>

<dt><code>-zw</code> <code><i>&lt;width&gt;</i></code>
<dd>This is the width of the zero grid line in pixels.
<b>Unsupported in the Java version.</b>
</dl>

<h2> Compatibility Issues </h2>
Various compatibility issues are documented above in <b>bold</b>.
Below are some other issues:
<li>The original <code>xgraph</code> program allowed many formatting
directives inside the file.  This version only supports
<code>draw</code> and <code>move</code>.
<li>This version does not support X resources.
<h2><a name="Installation Instructions">Installation Instructions</a></h2>
The instructions below are for using the Java <CODE>pxgraph</CODE>
script instead of the X11 <CODE>pxgraph</CODE> binary within Ptolemy.

<ol>

<LI> Obtain and install a Java Development Kit (JDK) for your
platform from
<A HREF="http://www.javasoft.com"><CODE>www.javasoft.com</CODE></A>.
Pxgraph works best under JDK1.1.4.  Under JDK1.0.2,
the <CODE>Print</CODE> and <CODE>About</CODE> buttons will not work.

<li> Obtain the Ptplot tar file from <A HREF="http://ptolemy.eecs.berkeley.edu/java/ptplot"><CODE>http://ptolemy.eecs.berkeley.edu/java/ptplot</CODE></A>.
<li> <CODE>cd</CODE> to <CODE>$PTOLEMY/tycho/java</CODE> and ungzip and
untar the tar file:
<PRE>
cd $PTOLEMY/tycho/java
gzcat /tmp/ptplot1.0.tar.gz | tar -xf
</PRE>

<LI> The <CODE>pxgraph</CODE>shell script reads
a few environment variables and attempts to run.  For the script to
run, it needs to find the location of the Java JDK installation and
the location of the Ptplot <CODE>.class</CODE> files.
<MENU>
<LI> If the <CODE>JAVAHOME</CODE> environment variable is set, then 
it is read and <CODE>$JAVAHOME/lib/classes.zip</CODE> is used in the
classpath.  For example, if your JDK was at <CODE>/opt/jdk1.1.4</CODE>,
then you would add the following to your <CODE>.cshrc</CODE>:
<PRE>
setenv JAVAHOME /opt/jdk1.1.4
</PRE>
 <BR>If <CODE>JAVAHOME</CODE> is not set, then the script searches
the path and looks for the <CODE>java</CODE> binary.  If the
<CODE>java</CODE> binary is found, then the script looks for the
appropriate <CODE>classes.zip</CODE> to use.
 <BR>If none of the above works, you can edit the <CODE>pxgraph</CODE>
script and change the <CODE>JAVADEFAULT</CODE> setting at the top of
the file to point to your JDK.
</MENU>
To find the Ptplot <CODE>.class</CODE> files, the script reads the
<CODE>TYCHO</CODE> and <CODE>PTOLEMY</CODE> variables and looks in
<CODE>PTOLEMY/tycho/java/ptplot</CODE>.  If you do not have Ptolemy
or Tycho installed, you can edit the <CODE>pxgraph</CODE> script
and change <CODE>TYDEFAULT</CODE> so that the <CODE>Pxgraph.class</CODE>
file will be found at <CODE>$TYDEFAULT/java/ptplot/Pxgraph.class</CODE>

<LI> Move the old <CODE>pxgraph</CODE> binary to a safe place and
create a link to the Java Ptplot <CODE>pxgraph</CODE> script.
<PRE>
cd $PTOLEMY/bin.$PTARCH
mv pxgraph pxgraph.x11
ln -s ../tycho/java/plot/pxgraph .
</PRE>
</ol>

<p>
For further information about this tool, see the
<a href="http://ptolemy.eecs.berkeley.edu/java/ptplot">Java Plot Website</a>.

 * @author Christopher Hylands (cxh@eecs.berkeley.edu)
 * @version @(#)Pxgraph.java	1.55 10/03/97
 * @see Plot 
 */
public class Pxgraph extends Frame { 

    /** Process the arguments and plot the data.
     */
    public Pxgraph(String args[]) {
        //setLayout(new FlowLayout(FlowLayout.LEFT));
        //setLayout(new BorderLayout());
        _plotApplet = new Plot();
        _makeButtons();

        pack();
        add("Center",_plotApplet);
        try {
            _parseArgs(args);
        } catch (CmdLineArgException e) {
            System.err.println("Failed to parse command line arguments: " + e);
        }

        show();
        _plotApplet.init();
        _plotApplet.start();
    }


    public boolean action(Event e, Object arg) {
        Object target = e.target;
        if (_debug > 20) System.out.println("Pxgraph: action: "+e+" "+target);
        if (target == _exitButton) {
            System.exit(1);
            return true;
        } else if (target == _printButton) {
            _print();
            return true;
        } else if (target == _htmlButton) {
            _html();
            return true;
        } else if (target == _aboutButton) {
            _about();
            return true;
        } else
            return super.action (e, arg);
    }

    /** Handle an event.
     * @deprecated As of JDK1.1 in java.awt.component 
     * but we need to compile under 1.0.2 for netscape3.x compatibility.
     */
    public boolean handleEvent(Event e) {
        switch (e.id) {
        case Event.WINDOW_ICONIFY:
            //stopAnimation();
            break;
        case Event.WINDOW_DEICONIFY:
            //startAnimation();
            break;
        case Event.WINDOW_DESTROY:
            System.exit(0);
            break;
        }  

        return super.handleEvent(e); // FIXME: handleEvent is
        // deprecated in 1.1, we should use processEvent(),
        // However, we need to compile under 1.0.2 for compatibility with
        // netscape3.x so we stick with handleEvent().
    }

    /** Handle key down and key up events.
     */
    public boolean keyDown(Event e, int key) {
        int keyflags = e.modifiers;
        if (e.id == Event.KEY_PRESS) {
            char c = (char) e.key;
            switch (c) {
            case '\003':        // Control-C
            case '\004':        // Control-D
            case 'q':
                System.exit(0);
                break;
            case 'x':
                _pxgraphX11();  // Used for debugging, brings up pxgraph.x11.
                break;
            }
        }
        return true;
    }

    /** Parse the command line arguments, do any preprocessing, then plot.
     * If you have the <code>pxgraph</code> shell script, then 
     * type <code>pxgraph -help</code> for the complete set of arguments.
     */
    public static void main(String args[]) {
        Pxgraph pxgraph = new Pxgraph(args);

        if (_test) {
            if (_debug > 4) System.out.println("Sleeping for 2 seconds");
            try {
                Thread.currentThread().sleep(2000);
            }
            catch (InterruptedException e) {
            }
            System.exit(0);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    ////                         private methods                          ////


    /* Bring up a dialog box with version information.
     */ 
    private void _about() {
        Message message = new Message(
                "               Pxgraph\n" +
                "        A Java Plotting Tool\n\n" +
                "By: Edward A. Lee, eal@eecs.berkeley.edu and\n" +
                "    Christopher Hylands, cxh@eecs.berkeley.edu\n" +
                "Version 1.0, Build: @(#)Pxgraph.java	1.55 10/03/97\n\n"+
                "For help, type 'pxgraph -help', or see \n" +
                "the Pxgraph class documentation.\n" +
                "For more information, see\n" +
                "http://ptolemy.eecs.berkeley.edu/java/plot\n",
                Color.white,Color.black);
        message.setTitle("About Pxgraph");
        message.pack();
        message.show();
    }

    /* help - print out help
     */ 
    private void _help() {
        // FIXME: we should bring up a dialog box or something.

        // We use a table here to keep things neat.
        // If we have:
        //  {"-bd",  "<color>", "Border",  "White", "(Unsupported)"},
        // -bd       - The argument
        // <color>   - The description of the value of the argument
        // Border    - The Xgraph file directive (not supported at this time).
        // White     - The default (not supported at this time)
        // "(Unsupported)" - The string that is printed to indicate if
        //                   a option is unsupported.
        String commandOptions[][] = {
            {"-bd",  "<color>", "Border",  "White", "(Unsupported)"},
            {"-bg",  "<color>", "BackGround",  "White", ""},
            {"-brb", "<base>", "BarBase",  "0", "(Unsupported)"},
            {"-brw", "<width>", "BarWidth",  "1", ""},
            {"-bw",  "<size>", "BorderSize",  "1", "(Unsupported)"},
            {"-fg",  "<color>", "Foreground",  "Black", ""},
            {"-gw",  "<pixels>", "GridStyle",  "1", "(Unsupported)"},
            {"-lf",  "<fontname>", "LabelFont",  "helvetica-12", ""},
            {"-lw",  "<width>", "LineWidth",  "0", "(Unsupported)"},
            {"-lx",  "<xl,xh>", "XLowLimit, XHighLimit",  "0", ""},
            {"-ly",  "<yl,yh>", "YLowLimit, YHighLimit",  "0", ""},
            {"-t",   "<title>", "TitleText",  "An X Graph", ""},
            {"-tf",  "<fontname>", "TitleFont",  "helvetica-b-14", ""},
            {"-x",   "<unitName>", "XUnitText",  "X", ""},
            {"-y",   "<unitName>", "YUnitText",  "Y", ""},
            {"-zg",  "<color>", "ZeroColor",  "Black", "(Unsupported)"},
            {"-zw",  "<width>", "ZeroWidth",  "0", "(Unsupported)"},
        };

        String commandFlags[][] = {
            {"-bar", "BarGraph",  ""},
            {"-bb", "BoundBox",  "(Unsupported)"},
            {"-binary", "Binary",  ""},
            {"-db", "Debug",  ""},
            // -help is not in the original X11 pxgraph.
            {"-help", "Help",  ""},
            // -impulses is not in the original X11 pxgraph.
            {"-impulses", "Impulses",  ""},
            {"-lnx", "LogX",  "(Unsupported)"},
            {"-lny", "LogY",  "(Unsupported)"},
            {"-m", "Markers",  ""},
            {"-M", "StyleMarkers",  ""},
            {"-nl", "NoLines",  ""},
            {"-p", "PixelMarkers",  ""},
            {"-P", "LargePixel",  ""},
            {"-rv", "ReverseVideo",  ""},
            // -test is not in the original X11 pxgraph.  We use it for testing
            {"-test", "Test",  ""},
            {"-tk", "Ticks",  ""},
            // -v is not in the original X11 pxgraph.
            {"-v", "Version",  ""},
            {"-version", "Version",  ""},
        };
        int i;
        System.out.println("Usage: pxgraph [ options ] [=WxH+X+Y] [file ...]");
        System.out.println(" options that take values as second args:");
        for(i=0; i < commandOptions.length; i++) {
            System.out.println(" " + commandOptions[i][0] +
                    " " + commandOptions[i][1] +
                    " " + commandOptions[i][4] );
        }
        System.out.println(" Boolean flags:");
        for(i=0; i < commandFlags.length; i++) {
            System.out.println(" " + commandFlags[i][0] +
                    " " + commandFlags[i][2]);
        }
        System.out.println("The following pxgraph features are "+
                "not supported:");
        System.out.println(" * Directives in pxgraph input files");
        System.out.println(" * Xresources");
        System.out.println(" For complete documentation, see the "+
                "Pxgraph Java class documentation.");
        System.exit(1);
    }

    /* Dump out html that can be used to redisplay the plot as an applet.
     */  
    private void _html() {
        Dimension dim = size(); // size is deprecated in 1.1, we
                                // should use getSize(), but getSize
                                // is not in 1.0.2.        

        // Read in the user's CLASSPATH and get the first directory,
        // which should be the location of the Plot classes
        StringTokenizer stoken =
            new StringTokenizer(System.getProperty("java.class.path"),";:");
        String plotclassdir = new String("");
        if (stoken.hasMoreTokens()) {
            plotclassdir = stoken.nextToken();
        } 

        StringBuffer applettag =
            new StringBuffer("<!-- Automatically generated by pxgraph. -->\n"+
                    "<!-- See http://ptolemy.eecs.berkeley.edu"+
                    "/java/plot for more information. -->\n"+
                    "<html>\n<head>\n"+
                    "<title>"+getTitle()+"</title>\n<body>\n"+
                    "<!-- You will need to edit the codebase tag\n"+
                    "     below.  To use the most recent version\n"+
                    "     from over the network set it to:\n"+ 
                    "     http://ptolemy.eecs.berkeley.edu/java\n"+
                    "-->\n"+
                    "<applet name =\""+getTitle()+"\""+
                    " code=\"ptplot.Plot\""+
                    " width="+dim.width+" height="+dim.height+"\n"+
                    "    codebase=\""+plotclassdir+"\"\n"+
                    "    archive=\"ptplot/ptplot.zip\"\n"+
                    "    alt=\"If you had a java-enabled "+
                    "browser, you would see an applet here.\"\n>\n"+
                    "<param name=\"pxgraphargs\" value=\"");

        if (_cmdLineArgs.length > 0) { 
            for(int i=0;i<(_cmdLineArgs.length - 1);i++) {
                // FIXME: we are not checking for args that contain 
                // single or double quotes.
                if (_cmdLineArgs[i].indexOf(" ") != -1) {
                    // If the arg contains a space, wrap it in single quotes.
                    applettag.append("'" + _cmdLineArgs[i] + "' ");
                } else if (_cmdLineArgs[i].length() == 0) {
                    // If the arg is the empty string, print two single quotes.
                    applettag.append("'' ");
                } else
                    applettag.append(_cmdLineArgs[i] + " ");
            }
            applettag.append(_cmdLineArgs[_cmdLineArgs.length - 1]);
        }
        // FIXME: we should handle the background and foreground params.
        applettag.append("\">\n</applet>\n</body>\n</html>");

        // In the short term, we dump to stdout
        System.out.println(applettag.toString());
    }

    /* Set the visibility of the buttons
     */
    private void _setButtonsVisibility(boolean vis) {
        //_exitButton.setVisible(vis);
        //_printButton.setVisible(vis);
        //_htmlButton.setVisible(vis);
        //_aboutButton.setVisible(vis);
        if (vis) {
            _exitButton.show(); // FIXME: show() is
            // deprecated in JDK1.1, but we need to compile under
            // 1.0.2 for netscape3.x compatibility.
            _printButton.show(); // FIXME: show() deprecated, but . . .
            _htmlButton.show(); // FIXME: show() deprecated, but . . .
            _aboutButton.show(); // FIXME: show() deprecated, but . . .
        } else {
            _exitButton.hide(); // FIXME: hide() is
            // deprecated in JDK1.1, but we need to compile under
            // 1.0.2 for netscape3.x compatibility.
            _printButton.hide(); // FIXME: hide() deprecated, but . . .
            _htmlButton.hide(); // FIXME: hide() deprecated, but . . .
            _aboutButton.hide(); // FIXME: hide() deprecated, but . . .
        }
    }

    /* Create buttons.
     */
    private void _makeButtons() {
        //setLayout(new FlowLayout(FlowLayout.LEFT));
        Panel panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        _exitButton = new Button("Exit");
        panel.add(_exitButton);

        _printButton = new Button("Print");
        panel.add(_printButton);

        _htmlButton = new Button("HTML");
        panel.add(_htmlButton);

        _aboutButton = new Button("About");
        panel.add(_aboutButton);

        add("South", panel);
    }


    /* Parse the arguments and make calls to the plotApplet accordingly.
     */ 
    private int _parseArgs(String args[]) throws CmdLineArgException {
        int i = 0, j, argsread;
        String arg;

        String title = "A plot";
        int width = 400;      // Default width of the graph
        int height = 400;     // Default height of the graph

        // Copy the command line args so we can use them in the printer
        _cmdLineArgs = new String[args.length];
        try {
            System.arraycopy(args,0,_cmdLineArgs,0,args.length);
        } catch (ArrayIndexOutOfBoundsException e) {}
        catch (ArrayStoreException e) {}

        while (i < args.length) {
            arg = args[i++];
            if (_debug > 2) System.out.print("Pxgraph: arg = " + arg + "\n");

            if (arg.startsWith("-")) {
                if (arg.equals("-bg")) {
                    setBackground(PlotBox.getColorByName(args[i++]));
                    continue;
                } else if (arg.equals("-db")) {
                    _debug = 10;
                } else if (arg.equals("-debug")) {
                    _debug = (int)Integer.valueOf(args[i++]).intValue();
                    continue;
                } else if (arg.equals("-fg")) {
                    setForeground(PlotBox.getColorByName(args[i++]));
                    continue;
                } else if (arg.equals("-help")) {
                    // -help is not in the original X11 pxgraph.
                    _help();
                    continue;
                } else if (arg.equals("-test")) {
                    // -test is not in the original X11 pxgraph.
                    _test = true;
                    continue;
                } else if (arg.equals("-t")) {
                    // -t <title> TitleText "An X Graph"
                    title =  args[i++];
                    continue;
                } else if (arg.equals("-v") || arg.equals("-version")) {
                    // -version is not in the original X11 pxgraph.
                    _about();
                    continue;
                }
            } else if (arg.startsWith("=")) {
                // Process =WxH+X+Y
                int xscreen = 1, yscreen = 1;
                boolean screenlocationgiven = false;
                StringTokenizer stoken =
                    new StringTokenizer(arg.substring(1,arg.length()), "=x-+");
                if (stoken.hasMoreTokens()) {
                    width = (int)Integer.valueOf(stoken.nextToken()).
                        intValue();
                } 
                if (stoken.hasMoreTokens()) {
                    height = (int)Integer.valueOf(stoken.nextToken()).
                        intValue();
                } 
                if (stoken.hasMoreTokens()) {
                    xscreen = (int)Integer.valueOf(stoken.nextToken()).
                        intValue();
                    screenlocationgiven = true;
                } 
                if (stoken.hasMoreTokens()) {
                    yscreen = (int)Integer.valueOf(stoken.nextToken()).
                        intValue();
                    screenlocationgiven = true;
                } 
                if (screenlocationgiven) { 
                    // Note: we add one so that =800x200+0+0 will show up
                    // in the proper location.
                    setLocation(new Point(xscreen+1, yscreen+1));
                }
                continue;

            }
        }

        // Set up the frame
        resize(width,height);   // FIXME: resize is deprecated in 1.1,
                                // we should use setsize(width,height)
                                // but setsize is not in JDK1.0.2
        setTitle(title);

        argsread = i++;

        if (_debug > 2) {
            System.err.println("Pxgraph: title = " + title);
            System.err.println("Pxgraph: width = " + width + 
                    " height = " + height + " _debug = " + _debug);
        }
        _plotApplet.parseArgs(args);
        return argsread;
    }

    /* Spawn a browser and run the applet Plot class so that the
     * user can print.  Note that we use the JDK1.1 PrintJob
     * class here.  If you are compiling under 1.0.2, you can
     * just comment out the body of this method.
     */ 
    private void _print () {
        // awt.print.destination   - can be "printer" or "file"
        // awt.print.printer       - print command
        // awt.print.fileName      - name of the file to print
        // awt.print.numCopies     - obvious
        // awt.print.options       - options to pass to the print command
        // awt.print.orientation   - can be "portrait" or "landscape"
        // awt.print.paperSize     - can be "letter", "legal", "executive"
        //                           or "a4"

        Properties newprops= new Properties();
        newprops.put("awt.print.destination", "file");
        newprops.put("awt.print.fileName", "/tmp/t.ps");
        PrintJob printjob = getToolkit().getPrintJob(this,
                getTitle(),newprops);
        if (printjob != null) {          
            Graphics printgraphics = printjob.getGraphics();
            if (printgraphics != null) {
                Dimension dim = size(); // size is deprecated in 1.1, we
                // should use getSize(), but getSize
                // is not in 1.0.2.        
                
                // Make the buttons invisible
                _setButtonsVisibility(false);
                _plotApplet._setButtonsVisibility(false);

                // Print
                printAll(printgraphics);

                // Make the buttons visible, reset the graphics.
                _plotApplet._setButtonsVisibility(true);
                _setButtonsVisibility(true);

                resize(dim.width,dim.height);   // FIXME: resize is deprecated
                // in 1.1, we should use
                // setsize(width,height) but
                // setsize is not in JDK1.0.2

                show();
                printgraphics.dispose();
                printjob.end();
            } else {
                printjob.end();
            }
        }
    }

    /* Run the X11 pxgraph binary.  This function is only used for
     * checking backward compatibility with the X11 pxgraph binary.
     */ 
    private void _pxgraphX11() {
 	try {
            boolean sawdebug = false; // True if we saw a -debug flag
            String command[] = new String[_cmdLineArgs.length +1];
            command[0] = new String("pxgraph.x11");
            System.out.print("Pxgraph: about to execute: "+command[0]+" ");
            // Copy over args, except for -debug
            int j = 1;
            for(int i = 0; i < _cmdLineArgs.length; i++) {
                if (_cmdLineArgs[i].equals("-debug")) {
                    i++;
                } else {
                    command[j++] = _cmdLineArgs[i];
                }
                System.out.print(command[j-1]+" ");
            }
            System.out.println("");
 	    Runtime runtime = Runtime.getRuntime();
 	    Process browser = runtime.exec(command);
 	} catch (SecurityException e) {
            System.out.println("Pxgraph: _pxgraphX11: "+e);
        }
        catch (IOException e) {
            System.out.println("Pxgraph: _pxgraphX11: "+e);
        }
    }

    //////////////////////////////////////////////////////////////////////////
    ////                         private variables                        ////

    private Button _exitButton, _printButton, _htmlButton, _aboutButton;

    //  Command line args pxgraph was called with.
    private String _cmdLineArgs[];

    // For debugging, call with -db or -debug.
    private static int _debug = 0;

    // The Plot applet.
    private Plot _plotApplet; 

    // If true, then auto exit after a few seconds.
    private static boolean _test = false;
}
