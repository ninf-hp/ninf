package bricks.util;

/** An Exception class for Bricks */
public class BricksNotScheduledException extends BricksException{

    public BricksNotScheduledException() {
	super();
    }

    public BricksNotScheduledException(String str) {
	super();
	this.str = str;
    }

    public BricksNotScheduledException(String str, Object o){
	super();
	this.str = FormatString.format(str, o);
    }

    public BricksNotScheduledException(String str, Object o, Object o2){
	super();
	this.str = FormatString.format(str, o, o2);
    }

    public String toString(){
	if (str != null)
	    return "BricksNotScheduledException:" + str;
	return super.toString();
    }
}
