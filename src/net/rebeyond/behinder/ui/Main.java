package net.rebeyond.behinder.ui;

import net.rebeyond.behinder.core.ShellManager;
import net.rebeyond.behinder.utils.Constants;
import net.rebeyond.behinder.utils.Utils;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;


// Main.afterLoader
// Main.crter
public class Main {
    public static String afterLoader = "afterLoader ";
    public static String crter = "M.T.X_51pwn.com";
    protected Shell shlGemini;
    private Table table;
    public static Proxy currentProxy;
    public static String proxyUserName;
    public static String proxyPassword;
    public static ShellManager shellManager;
    public static Map<String, String> globalHeaders;
    private Label proxyStatusLabel;

    public static void main(String[] args) {
        try {
            Main window = new Main();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            Main window = new Main();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProxy() {
        try {
            JSONObject proxyEntity = shellManager.findProxy("default");
            boolean enable = proxyEntity.getInt("status") == Constants.PROXY_ENABLE;
            if (enable) {
                String ip = proxyEntity.getString("ip");
                int port = proxyEntity.getInt("port");
                proxyUserName = proxyEntity.getString("username");
                proxyPassword = proxyEntity.getString("password");
                InetSocketAddress proxyAddr = new InetSocketAddress(ip, port);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddr);
                currentProxy = proxy;
                this.proxyStatusLabel.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillShells() throws Exception {
        this.table.removeAll();
        JSONArray shellList = shellManager.listShell();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Object shell : shellList) {
            JSONObject shellObj = (JSONObject) shell;
            String url = shellObj.getString("url");
            String ip = shellObj.getString("ip");
            String password = shellObj.getString("password");
            String type = shellObj.getString("type");
            String os = shellObj.getString("os");
            String comment = shellObj.getString("comment");
            String addTime = df.format(new Timestamp(shellObj.getLong("addtime")));
            String updateTime = df.format(new Timestamp(shellObj.getLong("updatetime")));
            String accessTime = df.format(new Timestamp(shellObj.getLong("accesstime")));
            TableItem item = new TableItem(this.table, 0);
            item.setText(new String[]{url, ip, password, type, os, comment, addTime});
            item.setData("id", Integer.valueOf(shellObj.getInt("id")));
        }

        int i = 0;
        for (int n = this.table.getColumnCount(); i < n; i++) {
            this.table.getColumn(i).pack();
        }
    }


    public void open() {
        Display display = Display.getDefault();
        try {
            shellManager = new ShellManager();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        createContents();
        this.shlGemini.open();
        this.shlGemini.layout();
        try {
            this.shlGemini.setImage(new Image(this.table.getDisplay(), new ByteArrayInputStream(Utils.getResourceData("net/rebeyond/behinder/resource/logo.jpg"))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (!this.shlGemini.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void openShellWindow(int shellID) throws Exception {
        JSONObject shellEntity = shellManager.findShell(shellID);
        MainShell m = new MainShell(Display.getDefault(), shellEntity);
        m.setText(shellEntity.getString("url") + "   " + Main.afterLoader + " " + Constants.VERSION);
        m.setImage(new Image(this.table.getDisplay(), new ByteArrayInputStream(Utils.getResourceData("net/rebeyond/behinder/resource/logo.jpg"))));
        m.open();
    }


    protected void createContents() {
        final Main me = this;
        this.shlGemini = new Shell();
        this.shlGemini.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent arg0) {
                Display.getDefault().dispose();
                Main.shellManager.closeConnection();
                System.exit(0);
            }
        });
        this.shlGemini.setSize(936, 565);
        this.shlGemini.setText(String.format(Main.afterLoader + " %s 51Pwn客户端", new Object[]{Constants.VERSION}));
        this.shlGemini.setLayout(new GridLayout(1, false));

        ToolBar toolBar = new ToolBar(this.shlGemini, 8519680);
        ToolItem toolItem = new ToolItem(toolBar, 8);
        toolItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                ProxyForm proxyForm = new ProxyForm(Display.getDefault(), Main.this.proxyStatusLabel);
                proxyForm.open();
            }

        });
        toolItem.setText("设置代理");


        this.table = new Table(this.shlGemini, 67584);
        this.table.setLayoutData(new GridData(4, 4, true, true, 1, 1));
        this.table.setHeaderVisible(true);
        this.table.setLinesVisible(true);
        this.table.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent e) {
                if (e.button == 3) {
                    Menu menu = new Menu(Main.this.table);
                    Main.this.table.setMenu(menu);
                    MenuItem addItem = new MenuItem(menu, 8);
                    addItem.setText("新增");
                    addItem.addListener(13, new Listener() {

                        public void handleEvent(Event arg0) {
                            ShellForm addShell = new ShellForm(Display.getDefault(), -1, Main.this);
                            addShell.open();
                        }
                    });
                    MenuItem refreshItem = new MenuItem(menu, 8);
                    refreshItem.setText("刷新");
                    refreshItem.addListener(13, new Listener() {
                        public void handleEvent(Event arg0) {
                            try {
                                Main.this.fillShells();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    if (Main.this.table.getSelection().length == 0)
                        return;
                    MenuItem openItem = new MenuItem(menu, 8);
                    openItem.setText("打开");
                    openItem.addListener(13, new Listener() {

                        public void handleEvent(Event arg0) {
                            try {
                                int ShellID = ((Integer) Main.this.table.getSelection()[0].getData("id")).intValue();
                                Main.this.openShellWindow(ShellID);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    MenuItem copyItem = new MenuItem(menu, 8);
                    copyItem.setText("拷贝");
                    copyItem.addListener(13, new Listener() {

                        public void handleEvent(Event arg0) {
                            try {
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                Transferable trans = new StringSelection(Main.this.table.getSelection()[0].getText(0));

                                clipboard.setContents(trans, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    });
                    MenuItem editItem = new MenuItem(menu, 8);
                    editItem.setText("编辑");
                    editItem.addListener(13, new Listener() {
                        public void handleEvent(Event arg0) {
                            int ShellID = ((Integer) Main.this.table.getSelection()[0].getData("id")).intValue();
                            ShellForm addShell = new ShellForm(Display.getDefault(), ShellID, Main.this);
                            addShell.open();
                        }
                    });
                    MenuItem deleteItem = new MenuItem(menu, 8);
                    deleteItem.setText("删除");
                    deleteItem.addListener(13, new Listener() {
                        public void handleEvent(Event arg0) {
                            MessageBox dialog = new MessageBox(Main.this.shlGemini, 196);
                            dialog.setText("确认");
                            dialog.setMessage("确定删除？");
                            if (dialog.open() == 128)
                                return;
                            int ShellID = ((Integer) Main.this.table.getSelection()[0].getData("id")).intValue();
                            try {
                                Main.shellManager.deleteShell(ShellID);
                                Main.this.fillShells();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }


            public void mouseDoubleClick(MouseEvent e) {
                try {
                    int ShellID = ((Integer) Main.this.table.getSelection()[0].getData("id")).intValue();
                    Main.this.openShellWindow(ShellID);

                } catch (Exception localException) {
                }
            }


        });
        String[] tableHeader = {"URL", "IP", "访问密码", "脚本类型", "OS类型", "备注", "添加时间"};
        for (int i = 0; i < tableHeader.length; i++) {
            TableColumn tableColumn = new TableColumn(this.table, 0);
            tableColumn.setText(tableHeader[i]);
        }


        Group group = new Group(this.shlGemini, 0);

        group.setLayoutData(new GridData(4, 16777216, true, false, 1, 1));
        group.setLayout(new GridLayout(7, false));


        Label label_1 = new Label(group, 0);
        label_1.setLayoutData(new GridData(4, 16777216, true, false, 1, 1));
        label_1.setText("请勿用于非法用途");

        this.proxyStatusLabel = new Label(group, 0);
        this.proxyStatusLabel.setVisible(false);
        this.proxyStatusLabel.setText("代理生效中");


        Label label = new Label(group, 2);
        GridData gd_label = new GridData(16384, 16777216, false, false, 1, 1);
        gd_label.heightHint = 20;
        label.setLayoutData(gd_label);
        Label lblV = new Label(group, 0);
        lblV.setText(String.format(Main.afterLoader + " %s", new Object[]{Constants.VERSION}));

        Label label_3 = new Label(group, 2);
        GridData gd_label_3 = new GridData(16384, 16777216, false, false, 1, 1);
        gd_label_3.heightHint = 20;
        label_3.setLayoutData(gd_label_3);

        Label lblByRebeyond = new Label(group, 0);
        lblByRebeyond.setText("By " + Main.crter);
        new Label(group, 0);
        try {
            fillShells();
            setProxy();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
