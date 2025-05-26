package ninf.calc.plus;
import java.awt.*;

public class HStackLayout implements LayoutManager {
    int hgap;
    public HStackLayout() {
	this(0);
    }
    public HStackLayout(int hgap) {
	this.hgap = hgap;
    }
	
    public Dimension preferredLayoutSize(Container target) {
	int totalWidth = 0;
	int maxHeight = 0;
	for (int i = 0; i < target.countComponents(); i++) {
	    Component c = target.getComponent(i);
	    if (c.isVisible()) {
		Dimension p = c.preferredSize();
		totalWidth += p.width;
		maxHeight = Math.max(maxHeight, p.height);
	    }
	}
	Insets insets = target.insets();
	int w = totalWidth + insets.left + insets.right;
	int h = maxHeight + insets.top + insets.bottom;
	return new Dimension(w, h);
    }
    public void layoutContainer(Container target) {
	Insets insets = target.insets();
	int height = target.size().height - (insets.top + insets.bottom);
	int x = 0;
	for (int i = 0; i < target.countComponents(); i++) {
	    Component c = target.getComponent(i);
	    if (c.isVisible()) {
		Dimension p = c.preferredSize();
		int w = p.width;
		int h = height;
		c.reshape(x + insets.left, insets.top, w, h);
		x += w + hgap;
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

