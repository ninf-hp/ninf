package ninf.calc.plus;

public class LocalOperand extends NinfCalcOperand {
    Matrix value;
    boolean available = false;

    public boolean available() {
    	return available;
    }
    public Object toNinfCallArg() {
        return value;
    }
}
