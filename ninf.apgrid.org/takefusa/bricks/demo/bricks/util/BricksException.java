package bricks.util;

/** An Exception class for Bricks */
public class BricksException extends Exception {

    public String str;

    public BricksException() {
	super();
    }

    public BricksException(String str) {
	super();
	this.str = str;
    }

    public BricksException(String str, Object o){
	super();
	this.str = FormatString.format(str, o);
    }

    public BricksException(String str, Object o, Object o2){
	super();
	this.str = FormatString.format(str, o, o2);
    }

    public void addMessage(String str){
	this.str += "\n" + str;
    }

    public void addMessage(String str, Object o){
	this.str += "\n" + FormatString.format(str, o);
    }

    public void addMessage(String str, Object o, Object o2){
	this.str += "\n" + FormatString.format(str, o, o2);
    }

    public void printStackTrace(){
	if (str != null)
	    System.out.println(str);
	super.printStackTrace();
    }

    public String toString(){
	if (str != null)
	    return "\nBricksException:" + str;
	return super.toString();
    }
}
