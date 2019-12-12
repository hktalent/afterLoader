 package net.rebeyond.behinder.ui;
 
 import java.net.InetSocketAddress;
 import java.net.Proxy;
 import java.net.Proxy.Type;
 import net.rebeyond.behinder.core.ShellManager;
 import net.rebeyond.behinder.utils.Constants;
 import net.rebeyond.behinder.utils.Utils;
 import org.eclipse.swt.events.SelectionAdapter;
 import org.eclipse.swt.events.SelectionEvent;
 import org.eclipse.swt.layout.FillLayout;
 import org.eclipse.swt.layout.GridData;
 import org.eclipse.swt.layout.GridLayout;
 import org.eclipse.swt.widgets.Button;
 import org.eclipse.swt.widgets.Combo;
 import org.eclipse.swt.widgets.Composite;
 import org.eclipse.swt.widgets.Control;
 import org.eclipse.swt.widgets.Display;
 import org.eclipse.swt.widgets.Label;
 import org.eclipse.swt.widgets.MessageBox;
 import org.eclipse.swt.widgets.Shell;
 import org.eclipse.swt.widgets.Text;
 import org.json.JSONObject;
 
 
 public class ProxyForm
   extends Shell
 {
   private Text proxyIPTxt;
   private Text proxyPortTxt;
   private Text ProxyUserTxt;
   private Text ProxyPassTxt;
   private Label ProxyStatusLabel;
   private Composite btnComp;
   private Button enableProxyBtn;
   private Button disableProxyBtn;
   private Composite proxyComp;
   private Composite switchComp;
   private Label errorLabel;
   
   private void loadProxyInfo()
     throws Exception
   {
     JSONObject proxyEntity = Main.shellManager.findProxy("default");
     this.proxyIPTxt.setText(proxyEntity.getString("ip"));
     this.proxyPortTxt.setText(proxyEntity.getInt("port"));
     this.ProxyUserTxt.setText(proxyEntity.getString("username"));
     this.ProxyPassTxt.setText(proxyEntity.getString("password"));
     boolean enable = proxyEntity.getInt("status") == Constants.PROXY_ENABLE;
     if (enable)
     {
       this.enableProxyBtn.setSelection(true);
     }
     else
     {
       this.disableProxyBtn.setSelection(true);
       enableControls(false);
     }
   }
   
   private void enableControls(boolean enable) {
     Control[] arrayOfControl;
     int j = (arrayOfControl = this.proxyComp.getChildren()).length; for (int i = 0; i < j; i++) { Control control = arrayOfControl[i];
       
       if ((control == this.switchComp) || (control == this.btnComp))
       {
         control.setEnabled(true);
       }
       else
       {
         control.setEnabled(enable);
       }
     }
   }
   
   private void showError(String errorTxt) {
     MessageBox dialog = new MessageBox(getShell(), 33);
     dialog.setText("保存失败");
     dialog.setMessage(errorTxt);
     dialog.open();
   }
   
 
 
 
   public ProxyForm(Display display, final Label ProxyStatusLabel)
   {
     super(display, 1264);
     this.ProxyStatusLabel = ProxyStatusLabel;
     setLayout(new FillLayout(256));
     
     this.proxyComp = new Composite(this, 0);
     this.proxyComp.setLayout(new GridLayout(2, false));
     new Label(this.proxyComp, 0);
     
     this.switchComp = new Composite(this.proxyComp, 0);
     this.switchComp.setLayout(new GridLayout(2, false));
     
     this.enableProxyBtn = new Button(this.switchComp, 16);
     this.enableProxyBtn.addSelectionListener(new SelectionAdapter()
     {
       public void widgetSelected(SelectionEvent arg0) {
         ProxyForm.this.enableControls(true);
       }
     });
     this.enableProxyBtn.setText("启用");
     
     this.disableProxyBtn = new Button(this.switchComp, 16);
     this.disableProxyBtn.addSelectionListener(new SelectionAdapter()
     {
       public void widgetSelected(SelectionEvent arg0) {
         ProxyForm.this.enableControls(false);
       }
     });
     this.disableProxyBtn.setText("禁用");
     
     Label lblNewLabel_4 = new Label(this.proxyComp, 0);
     lblNewLabel_4.setLayoutData(new GridData(131072, 16777216, false, false, 1, 1));
     lblNewLabel_4.setText("类型");
     
     final Combo proxyTypeCombo = new Combo(this.proxyComp, 0);
     proxyTypeCombo.setText("HTTP");
     
     Label lblNewLabel = new Label(this.proxyComp, 0);
     lblNewLabel.setLayoutData(new GridData(131072, 16777216, false, false, 1, 1));
     lblNewLabel.setText("IP地址：");
     
     this.proxyIPTxt = new Text(this.proxyComp, 2048);
     this.proxyIPTxt.setLayoutData(new GridData(4, 16777216, true, false, 1, 1));
     
     Label lblNewLabel_1 = new Label(this.proxyComp, 0);
     lblNewLabel_1.setLayoutData(new GridData(131072, 16777216, false, false, 1, 1));
     lblNewLabel_1.setText("端口：");
     
     this.proxyPortTxt = new Text(this.proxyComp, 2048);
     this.proxyPortTxt.setLayoutData(new GridData(4, 16777216, true, false, 1, 1));
     
     Label lblNewLabel_2 = new Label(this.proxyComp, 0);
     lblNewLabel_2.setLayoutData(new GridData(131072, 16777216, false, false, 1, 1));
     lblNewLabel_2.setText("用户名：");
     
     this.ProxyUserTxt = new Text(this.proxyComp, 2048);
     this.ProxyUserTxt.setLayoutData(new GridData(4, 16777216, true, false, 1, 1));
     
     Label lblNewLabel_3 = new Label(this.proxyComp, 0);
     lblNewLabel_3.setLayoutData(new GridData(131072, 16777216, false, false, 1, 1));
     lblNewLabel_3.setText("密码：");
     
     this.ProxyPassTxt = new Text(this.proxyComp, 2048);
     this.ProxyPassTxt.setLayoutData(new GridData(4, 16777216, true, false, 1, 1));
     new Label(this.proxyComp, 0);
     
     this.errorLabel = new Label(this.proxyComp, 0);
     
     this.btnComp = new Composite(this.proxyComp, 0);
     GridLayout gl_btnComp = new GridLayout(2, true);
     gl_btnComp.verticalSpacing = 0;
     this.btnComp.setLayout(gl_btnComp);
     this.btnComp.setLayoutData(new GridData(4, 16777216, false, false, 2, 1));
     
     final Button saveProxyBtn = new Button(this.btnComp, 0);
     saveProxyBtn.addSelectionListener(new SelectionAdapter()
     {
       public void widgetSelected(SelectionEvent arg0)
       {
         try
         {
           String type = proxyTypeCombo.getText();
           if (!type.toUpperCase().equals("HTTP"))
           {
             ProxyForm.this.showError("目前仅支持HTTP类型的代理");
             return;
           }
           String ip = ProxyForm.this.proxyIPTxt.getText().trim();
           if (!Utils.checkIP(ip))
           {
             ProxyForm.this.showError("IP格式有误");
             return;
           }
           String portTxt = ProxyForm.this.proxyPortTxt.getText().trim();
           if (!Utils.checkPort(portTxt))
           {
             ProxyForm.this.showError("端口格式有误");
             return;
           }
           int port = Integer.parseInt(portTxt);
           String username = ProxyForm.this.ProxyUserTxt.getText();
           String password = ProxyForm.this.ProxyPassTxt.getText();
           int status = ProxyForm.this.enableProxyBtn.getSelection() ? 0 : 1;
           if (ProxyForm.this.disableProxyBtn.getSelection())
           {
             Main.currentProxy = null;
             ProxyStatusLabel.setVisible(false);
           }
           else
           {
             ProxyForm.this.setProxy(type, ip, port, username, password);
             ProxyStatusLabel.setVisible(true);
           }
           try {
             Main.shellManager.updateProxy("default", "http", ip, port, username, password, status);
           } catch (Exception e) {
             e.printStackTrace();
           }
           saveProxyBtn.getShell().dispose();
         }
         catch (Exception e)
         {
           ProxyForm.this.showError(e.getMessage());
         }
         
       }
     });
     saveProxyBtn.setLayoutData(new GridData(131072, 16777216, false, false, 1, 1));
     saveProxyBtn.setText("保存");
     
     final Button cancelProxyBtn = new Button(this.btnComp, 0);
     cancelProxyBtn.addSelectionListener(new SelectionAdapter()
     {
       public void widgetSelected(SelectionEvent arg0) {
         cancelProxyBtn.getShell().dispose();
       }
     });
     cancelProxyBtn.setLayoutData(new GridData(16384, 16777216, true, false, 1, 1));
     cancelProxyBtn.setText("取消");
     new Label(this.proxyComp, 0);
     new Label(this.proxyComp, 0);
     createContents();
     try {
       loadProxyInfo();
     }
     catch (Exception e) {
       e.printStackTrace();
     }
   }
   
   private void setProxy(String type, String ip, int port, String username, String password) {
     InetSocketAddress proxyAddr = new InetSocketAddress(ip, port);
     Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddr);
     Main.currentProxy = proxy;
     Main.proxyUserName = username;
     Main.proxyPassword = password;
   }
   
 
   protected void createContents()
   {
     setText("代理服务器设置");
     setSize(456, 285);
   }
   
   protected void checkSubclass() {}
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/ui/ProxyForm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */