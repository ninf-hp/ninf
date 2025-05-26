package ninf.calc.plus;
import ninf.client.*;
import java.util.*;

public class NinfCalcOperator extends NinfCalcElement {
    String server;
    NinfStub stub;
    int numOfConsumes;
    Vector results;
    Vector ninfCallArgs;
    NinfCalcStack stack;
    
    public NinfCalcOperator(NinfCalcStack stack, String server, NinfStub stub) {
	this.server = server;
	this.stub = stub;
	this.stack = stack;

	int offset = 0;
	numOfConsumes = 0;
	ninfCallArgs = new Vector();
	results = new Vector();
	for (int i = 0; i < stub.params.length; i++) {
	    NinfParamDesc desc = stub.params[i];
	    if (desc.IS_IN_MODE()) {
		if (desc.ndim == 0) {
		    ninfCallArgs.addElement(new Integer(0)); // dummy
		} else {
		    numOfConsumes++;
		    NinfCalcElement el = stack.peek(offset++);

		    int[] d = el.dimension;
		    if (el instanceof NinfCalcOperator) {
		    } else {
		        RemoteOperand rel = (RemoteOperand)el;
		        ninfCallArgs.addElement(rel.url.toString());
		    }
		    
		    for (int j = 0; j < desc.ndim; j++) {
			NinfDimParamElem p = desc.dim[j].size;
			if (p.type == NinfVal.VALUE_IN_ARG) {
			    int pos = p.val;
			    ninfCallArgs.setElementAt(new Integer(d[j]), pos);
System.out.println("" + p + "" + d[j]);
			}
		    }
		}
	    } else if (desc.IS_OUT_MODE()) {
		TmpRemoteOperand r = new TmpRemoteOperand(
		    NinfCalc.paradiseURL, "test"
		);
		int[] dim = new int[desc.ndim];
		for (int j = 0; j < desc.ndim; j++) {
		    NinfDimParamElem p = desc.dim[j].size;
		    if (p.type == NinfVal.VALUE_IN_ARG) {
			int pos = p.val;
			Integer d = (Integer)ninfCallArgs.elementAt(pos);
			dim[j] = d.intValue();
		    } else if (p.type == NinfVal.VALUE_CONST) {
			dim[j] = p.val;
		    }
		}
		r.setSize(dim);
		results.addElement(r);
		ninfCallArgs.addElement(r.url.toString());
	    }
	}
    }

}
