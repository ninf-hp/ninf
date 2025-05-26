package ninf.calc.plus;
import java.net.*;

public class TmpRemoteOperand extends WritableRemoteOperand {    

    public TmpRemoteOperand(URL url) {
        super(url);
    }
    public TmpRemoteOperand(URL baseURL, String username) {
        super(makeFileURL(baseURL, generateUniqFilename(username)));
    }

    private static URL makeFileURL(URL baseURL, String filename) {
        try {
            return new URL(baseURL, filename);
        } catch (MalformedURLException e) {
            throw new InternalError();
        }
    }
    private static int seedForUniqFilename = 0;
    public static String generateUniqFilename(String keyword) {
        return keyword + "-" + 
            new Long(System.currentTimeMillis()).toString() + "-" + 
            new Integer(seedForUniqFilename++).toString() +
	    ".coordination";
    }

    public void setSize(int[] dim) {
	this.dimension = dim;
    }

}
