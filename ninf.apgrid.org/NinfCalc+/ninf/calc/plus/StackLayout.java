package ninf.calc.plus;
import java.awt.*;

public class StackLayout implements LayoutManager {
    int vgap;
    public StackLayout() {
	this(0);
    }
    public StackLayout(int vgap) {
	this.vgap = vgap;
    }
	
    public Dimension preferredLayoutSize(Container target) {
	int totalHeight = 0;
	int maxWidth = 0;
	for (int i = 0; i < target.countComponents(); i++) {
	    Component c = target.getComponent(i);
	    if (c.isVisible()) {
		Dimension p = c.preferredSize();
		totalHeight += p.height;
		maxWidth = Math.max(maxWidth, p.width);
	    }
	}
	Insets insets = target.insets();
	int w = maxWidth + insets.left + insets.right;
	int h = totalHeight + insets.top + insets.bottom;
	return new Dimension(w, h);
    }
    public void layoutContainer(Container target) {
	Insets insets = target.insets();
	int width = target.size().width - (insets.left + insets.right);
	int y = 0;
	for (int i = target.countComponents() -1; i >= 0; i--) {
	    Component c = target.getComponent(i);
	    if (c.isVisible()) {
		Dimension p = c.preferredSize();
		int w = width;
		int h = p.height;
		c.reshape(insets.left, y + insets.top, w, h);
		y += h + vgap;
	    }
	}
    }
    public Dimension minimumLayoutSize(Container target) {
	return preferredLayoutSize(target);
    }
    public void addLayoutComponent(String name, Component comp) {
    }
    public void removeLayoutComponent(Component comp) {
    }
}

