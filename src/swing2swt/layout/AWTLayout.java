
package swing2swt.layout;

import java.awt.Dimension;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/**
 * Superclass for all the AWT layouts ported to SWT.
 * @author Yannick Saillet
 */
public abstract class AWTLayout extends Layout {

   /** 
   * Key under which an eventual preferred size (set with setPreferredSize)
   * is stored as a user data in the SWT control.
   */
  public final static String KEY_PREFERRED_SIZE = "preferredSize";

  /**
   * Gets the preferred size of a component.
   * If a preferred size has been set with setPreferredSize, returns it, 
   * otherwise returns the component computed preferred size.
   */
  protected Point getPreferredSize(
    Control control,
    int wHint,
    int hHint,
    boolean changed) {
    // check if a preferred size was set on the control with 
    // SWTComponent.setPreferredSize(Dimension)
    Dimension d = (Dimension)control.getData(KEY_PREFERRED_SIZE);
    if (d != null)
      return new Point(d.width, d.height);
    return control.computeSize(wHint, hHint, changed);
  }
}