package test;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.Scanner;


public class CMD
        extends Shell {
    private Text text;

    public static void main(String[] args) {
        try {
            char a = '\n';
            System.out.println(a);
            Display display = Display.getDefault();
            CMD shell = new CMD(display);
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


    public CMD(Display display) {
        super(display, 1264);
        setLayout(new FillLayout(256));

        Composite composite = new Composite(this, 0);
        composite.setLayout(new GridLayout(1, false));

        this.text = new Text(composite, 2050);
        this.text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
            }

        });
        this.text.setLayoutData(new GridData(4, 4, true, true, 1, 1));
        Scanner textScan = new Scanner(this.text.getText());

        if (textScan.hasNextLine()) {
            String line = textScan.nextLine();
            System.out.println(line);
        }
        createContents();
    }


    protected void createContents() {
        setText("SWT Application");
        setSize(634, 469);
    }

    protected void checkSubclass() {
    }
}


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/test/CMD.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */