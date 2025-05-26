package ninf.calc.plus;

public abstract class NinfCalcOperand extends NinfCalcElement {    
    public abstract boolean available();
    public abstract Object toNinfCallArg();
}
