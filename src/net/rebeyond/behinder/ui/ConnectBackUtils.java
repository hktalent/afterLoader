 package net.rebeyond.behinder.ui;
 
 import net.rebeyond.behinder.core.ShellService;
 import org.eclipse.swt.widgets.Button;
 import org.eclipse.swt.widgets.Display;
 import org.eclipse.swt.widgets.Label;
 import org.json.JSONObject;
 
 
 public class ConnectBackUtils
 {
   private ShellService currentShellService;
   private Label statusLabel;
   
   public ConnectBackUtils(ShellService shellService, Label statusLabel)
   {
     this.currentShellService = shellService;
     this.statusLabel = statusLabel;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public void connectBack(Button btn, final String type, final String ip, final String port)
   {
     new Thread()
     {
       public void run()
       {
         try
         {
           final JSONObject resultObj = ConnectBackUtils.this.currentShellService.connectBack(type, ip, port);
           final String status = resultObj.getString("status");
           
           Display.getDefault().syncExec(new Runnable() {
             public void run() {
               if (ConnectBackUtils.this.statusLabel.isDisposed())
                 return;
               if (status.equals("fail"))
               {
                 String msg = resultObj.getString("msg");
                 ConnectBackUtils.this.statusLabel.setText("操作失败:" + msg);
               }
             }
           });
         }
         catch (Exception e) {
           e.printStackTrace();
           if (e.getMessage() != null)
           {
             Display.getDefault().syncExec(new Runnable() {
               public void run() {
                 if (ConnectBackUtils.this.statusLabel.isDisposed())
                   return;
                 ConnectBackUtils.this.statusLabel.setText(e.getMessage());
               }
             });
           }
         }
       }
     }.start();
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/ui/ConnectBackUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */