package bricks.util;

public abstract class ComponentCreator implements Creator {

    protected SimulationSet owner;
    protected SubComponentFactory subComponentFactory;

    public abstract void create(String str) throws BricksParseException;

    // for user Class
    public void set(
	SimulationSet owner, SubComponentFactory subComponentFactory
    ) {
	this.owner = owner;
	this.subComponentFactory = subComponentFactory;
    }
}
