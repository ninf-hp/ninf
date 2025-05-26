package ninf.client;

public interface NinfConstant {
/***********************   STATIC VARIABLES   ***********************/

  static final int NINF_ACK_ERROR =     -1;	/* error ack, < 0 */
  static final int NINF_ACK_OK =	   0;	/* OK */
  static final int NINF_REQ_CALL =       1;
  static final int NINF_REQ_STUB_INFO	 = 2;
  static final int NINF_REQ_KILL	 = 3;

  static final int NINF_CALLBACK         = 4;
  static final int NINF_CALLBACK_ACK_OK  = 5;
  static final int NINF_CALLBACK_ACK_ERR = 6;


  static final int NINF_REQ_CALL_WITH_STREAM = 7;

  static final int MAX_PARAMS = 20;
}
