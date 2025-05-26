package ninf.calc.plus;

public class SimpleElementViewFactory implements ElementViewFactory {
    public NinfCalcElementView create(NinfCalcElement model) {
	if (model instanceof ReadOnlyRemoteOperand) {
	    return new ReadOnlyRemoteOperandPanel((ReadOnlyRemoteOperand)model);
	} else if (model instanceof NinfCalcOperator) {
	    return new NinfCalcOperatorPanel((NinfCalcOperator)model);
	} else if (model instanceof TmpRemoteOperand) {
	    return new TmpRemoteOperandPanel((TmpRemoteOperand)model);
	} else if (model instanceof ErrorElement) {
	    return new ErrorElementPanel((ErrorElement)model);
	}
	return null;
    }
}
