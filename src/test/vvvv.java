package test;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;


public class vvvv {
    public static void main(String[] args) {
        Display display = Display.getDefault();
        Shell shell = new Shell();
        shell.setSize(738, 574);
        shell.setText("SWT Application");

        shell.setLayout(new FillLayout());

        ViewForm viewForm = new ViewForm(shell, 0);
        viewForm.setLayout(new FillLayout());
        ToolBar toolBar = new ToolBar(viewForm, 0);
        Text text = new Text(viewForm, 2560);
        viewForm.setContent(text);


        ToolItem toolItem = new ToolItem(toolBar, 8);

        toolItem.setText("取得");

        ToolItem toolItem2 = new ToolItem(toolBar, 8);

        toolItem2.setText("清除");

        viewForm.setTopLeft(toolBar);

        CLabel lblNewLabel = new CLabel(viewForm, 0);
        lblNewLabel.setImage(SWTResourceManager.getImage(vvvv.class, "/javax/swing/plaf/basic/icons/JavaCup16.png"));
        viewForm.setTopCenter(lblNewLabel);
        lblNewLabel.setText("New Label");
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
}


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/test/vvvv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */