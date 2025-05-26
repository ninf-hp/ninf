package ninf.calc.plus;
import java.awt.*;

public abstract class NinfCalcElementPanel extends Panel
    implements NinfCalcElementView 
{
    protected NinfCalcElement model;
    public NinfCalcElementPanel(NinfCalcElement model) {
    	this.model = model;
    }

    public void init() {
    }
}
