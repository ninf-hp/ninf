package bricks.util;

/** An Exception class for Bricks */
public class BricksParseException extends BricksException{

    public BricksParseException() {
	super();
    }

    public BricksParseException(String str) {
	super();
	this.str = str;
    }

    public BricksParseException(String str, Object o){
	super();
	this.str = FormatString.format(str, o);
    }

    public BricksParseException(String str, Object o, Object o2){
	super();
	this.str = FormatString.format(str, o, o2);
    }

    public String toString(){
	if (str != null)
	    return "\nBricksParseException: " + str;
	return super.toString();
    }
}
