import java.awt.*;

public class MyLayout implements LayoutManager, java.io.Serializable {
    double ratio[];
    int mode;

    static final int HORIZONTAL = 0;
    static final int VERTICAL = 1;

    public MyLayout() {
      double tmp[] = {1.0};
      this.mode = HORIZONTAL;
      this.ratio = tmp;
    }

    public MyLayout(int mode, double ratio[]) {
      this.mode = mode;
      this.ratio = ratio;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    /** 
     * Determines the preferred size of the container argument using 
     * this grid layout. 
     * <p>
     * The preferred width of a grid layout is the largest preferred 
     * width of any of the widths in the container times the number of 
     * columns, plus the horizontal padding times the number of columns 
     * plus one, plus the left and right insets of the target container. 
     * <p>
     * The preferred height of a grid layout is the largest preferred 
     * height of any of the widths in the container times the number of 
     * rows, plus the vertical padding times the number of rows plus one, 
     * plus the top and left insets of the target container. 
     * 
     * @param     target   the container in which to do the layout.
     * @return    the preferred dimensions to lay out the 
     *                      subcomponents of the specified container.
     * @see       java.awt.GridLayout#minimumLayoutSize 
     * @see       java.awt.Container#getPreferredSize()
     * @since     JDK1.0
     */
    public Dimension preferredLayoutSize(Container parent) {
      synchronized (parent.getTreeLock()){
	Insets insets = parent.getInsets();
	int ncomponents = parent.getComponentCount();
	int w = 0;
	int h = 0;
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = parent.getComponent(i);
	    Dimension d = comp.getPreferredSize();
	    int tmpW = 0, tmpH = 0;
	    try {
	      if (mode == HORIZONTAL){
		tmpW = (int)(d.width / ratio[i]);
		tmpH = d.height;
	      } else {
		tmpW = d.width;
		tmpH = (int)(d.height / ratio[i]);
	      }
	    } catch (ArrayIndexOutOfBoundsException e){
	      continue;
	    }

	    if (w < tmpW) {
		w = tmpW;
	    }
	    if (h < tmpH) {
		h = tmpH;
	    }
	}
	return new Dimension(insets.left + insets.right + w, 
			     insets.top + insets.bottom + h);
      }
    }

    /**
     * Determines the minimum size of the container argument using this 
     * grid layout. 
     * <p>
     * The minimum width of a grid layout is the largest minimum width 
     * of any of the widths in the container times the number of columns, 
     * plus the horizontal padding times the number of columns plus one, 
     * plus the left and right insets of the target container. 
     * <p>
     * The minimum height of a grid layout is the largest minimum height 
     * of any of the widths in the container times the number of rows, 
     * plus the vertical padding times the number of rows plus one, plus 
     * the top and left insets of the target container. 
     *  
     * @param       target   the container in which to do the layout.
     * @return      the minimum dimensions needed to lay out the 
     *                      subcomponents of the specified container.
     * @see         java.awt.GridLayout#preferredLayoutSize
     * @see         java.awt.Container#doLayout
     * @since       JDK1.0
     */
    public Dimension minimumLayoutSize(Container parent) {
      synchronized (parent.getTreeLock()){
	Insets insets = parent.getInsets();
	int ncomponents = parent.getComponentCount();
	int w = 0;
	int h = 0;
	for (int i = 0 ; i < ncomponents ; i++) {
	  Component comp = parent.getComponent(i);
	  Dimension d = comp.getMinimumSize();
	  int tmpW = 0, tmpH = 0;

	  try {
	    if (mode == HORIZONTAL){
	      tmpW = (int)(d.width / ratio[i]);
	      tmpH = d.height;
	    } else {
	      tmpW = d.width;
	      tmpH = (int)(d.height / ratio[i]);
	    }
	  } catch (ArrayIndexOutOfBoundsException e){
	    continue;
	  }
	  if (w < tmpW) {
	    w = tmpW;
	  }
	  if (h < tmpH) {
	    h = tmpH;
	  }
	}
	return new Dimension(insets.left + insets.right + w, 
			     insets.top + insets.bottom + h);
      }
    }

    /** 
     * Lays out the specified container using this layout. 
     * <p>
     * This method reshapes the components in the specified target 
     * container in order to satisfy the constraints of the 
     * <code>GridLayout</code> object. 
     * <p>
     * The grid layout manager determines the size of individual 
     * components by dividing the free space in the container into 
     * equal-sized portions according to the number of rows and columns 
     * in the layout. The container's free space equals the container's 
     * size minus any insets and any specified horizontal or vertical 
     * gap. All components in a grid layout are given the same size. 
     *  
     * @param      target   the container in which to do the layout.
     * @see        java.awt.Container
     * @see        java.awt.Container#doLayout
     * @since      JDK1.0
     */

    public void layoutContainer(Container parent) {
      synchronized (parent.getTreeLock()) {
	Insets insets = parent.getInsets();
	int ncomponents = parent.getComponentCount();

	int w = parent.getSize().width - (insets.left + insets.right);
	int h = parent.getSize().height - (insets.top + insets.bottom);

	double sum = 0.0;
	int x0 = insets.left;
	int y0 = insets.top;
	if (mode == HORIZONTAL){
	  for (int i = 0; i < ncomponents; i++){
	    try {
	      int tmpW = (int)(ratio[i] * w);
	      parent.getComponent(i).setBounds(x0 + (int)sum, y0, tmpW, h);
	      sum += tmpW;
	    } catch (ArrayIndexOutOfBoundsException e){
	    }
	  }
	} else {
	  for (int i = 0; i < ncomponents; i++){
	    try {
	      int tmpH = (int)(ratio[i] * h);
	      parent.getComponent(i).setBounds(x0, y0+(int)sum, w, tmpH);
	      sum += tmpH;
	    } catch (ArrayIndexOutOfBoundsException e){
	    }
	  }
	}
      }
    }
    
    /**
     * Returns the string representation of this grid layout's values.
     * @return     a string representation of this grid layout.
     * @since      JDK1.0
     */
    public String toString() {
	return getClass().getName() ;
    }
}
