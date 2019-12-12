package test;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class shell
        extends Shell {
    public static void main(String[] args) {
        try {
            Display display = Display.getDefault();
            shell shell = new shell(display);

            shell.open();
            shell.layout();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public shell(Display paramDisplay) {
    }


    protected void createContents() {
        setText("SWT Application");
        setSize(450, 300);
    }

    protected void checkSubclass() {
    }
}


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/test/shell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */