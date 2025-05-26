package ninf.client;
import ninf.basic.NinfException;

public
class NinfTypeErrorException extends NinfException {
  int type;
  public NinfTypeErrorException() {
    super();
  }
  public NinfTypeErrorException(int type) {
    super();
    this.type = type;
  }
}

