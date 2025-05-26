package ninf.basic;
import ninf.basic.NinfException;
import ninf.basic.NinfError;

public class NinfErrorException extends NinfException{
  public int errorNo;

  public NinfErrorException(int errorNo){
    super();
    this.errorNo = errorNo;
  }

  public String toString(){
    return NinfError.getString(errorNo);
  }

}
